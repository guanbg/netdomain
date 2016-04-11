package com.platform.cubism.expression.ast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.platform.cubism.expression.BooleanTypedValue;
import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.SpelMessage;

public class OperatorMatches extends Operator {
	public OperatorMatches(int pos, SpelNodeImpl... operands) {
		super("matches", pos, operands);
	}

	@Override
	public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		SpelNodeImpl leftOp = getLeftOperand();
		SpelNodeImpl rightOp = getRightOperand();
		Object left = leftOp.getValue(state, String.class);
		Object right = getRightOperand().getValueInternal(state).getValue();
		try {
			if (!(left instanceof String)) {
				throw new SpelEvaluationException(leftOp.getStartPosition(), SpelMessage.INVALID_FIRST_OPERAND_FOR_MATCHES_OPERATOR, left);
			}
			if (!(right instanceof String)) {
				throw new SpelEvaluationException(rightOp.getStartPosition(), SpelMessage.INVALID_SECOND_OPERAND_FOR_MATCHES_OPERATOR, right);
			}
			Pattern pattern = Pattern.compile((String) right);
			Matcher matcher = pattern.matcher((String) left);
			return BooleanTypedValue.forValue(matcher.matches());
		} catch (PatternSyntaxException pse) {
			throw new SpelEvaluationException(rightOp.getStartPosition(), pse, SpelMessage.INVALID_PATTERN, right);
		}
	}
}