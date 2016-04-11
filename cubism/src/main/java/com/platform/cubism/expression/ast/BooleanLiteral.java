package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.BooleanTypedValue;

public class BooleanLiteral extends Literal {
	private final BooleanTypedValue value;

	public BooleanLiteral(String payload, int pos, boolean value) {
		super(payload, pos);
		this.value = BooleanTypedValue.forValue(value);
	}

	@Override
	public BooleanTypedValue getLiteralValue() {
		return this.value;
	}
}