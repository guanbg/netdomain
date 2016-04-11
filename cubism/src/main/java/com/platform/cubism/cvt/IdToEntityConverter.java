package com.platform.cubism.cvt;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Set;

import com.platform.cubism.util.ReflectionUtils;

final class IdToEntityConverter implements ConditionalGenericConverter {
	private final ConversionService conversionService;

	public IdToEntityConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Object.class));
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		Method finder = getFinder(targetType.getType());
		return finder != null && this.conversionService.canConvert(sourceType, TypeDescriptor.valueOf(finder.getParameterTypes()[0]));
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		Method finder = getFinder(targetType.getType());
		Object id = this.conversionService.convert(source, sourceType, TypeDescriptor.valueOf(finder.getParameterTypes()[0]));
		return ReflectionUtils.invokeMethod(finder, source, id);
	}

	private Method getFinder(Class<?> entityClass) {
		String finderMethod = "find" + getEntityName(entityClass);
		Method[] methods = entityClass.getDeclaredMethods();
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == 1 && method.getReturnType().equals(entityClass)) {
				if (method.getName().equals(finderMethod)) {
					return method;
				}
			}
		}
		return null;
	}

	private String getEntityName(Class<?> entityClass) {
		String shortName = ReflectionUtils.getShortName(entityClass);
		int lastDot = shortName.lastIndexOf('.');
		if (lastDot != -1) {
			return shortName.substring(lastDot + 1);
		} else {
			return shortName;
		}
	}
}