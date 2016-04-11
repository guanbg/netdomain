package com.platform.cubism.cvt;

public abstract class ConversionException extends NestedRuntimeException {
	private static final long serialVersionUID = -385618442828623565L;

	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}
}