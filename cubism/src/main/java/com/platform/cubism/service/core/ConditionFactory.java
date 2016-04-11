package com.platform.cubism.service.core;

import com.platform.cubism.base.Json;
import com.platform.cubism.expression.Expression;
import com.platform.cubism.expression.SpelExpressionParser;

public class ConditionFactory {
	public static boolean isTrue(String condition, Json json) {
		SpelExpressionParser parser = new SpelExpressionParser();
		Expression expr = parser.parseExpression(condition);
		Object value = expr.getValue(json);
		if (value == null) {
			return false;
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		}

		return true;
	}
	
	public static Object getValue(String expr, Json json){
		SpelExpressionParser parser = new SpelExpressionParser();
		Expression expression = parser.parseExpression(expr);
		return expression.getValue(json);		
	}
	
	public static String getStrValue(String expr, Json json){
		Object obj = getValue(expr,json);
		if(obj == null){
			return "";
		}
		return obj.toString();
	}
}