package com.platform.cubism.cvt;

public interface ConverterFactory<S, R> {
	<T extends R> Converter<S, T> getConverter(Class<T> targetType);
}