package com.platform.cubism.expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.platform.cubism.cvt.TypeDescriptor;

public class ExpressionState {
	private final EvaluationContext relatedContext;
	private Stack<VariableScope> variableScopes;
	private Stack<TypedValue> contextObjects;
	private final TypedValue rootObject;
	private SpelParserConfiguration configuration;

	public ExpressionState(EvaluationContext context) {
		this.relatedContext = context;
		this.rootObject = context.getRootObject();
	}

	public ExpressionState(EvaluationContext context, SpelParserConfiguration configuration) {
		this.relatedContext = context;
		this.configuration = configuration;
		this.rootObject = context.getRootObject();
	}

	public ExpressionState(EvaluationContext context, TypedValue rootObject) {
		this.relatedContext = context;
		this.rootObject = rootObject;
	}

	public ExpressionState(EvaluationContext context, TypedValue rootObject, SpelParserConfiguration configuration) {
		this.relatedContext = context;
		this.configuration = configuration;
		this.rootObject = rootObject;
	}

	private void ensureVariableScopesInitialized() {
		if (this.variableScopes == null) {
			this.variableScopes = new Stack<VariableScope>();
			// top level empty variable scope
			this.variableScopes.add(new VariableScope());
		}
	}

	public TypedValue getActiveContextObject() {
		if (this.contextObjects == null || this.contextObjects.isEmpty()) {
			return this.rootObject;
		}

		return this.contextObjects.peek();
	}

	public void pushActiveContextObject(TypedValue obj) {
		if (this.contextObjects == null) {
			this.contextObjects = new Stack<TypedValue>();
		}
		this.contextObjects.push(obj);
	}

	public void popActiveContextObject() {
		if (this.contextObjects == null) {
			this.contextObjects = new Stack<TypedValue>();
		}
		this.contextObjects.pop();
	}

	public TypedValue getRootContextObject() {
		return this.rootObject;
	}

	public void setVariable(String name, Object value) {
		this.relatedContext.setVariable(name, value);
	}

	public TypedValue lookupVariable(String name) {
		Object value = this.relatedContext.lookupVariable(name);
		if (value == null) {
			return TypedValue.NULL;
		} else {
			return new TypedValue(value);
		}
	}

	public TypeComparator getTypeComparator() {
		return this.relatedContext.getTypeComparator();
	}

	public Class<?> findType(String type) throws EvaluationException {
		return this.relatedContext.getTypeLocator().findType(type);
	}

	public Object convertValue(Object value, TypeDescriptor targetTypeDescriptor) throws EvaluationException {
		return this.relatedContext.getTypeConverter().convertValue(value, TypeDescriptor.forObject(value), targetTypeDescriptor);
	}

	public Object convertValue(TypedValue value, TypeDescriptor targetTypeDescriptor) throws EvaluationException {
		Object val = value.getValue();
		return this.relatedContext.getTypeConverter().convertValue(val, TypeDescriptor.forObject(val), targetTypeDescriptor);
	}

	public void enterScope(Map<String, Object> argMap) {
		ensureVariableScopesInitialized();
		this.variableScopes.push(new VariableScope(argMap));
	}

	public void enterScope(String name, Object value) {
		ensureVariableScopesInitialized();
		this.variableScopes.push(new VariableScope(name, value));
	}

	public void exitScope() {
		ensureVariableScopesInitialized();
		this.variableScopes.pop();
	}

	public void setLocalVariable(String name, Object value) {
		ensureVariableScopesInitialized();
		this.variableScopes.peek().setVariable(name, value);
	}

	public Object lookupLocalVariable(String name) {
		ensureVariableScopesInitialized();
		int scopeNumber = this.variableScopes.size() - 1;
		for (int i = scopeNumber; i >= 0; i--) {
			if (this.variableScopes.get(i).definesVariable(name)) {
				return this.variableScopes.get(i).lookupVariable(name);
			}
		}
		return null;
	}

	public TypedValue operate(Operation op, Object left, Object right) throws EvaluationException {
		OperatorOverloader overloader = this.relatedContext.getOperatorOverloader();
		if (overloader.overridesOperation(op, left, right)) {
			Object returnValue = overloader.operate(op, left, right);
			return new TypedValue(returnValue);
		} else {
			String leftType = (left == null ? "null" : left.getClass().getName());
			String rightType = (right == null ? "null" : right.getClass().getName());
			throw new SpelEvaluationException(SpelMessage.OPERATOR_NOT_SUPPORTED_BETWEEN_TYPES, op, leftType, rightType);
		}
	}

	public List<PropertyAccessor> getPropertyAccessors() {
		return this.relatedContext.getPropertyAccessors();
	}

	public EvaluationContext getEvaluationContext() {
		return this.relatedContext;
	}

	public SpelParserConfiguration getConfiguration() {
		return this.configuration;
	}

	private static class VariableScope {

		private final Map<String, Object> vars = new HashMap<String, Object>();

		public VariableScope() {
		}

		public VariableScope(Map<String, Object> arguments) {
			if (arguments != null) {
				this.vars.putAll(arguments);
			}
		}

		public VariableScope(String name, Object value) {
			this.vars.put(name, value);
		}

		public Object lookupVariable(String name) {
			return this.vars.get(name);
		}

		public void setVariable(String name, Object value) {
			this.vars.put(name, value);
		}

		public boolean definesVariable(String name) {
			return this.vars.containsKey(name);
		}
	}
}