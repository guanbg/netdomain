package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.TypedValue;

/**
 * Represents a dot separated sequence of strings that indicate a package
 * qualified type reference.
 * <p>
 * Example: "java.lang.String" as in the expression
 * "new java.lang.String('hello')"
 */
public class QualifiedIdentifier extends SpelNodeImpl {
	private TypedValue value;

	public QualifiedIdentifier(int pos, SpelNodeImpl... operands) {
		super(pos, operands);
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		// Cache the concatenation of child identifiers
		if (this.value == null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < getChildCount(); i++) {
				Object value = children[i].getValueInternal(state).getValue();
				if (i > 0 && !value.toString().startsWith("$")) {
					sb.append(".");
				}
				sb.append(value);
			}
			this.value = new TypedValue(sb.toString());
		}
		return this.value;
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder();
		if (this.value != null) {
			sb.append(this.value.getValue());
		} else {
			for (int i = 0; i < getChildCount(); i++) {
				if (i > 0) {
					sb.append(".");
				}
				sb.append(getChild(i).toStringAST());
			}
		}
		return sb.toString();
	}
}