package com.platform.cubism.cvt;

@SuppressWarnings("rawtypes")
final class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {
	@SuppressWarnings("unchecked")
	public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
		return new StringToEnum(targetType);
	}

	private class StringToEnum<T extends Enum> implements Converter<String, T> {
		private final Class<T> enumType;

		public StringToEnum(Class<T> enumType) {
			this.enumType = enumType;
		}

		@SuppressWarnings("unchecked")
		public T convert(String source) {
			if (source.length() == 0) {
				// It's an empty enum identifier: reset the enum value to null.
				return null;
			}
			return (T) Enum.valueOf(this.enumType, source.trim());
		}
	}
}