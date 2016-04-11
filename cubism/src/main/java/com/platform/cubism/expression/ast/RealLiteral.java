package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.TypedValue;

public class RealLiteral extends Literal {
	private final TypedValue value;

	public RealLiteral(String payload, int pos, double value) {
		super(payload, pos);
		this.value = new TypedValue(value);
	}

	@Override
	public TypedValue getLiteralValue() {
		return this.value;
	}
}