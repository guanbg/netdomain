package com.platform.cubism.cvt;

final class CharacterToNumberFactory implements ConverterFactory<Character, Number> {

	public <T extends Number> Converter<Character, T> getConverter(Class<T> targetType) {
		return new CharacterToNumber<T>(targetType);
	}

	private static final class CharacterToNumber<T extends Number> implements Converter<Character, T> {
		private final Class<T> targetType;

		public CharacterToNumber(Class<T> targetType) {
			this.targetType = targetType;
		}

		public T convert(Character source) {
			return Tools.convertNumberToTargetClass((short) source.charValue(), this.targetType);
		}
	}
}