package com.platform.cubism.jdbc;

public interface TokenHandler {
	String handleToken(String before, String content, String after, String openToken, String closeToken);

	String processQuestionMark(String text, String after, int start, int end, StringBuilder sb);

	String processNullValue(String text, String after, int start, int end, StringBuilder sb);

	String processEmptyValue(String text, String after, int start, int end, StringBuilder sb);
}
