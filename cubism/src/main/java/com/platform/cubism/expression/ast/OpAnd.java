package com.platform.cubism.expression.ast;

import com.platform.cubism.cvt.TypeDescriptor;
import com.platform.cubism.expression.BooleanTypedValue;
import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.SpelMessage;
import com.platform.cubism.expression.TypedValue;

public class OpAnd extends Operator {
	public OpAnd(int pos, SpelNodeImpl... operands) {
		super("and", pos, operands);
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		boolean leftValue;
		boolean rightValue;

		try {
			TypedValue typedValue = getLeftOperand().getValueInternal(state);
			this.assertTypedValueNotNull(typedValue);
			leftValue = (Boolean) state.convertValue(typedValue, TypeDescriptor.valueOf(Boolean.class));
		} catch (SpelEvaluationException ee) {
			ee.setPosition(getLeftOperand().getStartPosition());
			throw ee;
		}

		if (leftValue == false) {
			return BooleanTypedValue.forValue(false); // no need to evaluate
														// right operand
		}

		try {
			TypedValue typedValue = getRightOperand().getValueInternal(state);
			this.assertTypedValueNotNull(typedValue);
			rightValue = (Boolean) state.convertValue(typedValue, TypeDescriptor.valueOf(Boolean.class));
		} catch (SpelEvaluationException ee) {
			ee.setPosition(getRightOperand().getStartPosition());
			throw ee;
		}

		return /* leftValue && */BooleanTypedValue.forValue(rightValue);
	}

	private void assertTypedValueNotNull(TypedValue typedValue) {
		if (TypedValue.NULL.equals(typedValue)) {
			throw new SpelEvaluationException(SpelMessage.TYPE_CONVERSION_ERROR, "null", "boolean");
		}
	}
}