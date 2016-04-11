package com.platform.cubism.cvt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

import com.platform.cubism.util.ReflectionUtils;

final class ObjectToObjectConverter implements ConditionalGenericConverter {
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, Object.class));
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return !sourceType.equals(targetType) && hasValueOfMethodOrConstructor(targetType.getType(), sourceType.getType());
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		Class<?> sourceClass = sourceType.getType();
		Class<?> targetClass = targetType.getType();
		Method method = getValueOfMethodOn(targetClass, sourceClass);
		try {
			if (method != null) {
				ReflectionUtils.makeAccessible(method);
				return method.invoke(null, source);
			} else {
				Constructor<?> constructor = getConstructor(targetClass, sourceClass);
				if (constructor != null) {
					return constructor.newInstance(source);
				}
			}
		} catch (InvocationTargetException ex) {
			throw new ConversionFailedException(sourceType, targetType, source, ex.getTargetException());
		} catch (Throwable ex) {
			throw new ConversionFailedException(sourceType, targetType, source, ex);
		}
		throw new IllegalStateException("No static valueOf(" + sourceClass.getName() + ") method or Constructor(" + sourceClass.getName() + ") exists on " + targetClass.getName());
	}

	static boolean hasValueOfMethodOrConstructor(Class<?> clazz, Class<?> sourceParameterType) {
		return getValueOfMethodOn(clazz, sourceParameterType) != null || getConstructor(clazz, sourceParameterType) != null;
	}

	private static Method getValueOfMethodOn(Class<?> clazz, Class<?> sourceParameterType) {
		return ReflectionUtils.getStaticMethod(clazz, "valueOf", sourceParameterType);
	}

	private static Constructor<?> getConstructor(Class<?> clazz, Class<?> sourceParameterType) {
		return ReflectionUtils.getConstructorIfAvailable(clazz, sourceParameterType);
	}
}