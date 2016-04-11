package com.platform.cubism.base;

import java.util.regex.Pattern;

public class JsonTokener {
	private int myIndex;
	private String mySource;
	private Pattern emptyStrucPat = Pattern.compile("^\\{(\\s*null\\s*[,]*)*\\}|^\\{\\s*\\}", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
	private Pattern emptyArrayPat = Pattern.compile("^\\[(\\s*null\\s*[,]*)*\\]|^\\[\\s*\\]", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

	public JsonTokener(String s) {
		this.myIndex = 0;
		if (s != null) {
			s = s.trim();
		} else {
			s = "";
		}
		if (s.length() > 0) {
			char first = s.charAt(0);
			char last = s.charAt(s.length() - 1);
			if (first == '[' && last != ']') {
				throw syntaxError("Found starting '[' but missing ']' at the end.");
			}
			if (first == '{' && last != '}') {
				throw syntaxError("Found starting '{' but missing '}' at the end.");
			}
		}
		this.mySource = s;
	}

	public boolean isNull() {
		if (length() <= 0) {
			return true;
		}

		String str = this.mySource.substring(this.myIndex);
		if (emptyStrucPat.matcher(str).find()) {
			nextTo('}');
			next();
			return true;
		}
		if (emptyArrayPat.matcher(str).find()) {
			nextTo(']');
			next();
			return true;
		}
		return false;
	}

	public int length() {
		if (this.mySource == null) {
			return 0;
		}
		return this.mySource.length();
	}

	public boolean more() {
		return this.myIndex < this.mySource.length();
	}

	public void back() {
		if (this.myIndex > 0) {
			this.myIndex -= 1;
		}
	}

	public char peek() {
		if (more()) {
			char c = this.mySource.charAt(this.myIndex);
			return c;
		}
		return 0;
	}

	public char next() {
		if (more()) {
			char c = this.mySource.charAt(this.myIndex);
			this.myIndex += 1;
			return c;
		}
		return 0;
	}

	public char next(char c) {
		char n = next();
		if (n != c) {
			throw syntaxError("Expected '" + c + "' and instead saw '" + n + "'.");
		}
		return n;
	}

	public String next(int n) {
		int i = this.myIndex;
		int j = i + n;
		if (j >= this.mySource.length()) {
			throw syntaxError("Substring bounds error");
		}
		this.myIndex += n;
		return this.mySource.substring(i, j);
	}

	public String nextTo(char d) {
		StringBuffer sb = new StringBuffer();
		for (;;) {
			char c = next();
			if (c == d || c == 0) {
				if (c != 0) {
					back();
				}
				return sb.toString().trim();
			}
			sb.append(c);
		}
	}

	public String nextTo(String delimiters) {
		char c;
		StringBuffer sb = new StringBuffer();
		for (;;) {
			c = next();
			if (delimiters.indexOf(c) >= 0 || c == 0) {
				if (c != 0) {
					back();
				}
				return sb.toString().trim();
			}
			sb.append(c);
		}
	}

	public char nextClean() {
		for (;;) {
			char c = next();
			if (c == '/') {
				switch (next()) {
				case '/':
					do {
						c = next();
					} while (c != 0);
					break;
				case '*':
					for (;;) {
						c = next();
						if (c == 0) {
							throw syntaxError("Unclosed comment.");
						}
						if (c == '*') {
							if (next() == '/') {
								break;
							}
							back();
						}
					}
					break;
				default:
					back();
					return '/';
				}
			} else if (c == '#') {
				do {
					c = next();
				} while (c != 0);
			} else if (c == 0 || c > ' ') {
				return c;
			}
		}
	}

	public String nextString(char quote) {
		char c;
		StringBuffer sb = new StringBuffer();
		for (;;) {
			c = next();
			switch (c) {
			case 0:
				throw syntaxError("Unterminated string");
			case '\n':
				sb.append('\n');
				break;
			case '\r':
				//throw syntaxError("Unterminated string");
				sb.append('\r');
				break;
			case '\\':
				c = next();
				switch (c) {
				case 'b':
					sb.append('\b');
					break;
				case 't':
					sb.append('\t');
					break;
				case 'n':
					sb.append('\n');
					break;
				case 'f':
					sb.append('\f');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 'u':
					sb.append((char) Integer.parseInt(next(4), 16));
					break;
				case 'x':
					sb.append((char) Integer.parseInt(next(2), 16));
					break;
				default:
					sb.append(c);
				}
				break;
			default:
				if (c == quote) {
					return sb.toString();
				}
				sb.append(c);
			}
		}
	}

	public String nextValue() {
		char c = nextClean();

		StringBuffer sb = new StringBuffer();
		while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {// 此处是否需要考虑转意字符
			sb.append(c);
			c = next();
		}
		back();

		String string = sb.toString().trim();
		if (string.equals("")) {
			throw syntaxError("Missing value");
		}
		return string;
	}

	public JsonException syntaxError(String message) {
		return new JsonException(message + toString());
	}

	public String toString() {
		return " at character " + this.myIndex + " of " + this.mySource;
	}
}
