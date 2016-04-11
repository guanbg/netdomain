package com.platform.cubism.cvt;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

final class ObjectToCollectionConverter implements ConditionalGenericConverter {
	private final ConversionService conversionService;

	public ObjectToCollectionConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Collection.class));
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return ConversionUtils.canConvertElements(sourceType, targetType.getElementTypeDescriptor(), this.conversionService);
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		Collection<Object> target = Tools.createCollection(targetType.getType(), 1);
		if (targetType.getElementTypeDescriptor() == null || targetType.getElementTypeDescriptor().isCollection()) {
			target.add(source);
		} else {
			Object singleElement = this.conversionService.convert(source, sourceType, targetType.getElementTypeDescriptor());
			target.add(singleElement);
		}
		return target;
	}
}