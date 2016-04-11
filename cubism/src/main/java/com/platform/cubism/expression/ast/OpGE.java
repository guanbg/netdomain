package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.BooleanTypedValue;
import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;

public class OpGE extends Operator {
	public OpGE(int pos, SpelNodeImpl... operands) {
		super(">=", pos, operands);
	}

	@Override
	public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		Object left = getLeftOperand().getValueInternal(state).getValue();
		Object right = getRightOperand().getValueInternal(state).getValue();
		if (left instanceof Number && right instanceof Number) {
			Number leftNumber = (Number) left;
			Number rightNumber = (Number) right;
			if (leftNumber instanceof Double || rightNumber instanceof Double) {
				return BooleanTypedValue.forValue(leftNumber.doubleValue() >= rightNumber.doubleValue());
			} else if (leftNumber instanceof Long || rightNumber instanceof Long) {
				return BooleanTypedValue.forValue(leftNumber.longValue() >= rightNumber.longValue());
			} else {
				return BooleanTypedValue.forValue(leftNumber.intValue() >= rightNumber.intValue());
			}
		}
		return BooleanTypedValue.forValue(state.getTypeComparator().compare(left, right) >= 0);
	}
}