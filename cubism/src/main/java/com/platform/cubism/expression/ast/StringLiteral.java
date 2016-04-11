package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.TypedValue;

public class StringLiteral extends Literal {
	private final TypedValue value;

	public StringLiteral(String payload, int pos, String value) {
		super(payload, pos);
		value = value.substring(1, value.length() - 1);
		this.value = new TypedValue(value.replaceAll("''", "'"));
	}

	@Override
	public TypedValue getLiteralValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return "'" + getLiteralValue().getValue() + "'";
	}
}