package com.platform.cubism.jdbc;

import java.util.HashMap;
import java.util.Map;

public class TokenParser {
	private final String[][] opencloseToken;
	private final TokenHandler handler;
	private String currOpenToken;
	private String currCloseToken;

	public TokenParser(TokenHandler handler, String[]... token) {
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
	
	private String innerParse(String text,Map<String,String> params) {
		if (!checkToken(text)) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		if (text != null) {
			String after = text;
			int start = getOpenTokenIndex(after);
			int end = getCloseTokenIndex(after);
			while (start > -1) {
				if (end > start) {
					builder.append(after.substring(0, start));
					builder.append(params.get(after.substring(start, end+currCloseToken.length())));
					after = after.substring(end + currCloseToken.length());
				} else if (end > -1) {
					String before = after.substring(0, end);
					builder.append(before);
					builder.append(currCloseToken);
					after = after.substring(end + currCloseToken.length());
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

	public String parse2(String text) {
		if (!checkToken(text)) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		Map<String,String> params = new HashMap<String,String>();;
		if (text != null) {
			String after = text;
			int start = getOpenTokenIndex(after);
			int end = getCloseTokenIndex(after);
			while (start > -1) {
				int nxt = end + currCloseToken.length();				
				if (end > start) {
					String before = after.substring(0, start);
					String content = after.substring(start + currOpenToken.length(), end);
					String substitution = handler.handleToken(before, content, after.substring(nxt), currOpenToken, currCloseToken);
					if (substitution == null) {
						after = handler.processNullValue(text, after, start, nxt, builder);
					} else if ("".equals(substitution)) {
						after = handler.processEmptyValue(text, after, start, nxt, builder);
					} else if ("?".equals(substitution)) {
						after = handler.processQuestionMark(text, after, start, nxt, builder);
					}else {
						String p = after.substring(start,nxt);
						if(!params.containsKey(p)){
							params.put(p, substitution);
						}
						
						builder.append(before);
						builder.append(p);
						after = after.substring(nxt);
					}
				} else if (end > -1) {
					String before = after.substring(0, end);
					builder.append(before);
					builder.append(currCloseToken);
					after = after.substring(nxt);
				} else {
					break;
				}
				start = getOpenTokenIndex(after);
				end = getCloseTokenIndex(after);
			}
			builder.append(after);
		}
		
		String sql = SqlTrim.getInstance().trim(builder.toString());
		if(params.size() > 0){
			return innerParse(sql,params);
		}
		return sql;
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
					String substitution = handler.handleToken(before, content, after.substring(nxt), currOpenToken, currCloseToken);
					if (substitution == null) {
						after = handler.processNullValue(text, after, start, nxt, builder);
					} else if ("".equals(substitution)) {
						after = handler.processEmptyValue(text, after, start, nxt, builder);
					} else if ("?".equals(substitution)) {
						after = handler.processQuestionMark(text, after, start, nxt, builder);
					}else {
						builder.append(before);
						builder.append(substitution);
						after = after.substring(nxt);
					}
				} else if (end > -1) {
					String before = after.substring(0, end);
					builder.append(before);
					builder.append(currCloseToken);
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
