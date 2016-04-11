package com.platform.cubism.expression;

public class StandardOperatorOverloader implements OperatorOverloader {
	public boolean overridesOperation(Operation operation, Object leftOperand, Object rightOperand) throws EvaluationException {
		return false;
	}

	public Object operate(Operation operation, Object leftOperand, Object rightOperand) throws EvaluationException {
		throw new EvaluationException("No operation overloaded by default");
	}
}