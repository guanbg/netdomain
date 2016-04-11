package com.platform.cubism.expression;

public interface ParserContext {
	boolean isTemplate();

	String getExpressionPrefix();

	String getExpressionSuffix();

	public static final ParserContext TEMPLATE_EXPRESSION = new ParserContext() {
		public String getExpressionPrefix() {
			return "#{";
		}

		public String getExpressionSuffix() {
			return "}";
		}

		public boolean isTemplate() {
			return true;
		}
	};
}