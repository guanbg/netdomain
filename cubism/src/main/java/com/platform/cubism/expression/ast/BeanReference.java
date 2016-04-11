package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.AccessException;
import com.platform.cubism.expression.BeanResolver;
import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.SpelMessage;
import com.platform.cubism.expression.TypedValue;

public class BeanReference extends SpelNodeImpl {
	private String beanname;

	public BeanReference(int pos, String beanname) {
		super(pos);
		this.beanname = beanname;
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		BeanResolver beanResolver = state.getEvaluationContext().getBeanResolver();
		if (beanResolver == null) {
			throw new SpelEvaluationException(getStartPosition(), SpelMessage.NO_BEAN_RESOLVER_REGISTERED, beanname);
		}
		try {
			TypedValue bean = new TypedValue(beanResolver.resolve(state.getEvaluationContext(), beanname));
			return bean;
		} catch (AccessException ae) {
			throw new SpelEvaluationException(getStartPosition(), ae, SpelMessage.EXCEPTION_DURING_BEAN_RESOLUTION, beanname, ae.getMessage());
		}
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder();
		sb.append("@");
		if (beanname.indexOf('.') == -1) {
			sb.append(beanname);
		} else {
			sb.append("'").append(beanname).append("'");
		}
		return sb.toString();
	}
}