package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.TypedValue;

/**
 * Represents a reference to a type, for example "T(String)" or
 * "T(com.somewhere.Foo)"
 */
public class TypeReference extends SpelNodeImpl {
	public TypeReference(int pos, SpelNodeImpl qualifiedId) {
		super(pos, qualifiedId);
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		String typename = (String) children[0].getValueInternal(state).getValue();
		if (typename.indexOf(".") == -1 && Character.isLowerCase(typename.charAt(0))) {
			TypeCode tc = TypeCode.valueOf(typename.toUpperCase());
			if (tc != TypeCode.OBJECT) {
				// it is a primitive type
				return new TypedValue(tc.getType());
			}
		}
		return new TypedValue(state.findType(typename));
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder();
		sb.append("T(");
		sb.append(getChild(0).toStringAST());
		sb.append(")");
		return sb.toString();
	}
}