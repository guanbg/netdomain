package com.platform.cubism.jdbc.tran;

public interface TransactionSynchronization {
	void suspend();

	void resume();

	void beforeCommit(boolean readOnly);

	void beforeCompletion();

	void afterCommit();

	void afterCompletion(CompletionStatus status);
}