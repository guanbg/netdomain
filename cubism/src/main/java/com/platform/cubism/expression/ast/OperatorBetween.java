package com.platform.cubism.expression.ast;

import java.util.List;

import com.platform.cubism.expression.BooleanTypedValue;
import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.SpelMessage;
import com.platform.cubism.expression.TypeComparator;

public class OperatorBetween extends Operator {
	public OperatorBetween(int pos, SpelNodeImpl... operands) {
		super("between", pos, operands);
	}

	@Override
	public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		Object left = getLeftOperand().getValueInternal(state).getValue();
		Object right = getRightOperand().getValueInternal(state).getValue();
		if (!(right instanceof List) || ((List<?>) right).size() != 2) {
			throw new SpelEvaluationException(getRightOperand().getStartPosition(), SpelMessage.BETWEEN_RIGHT_OPERAND_MUST_BE_TWO_ELEMENT_LIST);
		}
		List<?> l = (List<?>) right;
		Object low = l.get(0);
		Object high = l.get(1);
		TypeComparator comparator = state.getTypeComparator();
		try {
			return BooleanTypedValue.forValue((comparator.compare(left, low) >= 0 && comparator.compare(left, high) <= 0));
		} catch (SpelEvaluationException ex) {
			ex.setPosition(getStartPosition());
			throw ex;
		}
	}
}