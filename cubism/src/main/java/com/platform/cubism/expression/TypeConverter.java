package com.platform.cubism.expression;

import com.platform.cubism.cvt.TypeDescriptor;

public interface TypeConverter {
	boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);

	Object convertValue(Object value, TypeDescriptor sourceType, TypeDescriptor targetType);
}