package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.TypedValue;

/**
 * Represents a variable reference, eg. #someVar. Note this is different to a
 * *local* variable like $someVar
 */
public class VariableReference extends SpelNodeImpl {
	private final static String THIS = "this"; // currently active context
												// object
	private final static String ROOT = "root"; // root context object
	private final String name;

	public VariableReference(String variableName, int pos) {
		super(pos);
		name = variableName;
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws SpelEvaluationException {
		if (this.name.equals(THIS)) {
			return state.getActiveContextObject();
		}
		if (this.name.equals(ROOT)) {
			return state.getRootContextObject();
		}
		TypedValue result = state.lookupVariable(this.name);
		// a null value will mean either the value was null or the variable was
		// not found
		return result;
	}

	@Override
	public void setValue(ExpressionState state, Object value) throws SpelEvaluationException {
		state.setVariable(this.name, value);
	}

	@Override
	public String toStringAST() {
		return "#" + this.name;
	}

	@Override
	public boolean isWritable(ExpressionState expressionState) throws SpelEvaluationException {
		return !(this.name.equals(THIS) || this.name.equals(ROOT));
	}
}