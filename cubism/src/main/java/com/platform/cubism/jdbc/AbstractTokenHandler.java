package com.platform.cubism.jdbc;

import java.util.regex.Pattern;

public abstract class AbstractTokenHandler implements TokenHandler {
	private static final Pattern logic2 = Pattern.compile(".*(\\!=|<>|>=|<=|>|<|=|like|,|\\(|\\))\\s*$", Pattern.CASE_INSENSITIVE);
	private final static char quote = '\'';

	public String processQuestionMark(String text, String after, int start, int end, StringBuilder sb) {
		if(end >= after.length()){
			sb.append(after.substring(0, start));
			sb.append("?");
			return "";
		}
		
		
		if (after.charAt(start - 1) == after.charAt(end) && after.charAt(end) == quote) {
			sb.append(after.substring(0, start - 1));
			sb.append("?");
			return after.substring(end + 1);
		} else {
			sb.append(after.substring(0, start));
			sb.append("?");
			return after.substring(end);
		}
	}

	public String processNullValue(String text, String after, int start, int end, StringBuilder sb) {
		if(end >= after.length()){
			return processEmptyValue(text, after, start, end, sb);
		}
		
		
		if (after.charAt(start - 1) == after.charAt(end) && after.charAt(end) == quote) {
			sb.append(after.substring(0, start - 1));
			sb.append("NULL");//SQL不区分大小写，此处必须为大写，否则trim将会删除
			return after.substring(end + 1);
		}

		return processEmptyValue(text, after, start, end, sb);
	}

	public String processEmptyValue(String text, String after, int start, int end, StringBuilder sb) {
		if(end >= after.length()){
			if(logic2.matcher(after.substring(0, start)).find()){
				String before = after.substring(0, start);
				sb.append(before).append("''");
				return "";
			}
			else{
				String before = after.substring(0, start);
				sb.append(before);
				return "";
			}
		}
		
		
		if (after.charAt(start - 1) == after.charAt(end) && after.charAt(end) == quote) {
			String before = after.substring(0, start);
			sb.append(before);
			return after.substring(end);
		}
		else if(logic2.matcher(after.substring(0, start)).find()){
			String before = after.substring(0, start);
			sb.append(before).append("''");
			return after.substring(end);
		}
		else{
			String before = after.substring(0, start);
			sb.append(before);
			return after.substring(end);
		}
	}
}
