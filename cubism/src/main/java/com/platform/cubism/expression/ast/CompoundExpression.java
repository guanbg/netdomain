package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.TypedValue;

public class CompoundExpression extends SpelNodeImpl {
	public CompoundExpression(int pos, SpelNodeImpl... expressionComponents) {
		super(pos, expressionComponents);
		if (expressionComponents.length < 2) {
			throw new IllegalStateException("Dont build compound expression less than one entry: " + expressionComponents.length);
		}
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		TypedValue result = null;
		SpelNodeImpl nextNode = null;
		try {
			nextNode = children[0];
			result = nextNode.getValueInternal(state);
			for (int i = 1; i < getChildCount(); i++) {
				try {
					state.pushActiveContextObject(result);
					nextNode = children[i];
					result = nextNode.getValueInternal(state);
				} finally {
					state.popActiveContextObject();
				}
			}
		} catch (SpelEvaluationException ee) {
			ee.setPosition(nextNode.getStartPosition());
			throw ee;
		}
		return result;
	}

	@Override
	public void setValue(ExpressionState state, Object value) throws EvaluationException {
		if (getChildCount() == 1) {
			getChild(0).setValue(state, value);
			return;
		}
		TypedValue ctx = children[0].getValueInternal(state);
		for (int i = 1; i < getChildCount() - 1; i++) {
			try {
				state.pushActiveContextObject(ctx);
				ctx = children[i].getValueInternal(state);
			} finally {
				state.popActiveContextObject();
			}
		}
		try {
			state.pushActiveContextObject(ctx);
			getChild(getChildCount() - 1).setValue(state, value);
		} finally {
			state.popActiveContextObject();
		}
	}

	@Override
	public boolean isWritable(ExpressionState state) throws EvaluationException {
		if (getChildCount() == 1) {
			return getChild(0).isWritable(state);
		}
		TypedValue ctx = children[0].getValueInternal(state);
		for (int i = 1; i < getChildCount() - 1; i++) {
			try {
				state.pushActiveContextObject(ctx);
				ctx = children[i].getValueInternal(state);
			} finally {
				state.popActiveContextObject();
			}
		}
		try {
			state.pushActiveContextObject(ctx);
			return getChild(getChildCount() - 1).isWritable(state);
		} finally {
			state.popActiveContextObject();
		}
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getChildCount(); i++) {
			if (i > 0) {
				sb.append(".");
			}
			sb.append(getChild(i).toStringAST());
		}
		return sb.toString();
	}
}