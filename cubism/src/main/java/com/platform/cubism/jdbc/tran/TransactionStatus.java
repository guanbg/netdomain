package com.platform.cubism.jdbc.tran;

import java.sql.Connection;

public interface TransactionStatus {
	boolean isNewTransaction();

	void setRollbackOnly();

	boolean isRollbackOnly();

	boolean isCompleted();

	boolean hasSavepoint();

	Connection getConnection();
}