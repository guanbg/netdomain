package com.platform.cubism.expression;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.platform.cubism.cvt.MethodParameter;
import com.platform.cubism.cvt.TypeDescriptor;
import com.platform.cubism.util.Assert;
import com.platform.cubism.util.ReflectionUtils;

public class ReflectionHelper {
	static ArgumentsMatchInfo compareArguments(List<TypeDescriptor> expectedArgTypes, List<TypeDescriptor> suppliedArgTypes, TypeConverter typeConverter) {
		Assert.isTrue(expectedArgTypes.size() == suppliedArgTypes.size(), "Expected argument types and supplied argument types should be arrays of same length");
		ArgsMatchKind match = ArgsMatchKind.EXACT;
		List<Integer> argsRequiringConversion = null;
		for (int i = 0; i < expectedArgTypes.size() && match != null; i++) {
			TypeDescriptor suppliedArg = suppliedArgTypes.get(i);
			TypeDescriptor expectedArg = expectedArgTypes.get(i);
			if (!expectedArg.equals(suppliedArg)) {
				// The user may supply null - and that will be ok unless a
				// primitive is expected
				if (suppliedArg == null) {
					if (expectedArg.isPrimitive()) {
						match = null;
					}
				} else {
					if (suppliedArg.isAssignableTo(expectedArg)) {
						if (match != ArgsMatchKind.REQUIRES_CONVERSION) {
							match = ArgsMatchKind.CLOSE;
						}
					} else if (typeConverter.canConvert(suppliedArg, expectedArg)) {
						if (argsRequiringConversion == null) {
							argsRequiringConversion = new ArrayList<Integer>();
						}
						argsRequiringConversion.add(i);
						match = ArgsMatchKind.REQUIRES_CONVERSION;
					} else {
						match = null;
					}
				}
			}
		}
		if (match == null) {
			return null;
		} else {
			if (match == ArgsMatchKind.REQUIRES_CONVERSION) {
				int[] argsArray = new int[argsRequiringConversion.size()];
				for (int i = 0; i < argsRequiringConversion.size(); i++) {
					argsArray[i] = argsRequiringConversion.get(i);
				}
				return new ArgumentsMatchInfo(match, argsArray);
			} else {
				return new ArgumentsMatchInfo(match);
			}
		}
	}

	public static int getTypeDifferenceWeight(List<TypeDescriptor> paramTypes, List<TypeDescriptor> argTypes) {
		int result = 0;
		for (int i = 0, max = paramTypes.size(); i < max; i++) {
			TypeDescriptor argType = argTypes.get(i);
			TypeDescriptor paramType = paramTypes.get(i);
			if (argType == null) {
				if (paramType.isPrimitive()) {
					return Integer.MAX_VALUE;
				}
			}
			if (!ReflectionUtils.isAssignable(paramType.getClass(), argType.getClass())) {
				return Integer.MAX_VALUE;
			}
			if (argType != null) {
				Class<?> paramTypeClazz = paramType.getType();
				if (paramTypeClazz.isPrimitive()) {
					paramTypeClazz = Object.class;
				}
				Class<?> superClass = argType.getClass().getSuperclass();
				while (superClass != null) {
					if (paramType.equals(superClass)) {
						result = result + 2;
						superClass = null;
					} else if (ReflectionUtils.isAssignable(paramTypeClazz, superClass)) {
						result = result + 2;
						superClass = superClass.getSuperclass();
					} else {
						superClass = null;
					}
				}
				if (paramTypeClazz.isInterface()) {
					result = result + 1;
				}
			}
		}
		return result;
	}

	static ArgumentsMatchInfo compareArgumentsVarargs(List<TypeDescriptor> expectedArgTypes, List<TypeDescriptor> suppliedArgTypes, TypeConverter typeConverter) {

		Assert.isTrue(expectedArgTypes != null && expectedArgTypes.size() > 0, "Expected arguments must at least include one array (the vargargs parameter)");
		Assert.isTrue(expectedArgTypes.get(expectedArgTypes.size() - 1).isArray(), "Final expected argument should be array type (the varargs parameter)");

		ArgsMatchKind match = ArgsMatchKind.EXACT;
		List<Integer> argsRequiringConversion = null;

		int argCountUpToVarargs = expectedArgTypes.size() - 1;
		for (int i = 0; i < argCountUpToVarargs && match != null; i++) {
			TypeDescriptor suppliedArg = suppliedArgTypes.get(i);
			TypeDescriptor expectedArg = expectedArgTypes.get(i);
			if (suppliedArg == null) {
				if (expectedArg.isPrimitive()) {
					match = null;
				}
			} else {
				if (!expectedArg.equals(suppliedArg)) {
					if (suppliedArg.isAssignableTo(expectedArg)) {
						if (match != ArgsMatchKind.REQUIRES_CONVERSION) {
							match = ArgsMatchKind.CLOSE;
						}
					} else if (typeConverter.canConvert(suppliedArg, expectedArg)) {
						if (argsRequiringConversion == null) {
							argsRequiringConversion = new ArrayList<Integer>();
						}
						argsRequiringConversion.add(i);
						match = ArgsMatchKind.REQUIRES_CONVERSION;
					} else {
						match = null;
					}
				}
			}
		}
		// If already confirmed it cannot be a match, then return
		if (match == null) {
			return null;
		}

		if (suppliedArgTypes.size() == expectedArgTypes.size() && expectedArgTypes.get(expectedArgTypes.size() - 1).equals(suppliedArgTypes.get(suppliedArgTypes.size() - 1))) {
			// Special case: there is one parameter left and it is an array and
			// it matches the varargs
			// expected argument - that is a match, the caller has already built
			// the array. Proceed with it.
		} else {
			// Now... we have the final argument in the method we are checking
			// as a match and we have 0 or more other
			// arguments left to pass to it.
			Class<?> varargsParameterType = expectedArgTypes.get(expectedArgTypes.size() - 1).getElementTypeDescriptor().getType();

			// All remaining parameters must be of this type or convertable to
			// this type
			for (int i = expectedArgTypes.size() - 1; i < suppliedArgTypes.size(); i++) {
				TypeDescriptor suppliedArg = suppliedArgTypes.get(i);
				if (suppliedArg == null) {
					if (varargsParameterType.isPrimitive()) {
						match = null;
					}
				} else {
					if (varargsParameterType != suppliedArg.getType()) {
						if (ReflectionUtils.isAssignable(varargsParameterType, suppliedArg.getType())) {
							if (match != ArgsMatchKind.REQUIRES_CONVERSION) {
								match = ArgsMatchKind.CLOSE;
							}
						} else if (typeConverter.canConvert(suppliedArg, TypeDescriptor.valueOf(varargsParameterType))) {
							if (argsRequiringConversion == null) {
								argsRequiringConversion = new ArrayList<Integer>();
							}
							argsRequiringConversion.add(i);
							match = ArgsMatchKind.REQUIRES_CONVERSION;
						} else {
							match = null;
						}
					}
				}
			}
		}

		if (match == null) {
			return null;
		} else {
			if (match == ArgsMatchKind.REQUIRES_CONVERSION) {
				int[] argsArray = new int[argsRequiringConversion.size()];
				for (int i = 0; i < argsRequiringConversion.size(); i++) {
					argsArray[i] = argsRequiringConversion.get(i);
				}
				return new ArgumentsMatchInfo(match, argsArray);
			} else {
				return new ArgumentsMatchInfo(match);
			}
		}
	}

	static void convertArguments(TypeConverter converter, Object[] arguments, Object methodOrCtor, int[] argumentsRequiringConversion, Integer varargsPosition)
			throws EvaluationException {
		if (varargsPosition == null) {
			for (int i = 0; i < arguments.length; i++) {
				TypeDescriptor targetType = new TypeDescriptor(MethodParameter.forMethodOrConstructor(methodOrCtor, i));
				Object argument = arguments[i];
				arguments[i] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
			}
		} else {
			for (int i = 0; i < varargsPosition; i++) {
				TypeDescriptor targetType = new TypeDescriptor(MethodParameter.forMethodOrConstructor(methodOrCtor, i));
				Object argument = arguments[i];
				arguments[i] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
			}
			MethodParameter methodParam = MethodParameter.forMethodOrConstructor(methodOrCtor, varargsPosition);
			if (varargsPosition == arguments.length - 1) {
				TypeDescriptor targetType = new TypeDescriptor(methodParam);
				Object argument = arguments[varargsPosition];
				arguments[varargsPosition] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
			} else {
				TypeDescriptor targetType = TypeDescriptor.nested(methodParam, 1);
				for (int i = varargsPosition; i < arguments.length; i++) {
					Object argument = arguments[i];
					arguments[i] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
				}
			}
		}
	}

	public static void convertAllArguments(TypeConverter converter, Object[] arguments, Method method) throws SpelEvaluationException {
		Integer varargsPosition = null;
		if (method.isVarArgs()) {
			Class<?>[] paramTypes = method.getParameterTypes();
			varargsPosition = paramTypes.length - 1;
		}
		for (int argPosition = 0; argPosition < arguments.length; argPosition++) {
			TypeDescriptor targetType;
			if (varargsPosition != null && argPosition >= varargsPosition) {
				MethodParameter methodParam = new MethodParameter(method, varargsPosition);
				targetType = TypeDescriptor.nested(methodParam, 1);
			} else {
				targetType = new TypeDescriptor(new MethodParameter(method, argPosition));
			}
			try {
				Object argument = arguments[argPosition];
				if (argument != null && !targetType.getObjectType().isInstance(argument)) {
					if (converter == null) {
						throw new SpelEvaluationException(SpelMessage.TYPE_CONVERSION_ERROR, argument.getClass().getName(), targetType);
					}
					arguments[argPosition] = converter.convertValue(argument, TypeDescriptor.forObject(argument), targetType);
				}
			} catch (EvaluationException ex) {
				// allows for another type converter throwing a different kind
				// of EvaluationException
				if (ex instanceof SpelEvaluationException) {
					throw (SpelEvaluationException) ex;
				} else {
					throw new SpelEvaluationException(ex, SpelMessage.TYPE_CONVERSION_ERROR, arguments[argPosition].getClass().getName(), targetType);
				}
			}
		}
	}

	public static Object[] setupArgumentsForVarargsInvocation(Class<?>[] requiredParameterTypes, Object... args) {
		// Check if array already built for final argument
		int parameterCount = requiredParameterTypes.length;
		int argumentCount = args.length;

		// Check if repackaging is needed:
		if (parameterCount != args.length || requiredParameterTypes[parameterCount - 1] != (args[argumentCount - 1] == null ? null : args[argumentCount - 1].getClass())) {
			int arraySize = 0; // zero size array if nothing to pass as the
								// varargs parameter
			if (argumentCount >= parameterCount) {
				arraySize = argumentCount - (parameterCount - 1);
			}

			// Create an array for the varargs arguments
			Object[] newArgs = new Object[parameterCount];
			for (int i = 0; i < newArgs.length - 1; i++) {
				newArgs[i] = args[i];
			}
			// Now sort out the final argument, which is the varargs one. Before
			// entering this
			// method the arguments should have been converted to the box form
			// of the required type.
			Class<?> componentType = requiredParameterTypes[parameterCount - 1].getComponentType();
			if (componentType.isPrimitive()) {
				if (componentType == Integer.TYPE) {
					int[] repackagedArguments = (int[]) Array.newInstance(componentType, arraySize);
					for (int i = 0; i < arraySize; i++) {
						repackagedArguments[i] = ((Integer) args[parameterCount + i - 1]).intValue();
					}
					newArgs[newArgs.length - 1] = repackagedArguments;
				} else if (componentType == Float.TYPE) {
					float[] repackagedArguments = (float[]) Array.newInstance(componentType, arraySize);
					for (int i = 0; i < arraySize; i++) {
						repackagedArguments[i] = ((Float) args[parameterCount + i - 1]).floatValue();
					}
					newArgs[newArgs.length - 1] = repackagedArguments;
				} else if (componentType == Double.TYPE) {
					double[] repackagedArguments = (double[]) Array.newInstance(componentType, arraySize);
					for (int i = 0; i < arraySize; i++) {
						repackagedArguments[i] = ((Double) args[parameterCount + i - 1]).doubleValue();
					}
					newArgs[newArgs.length - 1] = repackagedArguments;
				} else if (componentType == Short.TYPE) {
					short[] repackagedArguments = (short[]) Array.newInstance(componentType, arraySize);
					for (int i = 0; i < arraySize; i++) {
						repackagedArguments[i] = ((Short) args[parameterCount + i - 1]).shortValue();
					}
					newArgs[newArgs.length - 1] = repackagedArguments;
				} else if (componentType == Character.TYPE) {
					char[] repackagedArguments = (char[]) Array.newInstance(componentType, arraySize);
					for (int i = 0; i < arraySize; i++) {
						repackagedArguments[i] = ((Character) args[parameterCount + i - 1]).charValue();
					}
					newArgs[newArgs.length - 1] = repackagedArguments;
				} else if (componentType == Byte.TYPE) {
					byte[] repackagedArguments = (byte[]) Array.newInstance(componentType, arraySize);
					for (int i = 0; i < arraySize; i++) {
						repackagedArguments[i] = ((Byte) args[parameterCount + i - 1]).byteValue();
					}
					newArgs[newArgs.length - 1] = repackagedArguments;
				} else if (componentType == Boolean.TYPE) {
					boolean[] repackagedArguments = (boolean[]) Array.newInstance(componentType, arraySize);
					for (int i = 0; i < arraySize; i++) {
						repackagedArguments[i] = ((Boolean) args[parameterCount + i - 1]).booleanValue();
					}
					newArgs[newArgs.length - 1] = repackagedArguments;
				} else if (componentType == Long.TYPE) {
					long[] repackagedArguments = (long[]) Array.newInstance(componentType, arraySize);
					for (int i = 0; i < arraySize; i++) {
						repackagedArguments[i] = ((Long) args[parameterCount + i - 1]).longValue();
					}
					newArgs[newArgs.length - 1] = repackagedArguments;
				}
			} else {
				Object[] repackagedArguments = (Object[]) Array.newInstance(componentType, arraySize);
				// Copy all but the varargs arguments
				for (int i = 0; i < arraySize; i++) {
					repackagedArguments[i] = args[parameterCount + i - 1];
				}
				newArgs[newArgs.length - 1] = repackagedArguments;
			}
			return newArgs;
		}
		return args;
	}

	public static enum ArgsMatchKind {
		// An exact match is where the parameter types exactly match what the
		// method/constructor being invoked is expecting
		EXACT,
		// A close match is where the parameter types either exactly match or
		// are assignment compatible with the method/constructor being invoked
		CLOSE,
		// A conversion match is where the type converter must be used to
		// transform some of the parameter types
		REQUIRES_CONVERSION
	}

	public static class ArgumentsMatchInfo {
		public final ArgsMatchKind kind;
		public int[] argsRequiringConversion;

		ArgumentsMatchInfo(ArgsMatchKind kind, int[] integers) {
			this.kind = kind;
			this.argsRequiringConversion = integers;
		}

		ArgumentsMatchInfo(ArgsMatchKind kind) {
			this.kind = kind;
		}

		public boolean isExactMatch() {
			return (this.kind == ArgsMatchKind.EXACT);
		}

		public boolean isCloseMatch() {
			return (this.kind == ArgsMatchKind.CLOSE);
		}

		public boolean isMatchRequiringConversion() {
			return (this.kind == ArgsMatchKind.REQUIRES_CONVERSION);
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("ArgumentMatch: ").append(this.kind);
			if (this.argsRequiringConversion != null) {
				sb.append("  (argsForConversion:");
				for (int i = 0; i < this.argsRequiringConversion.length; i++) {
					if (i > 0) {
						sb.append(",");
					}
					sb.append(this.argsRequiringConversion[i]);
				}
				sb.append(")");
			}
			return sb.toString();
		}
	}
}