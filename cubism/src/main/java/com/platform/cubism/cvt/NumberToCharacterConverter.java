package com.platform.cubism.cvt;

final class NumberToCharacterConverter implements Converter<Number, Character> {
	public Character convert(Number source) {
		return (char) source.shortValue();
	}
}