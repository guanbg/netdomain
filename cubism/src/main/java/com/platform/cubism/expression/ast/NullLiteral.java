package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.TypedValue;

public class NullLiteral extends Literal {
	public NullLiteral(int pos) {
		super(null, pos);
	}

	@Override
	public TypedValue getLiteralValue() {
		return TypedValue.NULL;
	}

	@Override
	public String toString() {
		return "null";
	}
}