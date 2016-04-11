package com.platform.cubism.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.platform.cubism.cvt.DefaultConversionService;
import com.platform.cubism.expression.StandardTypeLocator;

public class StringUtils {
	private static final String FOLDER_SEPARATOR = "/";
	private static final String WINDOWS_FOLDER_SEPARATOR = "\\";
	private static final String TOP_PATH = "..";
	private static final String CURRENT_PATH = ".";
	private static DefaultConversionService converter;
	private static StandardTypeLocator locator;

	public static String convert(Object obj) {
		if (converter == null) {
			converter = new DefaultConversionService();
		}
		return converter.convert(obj, String.class);
	}

	public static <T> T convert(String str, Class<T> targetType) {
		if (converter == null) {
			converter = new DefaultConversionService();
		}
		return converter.convert(str, targetType);
	}

	public static Object convert(String str, String targetType) {
		if (locator == null) {
			locator = new StandardTypeLocator();
		}
		return converter.convert(str, locator.findType(targetType));
	}

	/*
	 * 返回指定个数的缩进;
	 */
	public static String getTabSpace(int cnt) {
		if (cnt < 0) {
			return "";
		}
		String tab = "  ";// \t
		StringBuilder sb = new StringBuilder("\n\r");
		for (int i = 0; i < cnt; i++) {
			sb.append(tab);
		}
		return sb.toString();
	}

	/*
	 * 去掉前后白符(回车换行空格等)及去掉前缀和后缀
	 */
	public static String trim(String str, String openToken, String closeToken) {
		if (!hasText(str)) {
			return null;
		}
		String s = str.trim();
		if (!hasText(openToken) && !hasText(closeToken)) {
			return s;
		} else if (!hasText(openToken)) {
			boolean yes = str.endsWith(closeToken);
			return yes ? str.substring(0, str.length() - closeToken.length()) : s;
		} else if (!hasText(closeToken)) {
			boolean yes = str.startsWith(openToken);
			return yes ? str.substring(openToken.length()) : s;
		}

		boolean start = str.startsWith(openToken);
		boolean end = str.endsWith(closeToken);

		if (start && end) {
			return s.substring(openToken.length(), str.length() - closeToken.length());
		}
		return s;
	}

	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}

	public static boolean hasLength(String str) {
		return hasLength((CharSequence) str);
	}

	public static boolean hasText(CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasText(String str) {
		return hasText((CharSequence) str);
	}

	public static boolean containsWhitespace(CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsWhitespace(String str) {
		return containsWhitespace((CharSequence) str);
	}

	public static String[] tokenizeToStringArray(String str, String delimiters) {
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

		if (str == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	public static String[] toStringArray(Collection<String> collection) {
		if (collection == null) {
			return null;
		}
		return collection.toArray(new String[collection.size()]);
	}

	public static String[] commaDelimitedListToStringArray(String str) {
		return delimitedListToStringArray(str, ",");
	}

	public static String[] delimitedListToStringArray(String str, String delimiter) {
		return delimitedListToStringArray(str, delimiter, null);
	}

	public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
		if (str == null) {
			return new String[0];
		}
		if (delimiter == null) {
			return new String[] { str };
		}
		List<String> result = new ArrayList<String>();
		if ("".equals(delimiter)) {
			for (int i = 0; i < str.length(); i++) {
				result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
			}
		} else {
			int pos = 0;
			int delPos;
			while ((delPos = str.indexOf(delimiter, pos)) != -1) {
				result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
				pos = delPos + delimiter.length();
			}
			if (str.length() > 0 && pos <= str.length()) {
				// Add rest of String, but not in case of empty input.
				result.add(deleteAny(str.substring(pos), charsToDelete));
			}
		}
		return toStringArray(result);
	}

	public static String collectionToDelimitedString(Collection<?> coll, String delim) {
		return collectionToDelimitedString(coll, delim, "", "");
	}

	public static String collectionToDelimitedString(Collection<?> coll, String delim, String prefix, String suffix) {
		if (coll == null || coll.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = coll.iterator();
		while (it.hasNext()) {
			sb.append(prefix).append(it.next()).append(suffix);
			if (it.hasNext()) {
				sb.append(delim);
			}
		}
		return sb.toString();
	}

	public static String capitalize(String str) {
		return changeFirstCharacterCase(str, true);
	}

	public static String uncapitalize(String str) {
		return changeFirstCharacterCase(str, false);
	}

	private static String changeFirstCharacterCase(String str, boolean capitalize) {
		if (str == null || str.length() == 0) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str.length());
		if (capitalize) {
			sb.append(Character.toUpperCase(str.charAt(0)));
		} else {
			sb.append(Character.toLowerCase(str.charAt(0)));
		}
		sb.append(str.substring(1));
		return sb.toString();
	}

	public static String replace(String inString, String oldPattern, String newPattern) {
		if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
			return inString;
		}
		StringBuilder sb = new StringBuilder();
		int pos = 0; // our position in the old string
		int index = inString.indexOf(oldPattern);
		// the index of an occurrence we've found, or -1
		int patLen = oldPattern.length();
		while (index >= 0) {
			sb.append(inString.substring(pos, index));
			sb.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		sb.append(inString.substring(pos));
		// remember to append any characters to the right of a match
		return sb.toString();
	}

	public static String getFilePathName(String path, String name) {
		String newPath = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);
		if (newPath.charAt(newPath.length() - 1) != FOLDER_SEPARATOR.charAt(0)) {
			newPath += FOLDER_SEPARATOR;
		}
		return newPath + name;
	}

	public static String cleanPath(String path) {
		if (path == null) {
			return null;
		}
		String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);

		// Strip prefix from path to analyze, to not treat it as part of the
		// first path element. This is necessary to correctly parse paths like
		// "file:core/../core/io/Resource.class", where the ".." should just
		// strip the first "core" directory while keeping the "file:" prefix.
		int prefixIndex = pathToUse.indexOf(":");
		String prefix = "";
		if (prefixIndex != -1) {
			prefix = pathToUse.substring(0, prefixIndex + 1);
			pathToUse = pathToUse.substring(prefixIndex + 1);
		}
		if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
			prefix = prefix + FOLDER_SEPARATOR;
			pathToUse = pathToUse.substring(1);
		}

		String[] pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
		List<String> pathElements = new LinkedList<String>();
		int tops = 0;

		for (int i = pathArray.length - 1; i >= 0; i--) {
			String element = pathArray[i];
			if (CURRENT_PATH.equals(element)) {
				// Points to current directory - drop it.
			} else if (TOP_PATH.equals(element)) {
				// Registering top path found.
				tops++;
			} else {
				if (tops > 0) {
					// Merging path element with element corresponding to top
					// path.
					tops--;
				} else {
					// Normal path element found.
					pathElements.add(0, element);
				}
			}
		}

		// Remaining top paths need to be retained.
		for (int i = 0; i < tops; i++) {
			pathElements.add(0, TOP_PATH);
		}

		return prefix + collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
	}

	public static String deleteAny(String inString, String charsToDelete) {
		if (!hasLength(inString) || !hasLength(charsToDelete)) {
			return inString;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < inString.length(); i++) {
			char c = inString.charAt(i);
			if (charsToDelete.indexOf(c) == -1) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String applyRelativePath(String path, String relativePath) {
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		if (separatorIndex != -1) {
			String newPath = path.substring(0, separatorIndex);
			if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
				newPath += FOLDER_SEPARATOR;
			}
			return newPath + relativePath;
		} else {
			return relativePath;
		}
	}

	public static String intArrayToString(int[] value) {
		if (value == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i : value) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(i);
		}
		return sb.toString();
	}

	public static Locale parseLocaleString(String localeString) {
		for (int i = 0; i < localeString.length(); i++) {
			char ch = localeString.charAt(i);
			if (ch != '_' && ch != ' ' && !Character.isLetterOrDigit(ch)) {
				throw new IllegalArgumentException("Locale value \"" + localeString + "\" contains invalid characters");
			}
		}
		String[] parts = tokenizeToStringArray(localeString, "_ ", false, false);
		String language = (parts.length > 0 ? parts[0] : "");
		String country = (parts.length > 1 ? parts[1] : "");
		String variant = "";
		if (parts.length >= 2) {
			// There is definitely a variant, and it is everything after the
			// country
			// code sans the separator between the country code and the variant.
			int endIndexOfCountryCode = localeString.indexOf(country) + country.length();
			// Strip off any leading '_' and whitespace, what's left is the
			// variant.
			variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
			if (variant.startsWith("_")) {
				variant = trimLeadingCharacter(variant, '_');
			}
		}
		return (language.length() > 0 ? new Locale(language, country, variant) : null);
	}

	public static String trimLeadingWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	public static String trimLeadingCharacter(String str, char leadingCharacter) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	public static String trimAllWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		int index = 0;
		while (sb.length() > index) {
			if (Character.isWhitespace(sb.charAt(index))) {
				sb.deleteCharAt(index);
			} else {
				index++;
			}
		}
		return sb.toString();
	}

	public static String escapeJson(String str) {
		String encode = str.replaceAll("\\\\", "\\\\\\\\");
		encode = encode.replaceAll("\r", "\\\\r");
		encode = encode.replaceAll("\t", "\\\\t");
		encode = encode.replaceAll("\b", "\\\\b");
		encode = encode.replaceAll("\f", "\\\\f");
		encode = encode.replaceAll("\"", "\\\\\"");
		encode = encode.replaceAll("\n", "\\\\n");
		encode = encode.replaceAll("/", "\\\\/");
		return encode;
	}

	public static String escapeSql(String str) {
		Pattern likePattern = Pattern.compile("\\_|%|\\[", Pattern.CASE_INSENSITIVE);
		return likePattern.matcher(str).replaceAll("[$0]");
	}

	public static String fillZeroAtLeft(String src, int len) {
		return fillStr(src, len, "0", true);
	}

	// 填充指定个数的字符
	public static String fillStr(String src, int len, String padstr, boolean isleft) {
		int srclen = src.length();
		int addChars = len - srclen;
		if (addChars < 0) {
			if (isleft) {// left
				return src.subSequence(0, len).toString();
			} else {// right
				return src.subSequence(srclen - len, srclen).toString();
			}
		}
		final StringBuilder sb = new StringBuilder();

		while (addChars > 0) {
			sb.append(padstr);
			--addChars;
		}
		if (isleft) {// left
			sb.append(src);
		} else {// right
			sb.insert(0, src);
		}
		return sb.toString();
	}

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || 
				ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || 
				ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || 
				ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || 
				ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || 
				ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	public static boolean isMessyCode(String strName) {
		Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
		Matcher m = p.matcher(strName);
		String after = m.replaceAll("");
		String temp = after.replaceAll("\\p{P}", "");
		char[] ch = temp.trim().toCharArray();
		float chLength = ch.length;
		float count = 0;
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (!Character.isLetterOrDigit(c)) {

				if (!isChinese(c)) {
					count = count + 1;
					// System.out.print(c);
				}
			}
		}
		float result = count / chLength;
		if (result > 0.4) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 用getBytes(encoding)：返回字符串的一个byte数组 当b[0]为 63时，应该是转码错误 A、不乱码的汉字字符串：
	 * 1、encoding用GB2312时，每byte是负数； 2、encoding用ISO8859_1时，b[i]全是63。 B、乱码的汉字字符串：
	 * 1、encoding用ISO8859_1时，每byte也是负数； 2、encoding用GB2312时，b[i]大部分是63。 C、英文字符串
	 * 1、encoding用ISO8859_1和GB2312时，每byte都大于0；
	 * <p/>
	 * 总结：给定一个字符串，用getBytes("iso8859_1") 1、如果b[i]有63，不用转码； A-2
	 * 2、如果b[i]全大于0，那么为英文字符串，不用转码； B-1 3、如果b[i]有小于0的，那么已经乱码，要转码。 C-1
	 */
	private static String toGb2312(String str) {
		if (str == null)
			return null;
		String retStr = str;
		byte b[];
		try {
			b = str.getBytes("ISO8859_1");

			for (int i = 0; i < b.length; i++) {
				byte b1 = b[i];
				if (b1 == 63)
					break; // 1
				else if (b1 > 0)
					continue;// 2
				else if (b1 < 0) { // 不可能为0，0为字符串结束符
					retStr = new String(b, "GB2312");
					break;
				}
			}
		} catch (UnsupportedEncodingException e) {
			// e.printStackTrace(); //To change body of catch statement use File
			// | Settings | File Templates.
		}
		return retStr;
	}

	public static String getEncoding(String str) {
		String encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				return encode;
			}
		} catch (Exception e) {
		}

		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				return encode;
			}
		} catch (Exception e) {
		}

		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				return encode;
			}
		} catch (Exception e) {
		}

		encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				return encode;
			}
		} catch (Exception e) {
		}

		return "";
	}

	public static boolean isEncoding(String str, String encode) {
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				return true;
			}
		} catch (Exception e) {
		}

		return false;
	}
	
	public static String getUUID() {
		return getUUID(true);
	}
	public static String getUUID(boolean ismin) {
		if(ismin){
			return UUID.randomUUID().toString().replaceAll("-", "");
		}
		else{
			return UUID.randomUUID().toString();
		}
	}
}