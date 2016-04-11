package com.platform.cubism.expression;

public interface BeanResolver {
	Object resolve(EvaluationContext context, String beanName) throws AccessException;
}