package com.platform.cubism.expression;

public class InternalParseException extends RuntimeException {
	private static final long serialVersionUID = 566163486974574184L;

	public InternalParseException(SpelParseException cause) {
		super(cause);
	}

	public SpelParseException getCause() {
		return (SpelParseException) super.getCause();
	}
}