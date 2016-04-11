package com.platform.cubism.cvt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

final class PropertiesToStringConverter implements Converter<Properties, String> {
	public String convert(Properties source) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			source.store(os, null);
			return os.toString("ISO-8859-1");
		} catch (IOException ex) {
			// Should never happen.
			throw new IllegalArgumentException("Failed to store [" + source + "] into String", ex);
		}
	}
}