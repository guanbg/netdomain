package com.platform.cubism.cvt;

final class ObjectToStringConverter implements Converter<Object, String> {
	public String convert(Object source) {
		return source.toString();
	}
}