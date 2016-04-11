package com.platform.cubism.page.parser;

public interface ParamHandle {
	String handleToken(String before, String openToken, String content, String closeToken, String after);
}
