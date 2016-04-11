package com.platform.cubism.cvt;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface ParameterNameDiscoverer {
	String[] getParameterNames(Method method);

	String[] getParameterNames(Constructor<?> ctor);
}