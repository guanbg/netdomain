package com.platform.cubism.expression;

public interface MethodExecutor {
	TypedValue execute(EvaluationContext context, Object target, Object... arguments) throws AccessException;
}