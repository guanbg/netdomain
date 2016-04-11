package com.platform.cubism.jdbc.tran;

public class TransactionException extends RuntimeException {
	private static final long serialVersionUID = -2147509818354511076L;

	public TransactionException(String msg) {
		super(msg);
	}

	public TransactionException(String msg, Throwable cause) {
		super(msg, cause);
	}
}