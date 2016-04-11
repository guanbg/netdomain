package com.platform.cubism.jdbc.tran;

import java.io.Serializable;

public abstract class AbstractTransaction extends TransactionParam implements Transaction, Serializable {
	private static final long serialVersionUID = -39983948759810131L;

	protected void triggerBeforeCompletion() {
		for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
			try {
				synchronization.beforeCompletion();
			} catch (Throwable tsex) {
				logger.error("TransactionSynchronization.beforeCompletion threw exception", tsex);
			}
		}
	}

	protected void triggerAfterCompletion(CompletionStatus completionStatus) {
		for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
			try {
				synchronization.afterCompletion(completionStatus);
			} catch (Throwable tsex) {
				logger.error("TransactionSynchronization.afterCompletion threw exception", tsex);
			}
		}
	}

	protected void triggerBeforeCommit(boolean readOnly) {
		for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
			synchronization.beforeCommit(readOnly);
		}
	}

	protected void triggerAfterCommit() {
		for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
			synchronization.afterCommit();
		}
	}
}