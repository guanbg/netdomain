package com.platform.cubism.jdbc.tran;

import java.io.Serializable;

import org.slf4j.LoggerFactory;

public abstract class TransactionParam implements Serializable {
	private static final long serialVersionUID = 4148308261013226527L;

	// 是否使用嵌套事务,false-不使用 true-使用
	private boolean nestedTransactionAllowed = false;

	// 连接超时时间，-1为永不超时
	private int defaultTimeout = -1;

	private TranSync transactionSynchronization = TranSync.SYNCHRONIZATION_ALWAYS;

	private boolean validateExistingTransaction = false;

	private boolean globalRollbackOnParticipationFailure = true;

	private boolean failEarlyOnGlobalRollbackOnly = false;

	private boolean rollbackOnCommitFailure = false;

	protected transient org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	public boolean isNestedTransactionAllowed() {
		return nestedTransactionAllowed;
	}

	public int getDefaultTimeout() {
		return defaultTimeout;
	}

	public TranSync getTransactionSynchronization() {
		return transactionSynchronization;
	}

	public boolean isTransactionSynchronization() {
		return this.transactionSynchronization != TranSync.SYNCHRONIZATION_NEVER;
	}
	public boolean isAlwaysTransactionSynchronization() {
		return this.transactionSynchronization != TranSync.SYNCHRONIZATION_ALWAYS;
	}

	public boolean isValidateExistingTransaction() {
		return validateExistingTransaction;
	}

	public boolean isGlobalRollbackOnParticipationFailure() {
		return globalRollbackOnParticipationFailure;
	}

	public boolean isFailEarlyOnGlobalRollbackOnly() {
		return failEarlyOnGlobalRollbackOnly;
	}

	public boolean isRollbackOnCommitFailure() {
		return rollbackOnCommitFailure;
	}

	public void setNestedTransactionAllowed(boolean nestedTransactionAllowed) {
		this.nestedTransactionAllowed = nestedTransactionAllowed;
	}

	public void setDefaultTimeout(int defaultTimeout) {
		if (defaultTimeout < 0)
			this.defaultTimeout = -1;
		else
			this.defaultTimeout = defaultTimeout;
	}

	public void setTransactionSynchronization(TranSync transactionSynchronization) {
		this.transactionSynchronization = transactionSynchronization;
	}

	public void setValidateExistingTransaction(boolean validateExistingTransaction) {
		this.validateExistingTransaction = validateExistingTransaction;
	}

	public void setGlobalRollbackOnParticipationFailure(boolean globalRollbackOnParticipationFailure) {
		this.globalRollbackOnParticipationFailure = globalRollbackOnParticipationFailure;
	}

	public void setFailEarlyOnGlobalRollbackOnly(boolean failEarlyOnGlobalRollbackOnly) {
		this.failEarlyOnGlobalRollbackOnly = failEarlyOnGlobalRollbackOnly;
	}

	public void setRollbackOnCommitFailure(boolean rollbackOnCommitFailure) {
		this.rollbackOnCommitFailure = rollbackOnCommitFailure;
	}
}