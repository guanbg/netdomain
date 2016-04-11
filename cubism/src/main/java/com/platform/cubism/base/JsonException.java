package com.platform.cubism.base;

public class JsonException extends RuntimeException {
	private static final long serialVersionUID = 8686128675261099996L;
	private Throwable cause;

	public JsonException(String message) {
		super(message);
	}

	public JsonException(Throwable t) {
		super(t.getMessage());
		this.cause = t;
	}

	public Throwable getCause() {
		return this.cause;
	}
}