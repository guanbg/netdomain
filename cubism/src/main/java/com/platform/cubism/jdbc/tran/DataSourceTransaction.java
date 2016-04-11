package com.platform.cubism.jdbc.tran;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.platform.cubism.util.Assert;

public class DataSourceTransaction extends AbstractTransaction {
	private static final long serialVersionUID = 8992148421005098721L;
	private DataSource dataSource;

	public DataSourceTransaction() {
	}

	public DataSourceTransaction(DataSource dataSource) {
		setDataSource(dataSource);
	}

	public DataSource getDataSource() {
		Assert.notNull(dataSource, "Property 'dataSource' is required");
		return dataSource;
	}

	public DataSourceTransaction setDataSource(DataSource dataSource) {
		Assert.notNull(dataSource, "Property 'dataSource' is required");
		this.dataSource = dataSource;
		return this;
	}

	public TransactionStatus getTransaction() {
		return getTransaction(null);
	}

	public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
		getDataSource();

		if (definition == null) {
			definition = new DefaultTransactionDefinition();
		}

		ConnectionHolder conHolder = TransactionSynchronizationManager.getResource(this.dataSource);
		boolean isExistingTransaction = conHolder != null && conHolder.isTransactionActive();
		switch (definition.getPropagationBehavior()) {
		case REQUIRED:
			logger.info("=====>>>" + (isExistingTransaction ? "已存在一个事务，使用当前事务..." : "当前没有事务，开启一个新的数据库事务..."));
			return isExistingTransaction ? handleExistingTransaction(definition) : handleNewTransaction(definition, false);
		case SUPPORTS:
			logger.info("=====>>>" + (isExistingTransaction ? "已存在一个事务，使用当前事务..." : "当前没有事务，以非事务的方式执行..."));
			return isExistingTransaction ? handleExistingTransaction(definition) : handleNoTransaction(definition, false);
		case MANDATORY:
			if (isExistingTransaction) {
				logger.info("=====>>>已存在一个事务，使用当前事务...");
				return handleExistingTransaction(definition);
			}
			throw new TransactionException("No existing transaction found for transaction marked with propagation 'mandatory'");
		case REQUIRES_NEW:
			logger.info("=====>>>" + (isExistingTransaction ? "已存在一个事务，挂起当前事务，重新开启一个新的数据库事务..." : "当前没有事务，开启一个新的数据库事务..."));
			return isExistingTransaction ? handleNewTransaction(definition, true) : handleNewTransaction(definition, false);
		case NOT_SUPPORTED:
			logger.info("=====>>>" + (isExistingTransaction ? "已存在一个事务，挂起当前事务，以非事务方式执行..." : "当前没有事务，以非事务方式执行..."));
			return isExistingTransaction ? handleNoTransaction(definition, true) : handleNoTransaction(definition, false);
		case NEVER:
			if (isExistingTransaction) {
				throw new TransactionException("Existing transaction found for transaction marked with propagation 'never'");
			}
			logger.info("=====>>>当前没有事务，以非事务方式执行...");
			return handleNoTransaction(definition, false);
		case NESTED:
			logger.info("=====>>>" + (isExistingTransaction ? "已存在一个事务，以嵌套事务的方式执行..." : "当前没有事务，开启一个新的数据库事务..."));
			return isExistingTransaction ? handleNestedTransaction(definition) : handleNewTransaction(definition, false);
		}

		return null;
	}

	public void commit(TransactionStatus status) throws TransactionException {
		if (status.isCompleted()) {
			throw new TransactionException("Transaction is already completed - do not call commit or rollback more than once per transaction");
		}
		DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
		if (defStatus.isLocalRollbackOnly()) {
			logger.info("=====>>>回滚数据库事物...");
			processRollback(defStatus);
			return;
		}
		logger.info("=====>>>提交数据库事物...");
		processCommit(defStatus);
	}

	public void rollback(TransactionStatus status) throws TransactionException {
		if (status.isCompleted()) {
			throw new TransactionException("Transaction is already completed - do not call commit or rollback more than once per transaction");
		}
		logger.info("=====>>>回滚数据库事物...");
		DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
		processRollback(defStatus);
	}

	private void processRollback(DefaultTransactionStatus status) {
		try {
			try {
				if (status.isNewSynchronization()) {
					triggerBeforeCompletion();
				}
				if (status.hasSavepoint()) {
					status.rollbackToHeldSavepoint();
				} else if (status.isNewTransaction()) {
					status.getConHolder().getConnection().rollback();
				} else if (status.hasTransaction()) {
					if (status.isLocalRollbackOnly() || isGlobalRollbackOnParticipationFailure()) {
						status.setRollbackOnly();
					}
				}
			} catch (SQLException e) {
				triggerAfterCompletion(CompletionStatus.STATUS_UNKNOWN);
				logger.error("Rollback exception :" + e.getMessage());
			} catch (Error err) {
				triggerAfterCompletion(CompletionStatus.STATUS_UNKNOWN);
				throw err;
			}
			if (status.isNewSynchronization()) {
				triggerAfterCompletion(CompletionStatus.STATUS_ROLLED_BACK);
			}
		} finally {
			cleanupAfterCompletion(status);
		}
	}

	private void processCommit(DefaultTransactionStatus status) throws TransactionException {
		try {
			if (status.isNewSynchronization()) {
				try {
					triggerBeforeCommit(status.isReadOnly());
				} finally {
					triggerBeforeCompletion();
				}
			}

			try {
				if (status.hasSavepoint()) {
					status.releaseHeldSavepoint();
				} else if (status.isNewTransaction()) {
					status.getConHolder().getConnection().commit();
				}
			} catch (SQLException e) {
				logger.error("Commit exception :" + e.getMessage());
				if (isRollbackOnCommitFailure()) {
					doRollbackOnCommitException(status, e);
				} else {
					triggerAfterCompletion(CompletionStatus.STATUS_UNKNOWN);
					throw new TransactionException("commit error", e);
				}
			} catch (Error e) {
				logger.error("Commit exception :" + e.getMessage());
				triggerAfterCompletion(CompletionStatus.STATUS_UNKNOWN);
				throw new TransactionException("commit error", e);
			}

			if (status.isNewSynchronization()) {
				try {
					triggerAfterCommit();
				} finally {
					triggerAfterCompletion(CompletionStatus.STATUS_COMMITTED);
				}
			}
		} finally {
			cleanupAfterCompletion(status);
		}
	}

	private void doRollbackOnCommitException(DefaultTransactionStatus status, Throwable ex) throws TransactionException {
		try {
			if (status.isNewTransaction()) {
				rollback(status);
			} else if (status.hasTransaction() && isGlobalRollbackOnParticipationFailure()) {
				status.setRollbackOnly();
			}
		} catch (Error rberr) {
			logger.error("Commit exception overridden by rollback exception", ex);
			triggerAfterCompletion(CompletionStatus.STATUS_UNKNOWN);
			throw rberr;
		}
		triggerAfterCompletion(CompletionStatus.STATUS_ROLLED_BACK);
	}

	private void cleanupAfterCompletion(DefaultTransactionStatus status) {
		status.setCompleted();
		//if (status.isNewSynchronization()) {
		if (status.isNewTransaction()) {
			TransactionSynchronizationManager.unbindResource(getDataSource());
			TransactionSynchronizationManager.clear();
		}
		status.getConHolder().released();
		
		if (status.hasSuspended()) {
			resume(status.getSuspendedHolder());
		}
		if (status.hasSuspendedSynchronizations()) {
			resumeSynchronization(status.getSuspendedSynchronizations());
		}
	}

	private List<TransactionSynchronization> suspendSynchronization() {
		logger.info("=====>>>挂起数据库事物同步器...");
		List<TransactionSynchronization> suspendedSynchronizations = TransactionSynchronizationManager.getSynchronizations();
		for (TransactionSynchronization synchronization : suspendedSynchronizations) {
			synchronization.suspend();
		}
		TransactionSynchronizationManager.clearSynchronization();
		return suspendedSynchronizations;
	}

	private void resumeSynchronization(List<TransactionSynchronization> suspendedSynchronizations) {
		logger.info("=====>>>唤醒数据库事物同步器...");
		TransactionSynchronizationManager.initSynchronization();
		for (TransactionSynchronization synchronization : suspendedSynchronizations) {
			synchronization.resume();
			TransactionSynchronizationManager.registerSynchronization(synchronization);
		}
	}

	private ConnectionHolder suspend() {
		logger.info("=====>>>挂起数据库事物...");
		ConnectionHolder holder = TransactionSynchronizationManager.unbindResource(getDataSource());
		if (holder != null) {
			holder.setTransactionActive(false);
		}
		return holder;
	}

	private void resume(ConnectionHolder holder) {
		logger.info("=====>>>唤醒数据库事物...");
		if (holder != null) {
			holder.setTransactionActive(true);
			TransactionSynchronizationManager.bindResource(getDataSource(), holder);
		}
	}

	private TransactionStatus handleNewTransaction(TransactionDefinition definition, boolean isSuspend) throws TransactionException {
		boolean hasResource = TransactionSynchronizationManager.hasResource(getDataSource());
		boolean isSyncActive = TransactionSynchronizationManager.isSynchronizationActive();

		if (!isSuspend) {
			if (hasResource)
				throw new TransactionException("There has a transaction in this thread, it's must suspend");
			if (isSyncActive)
				throw new TransactionException("There has a active synchronization in this thread, it's must suspend");
		}

		DefaultTransactionStatus status = new DefaultTransactionStatus();
		Connection conn;
		try {
			conn = getDataSource().getConnection();
		} catch (Throwable t) {
			logger.error("获取连接失败："+t.getMessage());
			throw new TransactionException(t.getMessage()+" Can't get the connection from the datasource!");
		}
		try {
			status.setConHolder(new ConnectionHolder(conn));
			status.setNewConnectionHolder(true);
			status.setReadOnly(definition.isReadOnly());

			if (definition != null && definition.isReadOnly()) {
				conn.setReadOnly(true);
			}

			if (definition != null && definition.getIsolationLevel() != Isolation.DEFAULT) {
				int currentIsolation = conn.getTransactionIsolation();
				if (currentIsolation != definition.getIsolationLevel().value()) {
					conn.setTransactionIsolation(definition.getIsolationLevel().value());
					status.setPreviousIsolationLevel(Isolation.valueOf(currentIsolation));
				}
			}
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
				status.setMustRestoreAutoCommit(true);
			}
		} catch (SQLException e1) {
			//e1.printStackTrace();
			logger.error("设置连接参数失败："+e1.getMessage());
			throw new TransactionException(e1.getMessage()+" Can't set the connection param while get a new connection from the datasource!");
		}

		try {
			if (isSuspend && hasResource) {
				status.setSuspendedHolder(suspend());
			}
			TransactionSynchronizationManager.bindResource(getDataSource(), status.getConHolder());
			try {
				if (isSuspend && isSyncActive) {
					status.setSuspendedSynchronizations(suspendSynchronization());
				}
				status.setNewSynchronization(isTransactionSynchronization());
				if (status.isNewSynchronization()) {
					TransactionSynchronizationManager.initSynchronization();
				}
			} catch (Exception e) {
				resumeSynchronization(status.getSuspendedSynchronizations());
				throw new TransactionException("Can't init the synchronization!");
			}
		} catch (Exception e) {
			resume(status.getSuspendedHolder());
			throw new TransactionException("Can't bind the connection!");
		}

		status.setNewTransaction(true);
		status.getConHolder().setTransactionActive(true);

		return status;
	}

	private TransactionStatus handleExistingTransaction(TransactionDefinition definition) throws TransactionException {
		if (!TransactionSynchronizationManager.hasResource(getDataSource())) {
			throw new TransactionException("There hasn't a transaction in this thread, it's must a transaction");
		}

		DefaultTransactionStatus status = new DefaultTransactionStatus();
		status.setConHolder(TransactionSynchronizationManager.getResource(getDataSource()));
		status.setNewConnectionHolder(false);
		status.setNewTransaction(false);
		status.setNewSynchronization(isTransactionSynchronization());
		status.getConHolder().requested();

		if (isValidateExistingTransaction()) {
			if (definition.getIsolationLevel() != Isolation.DEFAULT) {
				try {
					int currentIsolationLevel = status.getConHolder().getConnection().getTransactionIsolation();
					if (currentIsolationLevel != definition.getIsolationLevel().value()) {
						throw new TransactionException("The connection.getTransactionIsolation() not equal definition.getIsolationLevel()");
					}
					boolean readOnly = status.getConHolder().getConnection().isReadOnly();
					if (readOnly != definition.isReadOnly()) {
						throw new TransactionException("The connection.isReadOnly() not equal definition.isReadOnly()");
					}
				} catch (SQLException e) {
					logger.error("handleExistingTransaction exception :", e);
				}
			}
		}

		return status;
	}

	private TransactionStatus handleNestedTransaction(TransactionDefinition definition) throws TransactionException {
		if (!isNestedTransactionAllowed()) {
			throw new TransactionException("Transaction manager does not allow nested transactions by default - specify 'nestedTransactionAllowed' property with value 'true'");
		}
		if (!TransactionSynchronizationManager.hasResource(getDataSource())) {
			throw new TransactionException("There hasn't a transaction in this thread, it's must a transaction");
		}

		DefaultTransactionStatus status = new DefaultTransactionStatus();
		status.setConHolder(TransactionSynchronizationManager.getResource(getDataSource()));
		status.setNewConnectionHolder(false);
		status.setNewTransaction(false);
		status.setNewSynchronization(false);
		status.getConHolder().requested();
		status.createAndHoldSavepoint();

		return status;
	}

	private TransactionStatus handleNoTransaction(TransactionDefinition definition, boolean isSuspend) throws TransactionException {
		boolean hasResource = TransactionSynchronizationManager.hasResource(getDataSource());
		boolean isSyncActive = TransactionSynchronizationManager.isSynchronizationActive();

		if (!isSuspend) {
			if (hasResource)
				throw new TransactionException("There has a transaction in this thread, it's must suspend");
			if (isSyncActive)
				throw new TransactionException("There has a active synchronization in this thread, it's must suspend");
		}

		DefaultTransactionStatus status = new DefaultTransactionStatus();
		status.setReadOnly(definition.isReadOnly());
		status.setNewTransaction(!isSuspend);

		try {
			if (isSuspend && hasResource) {
				status.setSuspendedHolder(suspend());
			}
			try {
				if (isSuspend && isSyncActive) {
					status.setSuspendedSynchronizations(suspendSynchronization());
				}
				status.setNewSynchronization(isAlwaysTransactionSynchronization());
				if (status.isNewSynchronization()) {
					TransactionSynchronizationManager.initSynchronization();
				}
			} catch (Exception e) {
				resumeSynchronization(status.getSuspendedSynchronizations());
				throw new TransactionException("Can't init the synchronization!");
			}
		} catch (Exception e) {
			resume(status.getSuspendedHolder());
			throw new TransactionException("Can't bind the connection!");
		}

		return status;
	}
}