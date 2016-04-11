package com.platform.cubism.jdbc.tran;

public interface TransactionDefinition {
	Propagation getPropagationBehavior();

	Isolation getIsolationLevel();

	int getTimeout();

	boolean isReadOnly();
}
