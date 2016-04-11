package com.platform.cubism.expression;

public class SpelParseException extends ParseException {
	private static final long serialVersionUID = -7491474292265178083L;
	private SpelMessage message;
	private Object[] inserts;

	public SpelParseException(String expressionString, int position, SpelMessage message, Object... inserts) {
		super(expressionString, position, message.formatMessage(position, inserts));
		this.position = position;
		this.message = message;
		this.inserts = inserts;
	}

	public SpelParseException(int position, SpelMessage message, Object... inserts) {
		super(position, message.formatMessage(position, inserts));
		this.position = position;
		this.message = message;
		this.inserts = inserts;
	}

	public SpelParseException(int position, Throwable cause, SpelMessage message, Object... inserts) {
		super(position, message.formatMessage(position, inserts), cause);
		this.position = position;
		this.message = message;
		this.inserts = inserts;
	}

	@Override
	public String getMessage() {
		if (message != null)
			return message.formatMessage(position, inserts);
		else
			return super.getMessage();
	}

	public SpelMessage getMessageCode() {
		return this.message;
	}

	public Object[] getInserts() {
		return inserts;
	}
}