package com.platform.cubism.cvt;

public interface Converter<S, T> {
	T convert(S source);
}