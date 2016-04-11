package com.platform.cubism.expression.ast;

import com.platform.cubism.cvt.TypeDescriptor;
import com.platform.cubism.expression.BooleanTypedValue;
import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.SpelMessage;
import com.platform.cubism.expression.TypedValue;

public class OpOr extends Operator {
	public OpOr(int pos, SpelNodeImpl... operands) {
		super("or", pos, operands);
	}

	@Override
	public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		boolean leftValue;
		boolean rightValue;
		try {
			TypedValue typedValue = getLeftOperand().getValueInternal(state);
			this.assertTypedValueNotNull(typedValue);
			leftValue = (Boolean) state.convertValue(typedValue, TypeDescriptor.valueOf(Boolean.class));
		} catch (SpelEvaluationException see) {
			see.setPosition(getLeftOperand().getStartPosition());
			throw see;
		}

		if (leftValue == true) {
			return BooleanTypedValue.TRUE; // no need to evaluate right operand
		}

		try {
			TypedValue typedValue = getRightOperand().getValueInternal(state);
			this.assertTypedValueNotNull(typedValue);
			rightValue = (Boolean) state.convertValue(typedValue, TypeDescriptor.valueOf(Boolean.class));
		} catch (SpelEvaluationException see) {
			see.setPosition(getRightOperand().getStartPosition());
			throw see;
		}

		return BooleanTypedValue.forValue(leftValue || rightValue);
	}

	private void assertTypedValueNotNull(TypedValue typedValue) {
		if (TypedValue.NULL.equals(typedValue)) {
			throw new SpelEvaluationException(SpelMessage.TYPE_CONVERSION_ERROR, "null", "boolean");
		}
	}
}