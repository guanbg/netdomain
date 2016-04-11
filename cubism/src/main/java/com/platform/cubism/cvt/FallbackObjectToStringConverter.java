package com.platform.cubism.cvt;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Set;

final class FallbackObjectToStringConverter implements ConditionalGenericConverter {
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, String.class));
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		Class<?> sourceClass = sourceType.getObjectType();
		return CharSequence.class.isAssignableFrom(sourceClass) || StringWriter.class.isAssignableFrom(sourceClass)
				|| ObjectToObjectConverter.hasValueOfMethodOrConstructor(sourceClass, String.class);
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		return (source != null ? source.toString() : null);
	}
}