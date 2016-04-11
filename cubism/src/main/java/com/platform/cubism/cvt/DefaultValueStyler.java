package com.platform.cubism.cvt;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.platform.cubism.util.ReflectionUtils;

public class DefaultValueStyler implements ValueStyler {
	private static final String EMPTY = "[empty]";
	private static final String NULL = "[null]";
	private static final String COLLECTION = "collection";
	private static final String SET = "set";
	private static final String LIST = "list";
	private static final String MAP = "map";
	private static final String ARRAY = "array";

	@SuppressWarnings("unchecked")
	public String style(Object value) {
		if (value == null) {
			return NULL;
		} else if (value instanceof String) {
			return "\'" + value + "\'";
		} else if (value instanceof Class) {
			return ReflectionUtils.getShortName((Class<?>) value);
		} else if (value instanceof Method) {
			Method method = (Method) value;
			return method.getName() + "@" + ReflectionUtils.getShortName(method.getDeclaringClass());
		} else if (value instanceof Map) {
			return style((Map<Object, Object>) value);
		} else if (value instanceof Map.Entry) {
			return style((Map.Entry<Object, Object>) value);
		} else if (value instanceof Collection) {
			return style((Collection<Object>) value);
		} else if (value.getClass().isArray()) {
			return styleArray(Tools.toObjectArray(value));
		} else {
			return String.valueOf(value);
		}
	}

	private String style(Map<Object, Object> value) {
		StringBuilder result = new StringBuilder(value.size() * 8 + 16);
		result.append(MAP + "[");
		for (Iterator<Entry<Object, Object>> it = value.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) it.next();
			result.append(style(entry));
			if (it.hasNext()) {
				result.append(',').append(' ');
			}
		}
		if (value.isEmpty()) {
			result.append(EMPTY);
		}
		result.append("]");
		return result.toString();
	}

	private String style(Map.Entry<Object, Object> value) {
		return style(value.getKey()) + " -> " + style(value.getValue());
	}

	private String style(Collection<Object> value) {
		StringBuilder result = new StringBuilder(value.size() * 8 + 16);
		result.append(getCollectionTypeString(value)).append('[');
		for (Iterator<Object> i = value.iterator(); i.hasNext();) {
			result.append(style(i.next()));
			if (i.hasNext()) {
				result.append(',').append(' ');
			}
		}
		if (value.isEmpty()) {
			result.append(EMPTY);
		}
		result.append("]");
		return result.toString();
	}

	private String getCollectionTypeString(Collection<Object> value) {
		if (value instanceof List) {
			return LIST;
		} else if (value instanceof Set) {
			return SET;
		} else {
			return COLLECTION;
		}
	}

	private String styleArray(Object[] array) {
		StringBuilder result = new StringBuilder(array.length * 8 + 16);
		result.append(ARRAY + "<").append(ReflectionUtils.getShortName(array.getClass().getComponentType())).append(">[");
		for (int i = 0; i < array.length - 1; i++) {
			result.append(style(array[i]));
			result.append(',').append(' ');
		}
		if (array.length > 0) {
			result.append(style(array[array.length - 1]));
		} else {
			result.append(EMPTY);
		}
		result.append("]");
		return result.toString();
	}
}