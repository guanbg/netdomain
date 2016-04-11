package com.platform.cubism.expression;

import com.platform.cubism.cvt.TypeDescriptor;

public class LiteralExpression implements Expression {
	private final String literalValue;

	public LiteralExpression(String literalValue) {
		this.literalValue = literalValue;
	}

	public final String getExpressionString() {
		return this.literalValue;
	}

	public String getValue() {
		return this.literalValue;
	}

	public String getValue(EvaluationContext context) {
		return this.literalValue;
	}

	public String getValue(Object rootObject) {
		return this.literalValue;
	}

	public Class<?> getValueType(EvaluationContext context) {
		return String.class;
	}

	public TypeDescriptor getValueTypeDescriptor(EvaluationContext context) {
		return TypeDescriptor.valueOf(String.class);
	}

	public TypeDescriptor getValueTypeDescriptor() {
		return TypeDescriptor.valueOf(String.class);
	}

	public void setValue(EvaluationContext context, Object value) throws EvaluationException {
		throw new EvaluationException(literalValue, "Cannot call setValue() on a LiteralExpression");
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

	public Class<?> getValueType() {
		return String.class;
	}

	public <T> T getValue(Object rootObject, Class<T> desiredResultType) throws EvaluationException {
		Object value = getValue(rootObject);
		return ExpressionUtils.convert(null, value, desiredResultType);
	}

	public String getValue(EvaluationContext context, Object rootObject) throws EvaluationException {
		return this.literalValue;
	}

	public <T> T getValue(EvaluationContext context, Object rootObject, Class<T> desiredResultType) throws EvaluationException {
		Object value = getValue(context, rootObject);
		return ExpressionUtils.convert(null, value, desiredResultType);
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
		throw new EvaluationException(literalValue, "Cannot call setValue() on a LiteralExpression");
	}

	public boolean isWritable(Object rootObject) throws EvaluationException {
		return false;
	}

	public void setValue(Object rootObject, Object value) throws EvaluationException {
		throw new EvaluationException(literalValue, "Cannot call setValue() on a LiteralExpression");
	}
}