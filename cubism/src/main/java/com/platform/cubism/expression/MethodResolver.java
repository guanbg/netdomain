package com.platform.cubism.expression;

import java.util.List;

import com.platform.cubism.cvt.TypeDescriptor;

public interface MethodResolver {
	MethodExecutor resolve(EvaluationContext context, Object targetObject, String name, List<TypeDescriptor> argumentTypes) throws AccessException;
}