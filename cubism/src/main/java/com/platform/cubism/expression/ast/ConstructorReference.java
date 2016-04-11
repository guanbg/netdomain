package com.platform.cubism.expression.ast;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.platform.cubism.cvt.Tools;
import com.platform.cubism.cvt.TypeDescriptor;
import com.platform.cubism.expression.AccessException;
import com.platform.cubism.expression.ConstructorExecutor;
import com.platform.cubism.expression.ConstructorResolver;
import com.platform.cubism.expression.EvaluationContext;
import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.ExpressionUtils;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.SpelMessage;
import com.platform.cubism.expression.SpelNode;
import com.platform.cubism.expression.TypeConverter;
import com.platform.cubism.expression.TypedValue;

public class ConstructorReference extends SpelNodeImpl {
	private boolean isArrayConstructor = false;
	private SpelNodeImpl[] dimensions;
	private volatile ConstructorExecutor cachedExecutor;

	public ConstructorReference(int pos, SpelNodeImpl... arguments) {
		super(pos, arguments);
		this.isArrayConstructor = false;
	}

	public ConstructorReference(int pos, SpelNodeImpl[] dimensions, SpelNodeImpl... arguments) {
		super(pos, arguments);
		this.isArrayConstructor = true;
		this.dimensions = dimensions;
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		if (this.isArrayConstructor) {
			return createArray(state);
		} else {
			return createNewInstance(state);
		}
	}

	private TypedValue createNewInstance(ExpressionState state) throws EvaluationException {
		Object[] arguments = new Object[getChildCount() - 1];
		List<TypeDescriptor> argumentTypes = new ArrayList<TypeDescriptor>(getChildCount() - 1);
		for (int i = 0; i < arguments.length; i++) {
			TypedValue childValue = this.children[i + 1].getValueInternal(state);
			Object value = childValue.getValue();
			arguments[i] = value;
			argumentTypes.add(TypeDescriptor.forObject(value));
		}

		ConstructorExecutor executorToUse = this.cachedExecutor;
		if (executorToUse != null) {
			try {
				return executorToUse.execute(state.getEvaluationContext(), arguments);
			} catch (AccessException ae) {
				// Two reasons this can occur:
				// 1. the method invoked actually threw a real exception
				// 2. the method invoked was not passed the arguments it
				// expected and has become 'stale'

				// In the first case we should not retry, in the second case we
				// should see if there is a
				// better suited method.

				// To determine which situation it is, the AccessException will
				// contain a cause.
				// If the cause is an InvocationTargetException, a user
				// exception was thrown inside the constructor.
				// Otherwise the constructor could not be invoked.
				if (ae.getCause() instanceof InvocationTargetException) {
					// User exception was the root cause - exit now
					Throwable rootCause = ae.getCause().getCause();
					if (rootCause instanceof RuntimeException) {
						throw (RuntimeException) rootCause;
					} else {
						String typename = (String) this.children[0].getValueInternal(state).getValue();
						throw new SpelEvaluationException(getStartPosition(), rootCause, SpelMessage.CONSTRUCTOR_INVOCATION_PROBLEM, typename, Tools.formatMethodForMessage(
								"", argumentTypes));
					}
				}

				// at this point we know it wasn't a user problem so worth a
				// retry if a better candidate can be found
				this.cachedExecutor = null;
			}
		}

		// either there was no accessor or it no longer exists
		String typename = (String) this.children[0].getValueInternal(state).getValue();
		executorToUse = findExecutorForConstructor(typename, argumentTypes, state);
		try {
			this.cachedExecutor = executorToUse;
			return executorToUse.execute(state.getEvaluationContext(), arguments);
		} catch (AccessException ae) {
			throw new SpelEvaluationException(getStartPosition(), ae, SpelMessage.CONSTRUCTOR_INVOCATION_PROBLEM, typename, Tools.formatMethodForMessage("", argumentTypes));

		}
	}

	private ConstructorExecutor findExecutorForConstructor(String typename, List<TypeDescriptor> argumentTypes, ExpressionState state) throws SpelEvaluationException {

		EvaluationContext eContext = state.getEvaluationContext();
		List<ConstructorResolver> cResolvers = eContext.getConstructorResolvers();
		if (cResolvers != null) {
			for (ConstructorResolver ctorResolver : cResolvers) {
				try {
					ConstructorExecutor cEx = ctorResolver.resolve(state.getEvaluationContext(), typename, argumentTypes);
					if (cEx != null) {
						return cEx;
					}
				} catch (AccessException ex) {
					throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.CONSTRUCTOR_INVOCATION_PROBLEM, typename, Tools.formatMethodForMessage("",
							argumentTypes));
				}
			}
		}
		throw new SpelEvaluationException(getStartPosition(), SpelMessage.CONSTRUCTOR_NOT_FOUND, typename, Tools.formatMethodForMessage("", argumentTypes));
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder();
		sb.append("new ");

		int index = 0;
		sb.append(getChild(index++).toStringAST());
		sb.append("(");
		for (int i = index; i < getChildCount(); i++) {
			if (i > index)
				sb.append(",");
			sb.append(getChild(i).toStringAST());
		}
		sb.append(")");
		return sb.toString();
	}

	private TypedValue createArray(ExpressionState state) throws EvaluationException {
		// First child gives us the array type which will either be a primitive
		// or reference type
		Object intendedArrayType = getChild(0).getValue(state);
		if (!(intendedArrayType instanceof String)) {
			throw new SpelEvaluationException(getChild(0).getStartPosition(), SpelMessage.TYPE_NAME_EXPECTED_FOR_ARRAY_CONSTRUCTION,
					Tools.formatClassNameForMessage(intendedArrayType.getClass()));
		}
		String type = (String) intendedArrayType;
		Class<?> componentType;
		TypeCode arrayTypeCode = TypeCode.forName(type);
		if (arrayTypeCode == TypeCode.OBJECT) {
			componentType = state.findType(type);
		} else {
			componentType = arrayTypeCode.getType();
		}
		Object newArray;
		if (!hasInitializer()) {
			// Confirm all dimensions were specified (for example [3][][5] is
			// missing the 2nd dimension)
			for (SpelNodeImpl dimension : this.dimensions) {
				if (dimension == null) {
					throw new SpelEvaluationException(getStartPosition(), SpelMessage.MISSING_ARRAY_DIMENSION);
				}
			}
			TypeConverter typeConverter = state.getEvaluationContext().getTypeConverter();

			// Shortcut for 1 dimensional
			if (this.dimensions.length == 1) {
				TypedValue o = this.dimensions[0].getTypedValue(state);
				int arraySize = ExpressionUtils.toInt(typeConverter, o);
				newArray = Array.newInstance(componentType, arraySize);
			} else {
				// Multi-dimensional - hold onto your hat!
				int[] dims = new int[this.dimensions.length];
				for (int d = 0; d < this.dimensions.length; d++) {
					TypedValue o = this.dimensions[d].getTypedValue(state);
					dims[d] = ExpressionUtils.toInt(typeConverter, o);
				}
				newArray = Array.newInstance(componentType, dims);
			}
		} else {
			// There is an initializer
			if (this.dimensions.length > 1) {
				// There is an initializer but this is a multi-dimensional array
				// (e.g. new int[][]{{1,2},{3,4}}) - this
				// is not currently supported
				throw new SpelEvaluationException(getStartPosition(), SpelMessage.MULTIDIM_ARRAY_INITIALIZER_NOT_SUPPORTED);
			}
			TypeConverter typeConverter = state.getEvaluationContext().getTypeConverter();
			InlineList initializer = (InlineList) getChild(1);
			// If a dimension was specified, check it matches the initializer
			// length
			if (this.dimensions[0] != null) {
				TypedValue dValue = this.dimensions[0].getTypedValue(state);
				int i = ExpressionUtils.toInt(typeConverter, dValue);
				if (i != initializer.getChildCount()) {
					throw new SpelEvaluationException(getStartPosition(), SpelMessage.INITIALIZER_LENGTH_INCORRECT);
				}
			}
			// Build the array and populate it
			int arraySize = initializer.getChildCount();
			newArray = Array.newInstance(componentType, arraySize);
			if (arrayTypeCode == TypeCode.OBJECT) {
				populateReferenceTypeArray(state, newArray, typeConverter, initializer, componentType);
			} else if (arrayTypeCode == TypeCode.INT) {
				populateIntArray(state, newArray, typeConverter, initializer);
			} else if (arrayTypeCode == TypeCode.BOOLEAN) {
				populateBooleanArray(state, newArray, typeConverter, initializer);
			} else if (arrayTypeCode == TypeCode.CHAR) {
				populateCharArray(state, newArray, typeConverter, initializer);
			} else if (arrayTypeCode == TypeCode.LONG) {
				populateLongArray(state, newArray, typeConverter, initializer);
			} else if (arrayTypeCode == TypeCode.SHORT) {
				populateShortArray(state, newArray, typeConverter, initializer);
			} else if (arrayTypeCode == TypeCode.DOUBLE) {
				populateDoubleArray(state, newArray, typeConverter, initializer);
			} else if (arrayTypeCode == TypeCode.FLOAT) {
				populateFloatArray(state, newArray, typeConverter, initializer);
			} else if (arrayTypeCode == TypeCode.BYTE) {
				populateByteArray(state, newArray, typeConverter, initializer);
			} else {
				throw new IllegalStateException(arrayTypeCode.name());
			}
		}
		return new TypedValue(newArray);
	}

	private void populateReferenceTypeArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer, Class<?> componentType) {
		TypeDescriptor toTypeDescriptor = TypeDescriptor.valueOf(componentType);
		Object[] newObjectArray = (Object[]) newArray;
		for (int i = 0; i < newObjectArray.length; i++) {
			SpelNode elementNode = initializer.getChild(i);
			Object arrayEntry = elementNode.getValue(state);
			newObjectArray[i] = typeConverter.convertValue(arrayEntry, TypeDescriptor.forObject(arrayEntry), toTypeDescriptor);
		}
	}

	private void populateByteArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
		byte[] newByteArray = (byte[]) newArray;
		for (int i = 0; i < newByteArray.length; i++) {
			TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
			newByteArray[i] = ExpressionUtils.toByte(typeConverter, typedValue);
		}
	}

	private void populateFloatArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
		float[] newFloatArray = (float[]) newArray;
		for (int i = 0; i < newFloatArray.length; i++) {
			TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
			newFloatArray[i] = ExpressionUtils.toFloat(typeConverter, typedValue);
		}
	}

	private void populateDoubleArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
		double[] newDoubleArray = (double[]) newArray;
		for (int i = 0; i < newDoubleArray.length; i++) {
			TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
			newDoubleArray[i] = ExpressionUtils.toDouble(typeConverter, typedValue);
		}
	}

	private void populateShortArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
		short[] newShortArray = (short[]) newArray;
		for (int i = 0; i < newShortArray.length; i++) {
			TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
			newShortArray[i] = ExpressionUtils.toShort(typeConverter, typedValue);
		}
	}

	private void populateLongArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
		long[] newLongArray = (long[]) newArray;
		for (int i = 0; i < newLongArray.length; i++) {
			TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
			newLongArray[i] = ExpressionUtils.toLong(typeConverter, typedValue);
		}
	}

	private void populateCharArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
		char[] newCharArray = (char[]) newArray;
		for (int i = 0; i < newCharArray.length; i++) {
			TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
			newCharArray[i] = ExpressionUtils.toChar(typeConverter, typedValue);
		}
	}

	private void populateBooleanArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
		boolean[] newBooleanArray = (boolean[]) newArray;
		for (int i = 0; i < newBooleanArray.length; i++) {
			TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
			newBooleanArray[i] = ExpressionUtils.toBoolean(typeConverter, typedValue);
		}
	}

	private void populateIntArray(ExpressionState state, Object newArray, TypeConverter typeConverter, InlineList initializer) {
		int[] newIntArray = (int[]) newArray;
		for (int i = 0; i < newIntArray.length; i++) {
			TypedValue typedValue = initializer.getChild(i).getTypedValue(state);
			newIntArray[i] = ExpressionUtils.toInt(typeConverter, typedValue);
		}
	}

	private boolean hasInitializer() {
		return getChildCount() > 1;
	}
}