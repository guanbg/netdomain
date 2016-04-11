package com.platform.cubism.cvt;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

final class ObjectToArrayConverter implements ConditionalGenericConverter {
	private final ConversionService conversionService;

	public ObjectToArrayConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Object[].class));
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return ConversionUtils.canConvertElements(sourceType, targetType.getElementTypeDescriptor(), this.conversionService);
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		Object target = Array.newInstance(targetType.getElementTypeDescriptor().getType(), 1);
		Object targetElement = this.conversionService.convert(source, sourceType, targetType.getElementTypeDescriptor());
		Array.set(target, 0, targetElement);
		return target;
	}
}