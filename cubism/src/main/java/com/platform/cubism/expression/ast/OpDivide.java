package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.Operation;
import com.platform.cubism.expression.TypedValue;

public class OpDivide extends Operator {
	public OpDivide(int pos, SpelNodeImpl... operands) {
		super("/", pos, operands);
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		Object operandOne = getLeftOperand().getValueInternal(state).getValue();
		Object operandTwo = getRightOperand().getValueInternal(state).getValue();
		if (operandOne instanceof Number && operandTwo instanceof Number) {
			Number op1 = (Number) operandOne;
			Number op2 = (Number) operandTwo;
			if (op1 instanceof Double || op2 instanceof Double) {
				return new TypedValue(op1.doubleValue() / op2.doubleValue());
			} else if (op1 instanceof Long || op2 instanceof Long) {
				return new TypedValue(op1.longValue() / op2.longValue());
			} else {
				return new TypedValue(op1.intValue() / op2.intValue());
			}
		}
		Object result = state.operate(Operation.DIVIDE, operandOne, operandTwo);
		return new TypedValue(result);
	}
}