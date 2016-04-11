package com.platform.cubism.expression;

public class TemplateParserContext implements ParserContext {
	private final String expressionPrefix;
	private final String expressionSuffix;

	public TemplateParserContext() {
		this("#{", "}");
	}

	public TemplateParserContext(String expressionPrefix, String expressionSuffix) {
		this.expressionPrefix = expressionPrefix;
		this.expressionSuffix = expressionSuffix;
	}

	public final boolean isTemplate() {
		return true;
	}

	public final String getExpressionPrefix() {
		return this.expressionPrefix;
	}

	public final String getExpressionSuffix() {
		return this.expressionSuffix;
	}
}