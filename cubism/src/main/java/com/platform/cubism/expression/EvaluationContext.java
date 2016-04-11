package com.platform.cubism.expression;

import java.util.List;

public interface EvaluationContext {
	TypedValue getRootObject();

	List<ConstructorResolver> getConstructorResolvers();

	List<MethodResolver> getMethodResolvers();

	List<PropertyAccessor> getPropertyAccessors();

	TypeLocator getTypeLocator();

	TypeConverter getTypeConverter();

	TypeComparator getTypeComparator();

	OperatorOverloader getOperatorOverloader();

	BeanResolver getBeanResolver();

	void setVariable(String name, Object value);

	Object lookupVariable(String name);
}