package com.platform.cubism.expression;

public class ParseException extends ExpressionException {
	private static final long serialVersionUID = -4062312293162643789L;

	public ParseException(String expressionString, int position, String message) {
		super(expressionString, position, message);
	}

	public ParseException(int position, String message, Throwable cause) {
		super(position, message, cause);
	}

	public ParseException(int position, String message) {
		super(position, message);
	}
}