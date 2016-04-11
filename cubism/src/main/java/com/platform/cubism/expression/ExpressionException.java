package com.platform.cubism.expression;

public class ExpressionException extends RuntimeException {
	private static final long serialVersionUID = 8050240625977883091L;
	protected String expressionString;
	protected int position; // -1 if not known - but should be known in all
							// reasonable cases

	public ExpressionException(String expressionString, String message) {
		super(message);
		this.position = -1;
		this.expressionString = expressionString;
	}

	public ExpressionException(String expressionString, int position, String message) {
		super(message);
		this.position = position;
		this.expressionString = expressionString;
	}

	public ExpressionException(int position, String message) {
		super(message);
		this.position = position;
	}

	public ExpressionException(int position, String message, Throwable cause) {
		super(message, cause);
		this.position = position;
	}

	public ExpressionException(String message) {
		super(message);
	}

	public ExpressionException(String message, Throwable cause) {
		super(message, cause);
	}

	public String toDetailedString() {
		StringBuilder output = new StringBuilder();
		if (expressionString != null) {
			output.append("Expression '");
			output.append(expressionString);
			output.append("'");
			if (position != -1) {
				output.append(" @ ");
				output.append(position);
			}
			output.append(": ");
		}
		output.append(getMessage());
		return output.toString();
	}

	public final String getExpressionString() {
		return this.expressionString;
	}

	public final int getPosition() {
		return position;
	}
}