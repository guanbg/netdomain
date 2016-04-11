package com.platform.cubism.cvt;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

final class CollectionToCollectionConverter implements ConditionalGenericConverter {
	private final ConversionService conversionService;

	public CollectionToCollectionConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Collection.class, Collection.class));
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return ConversionUtils.canConvertElements(sourceType.getElementTypeDescriptor(), targetType.getElementTypeDescriptor(), conversionService);
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		Collection<?> sourceCollection = (Collection<?>) source;
		Collection<Object> target = Tools.createCollection(targetType.getType(), sourceCollection.size());
		if (targetType.getElementTypeDescriptor() == null) {
			for (Object element : sourceCollection) {
				target.add(element);
			}
		} else {
			for (Object sourceElement : sourceCollection) {
				Object targetElement = this.conversionService.convert(sourceElement, sourceType.elementTypeDescriptor(sourceElement), targetType.getElementTypeDescriptor());
				target.add(targetElement);
			}
		}
		return target;
	}
}