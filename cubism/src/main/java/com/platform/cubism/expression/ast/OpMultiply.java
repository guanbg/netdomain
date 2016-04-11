package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.Operation;
import com.platform.cubism.expression.TypedValue;

public class OpMultiply extends Operator {
	public OpMultiply(int pos, SpelNodeImpl... operands) {
		super("*", pos, operands);
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		Object operandOne = getLeftOperand().getValueInternal(state).getValue();
		Object operandTwo = getRightOperand().getValueInternal(state).getValue();
		if (operandOne instanceof Number && operandTwo instanceof Number) {
			Number leftNumber = (Number) operandOne;
			Number rightNumber = (Number) operandTwo;
			if (leftNumber instanceof Double || rightNumber instanceof Double) {
				return new TypedValue(leftNumber.doubleValue() * rightNumber.doubleValue());
			} else if (leftNumber instanceof Long || rightNumber instanceof Long) {
				return new TypedValue(leftNumber.longValue() * rightNumber.longValue());
			} else {
				return new TypedValue(leftNumber.intValue() * rightNumber.intValue());
			}
		} else if (operandOne instanceof String && operandTwo instanceof Integer) {
			int repeats = (Integer) operandTwo;
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < repeats; i++) {
				result.append(operandOne);
			}
			return new TypedValue(result.toString());
		}
		return state.operate(Operation.MULTIPLY, operandOne, operandTwo);
	}
}