package com.platform.cubism.expression.ast;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.platform.cubism.cvt.TypeDescriptor;
import com.platform.cubism.expression.AccessException;
import com.platform.cubism.expression.EvaluationContext;
import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.PropertyAccessor;
import com.platform.cubism.expression.ReflectivePropertyAccessor;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.SpelMessage;
import com.platform.cubism.expression.TypedValue;

/**
 * An Indexer can index into some proceeding structure to access a particular
 * piece of it. Supported structures are: strings/collections
 * (lists/sets)/arrays
 */
public class Indexer extends SpelNodeImpl {
	private String cachedReadName;
	private Class<?> cachedReadTargetType;
	private PropertyAccessor cachedReadAccessor;
	private String cachedWriteName;
	private Class<?> cachedWriteTargetType;
	private PropertyAccessor cachedWriteAccessor;

	public Indexer(int pos, SpelNodeImpl expr) {
		super(pos, expr);
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		TypedValue context = state.getActiveContextObject();
		Object targetObject = context.getValue();
		TypeDescriptor targetObjectTypeDescriptor = context.getTypeDescriptor();
		TypedValue indexValue = null;
		Object index = null;

		// This first part of the if clause prevents a 'double dereference' of
		// the property (SPR-5847)
		if (targetObject instanceof Map && (children[0] instanceof PropertyOrFieldReference)) {
			PropertyOrFieldReference reference = (PropertyOrFieldReference) children[0];
			index = reference.getName();
			indexValue = new TypedValue(index);
		} else {
			try {
				state.pushActiveContextObject(state.getRootContextObject());
				indexValue = children[0].getValueInternal(state);
				index = indexValue.getValue();
			} finally {
				state.popActiveContextObject();
			}
		}

		// Indexing into a Map
		if (targetObject instanceof Map) {
			Object key = index;
			if (targetObjectTypeDescriptor.getMapKeyTypeDescriptor() != null) {
				key = state.convertValue(key, targetObjectTypeDescriptor.getMapKeyTypeDescriptor());
			}
			Object value = ((Map<?, ?>) targetObject).get(key);
			return new TypedValue(value, targetObjectTypeDescriptor.mapValueTypeDescriptor(value));
		}

		if (targetObject == null) {
			throw new SpelEvaluationException(getStartPosition(), SpelMessage.CANNOT_INDEX_INTO_NULL_VALUE);
		}

		// if the object is something that looks indexable by an integer,
		// attempt to treat the index value as a number
		if (targetObject instanceof Collection || targetObject.getClass().isArray() || targetObject instanceof String) {
			int idx = (Integer) state.convertValue(index, TypeDescriptor.valueOf(Integer.class));
			if (targetObject.getClass().isArray()) {
				Object arrayElement = accessArrayElement(targetObject, idx);
				return new TypedValue(arrayElement, targetObjectTypeDescriptor.elementTypeDescriptor(arrayElement));
			} else if (targetObject instanceof Collection) {
				@SuppressWarnings("unchecked")
				Collection<Object> c = (Collection<Object>) targetObject;
				if (idx >= c.size()) {
					if (!growCollection(state, targetObjectTypeDescriptor, idx, c)) {
						throw new SpelEvaluationException(getStartPosition(), SpelMessage.COLLECTION_INDEX_OUT_OF_BOUNDS, c.size(), idx);
					}
				}
				int pos = 0;
				for (Object o : c) {
					if (pos == idx) {
						return new TypedValue(o, targetObjectTypeDescriptor.elementTypeDescriptor(o));
					}
					pos++;
				}
			} else if (targetObject instanceof String) {
				String ctxString = (String) targetObject;
				if (idx >= ctxString.length()) {
					throw new SpelEvaluationException(getStartPosition(), SpelMessage.STRING_INDEX_OUT_OF_BOUNDS, ctxString.length(), idx);
				}
				return new TypedValue(String.valueOf(ctxString.charAt(idx)));
			}
		}

		if (indexValue.getTypeDescriptor().getType() == String.class) {
			Class<?> targetObjectRuntimeClass = getObjectClass(targetObject);
			String name = (String) indexValue.getValue();
			EvaluationContext eContext = state.getEvaluationContext();

			try {
				if (cachedReadName != null && cachedReadName.equals(name) && cachedReadTargetType != null && cachedReadTargetType.equals(targetObjectRuntimeClass)) {
					// it is OK to use the cached accessor
					return cachedReadAccessor.read(eContext, targetObject, name);
				}

				List<PropertyAccessor> accessorsToTry = AstUtils.getPropertyAccessorsToTry(targetObjectRuntimeClass, state);

				if (accessorsToTry != null) {
					for (PropertyAccessor accessor : accessorsToTry) {
						if (accessor.canRead(eContext, targetObject, name)) {
							if (accessor instanceof ReflectivePropertyAccessor) {
								accessor = ((ReflectivePropertyAccessor) accessor).createOptimalAccessor(eContext, targetObject, name);
							}
							this.cachedReadAccessor = accessor;
							this.cachedReadName = name;
							this.cachedReadTargetType = targetObjectRuntimeClass;
							return accessor.read(eContext, targetObject, name);
						}
					}
				}
			} catch (AccessException e) {
				throw new SpelEvaluationException(getStartPosition(), e, SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, targetObjectTypeDescriptor.toString());
			}
		}

		throw new SpelEvaluationException(getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, targetObjectTypeDescriptor.toString());
	}

	@Override
	public boolean isWritable(ExpressionState expressionState) throws SpelEvaluationException {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(ExpressionState state, Object newValue) throws EvaluationException {
		TypedValue contextObject = state.getActiveContextObject();
		Object targetObject = contextObject.getValue();
		TypeDescriptor targetObjectTypeDescriptor = contextObject.getTypeDescriptor();
		TypedValue index = children[0].getValueInternal(state);

		if (targetObject == null) {
			throw new SpelEvaluationException(SpelMessage.CANNOT_INDEX_INTO_NULL_VALUE);
		}
		// Indexing into a Map
		if (targetObject instanceof Map) {
			Map<Object, Object> map = (Map<Object, Object>) targetObject;
			Object key = index.getValue();
			if (targetObjectTypeDescriptor.getMapKeyTypeDescriptor() != null) {
				key = state.convertValue(index, targetObjectTypeDescriptor.getMapKeyTypeDescriptor());
			}
			if (targetObjectTypeDescriptor.getMapValueTypeDescriptor() != null) {
				newValue = state.convertValue(newValue, targetObjectTypeDescriptor.getMapValueTypeDescriptor());
			}
			map.put(key, newValue);
			return;
		}

		if (targetObjectTypeDescriptor.isArray()) {
			int idx = (Integer) state.convertValue(index, TypeDescriptor.valueOf(Integer.class));
			setArrayElement(state, contextObject.getValue(), idx, newValue, targetObjectTypeDescriptor.getElementTypeDescriptor().getType());
			return;
		} else if (targetObject instanceof Collection) {
			int idx = (Integer) state.convertValue(index, TypeDescriptor.valueOf(Integer.class));
			Collection<Object> c = (Collection<Object>) targetObject;
			if (idx >= c.size()) {
				if (!growCollection(state, targetObjectTypeDescriptor, idx, c)) {
					throw new SpelEvaluationException(getStartPosition(), SpelMessage.COLLECTION_INDEX_OUT_OF_BOUNDS, c.size(), idx);
				}
			}
			if (targetObject instanceof List) {
				List<Object> list = (List<Object>) targetObject;
				if (targetObjectTypeDescriptor.getElementTypeDescriptor() != null) {
					newValue = state.convertValue(newValue, targetObjectTypeDescriptor.getElementTypeDescriptor());
				}
				list.set(idx, newValue);
				return;
			} else {
				throw new SpelEvaluationException(getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, targetObjectTypeDescriptor.toString());
			}
		}

		if (index.getTypeDescriptor().getType() == String.class) {
			Class<?> contextObjectClass = getObjectClass(contextObject.getValue());
			String name = (String) index.getValue();
			EvaluationContext eContext = state.getEvaluationContext();
			try {
				if (cachedWriteName != null && cachedWriteName.equals(name) && cachedWriteTargetType != null && cachedWriteTargetType.equals(contextObjectClass)) {
					// it is OK to use the cached accessor
					cachedWriteAccessor.write(eContext, targetObject, name, newValue);
					return;
				}

				List<PropertyAccessor> accessorsToTry = AstUtils.getPropertyAccessorsToTry(contextObjectClass, state);
				if (accessorsToTry != null) {
					for (PropertyAccessor accessor : accessorsToTry) {
						if (accessor.canWrite(eContext, contextObject.getValue(), name)) {
							this.cachedWriteName = name;
							this.cachedWriteTargetType = contextObjectClass;
							this.cachedWriteAccessor = accessor;
							accessor.write(eContext, contextObject.getValue(), name, newValue);
							return;
						}
					}
				}
			} catch (AccessException ae) {
				throw new SpelEvaluationException(getStartPosition(), ae, SpelMessage.EXCEPTION_DURING_PROPERTY_WRITE, name, ae.getMessage());
			}

		}

		throw new SpelEvaluationException(getStartPosition(), SpelMessage.INDEXING_NOT_SUPPORTED_FOR_TYPE, targetObjectTypeDescriptor.toString());
	}

	private boolean growCollection(ExpressionState state, TypeDescriptor targetType, int index, Collection<Object> collection) {
		if (state.getConfiguration().isAutoGrowCollections()) {
			if (targetType.getElementTypeDescriptor() == null) {
				throw new SpelEvaluationException(getStartPosition(), SpelMessage.UNABLE_TO_GROW_COLLECTION_UNKNOWN_ELEMENT_TYPE);
			}
			TypeDescriptor elementType = targetType.getElementTypeDescriptor();
			Object newCollectionElement = null;
			try {
				int newElements = index - collection.size();
				while (newElements > 0) {
					collection.add(elementType.getType().newInstance());
					newElements--;
				}
				newCollectionElement = elementType.getType().newInstance();
			} catch (Exception ex) {
				throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.UNABLE_TO_GROW_COLLECTION);
			}
			collection.add(newCollectionElement);
			return true;
		}
		return false;
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < getChildCount(); i++) {
			if (i > 0)
				sb.append(",");
			sb.append(getChild(i).toStringAST());
		}
		sb.append("]");
		return sb.toString();
	}

	private void setArrayElement(ExpressionState state, Object ctx, int idx, Object newValue, Class<?> clazz) throws EvaluationException {
		Class<?> arrayComponentType = clazz;
		if (arrayComponentType == Integer.TYPE) {
			int[] array = (int[]) ctx;
			checkAccess(array.length, idx);
			array[idx] = (Integer) state.convertValue(newValue, TypeDescriptor.valueOf(Integer.class));
		} else if (arrayComponentType == Boolean.TYPE) {
			boolean[] array = (boolean[]) ctx;
			checkAccess(array.length, idx);
			array[idx] = (Boolean) state.convertValue(newValue, TypeDescriptor.valueOf(Boolean.class));
		} else if (arrayComponentType == Character.TYPE) {
			char[] array = (char[]) ctx;
			checkAccess(array.length, idx);
			array[idx] = (Character) state.convertValue(newValue, TypeDescriptor.valueOf(Character.class));
		} else if (arrayComponentType == Long.TYPE) {
			long[] array = (long[]) ctx;
			checkAccess(array.length, idx);
			array[idx] = (Long) state.convertValue(newValue, TypeDescriptor.valueOf(Long.class));
		} else if (arrayComponentType == Short.TYPE) {
			short[] array = (short[]) ctx;
			checkAccess(array.length, idx);
			array[idx] = (Short) state.convertValue(newValue, TypeDescriptor.valueOf(Short.class));
		} else if (arrayComponentType == Double.TYPE) {
			double[] array = (double[]) ctx;
			checkAccess(array.length, idx);
			array[idx] = (Double) state.convertValue(newValue, TypeDescriptor.valueOf(Double.class));
		} else if (arrayComponentType == Float.TYPE) {
			float[] array = (float[]) ctx;
			checkAccess(array.length, idx);
			array[idx] = (Float) state.convertValue(newValue, TypeDescriptor.valueOf(Float.class));
		} else if (arrayComponentType == Byte.TYPE) {
			byte[] array = (byte[]) ctx;
			checkAccess(array.length, idx);
			array[idx] = (Byte) state.convertValue(newValue, TypeDescriptor.valueOf(Byte.class));
		} else {
			Object[] array = (Object[]) ctx;
			checkAccess(array.length, idx);
			array[idx] = state.convertValue(newValue, TypeDescriptor.valueOf(clazz));
		}
	}

	private Object accessArrayElement(Object ctx, int idx) throws SpelEvaluationException {
		Class<?> arrayComponentType = ctx.getClass().getComponentType();
		if (arrayComponentType == Integer.TYPE) {
			int[] array = (int[]) ctx;
			checkAccess(array.length, idx);
			return array[idx];
		} else if (arrayComponentType == Boolean.TYPE) {
			boolean[] array = (boolean[]) ctx;
			checkAccess(array.length, idx);
			return array[idx];
		} else if (arrayComponentType == Character.TYPE) {
			char[] array = (char[]) ctx;
			checkAccess(array.length, idx);
			return array[idx];
		} else if (arrayComponentType == Long.TYPE) {
			long[] array = (long[]) ctx;
			checkAccess(array.length, idx);
			return array[idx];
		} else if (arrayComponentType == Short.TYPE) {
			short[] array = (short[]) ctx;
			checkAccess(array.length, idx);
			return array[idx];
		} else if (arrayComponentType == Double.TYPE) {
			double[] array = (double[]) ctx;
			checkAccess(array.length, idx);
			return array[idx];
		} else if (arrayComponentType == Float.TYPE) {
			float[] array = (float[]) ctx;
			checkAccess(array.length, idx);
			return array[idx];
		} else if (arrayComponentType == Byte.TYPE) {
			byte[] array = (byte[]) ctx;
			checkAccess(array.length, idx);
			return array[idx];
		} else {
			Object[] array = (Object[]) ctx;
			checkAccess(array.length, idx);
			return array[idx];
		}
	}

	private void checkAccess(int arrayLength, int index) throws SpelEvaluationException {
		if (index > arrayLength) {
			throw new SpelEvaluationException(getStartPosition(), SpelMessage.ARRAY_INDEX_OUT_OF_BOUNDS, arrayLength, index);
		}
	}
}