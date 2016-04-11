package com.platform.cubism.cvt;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

import com.platform.cubism.util.StringUtils;

final class StringToArrayConverter implements ConditionalGenericConverter {
	private final ConversionService conversionService;

	public StringToArrayConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(String.class, Object[].class));
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return this.conversionService.canConvert(sourceType, targetType.getElementTypeDescriptor());
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		String string = (String) source;
		String[] fields = StringUtils.commaDelimitedListToStringArray(string);
		Object target = Array.newInstance(targetType.getElementTypeDescriptor().getType(), fields.length);
		for (int i = 0; i < fields.length; i++) {
			String sourceElement = fields[i];
			Object targetElement = this.conversionService.convert(sourceElement.trim(), sourceType, targetType.getElementTypeDescriptor());
			Array.set(target, i, targetElement);
		}
		return target;
	}
}