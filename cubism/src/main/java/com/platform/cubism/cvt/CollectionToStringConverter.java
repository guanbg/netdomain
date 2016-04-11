package com.platform.cubism.cvt;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

final class CollectionToStringConverter implements ConditionalGenericConverter {
	private static final String DELIMITER = ",";
	private final ConversionService conversionService;

	public CollectionToStringConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Collection.class, String.class));
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return ConversionUtils.canConvertElements(sourceType.getElementTypeDescriptor(), targetType, this.conversionService);
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		Collection<?> sourceCollection = (Collection<?>) source;
		if (sourceCollection.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Object sourceElement : sourceCollection) {
			if (i > 0) {
				sb.append(DELIMITER);
			}
			Object targetElement = this.conversionService.convert(sourceElement, sourceType.elementTypeDescriptor(sourceElement), targetType);
			sb.append(targetElement);
			i++;
		}
		return sb.toString();
	}
}