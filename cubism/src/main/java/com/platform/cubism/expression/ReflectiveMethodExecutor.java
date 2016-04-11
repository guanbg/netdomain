package com.platform.cubism.expression;

import java.lang.reflect.Method;

import com.platform.cubism.util.ReflectionUtils;
import com.platform.cubism.cvt.TypeDescriptor;
import com.platform.cubism.cvt.MethodParameter;

class ReflectiveMethodExecutor implements MethodExecutor {
	private final Method method;
	private final Integer varargsPosition;
	private final int[] argsRequiringConversion;

	public ReflectiveMethodExecutor(Method theMethod, int[] argumentsRequiringConversion) {
		this.method = theMethod;
		if (theMethod.isVarArgs()) {
			Class<?>[] paramTypes = theMethod.getParameterTypes();
			this.varargsPosition = paramTypes.length - 1;
		} else {
			this.varargsPosition = null;
		}
		this.argsRequiringConversion = argumentsRequiringConversion;
	}

	public TypedValue execute(EvaluationContext context, Object target, Object... arguments) throws AccessException {
		try {
			if (arguments != null) {
				ReflectionHelper.convertArguments(context.getTypeConverter(), arguments, this.method, this.argsRequiringConversion, this.varargsPosition);
			}
			if (this.method.isVarArgs()) {
				arguments = ReflectionHelper.setupArgumentsForVarargsInvocation(this.method.getParameterTypes(), arguments);
			}
			ReflectionUtils.makeAccessible(this.method);
			Object value = this.method.invoke(target, arguments);
			return new TypedValue(value, new TypeDescriptor(new MethodParameter(this.method, -1)).narrow(value));
		} catch (Exception ex) {
			throw new AccessException("Problem invoking method: " + this.method, ex);
		}
	}
}