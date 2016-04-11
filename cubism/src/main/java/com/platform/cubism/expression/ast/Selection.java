package com.platform.cubism.expression.ast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
 * Represents selection over a map or collection. For example:
 * {1,2,3,4,5,6,7,8,9,10}.?{#isEven(#this) == 'y'} returns [2, 4, 6, 8, 10]
 * 
 * <p>
 * Basically a subset of the input data is returned based on the evaluation of
 * the expression supplied as selection criteria.
 */
public class Selection extends SpelNodeImpl {
	public final static int ALL = 0; // ?[]
	public final static int FIRST = 1; // ^[]
	public final static int LAST = 2; // $[]

	private final int variant;
	private final boolean nullSafe;

	public Selection(boolean nullSafe, int variant, int pos, SpelNodeImpl expression) {
		super(pos, expression);
		this.nullSafe = nullSafe;
		this.variant = variant;
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		TypedValue op = state.getActiveContextObject();
		Object operand = op.getValue();

		SpelNodeImpl selectionCriteria = children[0];
		if (operand instanceof Map) {
			Map<?, ?> mapdata = (Map<?, ?>) operand;
			Map<Object, Object> result = new HashMap<Object, Object>();
			Object lastKey = null;
			for (Map.Entry<?, ?> entry : mapdata.entrySet()) {
				try {
					TypedValue kvpair = new TypedValue(entry);
					state.pushActiveContextObject(kvpair);
					Object o = selectionCriteria.getValueInternal(state).getValue();
					if (o instanceof Boolean) {
						if (((Boolean) o).booleanValue() == true) {
							if (variant == FIRST) {
								result.put(entry.getKey(), entry.getValue());
								return new TypedValue(result);
							}
							result.put(entry.getKey(), entry.getValue());
							lastKey = entry.getKey();
						}
					} else {
						throw new SpelEvaluationException(selectionCriteria.getStartPosition(), SpelMessage.RESULT_OF_SELECTION_CRITERIA_IS_NOT_BOOLEAN);// ,selectionCriteria.stringifyAST());
					}
				} finally {
					state.popActiveContextObject();
				}
			}
			if ((variant == FIRST || variant == LAST) && result.size() == 0) {
				return new TypedValue(null);
			}
			if (variant == LAST) {
				Map<Object, Object> resultMap = new HashMap<Object, Object>();
				Object lastValue = result.get(lastKey);
				resultMap.put(lastKey, lastValue);
				return new TypedValue(resultMap);
			}
			return new TypedValue(result);
		} else if ((operand instanceof Collection) || ReflectionUtils.isArray(operand)) {
			List<Object> data = new ArrayList<Object>();
			Collection<?> c = (operand instanceof Collection) ? (Collection<?>) operand : Arrays.asList(Tools.toObjectArray(operand));
			data.addAll(c);
			List<Object> result = new ArrayList<Object>();
			int idx = 0;
			for (Object element : data) {
				try {
					state.pushActiveContextObject(new TypedValue(element));
					state.enterScope("index", idx);
					Object o = selectionCriteria.getValueInternal(state).getValue();
					if (o instanceof Boolean) {
						if (((Boolean) o).booleanValue() == true) {
							if (variant == FIRST) {
								return new TypedValue(element);
							}
							result.add(element);
						}
					} else {
						throw new SpelEvaluationException(selectionCriteria.getStartPosition(), SpelMessage.RESULT_OF_SELECTION_CRITERIA_IS_NOT_BOOLEAN);// ,selectionCriteria.stringifyAST());
					}
					idx++;
				} finally {
					state.exitScope();
					state.popActiveContextObject();
				}
			}
			if ((variant == FIRST || variant == LAST) && result.size() == 0) {
				return TypedValue.NULL;
			}
			if (variant == LAST) {
				return new TypedValue(result.get(result.size() - 1));
			}
			if (operand instanceof Collection) {
				return new TypedValue(result);
			} else {
				Class<?> elementType = ReflectionUtils.resolvePrimitiveIfNecessary(op.getTypeDescriptor().getElementTypeDescriptor().getType());
				Object resultArray = Array.newInstance(elementType, result.size());
				System.arraycopy(result.toArray(), 0, resultArray, 0, result.size());
				return new TypedValue(resultArray);
			}
		} else {
			if (operand == null) {
				if (nullSafe) {
					return TypedValue.NULL;
				} else {
					throw new SpelEvaluationException(getStartPosition(), SpelMessage.INVALID_TYPE_FOR_SELECTION, "null");
				}
			} else {
				throw new SpelEvaluationException(getStartPosition(), SpelMessage.INVALID_TYPE_FOR_SELECTION, operand.getClass().getName());
			}
		}
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder();
		switch (variant) {
		case ALL:
			sb.append("?[");
			break;
		case FIRST:
			sb.append("^[");
			break;
		case LAST:
			sb.append("$[");
			break;
		}
		return sb.append(getChild(0).toStringAST()).append("]").toString();
	}
}