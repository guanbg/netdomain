package com.platform.cubism.expression.ast;

import com.platform.cubism.cvt.TypeDescriptor;
import com.platform.cubism.expression.BooleanTypedValue;
import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.SpelMessage;
import com.platform.cubism.expression.TypedValue;

public class OperatorNot extends SpelNodeImpl { // Not is a unary operator so do
												// not extend BinaryOperator
	public OperatorNot(int pos, SpelNodeImpl operand) {
		super(pos, operand);
	}

	@Override
	public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		try {
			TypedValue typedValue = children[0].getValueInternal(state);
			if (TypedValue.NULL.equals(typedValue)) {
				throw new SpelEvaluationException(SpelMessage.TYPE_CONVERSION_ERROR, "null", "boolean");
			}
			boolean value = (Boolean) state.convertValue(typedValue, TypeDescriptor.valueOf(Boolean.class));
			return BooleanTypedValue.forValue(!value);
		} catch (SpelEvaluationException see) {
			see.setPosition(getChild(0).getStartPosition());
			throw see;
		}
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder();
		sb.append("!").append(getChild(0).toStringAST());
		return sb.toString();
	}
}