package com.platform.cubism;

public class CubismException extends RuntimeException {
	private static final long serialVersionUID = -5689692460034337155L;

	public CubismException(String message) {
		super(message);
	}

	public CubismException(String message, Throwable cause) {
		super(message, cause);
	}
}
