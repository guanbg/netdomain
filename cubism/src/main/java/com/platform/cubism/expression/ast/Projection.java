package com.platform.cubism.expression.ast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.platform.cubism.cvt.Tools;
import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.SpelMessage;
import com.platform.cubism.expression.TypedValue;
import com.platform.cubism.util.ReflectionUtils;

/**
 * Represents projection, where a given operation is performed on all elements
 * in some input sequence, returning a new sequence of the same size. For
 * example: "{1,2,3,4,5,6,7,8,9,10}.!{#isEven(#this)}" returns
 * "[n, y, n, y, n, y, n, y, n, y]"
 */
public class Projection extends SpelNodeImpl {
	private final boolean nullSafe;

	public Projection(boolean nullSafe, int pos, SpelNodeImpl expression) {
		super(pos, expression);
		this.nullSafe = nullSafe;
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		TypedValue op = state.getActiveContextObject();

		Object operand = op.getValue();
		boolean operandIsArray = ReflectionUtils.isArray(operand);
		// TypeDescriptor operandTypeDescriptor = op.getTypeDescriptor();

		// When the input is a map, we push a special context object on the
		// stack
		// before calling the specified operation. This special context object
		// has two fields 'key' and 'value' that refer to the map entries key
		// and value, and they can be referenced in the operation
		// eg. {'a':'y','b':'n'}.!{value=='y'?key:null}" == ['a', null]
		if (operand instanceof Map) {
			Map<?, ?> mapData = (Map<?, ?>) operand;
			List<Object> result = new ArrayList<Object>();
			for (Map.Entry<?, ?> entry : mapData.entrySet()) {
				try {
					state.pushActiveContextObject(new TypedValue(entry));
					result.add(this.children[0].getValueInternal(state).getValue());
				} finally {
					state.popActiveContextObject();
				}
			}
			return new TypedValue(result);
		} else if (operand instanceof Collection || operandIsArray) {
			Collection<?> data = (operand instanceof Collection ? (Collection<?>) operand : Arrays.asList(Tools.toObjectArray(operand)));
			List<Object> result = new ArrayList<Object>();
			int idx = 0;
			Class<?> arrayElementType = null;
			for (Object element : data) {
				try {
					state.pushActiveContextObject(new TypedValue(element));
					state.enterScope("index", idx);
					Object value = children[0].getValueInternal(state).getValue();
					if (value != null && operandIsArray) {
						arrayElementType = determineCommonType(arrayElementType, value.getClass());
					}
					result.add(value);
				} finally {
					state.exitScope();
					state.popActiveContextObject();
				}
				idx++;
			}
			if (operandIsArray) {
				if (arrayElementType == null) {
					arrayElementType = Object.class;
				}
				Object resultArray = Array.newInstance(arrayElementType, result.size());
				System.arraycopy(result.toArray(), 0, resultArray, 0, result.size());
				return new TypedValue(resultArray);
			}
			return new TypedValue(result);
		} else {
			if (operand == null) {
				if (this.nullSafe) {
					return TypedValue.NULL;
				} else {
					throw new SpelEvaluationException(getStartPosition(), SpelMessage.PROJECTION_NOT_SUPPORTED_ON_TYPE, "null");
				}
			} else {
				throw new SpelEvaluationException(getStartPosition(), SpelMessage.PROJECTION_NOT_SUPPORTED_ON_TYPE, operand.getClass().getName());
			}
		}
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder();
		return sb.append("![").append(getChild(0).toStringAST()).append("]").toString();
	}

	private Class<?> determineCommonType(Class<?> oldType, Class<?> newType) {
		if (oldType == null) {
			return newType;
		}
		if (oldType.isAssignableFrom(newType)) {
			return oldType;
		}
		Class<?> nextType = newType;
		while (nextType != Object.class) {
			if (nextType.isAssignableFrom(oldType)) {
				return nextType;
			}
			nextType = nextType.getSuperclass();
		}
		Class<?>[] interfaces = ReflectionUtils.getAllInterfacesForClass(newType);
		for (Class<?> nextInterface : interfaces) {
			if (nextInterface.isAssignableFrom(oldType)) {
				return nextInterface;
			}
		}
		return Object.class;
	}
}