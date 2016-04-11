package com.platform.cubism.cvt;

final class NumberToNumberConverterFactory implements ConverterFactory<Number, Number> {

	public <T extends Number> Converter<Number, T> getConverter(Class<T> targetType) {
		return new NumberToNumber<T>(targetType);
	}

	private final static class NumberToNumber<T extends Number> implements Converter<Number, T> {

		private final Class<T> targetType;

		public NumberToNumber(Class<T> targetType) {
			this.targetType = targetType;
		}

		public T convert(Number source) {
			return Tools.convertNumberToTargetClass(source, this.targetType);
		}
	}
}