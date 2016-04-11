package com.platform.cubism.expression;

import java.util.List;

import com.platform.cubism.cvt.TypeDescriptor;

public interface ConstructorResolver {
	ConstructorExecutor resolve(EvaluationContext context, String typeName, List<TypeDescriptor> argumentTypes) throws AccessException;
}