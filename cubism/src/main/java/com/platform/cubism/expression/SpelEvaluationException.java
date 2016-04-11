package com.platform.cubism.expression;

public class SpelEvaluationException extends EvaluationException {
	private static final long serialVersionUID = -1697019673882087768L;
	private SpelMessage message;
	private Object[] inserts;

	public SpelEvaluationException(SpelMessage message, Object... inserts) {
		super(message.formatMessage(0, inserts));
		this.message = message;
		this.inserts = inserts;
	}

	public SpelEvaluationException(int position, SpelMessage message, Object... inserts) {
		super(position, message.formatMessage(position, inserts));
		this.message = message;
		this.inserts = inserts;
	}

	public SpelEvaluationException(int position, Throwable cause, SpelMessage message, Object... inserts) {
		super(position, message.formatMessage(position, inserts), cause);
		this.message = message;
		this.inserts = inserts;
	}

	public SpelEvaluationException(Throwable cause, SpelMessage message, Object... inserts) {
		super(message.formatMessage(0, inserts), cause);
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

	public void setPosition(int position) {
		this.position = position;
	}

	public Object[] getInserts() {
		return inserts;
	}
}