package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.TypedValue;

public class LongLiteral extends Literal {
	private final TypedValue value;

	LongLiteral(String payload, int pos, long value) {
		super(payload, pos);
		this.value = new TypedValue(value);
	}

	@Override
	public TypedValue getLiteralValue() {
		return this.value;
	}
}