package com.platform.cubism.cvt;

import java.lang.annotation.Annotation;

class BeanPropertyDescriptor extends AbstractDescriptor {

	private final Property property;

	private final MethodParameter methodParameter;

	private final Annotation[] annotations;

	public BeanPropertyDescriptor(Property property) {
		super(property.getType());
		this.property = property;
		this.methodParameter = property.getMethodParameter();
		this.annotations = property.getAnnotations();
	}

	@Override
	public Annotation[] getAnnotations() {
		return annotations;
	}

	@Override
	protected Class<?> resolveCollectionElementType() {
		return GenericCollectionTypeResolver.getCollectionParameterType(methodParameter);
	}

	@Override
	protected Class<?> resolveMapKeyType() {
		return GenericCollectionTypeResolver.getMapKeyParameterType(methodParameter);
	}

	@Override
	protected Class<?> resolveMapValueType() {
		return GenericCollectionTypeResolver.getMapValueParameterType(methodParameter);
	}

	@Override
	protected AbstractDescriptor nested(Class<?> type, int typeIndex) {
		MethodParameter methodParameter = new MethodParameter(this.methodParameter);
		methodParameter.increaseNestingLevel();
		methodParameter.setTypeIndexForCurrentLevel(typeIndex);
		return new BeanPropertyDescriptor(type, property, methodParameter, annotations);
	}

	private BeanPropertyDescriptor(Class<?> type, Property propertyDescriptor, MethodParameter methodParameter, Annotation[] annotations) {
		super(type);
		this.property = propertyDescriptor;
		this.methodParameter = methodParameter;
		this.annotations = annotations;
	}
}