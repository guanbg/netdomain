package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.BooleanTypedValue;
import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.SpelMessage;
import com.platform.cubism.expression.TypedValue;

public class OperatorInstanceof extends Operator {
	public OperatorInstanceof(int pos, SpelNodeImpl... operands) {
		super("instanceof", pos, operands);
	}

	@Override
	public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		TypedValue left = getLeftOperand().getValueInternal(state);
		TypedValue right = getRightOperand().getValueInternal(state);
		Object leftValue = left.getValue();
		Object rightValue = right.getValue();
		if (leftValue == null) {
			return BooleanTypedValue.FALSE; // null is not an instanceof
											// anything
		}
		if (rightValue == null || !(rightValue instanceof Class<?>)) {
			throw new SpelEvaluationException(getRightOperand().getStartPosition(), SpelMessage.INSTANCEOF_OPERATOR_NEEDS_CLASS_OPERAND, (rightValue == null ? "null" : rightValue
					.getClass().getName()));
		}
		Class<?> rightClass = (Class<?>) rightValue;
		return BooleanTypedValue.forValue(rightClass.isAssignableFrom(leftValue.getClass()));
	}
}