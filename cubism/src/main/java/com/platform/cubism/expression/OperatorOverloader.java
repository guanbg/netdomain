package com.platform.cubism.expression;

public interface OperatorOverloader {
	boolean overridesOperation(Operation operation, Object leftOperand, Object rightOperand) throws EvaluationException;

	Object operate(Operation operation, Object leftOperand, Object rightOperand) throws EvaluationException;
}