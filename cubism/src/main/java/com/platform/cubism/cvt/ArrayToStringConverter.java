package com.platform.cubism.cvt;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

final class ArrayToStringConverter implements ConditionalGenericConverter {
	private final CollectionToStringConverter helperConverter;

	public ArrayToStringConverter(ConversionService conversionService) {
		this.helperConverter = new CollectionToStringConverter(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object[].class, String.class));
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return this.helperConverter.matches(sourceType, targetType);
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		return this.helperConverter.convert(Arrays.asList(Tools.toObjectArray(source)), sourceType, targetType);
	}
}