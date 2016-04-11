package com.platform.cubism.cvt;

import com.platform.cubism.cvt.Converter;

final class EnumToStringConverter implements Converter<Enum<?>, String> {

	public String convert(Enum<?> source) {
		return source.name();
	}
}