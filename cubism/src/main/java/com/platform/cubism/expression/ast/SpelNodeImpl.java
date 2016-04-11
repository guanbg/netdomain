package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.ExpressionUtils;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.SpelMessage;
import com.platform.cubism.expression.SpelNode;
import com.platform.cubism.expression.StandardEvaluationContext;
import com.platform.cubism.expression.TypedValue;
import com.platform.cubism.util.Assert;

public abstract class SpelNodeImpl implements SpelNode {
	private static SpelNodeImpl[] NO_CHILDREN = new SpelNodeImpl[0];
	protected int pos; // start = top 16bits, end = bottom 16bits
	protected SpelNodeImpl[] children = SpelNodeImpl.NO_CHILDREN;
	private SpelNodeImpl parent;

	public SpelNodeImpl(int pos, SpelNodeImpl... operands) {
		this.pos = pos;
		// pos combines start and end so can never be zero because tokens cannot
		// be zero length
		Assert.isTrue(pos != 0);
		if (operands != null && operands.length > 0) {
			this.children = operands;
			for (SpelNodeImpl childnode : operands) {
				childnode.parent = this;
			}
		}
	}

	protected SpelNodeImpl getPreviousChild() {
		SpelNodeImpl result = null;
		if (parent != null) {
			for (SpelNodeImpl child : parent.children) {
				if (this == child)
					break;
				result = child;
			}
		}
		return result;
	}

	protected boolean nextChildIs(Class<?>... clazzes) {
		if (parent != null) {
			SpelNodeImpl[] peers = parent.children;
			for (int i = 0, max = peers.length; i < max; i++) {
				if (peers[i] == this) {
					if ((i + 1) >= max) {
						return false;
					} else {
						Class<?> clazz = peers[i + 1].getClass();
						for (Class<?> desiredClazz : clazzes) {
							if (clazz.equals(desiredClazz)) {
								return true;
							}
						}
						return false;
					}
				}
			}
		}
		return false;
	}

	public final Object getValue(ExpressionState expressionState) throws EvaluationException {
		if (expressionState != null) {
			return getValueInternal(expressionState).getValue();
		} else {
			// configuration not set - does that matter?
			return getValue(new ExpressionState(new StandardEvaluationContext()));
		}
	}

	public final TypedValue getTypedValue(ExpressionState expressionState) throws EvaluationException {
		if (expressionState != null) {
			return getValueInternal(expressionState);
		} else {
			// configuration not set - does that matter?
			return getTypedValue(new ExpressionState(new StandardEvaluationContext()));
		}
	}

	// by default Ast nodes are not writable
	public boolean isWritable(ExpressionState expressionState) throws EvaluationException {
		return false;
	}

	public void setValue(ExpressionState expressionState, Object newValue) throws EvaluationException {
		throw new SpelEvaluationException(getStartPosition(), SpelMessage.SETVALUE_NOT_SUPPORTED, getClass());
	}

	public SpelNode getChild(int index) {
		return children[index];
	}

	public int getChildCount() {
		return children.length;
	}

	public Class<?> getObjectClass(Object obj) {
		if (obj == null) {
			return null;
		}
		return (obj instanceof Class ? ((Class<?>) obj) : obj.getClass());
	}

	@SuppressWarnings("unchecked")
	protected final <T> T getValue(ExpressionState state, Class<T> desiredReturnType) throws EvaluationException {
		Object result = getValueInternal(state).getValue();
		if (result != null && desiredReturnType != null) {
			Class<?> resultType = result.getClass();
			if (desiredReturnType.isAssignableFrom(resultType)) {
				return (T) result;
			}
			// Attempt conversion to the requested type, may throw an exception
			return ExpressionUtils.convert(state.getEvaluationContext(), result, desiredReturnType);
		}
		return (T) result;
	}

	public abstract TypedValue getValueInternal(ExpressionState expressionState) throws EvaluationException;

	public abstract String toStringAST();

	public int getStartPosition() {
		return (pos >> 16);
	}

	public int getEndPosition() {
		return (pos & 0xffff);
	}
}