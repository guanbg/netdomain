package com.platform.cubism.cvt;

import java.util.Locale;

import com.platform.cubism.util.StringUtils;

final class StringToLocaleConverter implements Converter<String, Locale> {
	public Locale convert(String source) {
		return StringUtils.parseLocaleString(source);
	}
}