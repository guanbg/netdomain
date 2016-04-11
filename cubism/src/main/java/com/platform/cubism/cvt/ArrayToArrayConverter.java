package com.platform.cubism.cvt;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

final class ArrayToArrayConverter implements ConditionalGenericConverter {

	private final CollectionToArrayConverter helperConverter;

	public ArrayToArrayConverter(ConversionService conversionService) {
		this.helperConverter = new CollectionToArrayConverter(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object[].class, Object[].class));
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return this.helperConverter.matches(sourceType, targetType);
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		return this.helperConverter.convert(Arrays.asList(Tools.toObjectArray(source)), sourceType, targetType);
	}
}
