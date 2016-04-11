package com.platform.cubism.expression;

public interface ExpressionParser {
	Expression parseExpression(String expressionString) throws ParseException;

	Expression parseExpression(String expressionString, ParserContext context) throws ParseException;
}