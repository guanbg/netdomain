package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.SpelMessage;
import com.platform.cubism.expression.TypedValue;

/**
 * Represents a ternary expression, for example: "someCheck()?true:false".
 */
public class Ternary extends SpelNodeImpl {
	public Ternary(int pos, SpelNodeImpl... args) {
		super(pos, args);
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		Boolean value = children[0].getValue(state, Boolean.class);
		if (value == null) {
			throw new SpelEvaluationException(getChild(0).getStartPosition(), SpelMessage.TYPE_CONVERSION_ERROR, "null", "boolean");
		}
		if (value.booleanValue()) {
			return children[1].getValueInternal(state);
		} else {
			return children[2].getValueInternal(state);
		}
	}

	@Override
	public String toStringAST() {
		return new StringBuilder().append(getChild(0).toStringAST()).append(" ? ").append(getChild(1).toStringAST()).append(" : ").append(getChild(2).toStringAST()).toString();
	}
}