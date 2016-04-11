package com.platform.cubism.cvt;

import java.util.Set;

public abstract class ConversionServiceFactory {
	public static void registerConverters(Set<?> converters, ConverterRegistry registry) {
		if (converters != null) {
			for (Object converter : converters) {
				if (converter instanceof GenericConverter) {
					registry.addConverter((GenericConverter) converter);
				} else if (converter instanceof Converter<?, ?>) {
					registry.addConverter((Converter<?, ?>) converter);
				} else if (converter instanceof ConverterFactory<?, ?>) {
					registry.addConverterFactory((ConverterFactory<?, ?>) converter);
				} else {
					throw new IllegalArgumentException("Each converter object must implement one of the " + "Converter, ConverterFactory, or GenericConverter interfaces");
				}
			}
		}
	}
}