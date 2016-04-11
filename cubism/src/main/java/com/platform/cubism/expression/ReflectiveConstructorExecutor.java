package com.platform.cubism.expression;

import java.lang.reflect.Constructor;

import com.platform.cubism.util.ReflectionUtils;

class ReflectiveConstructorExecutor implements ConstructorExecutor {
	private final Constructor<?> ctor;
	private final Integer varargsPosition;
	private final int[] argsRequiringConversion;

	public ReflectiveConstructorExecutor(Constructor<?> ctor, int[] argsRequiringConversion) {
		this.ctor = ctor;
		if (ctor.isVarArgs()) {
			Class<?>[] paramTypes = ctor.getParameterTypes();
			this.varargsPosition = paramTypes.length - 1;
		} else {
			this.varargsPosition = null;
		}
		this.argsRequiringConversion = argsRequiringConversion;
	}

	public TypedValue execute(EvaluationContext context, Object... arguments) throws AccessException {
		try {
			if (arguments != null) {
				ReflectionHelper.convertArguments(context.getTypeConverter(), arguments, this.ctor, this.argsRequiringConversion, this.varargsPosition);
			}
			if (this.ctor.isVarArgs()) {
				arguments = ReflectionHelper.setupArgumentsForVarargsInvocation(this.ctor.getParameterTypes(), arguments);
			}
			ReflectionUtils.makeAccessible(this.ctor);
			return new TypedValue(this.ctor.newInstance(arguments));
		} catch (Exception ex) {
			throw new AccessException("Problem invoking constructor: " + this.ctor, ex);
		}
	}
}