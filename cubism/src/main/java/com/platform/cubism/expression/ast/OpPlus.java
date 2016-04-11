package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.Operation;
import com.platform.cubism.expression.TypedValue;

public class OpPlus extends Operator {
	public OpPlus(int pos, SpelNodeImpl... operands) {
		super("+", pos, operands);
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		SpelNodeImpl leftOp = getLeftOperand();
		SpelNodeImpl rightOp = getRightOperand();
		if (rightOp == null) { // If only one operand, then this is unary plus
			Object operandOne = leftOp.getValueInternal(state).getValue();
			if (operandOne instanceof Number) {
				if (operandOne instanceof Double) {
					return new TypedValue(((Double) operandOne).doubleValue());
				} else if (operandOne instanceof Long) {
					return new TypedValue(((Long) operandOne).longValue());
				} else {
					return new TypedValue(((Integer) operandOne).intValue());
				}
			}
			return state.operate(Operation.ADD, operandOne, null);
		} else {
			Object operandOne = leftOp.getValueInternal(state).getValue();
			Object operandTwo = rightOp.getValueInternal(state).getValue();
			if (operandOne instanceof Number && operandTwo instanceof Number) {
				Number op1 = (Number) operandOne;
				Number op2 = (Number) operandTwo;
				if (op1 instanceof Double || op2 instanceof Double) {
					return new TypedValue(op1.doubleValue() + op2.doubleValue());
				} else if (op1 instanceof Long || op2 instanceof Long) {
					return new TypedValue(op1.longValue() + op2.longValue());
				} else {
					return new TypedValue(op1.intValue() + op2.intValue());
				}
			} else if (operandOne instanceof String && operandTwo instanceof String) {
				return new TypedValue(new StringBuilder((String) operandOne).append((String) operandTwo).toString());
			} else if (operandOne instanceof String) {
				StringBuilder result = new StringBuilder((String) operandOne);
				result.append((operandTwo == null ? "null" : operandTwo.toString()));
				return new TypedValue(result.toString());
			} else if (operandTwo instanceof String) {
				StringBuilder result = new StringBuilder((operandOne == null ? "null" : operandOne.toString()));
				result.append((String) operandTwo);
				return new TypedValue(result.toString());
			}
			return state.operate(Operation.ADD, operandOne, operandTwo);
		}
	}

	@Override
	public String toStringAST() {
		if (children.length < 2) { // unary plus
			return new StringBuilder().append("+").append(getLeftOperand().toStringAST()).toString();
		}
		return super.toStringAST();
	}

	public SpelNodeImpl getRightOperand() {
		if (children.length < 2) {
			return null;
		}
		return children[1];
	}
}