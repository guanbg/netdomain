package com.platform.cubism.expression;

public class EvaluationException extends ExpressionException {
	private static final long serialVersionUID = 5539530464303856482L;

	public EvaluationException(int position, String message) {
		super(position, message);
	}

	public EvaluationException(String expressionString, String message) {
		super(expressionString, message);
	}

	public EvaluationException(int position, String message, Throwable cause) {
		super(position, message, cause);
	}

	public EvaluationException(String message) {
		super(message);
	}

	public EvaluationException(String message, Throwable cause) {
		super(message, cause);
	}
}