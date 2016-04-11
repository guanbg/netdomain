package com.platform.cubism.cvt;

public interface ConditionalGenericConverter extends GenericConverter {
	boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);
}