package com.platform.cubism.page.parser;

public class ParamParser {
	private final String[][] opencloseToken;
	private final ParamHandle handler;
	private String currOpenToken;
	private String currCloseToken;
	
	public ParamParser(ParamHandle handler, String[]... token) {
		this.opencloseToken = token;
		this.handler = handler;
	}
	
	private boolean checkToken(String text) {
		if (opencloseToken == null || handler == null || text == null || "".equals(text)) {
			return false;
		}

		for (String[] str : opencloseToken) {
			if (str == null || str.length < 2) {
				return false;
			}
			for (String s : str) {
				if (s == null || "".equals(s)) {
					return false;
				}
			}
		}
		return true;
	}

	private int getOpenTokenIndex(String text) {
		if (text == null || "".equals(text)) {
			currOpenToken = "";
			currCloseToken = "";
			return -1;
		}
		int idx = -1;
		int min = -1;
		String openToken, closeToken;
		for (String[] str : opencloseToken) {
			if (str == null || str.length < 2) {
				currOpenToken = "";
				currCloseToken = "";
				return -1;
			}
			openToken = str[0];
			closeToken = str[1];

			idx = text.indexOf(openToken);
			if ((idx > -1 && idx < min) || (min < 0)) {
				min = idx;
				currOpenToken = openToken;
				currCloseToken = closeToken;
			}
		}

		if (min < 0) {
			currOpenToken = "";
			currCloseToken = "";
		}

		return min;
	}

	private int getCloseTokenIndex(String text) {
		if (text == null || "".equals(text)) {
			return -1;
		}
		return text.indexOf(currCloseToken);
	}
	
	public String parse(String text) {
		if (!checkToken(text)) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		if (text != null) {
			String after = text;
			int start = getOpenTokenIndex(after);
			int end = getCloseTokenIndex(after);
			while (start > -1) {
				int nxt = end + currCloseToken.length();
				if (end > start) {
					String before = after.substring(0, start);
					String content = after.substring(start + currOpenToken.length(), end);
					String substitution = handler.handleToken(before, currOpenToken, content, currCloseToken, after.substring(nxt));
					builder.append(before);
					builder.append(substitution);
					after = after.substring(nxt);
				} else if (end > -1) {
					builder.append(after.substring(0, nxt));
					after = after.substring(nxt);
				} else {
					break;
				}
				start = getOpenTokenIndex(after);
				end = getCloseTokenIndex(after);
			}
			builder.append(after);
		}
		return builder.toString();
	}
}
