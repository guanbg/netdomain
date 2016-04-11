package com.platform.cubism.expression;

import com.platform.cubism.util.Assert;

public class SpelExpressionParser extends TemplateAwareExpressionParser {
	private final SpelParserConfiguration configuration;

	public SpelExpressionParser() {
		this.configuration = new SpelParserConfiguration(false, false);
	}

	public SpelExpressionParser(SpelParserConfiguration configuration) {
		Assert.notNull(configuration, "SpelParserConfiguration must not be null");
		this.configuration = configuration;
	}

	@Override
	protected SpelExpression doParseExpression(String expressionString, ParserContext context) throws ParseException {
		return new InternalSpelExpressionParser(this.configuration).doParseExpression(expressionString, context);
	}

	public SpelExpression parseRaw(String expressionString) throws ParseException {
		return doParseExpression(expressionString, null);
	}
}