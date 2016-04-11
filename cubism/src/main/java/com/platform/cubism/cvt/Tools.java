package com.platform.cubism.cvt;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.platform.cubism.util.Assert;
import com.platform.cubism.util.StringUtils;

public class Tools {
	public static Object[] toObjectArray(Object source) {
		if (source instanceof Object[]) {
			return (Object[]) source;
		}
		if (source == null) {
			return new Object[0];
		}
		if (!source.getClass().isArray()) {
			throw new IllegalArgumentException("Source is not an array: " + source);
		}
		int length = Array.getLength(source);
		if (length == 0) {
			return new Object[0];
		}
		Class<?> wrapperType = Array.get(source, 0).getClass();
		Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
		for (int i = 0; i < length; i++) {
			newArray[i] = Array.get(source, i);
		}
		return newArray;
	}

	public static String nullSafeToString(Object obj) {
		if (obj == null) {
			return "null";
		}
		if (obj instanceof String) {
			return (String) obj;
		}
		if (obj instanceof Object[]) {
			return nullSafeToString((Object[]) obj);
		}
		if (obj instanceof boolean[]) {
			return nullSafeToString((boolean[]) obj);
		}
		if (obj instanceof byte[]) {
			return nullSafeToString((byte[]) obj);
		}
		if (obj instanceof char[]) {
			return nullSafeToString((char[]) obj);
		}
		if (obj instanceof double[]) {
			return nullSafeToString((double[]) obj);
		}
		if (obj instanceof float[]) {
			return nullSafeToString((float[]) obj);
		}
		if (obj instanceof int[]) {
			return nullSafeToString((int[]) obj);
		}
		if (obj instanceof long[]) {
			return nullSafeToString((long[]) obj);
		}
		if (obj instanceof short[]) {
			return nullSafeToString((short[]) obj);
		}
		String str = obj.toString();
		return (str != null ? str : "");
	}

	public static boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		if (o1.equals(o2)) {
			return true;
		}
		if (o1.getClass().isArray() && o2.getClass().isArray()) {
			if (o1 instanceof Object[] && o2 instanceof Object[]) {
				return Arrays.equals((Object[]) o1, (Object[]) o2);
			}
			if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
				return Arrays.equals((boolean[]) o1, (boolean[]) o2);
			}
			if (o1 instanceof byte[] && o2 instanceof byte[]) {
				return Arrays.equals((byte[]) o1, (byte[]) o2);
			}
			if (o1 instanceof char[] && o2 instanceof char[]) {
				return Arrays.equals((char[]) o1, (char[]) o2);
			}
			if (o1 instanceof double[] && o2 instanceof double[]) {
				return Arrays.equals((double[]) o1, (double[]) o2);
			}
			if (o1 instanceof float[] && o2 instanceof float[]) {
				return Arrays.equals((float[]) o1, (float[]) o2);
			}
			if (o1 instanceof int[] && o2 instanceof int[]) {
				return Arrays.equals((int[]) o1, (int[]) o2);
			}
			if (o1 instanceof long[] && o2 instanceof long[]) {
				return Arrays.equals((long[]) o1, (long[]) o2);
			}
			if (o1 instanceof short[] && o2 instanceof short[]) {
				return Arrays.equals((short[]) o1, (short[]) o2);
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static Collection<Object> createCollection(Class<?> collectionType, int initialCapacity) {
		if (collectionType.isInterface()) {
			if (List.class.equals(collectionType)) {
				return new ArrayList<Object>(initialCapacity);
			} else if (SortedSet.class.equals(collectionType) || NavigableSet.class.equals(collectionType)) {
				return new TreeSet<Object>();
			} else if (Set.class.equals(collectionType) || Collection.class.equals(collectionType)) {
				return new LinkedHashSet<Object>(initialCapacity);
			} else {
				throw new IllegalArgumentException("Unsupported Collection interface: " + collectionType.getName());
			}
		} else {
			if (!Collection.class.isAssignableFrom(collectionType)) {
				throw new IllegalArgumentException("Unsupported Collection type: " + collectionType.getName());
			}
			try {
				return (Collection<Object>) collectionType.newInstance();
			} catch (Exception ex) {
				throw new IllegalArgumentException("Could not instantiate Collection type: " + collectionType.getName());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<Object, Object> createMap(Class<?> mapType, int initialCapacity) {
		if (mapType.isInterface()) {
			if (Map.class.equals(mapType)) {
				return new LinkedHashMap<Object, Object>(initialCapacity);
			} else if (SortedMap.class.equals(mapType) || NavigableMap.class.equals(mapType)) {
				return new TreeMap<Object, Object>();
			} else {
				throw new IllegalArgumentException("Unsupported Map interface: " + mapType.getName());
			}
		} else {
			if (!Map.class.isAssignableFrom(mapType)) {
				throw new IllegalArgumentException("Unsupported Map type: " + mapType.getName());
			}
			try {
				return (Map<Object, Object>) mapType.newInstance();
			} catch (Exception ex) {
				throw new IllegalArgumentException("Could not instantiate Map type: " + mapType.getName());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Number> T convertNumberToTargetClass(Number number, Class<T> targetClass) throws IllegalArgumentException {
		Assert.notNull(number, "Number must not be null");
		Assert.notNull(targetClass, "Target class must not be null");

		if (targetClass.isInstance(number)) {
			return (T) number;
		} else if (targetClass.equals(Byte.class)) {
			long value = number.longValue();
			if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
				raiseOverflowException(number, targetClass);
			}
			return (T) new Byte(number.byteValue());
		} else if (targetClass.equals(Short.class)) {
			long value = number.longValue();
			if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
				raiseOverflowException(number, targetClass);
			}
			return (T) new Short(number.shortValue());
		} else if (targetClass.equals(Integer.class)) {
			long value = number.longValue();
			if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
				raiseOverflowException(number, targetClass);
			}
			return (T) new Integer(number.intValue());
		} else if (targetClass.equals(Long.class)) {
			return (T) new Long(number.longValue());
		} else if (targetClass.equals(BigInteger.class)) {
			if (number instanceof BigDecimal) {
				// do not lose precision - use BigDecimal's own conversion
				return (T) ((BigDecimal) number).toBigInteger();
			} else {
				// original value is not a Big* number - use standard long
				// conversion
				return (T) BigInteger.valueOf(number.longValue());
			}
		} else if (targetClass.equals(Float.class)) {
			return (T) new Float(number.floatValue());
		} else if (targetClass.equals(Double.class)) {
			return (T) new Double(number.doubleValue());
		} else if (targetClass.equals(BigDecimal.class)) {
			// always use BigDecimal(String) here to avoid unpredictability of
			// BigDecimal(double)
			// (see BigDecimal javadoc for details)
			return (T) new BigDecimal(number.toString());
		} else {
			throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number.getClass().getName() + "] to unknown target class ["
					+ targetClass.getName() + "]");
		}
	}

	private static void raiseOverflowException(Number number, Class<?> targetClass) {
		throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number.getClass().getName() + "] to target class [" + targetClass.getName()
				+ "]: overflow");
	}

	private static boolean isHexNumber(String value) {
		int index = (value.startsWith("-") ? 1 : 0);
		return (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index));
	}

	private static BigInteger decodeBigInteger(String value) {
		int radix = 10;
		int index = 0;
		boolean negative = false;

		// Handle minus sign, if present.
		if (value.startsWith("-")) {
			negative = true;
			index++;
		}

		// Handle radix specifier, if present.
		if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
			index += 2;
			radix = 16;
		} else if (value.startsWith("#", index)) {
			index++;
			radix = 16;
		} else if (value.startsWith("0", index) && value.length() > 1 + index) {
			index++;
			radix = 8;
		}

		BigInteger result = new BigInteger(value.substring(index), radix);
		return (negative ? result.negate() : result);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Number> T parseNumber(String text, Class<T> targetClass) {
		Assert.notNull(text, "Text must not be null");
		Assert.notNull(targetClass, "Target class must not be null");
		String trimmed = StringUtils.trimAllWhitespace(text);

		if (targetClass.equals(Byte.class)) {
			return (T) (isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed));
		} else if (targetClass.equals(Short.class)) {
			return (T) (isHexNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed));
		} else if (targetClass.equals(Integer.class)) {
			return (T) (isHexNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed));
		} else if (targetClass.equals(Long.class)) {
			return (T) (isHexNumber(trimmed) ? Long.decode(trimmed) : Long.valueOf(trimmed));
		} else if (targetClass.equals(BigInteger.class)) {
			return (T) (isHexNumber(trimmed) ? decodeBigInteger(trimmed) : new BigInteger(trimmed));
		} else if (targetClass.equals(Float.class)) {
			return (T) Float.valueOf(trimmed);
		} else if (targetClass.equals(Double.class)) {
			return (T) Double.valueOf(trimmed);
		} else if (targetClass.equals(BigDecimal.class) || targetClass.equals(Number.class)) {
			return (T) new BigDecimal(trimmed);
		} else {
			throw new IllegalArgumentException("Cannot convert String [" + text + "] to target class [" + targetClass.getName() + "]");
		}
	}

	public static String formatMethodForMessage(String name, List<TypeDescriptor> argumentTypes) {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append("(");
		for (int i = 0; i < argumentTypes.size(); i++) {
			if (i > 0) {
				sb.append(",");
			}
			TypeDescriptor typeDescriptor = argumentTypes.get(i);
			if (typeDescriptor != null) {
				sb.append(formatClassNameForMessage(typeDescriptor.getType()));
			} else {
				sb.append(formatClassNameForMessage(null));
			}
		}
		sb.append(")");
		return sb.toString();
	}

	public static String formatClassNameForMessage(Class<?> clazz) {
		if (clazz == null) {
			return "null";
		}
		StringBuilder fmtd = new StringBuilder();
		if (clazz.isArray()) {
			int dims = 1;
			Class<?> baseClass = clazz.getComponentType();
			while (baseClass.isArray()) {
				baseClass = baseClass.getComponentType();
				dims++;
			}
			fmtd.append(baseClass.getName());
			for (int i = 0; i < dims; i++) {
				fmtd.append("[]");
			}
		} else {
			fmtd.append(clazz.getName());
		}
		return fmtd.toString();
	}
}