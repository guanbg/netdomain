package com.platform.cubism.expression;

import com.platform.cubism.cvt.TypeDescriptor;
import com.platform.cubism.expression.ast.SpelNodeImpl;
import com.platform.cubism.util.Assert;

public class SpelExpression implements Expression {
	private final String expression;
	private final SpelNodeImpl ast;
	private final SpelParserConfiguration configuration;
	private EvaluationContext defaultContext;

	public SpelExpression(String expression, SpelNodeImpl ast, SpelParserConfiguration configuration) {
		this.expression = expression;
		this.ast = ast;
		this.configuration = configuration;
	}

	public Object getValue() throws EvaluationException {
		ExpressionState expressionState = new ExpressionState(getEvaluationContext(), configuration);
		return ast.getValue(expressionState);
	}

	public Object getValue(Object rootObject) throws EvaluationException {
		ExpressionState expressionState = new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), configuration);
		return ast.getValue(expressionState);
	}

	public <T> T getValue(Class<T> expectedResultType) throws EvaluationException {
		ExpressionState expressionState = new ExpressionState(getEvaluationContext(), configuration);
		TypedValue typedResultValue = ast.getTypedValue(expressionState);
		return ExpressionUtils.convertTypedValue(expressionState.getEvaluationContext(), typedResultValue, expectedResultType);
	}

	public <T> T getValue(Object rootObject, Class<T> expectedResultType) throws EvaluationException {
		ExpressionState expressionState = new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), configuration);
		TypedValue typedResultValue = ast.getTypedValue(expressionState);
		return ExpressionUtils.convertTypedValue(expressionState.getEvaluationContext(), typedResultValue, expectedResultType);
	}

	public Object getValue(EvaluationContext context) throws EvaluationException {
		Assert.notNull(context, "The EvaluationContext is required");
		return ast.getValue(new ExpressionState(context, configuration));
	}

	public Object getValue(EvaluationContext context, Object rootObject) throws EvaluationException {
		Assert.notNull(context, "The EvaluationContext is required");
		return ast.getValue(new ExpressionState(context, toTypedValue(rootObject), configuration));
	}

	public <T> T getValue(EvaluationContext context, Class<T> expectedResultType) throws EvaluationException {
		TypedValue typedResultValue = ast.getTypedValue(new ExpressionState(context, configuration));
		return ExpressionUtils.convertTypedValue(context, typedResultValue, expectedResultType);
	}

	public <T> T getValue(EvaluationContext context, Object rootObject, Class<T> expectedResultType) throws EvaluationException {
		TypedValue typedResultValue = ast.getTypedValue(new ExpressionState(context, toTypedValue(rootObject), configuration));
		return ExpressionUtils.convertTypedValue(context, typedResultValue, expectedResultType);
	}

	public Class<?> getValueType() throws EvaluationException {
		return getValueType(getEvaluationContext());
	}

	public Class<?> getValueType(Object rootObject) throws EvaluationException {
		return getValueType(getEvaluationContext(), rootObject);
	}

	public Class<?> getValueType(EvaluationContext context) throws EvaluationException {
		Assert.notNull(context, "The EvaluationContext is required");
		ExpressionState eState = new ExpressionState(context, configuration);
		TypeDescriptor typeDescriptor = ast.getValueInternal(eState).getTypeDescriptor();
		return typeDescriptor != null ? typeDescriptor.getType() : null;
	}

	public Class<?> getValueType(EvaluationContext context, Object rootObject) throws EvaluationException {
		ExpressionState eState = new ExpressionState(context, toTypedValue(rootObject), configuration);
		TypeDescriptor typeDescriptor = ast.getValueInternal(eState).getTypeDescriptor();
		return typeDescriptor != null ? typeDescriptor.getType() : null;
	}

	public TypeDescriptor getValueTypeDescriptor() throws EvaluationException {
		return getValueTypeDescriptor(getEvaluationContext());
	}

	public TypeDescriptor getValueTypeDescriptor(Object rootObject) throws EvaluationException {
		ExpressionState eState = new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), configuration);
		return ast.getValueInternal(eState).getTypeDescriptor();
	}

	public TypeDescriptor getValueTypeDescriptor(EvaluationContext context) throws EvaluationException {
		Assert.notNull(context, "The EvaluationContext is required");
		ExpressionState eState = new ExpressionState(context, configuration);
		return ast.getValueInternal(eState).getTypeDescriptor();
	}

	public TypeDescriptor getValueTypeDescriptor(EvaluationContext context, Object rootObject) throws EvaluationException {
		Assert.notNull(context, "The EvaluationContext is required");
		ExpressionState eState = new ExpressionState(context, toTypedValue(rootObject), configuration);
		return ast.getValueInternal(eState).getTypeDescriptor();
	}

	public String getExpressionString() {
		return expression;
	}

	public boolean isWritable(EvaluationContext context) throws EvaluationException {
		Assert.notNull(context, "The EvaluationContext is required");
		return ast.isWritable(new ExpressionState(context, configuration));
	}

	public boolean isWritable(Object rootObject) throws EvaluationException {
		return ast.isWritable(new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), configuration));
	}

	public boolean isWritable(EvaluationContext context, Object rootObject) throws EvaluationException {
		Assert.notNull(context, "The EvaluationContext is required");
		return ast.isWritable(new ExpressionState(context, toTypedValue(rootObject), configuration));
	}

	public void setValue(EvaluationContext context, Object value) throws EvaluationException {
		Assert.notNull(context, "The EvaluationContext is required");
		ast.setValue(new ExpressionState(context, configuration), value);
	}

	public void setValue(Object rootObject, Object value) throws EvaluationException {
		ast.setValue(new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), configuration), value);
	}

	public void setValue(EvaluationContext context, Object rootObject, Object value) throws EvaluationException {
		Assert.notNull(context, "The EvaluationContext is required");
		ast.setValue(new ExpressionState(context, toTypedValue(rootObject), configuration), value);
	}

	public SpelNode getAST() {
		return ast;
	}

	public String toStringAST() {
		return ast.toStringAST();
	}

	public EvaluationContext getEvaluationContext() {
		if (defaultContext == null) {
			defaultContext = new StandardEvaluationContext();
		}
		return defaultContext;
	}

	public void setEvaluationContext(EvaluationContext context) {
		this.defaultContext = context;
	}

	private TypedValue toTypedValue(Object object) {
		if (object == null) {
			return TypedValue.NULL;
		} else {
			return new TypedValue(object);
		}
	}
}