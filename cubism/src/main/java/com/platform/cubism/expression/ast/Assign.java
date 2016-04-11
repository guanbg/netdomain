package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.TypedValue;

public class Assign extends SpelNodeImpl {
	public Assign(int pos, SpelNodeImpl... operands) {
		super(pos, operands);
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		TypedValue newValue = children[1].getValueInternal(state);
		getChild(0).setValue(state, newValue.getValue());
		return newValue;
	}

	@Override
	public String toStringAST() {
		return new StringBuilder().append(getChild(0).toStringAST()).append("=").append(getChild(1).toStringAST()).toString();
	}
}