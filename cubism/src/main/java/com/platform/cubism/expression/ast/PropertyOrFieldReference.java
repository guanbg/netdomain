package com.platform.cubism.expression.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.platform.cubism.cvt.Tools;
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

public class PropertyOrFieldReference extends SpelNodeImpl {
	private final boolean nullSafe;
	private final String name;
	private volatile PropertyAccessor cachedReadAccessor;
	private volatile PropertyAccessor cachedWriteAccessor;

	public PropertyOrFieldReference(boolean nullSafe, String propertyOrFieldName, int pos) {
		super(pos);
		this.nullSafe = nullSafe;
		this.name = propertyOrFieldName;
	}

	public boolean isNullSafe() {
		return this.nullSafe;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		TypedValue result = readProperty(state, this.name);

		// Dynamically create the objects if the user has requested that
		// optional behaviour
		if (result.getValue() == null && state.getConfiguration().isAutoGrowNullReferences() && nextChildIs(Indexer.class, PropertyOrFieldReference.class)) {
			TypeDescriptor resultDescriptor = result.getTypeDescriptor();
			// Creating lists and maps
			if ((resultDescriptor.getType().equals(List.class) || resultDescriptor.getType().equals(Map.class))) {
				// Create a new collection or map ready for the indexer
				if (resultDescriptor.getType().equals(List.class)) {
					try {
						if (isWritable(state)) {
							List<?> newList = ArrayList.class.newInstance();
							writeProperty(state, this.name, newList);
							result = readProperty(state, this.name);
						}
					} catch (InstantiationException ex) {
						throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.UNABLE_TO_CREATE_LIST_FOR_INDEXING);
					} catch (IllegalAccessException ex) {
						throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.UNABLE_TO_CREATE_LIST_FOR_INDEXING);
					}
				} else {
					try {
						if (isWritable(state)) {
							Map<?, ?> newMap = HashMap.class.newInstance();
							writeProperty(state, name, newMap);
							result = readProperty(state, this.name);
						}
					} catch (InstantiationException ex) {
						throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.UNABLE_TO_CREATE_MAP_FOR_INDEXING);
					} catch (IllegalAccessException ex) {
						throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.UNABLE_TO_CREATE_MAP_FOR_INDEXING);
					}
				}
			} else {
				// 'simple' object
				try {
					if (isWritable(state)) {
						Object newObject = result.getTypeDescriptor().getType().newInstance();
						writeProperty(state, name, newObject);
						result = readProperty(state, this.name);
					}
				} catch (InstantiationException ex) {
					throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.UNABLE_TO_DYNAMICALLY_CREATE_OBJECT, result.getTypeDescriptor().getType());
				} catch (IllegalAccessException ex) {
					throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.UNABLE_TO_DYNAMICALLY_CREATE_OBJECT, result.getTypeDescriptor().getType());
				}
			}
		}
		return result;
	}

	@Override
	public void setValue(ExpressionState state, Object newValue) throws SpelEvaluationException {
		writeProperty(state, this.name, newValue);
	}

	@Override
	public boolean isWritable(ExpressionState state) throws SpelEvaluationException {
		return isWritableProperty(this.name, state);
	}

	@Override
	public String toStringAST() {
		return this.name;
	}

	private TypedValue readProperty(ExpressionState state, String name) throws EvaluationException {
		TypedValue contextObject = state.getActiveContextObject();
		Object targetObject = contextObject.getValue();

		if (targetObject == null && this.nullSafe) {
			return TypedValue.NULL;
		}

		PropertyAccessor accessorToUse = this.cachedReadAccessor;
		if (accessorToUse != null) {
			try {
				return accessorToUse.read(state.getEvaluationContext(), contextObject.getValue(), name);
			} catch (AccessException ae) {
				// this is OK - it may have gone stale due to a class change,
				// let's try to get a new one and call it before giving up
				this.cachedReadAccessor = null;
			}
		}

		Class<?> contextObjectClass = getObjectClass(contextObject.getValue());
		List<PropertyAccessor> accessorsToTry = getPropertyAccessorsToTry(contextObjectClass, state);
		EvaluationContext eContext = state.getEvaluationContext();

		// Go through the accessors that may be able to resolve it. If they are
		// a cacheable accessor then
		// get the accessor and use it. If they are not cacheable but report
		// they can read the property
		// then ask them to read it
		if (accessorsToTry != null) {
			try {
				for (PropertyAccessor accessor : accessorsToTry) {
					if (accessor.canRead(eContext, contextObject.getValue(), name)) {
						if (accessor instanceof ReflectivePropertyAccessor) {
							accessor = ((ReflectivePropertyAccessor) accessor).createOptimalAccessor(eContext, contextObject.getValue(), name);
						}
						this.cachedReadAccessor = accessor;
						return accessor.read(eContext, contextObject.getValue(), name);
					}
				}
			} catch (AccessException ae) {
				throw new SpelEvaluationException(ae, SpelMessage.EXCEPTION_DURING_PROPERTY_READ, name, ae.getMessage());
			}
		}
		if (contextObject.getValue() == null) {
			throw new SpelEvaluationException(SpelMessage.PROPERTY_OR_FIELD_NOT_READABLE_ON_NULL, name);
		} else {
			throw new SpelEvaluationException(getStartPosition(), SpelMessage.PROPERTY_OR_FIELD_NOT_READABLE, name, Tools.formatClassNameForMessage(contextObjectClass));
		}
	}

	private void writeProperty(ExpressionState state, String name, Object newValue) throws SpelEvaluationException {
		TypedValue contextObject = state.getActiveContextObject();
		EvaluationContext eContext = state.getEvaluationContext();

		if (contextObject.getValue() == null && nullSafe) {
			return;
		}

		PropertyAccessor accessorToUse = this.cachedWriteAccessor;
		if (accessorToUse != null) {
			try {
				accessorToUse.write(state.getEvaluationContext(), contextObject.getValue(), name, newValue);
				return;
			} catch (AccessException ae) {
				// this is OK - it may have gone stale due to a class change,
				// let's try to get a new one and call it before giving up
				this.cachedWriteAccessor = null;
			}
		}

		Class<?> contextObjectClass = getObjectClass(contextObject.getValue());

		List<PropertyAccessor> accessorsToTry = getPropertyAccessorsToTry(contextObjectClass, state);
		if (accessorsToTry != null) {
			try {
				for (PropertyAccessor accessor : accessorsToTry) {
					if (accessor.canWrite(eContext, contextObject.getValue(), name)) {
						this.cachedWriteAccessor = accessor;
						accessor.write(eContext, contextObject.getValue(), name, newValue);
						return;
					}
				}
			} catch (AccessException ae) {
				throw new SpelEvaluationException(getStartPosition(), ae, SpelMessage.EXCEPTION_DURING_PROPERTY_WRITE, name, ae.getMessage());
			}
		}
		if (contextObject.getValue() == null) {
			throw new SpelEvaluationException(getStartPosition(), SpelMessage.PROPERTY_OR_FIELD_NOT_WRITABLE_ON_NULL, name);
		} else {
			throw new SpelEvaluationException(getStartPosition(), SpelMessage.PROPERTY_OR_FIELD_NOT_WRITABLE, name, Tools.formatClassNameForMessage(contextObjectClass));
		}
	}

	public boolean isWritableProperty(String name, ExpressionState state) throws SpelEvaluationException {
		Object contextObject = state.getActiveContextObject().getValue();
		// TypeDescriptor td =
		// state.getActiveContextObject().getTypeDescriptor();
		EvaluationContext eContext = state.getEvaluationContext();
		List<PropertyAccessor> resolversToTry = getPropertyAccessorsToTry(getObjectClass(contextObject), state);
		if (resolversToTry != null) {
			for (PropertyAccessor pfResolver : resolversToTry) {
				try {
					if (pfResolver.canWrite(eContext, contextObject, name)) {
						return true;
					}
				} catch (AccessException ae) {
					// let others try
				}
			}
		}
		return false;
	}

	private List<PropertyAccessor> getPropertyAccessorsToTry(Class<?> targetType, ExpressionState state) {
		List<PropertyAccessor> specificAccessors = new ArrayList<PropertyAccessor>();
		List<PropertyAccessor> generalAccessors = new ArrayList<PropertyAccessor>();
		for (PropertyAccessor resolver : state.getPropertyAccessors()) {
			Class<?>[] targets = resolver.getSpecificTargetClasses();
			if (targets == null) { // generic resolver that says it can be used
									// for any type
				generalAccessors.add(resolver);
			} else {
				if (targetType != null) {
					for (Class<?> clazz : targets) {
						if (clazz == targetType) {
							specificAccessors.add(resolver);
							break;
						} else if (clazz.isAssignableFrom(targetType)) {
							generalAccessors.add(resolver);
						}
					}
				}
			}
		}
		List<PropertyAccessor> resolvers = new ArrayList<PropertyAccessor>();
		resolvers.addAll(specificAccessors);
		generalAccessors.removeAll(specificAccessors);
		resolvers.addAll(generalAccessors);
		return resolvers;
	}
}