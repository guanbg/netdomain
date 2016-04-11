package com.platform.cubism.jdbc.tran;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

public class DefaultTransactionStatus implements TransactionStatus {
	private boolean newConnectionHolder;
	private boolean newTransaction;
	private boolean newSynchronization;
	private boolean mustRestoreAutoCommit;
	private boolean readOnly;
	private boolean savepointAllowed;
	private boolean completed;
	private boolean rollbackOnly;
	private Isolation previousIsolationLevel;
	private List<TransactionSynchronization> suspendedSynchronizations;
	private ConnectionHolder suspendedHolder;
	private ConnectionHolder conHolder;
	private Savepoint savepoint;

	public DefaultTransactionStatus() {
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public boolean isNewConnectionHolder() {
		return newConnectionHolder;
	}

	public boolean isMustRestoreAutoCommit() {
		return mustRestoreAutoCommit;
	}

	public void setNewConnectionHolder(boolean newConnectionHolder) {
		this.newConnectionHolder = newConnectionHolder;
	}

	public void setMustRestoreAutoCommit(boolean mustRestoreAutoCommit) {
		this.mustRestoreAutoCommit = mustRestoreAutoCommit;
	}

	public boolean isNewSynchronization() {
		return newSynchronization;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public Isolation getPreviousIsolationLevel() {
		return previousIsolationLevel;
	}

	public boolean isSavepointAllowed() {
		return savepointAllowed;
	}

	public List<TransactionSynchronization> getSuspendedSynchronizations() {
		return suspendedSynchronizations;
	}

	public ConnectionHolder getSuspendedHolder() {
		return suspendedHolder;
	}

	public ConnectionHolder getConHolder() {
		return conHolder;
	}

	public void setNewTransaction(boolean newTransaction) {
		this.newTransaction = newTransaction;
	}

	public void setNewSynchronization(boolean newSynchronization) {
		this.newSynchronization = newSynchronization;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setPreviousIsolationLevel(Isolation previousIsolationLevel) {
		this.previousIsolationLevel = previousIsolationLevel;
	}

	public void setSavepointAllowed(boolean savepointAllowed) {
		this.savepointAllowed = savepointAllowed;
	}

	public void setSuspendedSynchronizations(List<TransactionSynchronization> suspendedSynchronizations) {
		this.suspendedSynchronizations = suspendedSynchronizations;
	}

	public void setSuspendedHolder(ConnectionHolder suspendedHolder) {
		this.suspendedHolder = suspendedHolder;
	}

	public void setConHolder(ConnectionHolder conHolder) {
		this.conHolder = conHolder;
	}

	public Savepoint getSavepoint() {
		return savepoint;
	}

	public void setSavepoint(Savepoint savepoint) {
		this.savepoint = savepoint;
	}

	public void createAndHoldSavepoint() throws TransactionException {
		try {
			setSavepoint(getConHolder().createSavepoint());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new TransactionException("Don't create savepoint associated with current transaction");
		}
	}

	public void rollbackToHeldSavepoint() throws TransactionException {
		if (!hasSavepoint()) {
			throw new TransactionException("No savepoint associated with current transaction");
		}
		try {
			getConHolder().rollbackToSavepoint(getSavepoint());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new TransactionException("Don't rollback savepoint associated with current transaction");
		}
		setSavepoint(null);
	}

	public void releaseHeldSavepoint() throws TransactionException {
		if (!hasSavepoint()) {
			throw new TransactionException("No savepoint associated with current transaction");
		}
		try {
			getConHolder().releaseSavepoint(getSavepoint());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new TransactionException("Don't release savepoint associated with current transaction");
		}
		setSavepoint(null);
	}

	public boolean hasSuspendedSynchronizations() {
		return (this.suspendedSynchronizations != null);
	}

	public boolean hasSuspended() {
		return (this.suspendedHolder != null);
	}

	public boolean hasTransaction() {
		return (this.conHolder != null);
	}

	public boolean hasSavepoint() {
		return (this.savepoint != null);
	}

	public boolean isNewTransaction() {
		return hasTransaction() && newTransaction;
	}

	public void setRollbackOnly() {
		this.rollbackOnly = true;
	}

	public boolean isRollbackOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isLocalRollbackOnly() {
		return this.rollbackOnly;
	}

	public boolean isCompleted() {
		return this.completed;
	}

	public void setCompleted() {
		this.completed = true;
	}

	public Connection getConnection() {
		if (conHolder == null || !conHolder.hasConnection()) {
			return null;
		}

		return getConnectionProxy(conHolder.getConnection());
	}

	private Connection getConnectionProxy(Connection conn) {
		return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[] { Connection.class }, new NoTranInvocationHandler(conn));
	}

	private class NoTranInvocationHandler implements InvocationHandler {

		private final Connection target;

		public NoTranInvocationHandler(Connection target) {
			this.target = target;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("equals")) {
				// Only consider equal when proxies are identical.
				return (proxy == args[0]);
			} else if (method.getName().equals("hashCode")) {
				// Use hashCode of PersistenceManager proxy.
				return System.identityHashCode(proxy);
			} else if (method.getName().equals("unwrap")) {
				if (((Class<?>) args[0]).isInstance(proxy)) {
					return proxy;
				}
			} else if (method.getName().equals("isWrapperFor")) {
				if (((Class<?>) args[0]).isInstance(proxy)) {
					return true;
				}
			} else if (method.getName().equals("close")) {
				// Handle close method: suppress, not valid.
				return null;
			} else if (method.getName().equals("isClosed")) {
				return false;
			} else if (method.getName().equals("setAutoCommit")) {
				return null;
			} else if (method.getName().equals("getAutoCommit")) {
				return false;
			} else if (method.getName().equals("commit")) {
				return null;
			} else if (method.getName().equals("rollback")) {
				return null;
			} else if (method.getName().equals("setSavepoint")) {
				return null;
			} else if (method.getName().equals("releaseSavepoint")) {
				return null;
			} else if (method.getName().equals("getTargetConnection")) {
				// Handle getTargetConnection method: return underlying
				// Connection.
				return this.target;
			}

			// Invoke method on target Connection.
			try {
				Object retVal = method.invoke(this.target, args);
				return retVal;
			} catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		}
	}
}