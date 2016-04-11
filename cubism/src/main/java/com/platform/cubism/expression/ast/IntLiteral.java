package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.TypedValue;

public class IntLiteral extends Literal {
	private final TypedValue value;

	IntLiteral(String payload, int pos, int value) {
		super(payload, pos);
		this.value = new TypedValue(value);
	}

	@Override
	public TypedValue getLiteralValue() {
		return this.value;
	}
}