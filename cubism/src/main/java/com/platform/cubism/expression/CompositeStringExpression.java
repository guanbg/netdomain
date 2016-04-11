package com.platform.cubism.expression;

import com.platform.cubism.cvt.TypeDescriptor;

public class CompositeStringExpression implements Expression {
	private final String expressionString;
	private final Expression[] expressions;

	public CompositeStringExpression(String expressionString, Expression[] expressions) {
		this.expressionString = expressionString;
		this.expressions = expressions;
	}

	public final String getExpressionString() {
		return this.expressionString;
	}

	public String getValue() throws EvaluationException {
		StringBuilder sb = new StringBuilder();
		for (Expression expression : this.expressions) {
			String value = expression.getValue(String.class);
			if (value != null) {
				sb.append(value);
			}
		}
		return sb.toString();
	}

	public String getValue(Object rootObject) throws EvaluationException {
		StringBuilder sb = new StringBuilder();
		for (Expression expression : this.expressions) {
			String value = expression.getValue(rootObject, String.class);
			if (value != null) {
				sb.append(value);
			}
		}
		return sb.toString();
	}

	public String getValue(EvaluationContext context) throws EvaluationException {
		StringBuilder sb = new StringBuilder();
		for (Expression expression : this.expressions) {
			String value = expression.getValue(context, String.class);
			if (value != null) {
				sb.append(value);
			}
		}
		return sb.toString();
	}

	public String getValue(EvaluationContext context, Object rootObject) throws EvaluationException {
		StringBuilder sb = new StringBuilder();
		for (Expression expression : this.expressions) {
			String value = expression.getValue(context, rootObject, String.class);
			if (value != null) {
				sb.append(value);
			}
		}
		return sb.toString();
	}

	public Class<?> getValueType(EvaluationContext context) {
		return String.class;
	}

	public Class<?> getValueType() {
		return String.class;
	}

	public TypeDescriptor getValueTypeDescriptor(EvaluationContext context) {
		return TypeDescriptor.valueOf(String.class);
	}

	public TypeDescriptor getValueTypeDescriptor() {
		return TypeDescriptor.valueOf(String.class);
	}

	public void setValue(EvaluationContext context, Object value) throws EvaluationException {
		throw new EvaluationException(this.expressionString, "Cannot call setValue on a composite expression");
	}

	public <T> T getValue(EvaluationContext context, Class<T> expectedResultType) throws EvaluationException {
		Object value = getValue(context);
		return ExpressionUtils.convert(context, value, expectedResultType);
	}

	public <T> T getValue(Class<T> expectedResultType) throws EvaluationException {
		Object value = getValue();
		return ExpressionUtils.convert(null, value, expectedResultType);
	}

	public boolean isWritable(EvaluationContext context) {
		return false;
	}

	public Expression[] getExpressions() {
		return expressions;
	}

	public <T> T getValue(Object rootObject, Class<T> desiredResultType) throws EvaluationException {
		Object value = getValue(rootObject);
		return ExpressionUtils.convert(null, value, desiredResultType);
	}

	public <T> T getValue(EvaluationContext context, Object rootObject, Class<T> desiredResultType) throws EvaluationException {
		Object value = getValue(context, rootObject);
		return ExpressionUtils.convert(context, value, desiredResultType);
	}

	public Class<?> getValueType(Object rootObject) throws EvaluationException {
		return String.class;
	}

	public Class<?> getValueType(EvaluationContext context, Object rootObject) throws EvaluationException {
		return String.class;
	}

	public TypeDescriptor getValueTypeDescriptor(Object rootObject) throws EvaluationException {
		return TypeDescriptor.valueOf(String.class);
	}

	public TypeDescriptor getValueTypeDescriptor(EvaluationContext context, Object rootObject) throws EvaluationException {
		return TypeDescriptor.valueOf(String.class);
	}

	public boolean isWritable(EvaluationContext context, Object rootObject) throws EvaluationException {
		return false;
	}

	public void setValue(EvaluationContext context, Object rootObject, Object value) throws EvaluationException {
		throw new EvaluationException(this.expressionString, "Cannot call setValue on a composite expression");
	}

	public boolean isWritable(Object rootObject) throws EvaluationException {
		return false;
	}

	public void setValue(Object rootObject, Object value) throws EvaluationException {
		throw new EvaluationException(this.expressionString, "Cannot call setValue on a composite expression");
	}
}