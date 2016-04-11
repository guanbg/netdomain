package com.platform.cubism.cvt;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

final class MapToMapConverter implements ConditionalGenericConverter {
	private final ConversionService conversionService;

	public MapToMapConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Map.class, Map.class));
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return canConvertKey(sourceType, targetType) && canConvertValue(sourceType, targetType);
	}

	@SuppressWarnings("unchecked")
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		Map<Object, Object> sourceMap = (Map<Object, Object>) source;
		Map<Object, Object> targetMap = Tools.createMap(targetType.getType(), sourceMap.size());
		for (Map.Entry<Object, Object> entry : sourceMap.entrySet()) {
			Object sourceKey = entry.getKey();
			Object sourceValue = entry.getValue();
			Object targetKey = convertKey(sourceKey, sourceType, targetType.getMapKeyTypeDescriptor());
			Object targetValue = convertValue(sourceValue, sourceType, targetType.getMapValueTypeDescriptor());
			targetMap.put(targetKey, targetValue);
		}
		return targetMap;
	}

	private boolean canConvertKey(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return ConversionUtils.canConvertElements(sourceType.getMapKeyTypeDescriptor(), targetType.getMapKeyTypeDescriptor(), this.conversionService);
	}

	private boolean canConvertValue(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return ConversionUtils.canConvertElements(sourceType.getMapValueTypeDescriptor(), targetType.getMapValueTypeDescriptor(), this.conversionService);
	}

	private Object convertKey(Object sourceKey, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (targetType == null) {
			return sourceKey;
		}
		return this.conversionService.convert(sourceKey, sourceType.mapKeyTypeDescriptor(sourceKey), targetType);
	}

	private Object convertValue(Object sourceValue, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (targetType == null) {
			return sourceValue;
		}
		return this.conversionService.convert(sourceValue, sourceType.mapValueTypeDescriptor(sourceValue), targetType);
	}
}