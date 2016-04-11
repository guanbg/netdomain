package com.platform.cubism.expression;

public class AccessException extends Exception {
	private static final long serialVersionUID = 8495364722490130044L;

	public AccessException(String message, Exception cause) {
		super(message, cause);
	}

	public AccessException(String message) {
		super(message);
	}
}