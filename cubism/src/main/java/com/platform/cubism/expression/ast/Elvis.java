package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.TypedValue;

/**
 * Represents the elvis operator ?:. For an expression "a?:b" if a is not null,
 * the value of the expression is "a", if a is null then the value of the
 * expression is "b".
 */
public class Elvis extends SpelNodeImpl {
	public Elvis(int pos, SpelNodeImpl... args) {
		super(pos, args);
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		TypedValue value = children[0].getValueInternal(state);
		if (value.getValue() != null && !((value.getValue() instanceof String) && ((String) value.getValue()).length() == 0)) {
			return value;
		} else {
			return children[1].getValueInternal(state);
		}
	}

	@Override
	public String toStringAST() {
		return new StringBuilder().append(getChild(0).toStringAST()).append(" ?: ").append(getChild(1).toStringAST()).toString();
	}
}