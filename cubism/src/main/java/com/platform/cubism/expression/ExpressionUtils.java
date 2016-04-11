package com.platform.cubism.expression;

import com.platform.cubism.cvt.TypeDescriptor;
import com.platform.cubism.util.ReflectionUtils;

public abstract class ExpressionUtils {
	public static <T> T convert(EvaluationContext context, Object value, Class<T> targetType) throws EvaluationException {
		return convertTypedValue(context, new TypedValue(value), targetType);
	}

	@SuppressWarnings("unchecked")
	public static <T> T convertTypedValue(EvaluationContext context, TypedValue typedValue, Class<T> targetType) {
		Object value = typedValue.getValue();
		if (targetType == null || ReflectionUtils.isAssignableValue(targetType, value)) {
			return (T) value;
		}
		if (context != null) {
			return (T) context.getTypeConverter().convertValue(value, typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(targetType));
		}
		throw new EvaluationException("Cannot convert value '" + value + "' to type '" + targetType.getName() + "'");
	}

	public static int toInt(TypeConverter typeConverter, TypedValue typedValue) {
		return (Integer) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Integer.class));
	}

	public static boolean toBoolean(TypeConverter typeConverter, TypedValue typedValue) {
		return (Boolean) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Boolean.class));
	}

	public static double toDouble(TypeConverter typeConverter, TypedValue typedValue) {
		return (Double) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Double.class));
	}

	public static long toLong(TypeConverter typeConverter, TypedValue typedValue) {
		return (Long) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Long.class));
	}

	public static char toChar(TypeConverter typeConverter, TypedValue typedValue) {
		return (Character) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Character.class));
	}

	public static short toShort(TypeConverter typeConverter, TypedValue typedValue) {
		return (Short) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Short.class));
	}

	public static float toFloat(TypeConverter typeConverter, TypedValue typedValue) {
		return (Float) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Float.class));
	}

	public static byte toByte(TypeConverter typeConverter, TypedValue typedValue) {
		return (Byte) typeConverter.convertValue(typedValue.getValue(), typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(Byte.class));
	}
}