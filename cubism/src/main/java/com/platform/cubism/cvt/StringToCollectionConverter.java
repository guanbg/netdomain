package com.platform.cubism.cvt;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.platform.cubism.util.StringUtils;

final class StringToCollectionConverter implements ConditionalGenericConverter {
	private final ConversionService conversionService;

	public StringToCollectionConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(String.class, Collection.class));
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (targetType.getElementTypeDescriptor() != null) {
			return this.conversionService.canConvert(sourceType, targetType.getElementTypeDescriptor());
		} else {
			return true;
		}
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		String string = (String) source;
		String[] fields = StringUtils.commaDelimitedListToStringArray(string);
		Collection<Object> target = Tools.createCollection(targetType.getType(), fields.length);
		if (targetType.getElementTypeDescriptor() == null) {
			for (String field : fields) {
				target.add(field.trim());
			}
		} else {
			for (String field : fields) {
				Object targetElement = this.conversionService.convert(field.trim(), sourceType, targetType.getElementTypeDescriptor());
				target.add(targetElement);
			}
		}
		return target;
	}
}