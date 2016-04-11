package com.platform.cubism.jdbc;

import static com.platform.cubism.util.StringUtils.hasText;

import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class SqlTrim {
	private static final String NULL = "null";
	private static final Pattern hasNull = Pattern.compile("\\s*[<>]*={1}\\s*\'\'", Pattern.CASE_INSENSITIVE);

	private static final Pattern betweenAndBothNull = Pattern.compile("between\\s*\'\'\\s*and\\s*\'\'", Pattern.CASE_INSENSITIVE);
	private static final Pattern betweenNull = Pattern.compile("between\\s*\'\'\\s*and", Pattern.CASE_INSENSITIVE);
	private static final Pattern betweenAndNull = Pattern.compile("between\\s*(.*)and\\s*\'\'", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern startWithConnectByNull = Pattern.compile("(\\s+(or|and)\\s+)*\\S+\\s+in\\s*\\(\\s*(.*)start\\s+with\\s+connect\\s+by\\s+(.*)\\)", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern funcNull = Pattern.compile("\\S+\\s*\\(\\s*[']*\\s*,.*?\\)", Pattern.CASE_INSENSITIVE);
	private static final Pattern isFuncNull = Pattern.compile(".*\\s+values\\s*\\(\\s*[']*\\s*,.*?\\).*", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern inNull = Pattern.compile("(\\s*not\\s*)?in\\s*\\((\\s*\'\'\\s*,*\\s*)+\\)", Pattern.CASE_INSENSITIVE);
	private static final Pattern likeNull = Pattern.compile("like\\s*\'[%]*\'", Pattern.CASE_INSENSITIVE);
	private static final Pattern otherNull = Pattern.compile("\\s*\'\'\\s*", Pattern.CASE_INSENSITIVE);
	private static final Pattern otherNull2 = Pattern.compile("\\s+(in|or|and|where|select|from|between|like|start|with|connect|by|order|group)\\s*(\\(\\s*\\))\\s*", Pattern.CASE_INSENSITIVE);//考虑SQL函数调用
	private static final Pattern otherNull3 = Pattern.compile("(\\s+\\S+\\s+)(\\()\\s*(\\))\\s*", Pattern.CASE_INSENSITIVE);//考虑SQL函数调用
	private static final Pattern otherNull4a = Pattern.compile("(.*)(\\()(\\S+?)(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern otherNull4b = Pattern.compile("(。*)(\\S+?)(\\))(.*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern logic = Pattern.compile("\\!=|<>|>=|<=|>|<|=|,", Pattern.CASE_INSENSITIVE);
	private static final Pattern logic2 = Pattern.compile("^\\!=$|^<>$|^>=$|^<=$|^>$|^<$|^=$", Pattern.CASE_INSENSITIVE);

	private static final Pattern lastone = Pattern.compile(
					"(from|where|or|and|in)+\\s*\\(\\s*(or|and|\\(\\s*\\))*\\s*\\)|" +
					"(from|where)+\\(\\s*(or|and|\\(\\s*\\))*\\s*\\)\\s*(or|and|in)+|" +
					"\\s+(or|and|where)\\s*$|" +
					"\\s+where\\s+.*\\s+in\\s*\\(\\s*\\)\\s*$|" +
					"\\s+(or|and)\\s+(where|select|from|order|group)\\s+|" +
					"\\s+(or|and|where|select|from|order|group)\\s*$",
					Pattern.CASE_INSENSITIVE);
	private static final Pattern lastone2 = Pattern.compile(
			"^.*(from|where|or|and|in)+\\s*\\(\\s*(or|and|\\(\\s*\\))*\\s*\\).*$|" +
			"^.*(from|where)+\\(\\s*(or|and|\\(\\s*\\))*\\s*\\)\\s*(or|and|in)+.*$|" +
			"^.*\\s+(or|and|where)\\s*$|" +
			"^.*\\s+where\\s+.*\\s+in\\s*\\(\\s*\\)\\s*$|" +
			"^.*\\s+(or|and)\\s+(where|select|from|order|group)\\s+.*$|" +
			"^.*\\s+(or|and|where|select|from|order|group)\\s*$", 
			Pattern.CASE_INSENSITIVE);

	private static final Pattern lasttwo = Pattern.compile("\\s+(or|and|where|\\(\\s*\\))\\s+(where|select|from|order|group)\\s+", Pattern.CASE_INSENSITIVE);
	private static final Pattern lasttwo2 = Pattern.compile("^.*\\s+(or|and|where|\\(\\s*\\))\\s+(where|select|from|order|group)\\s+.*$", Pattern.CASE_INSENSITIVE);

	private static final Pattern lastthree = Pattern.compile("\\s*(where)\\s+(,|\\)|where|select|from|order|group)\\s+", Pattern.CASE_INSENSITIVE);
	private static final Pattern lastthree2 = Pattern.compile("^.*\\s*(where)\\s+(,|\\)|where|select|from|order|group)\\s+.*$", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern andorcomma = Pattern.compile("^or$|^and$|^,$", Pattern.CASE_INSENSITIVE);
	private static final Pattern andorcomma2 = Pattern.compile("^\\($|^or$|^and$|^from$|^where$|^order$", Pattern.CASE_INSENSITIVE);
	private static final Pattern andorcomma3 = Pattern.compile("^\\)$|^or$|^and$|^from$|^where$|^order$", Pattern.CASE_INSENSITIVE);
	private static final Pattern sqlkey = Pattern.compile("^,$|^\\($|^\\)$|^or$|^and$|^between$|^insert$|^value$|^select$|^from$|^where$|^order$|^group$", Pattern.CASE_INSENSITIVE);
	
	private Stack<String> sqlStack = new Stack<String>();

	private SqlTrim() {
		;
	}

	public static SqlTrim getInstance() {
		return new SqlTrim();
	}
	
	private String trimSpace(String sql){
		if (!hasText(sql)) {
			return "";
		}
		return sql.replaceAll("\\s+\\(\\s+|\\s+\\(\\s*|\\s*\\(\\s+", "(").replaceAll("\\s+\\)\\s+|\\s+\\)\\s*|\\s*\\)\\s+", ")").replaceAll("\\s+", " ").replaceAll(":\\s+=\\s*", ":=");
	}
	
	public String trim(String sql) {
		if (!hasText(sql)) {
			return "";
		}
		String quotes = "''";
		String newsql = sqlReplace(sql);
		if (newsql.indexOf(quotes) < 0) {
			return trimSpace(newsql);
		}
		if (!hasNull.matcher(newsql).find()) {
			return trimSpace(newsql).replace("''", NULL);
		}
		String token;
		StringTokenizer st = new StringTokenizer(newsql, " ");
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if (!hasText(token)) {
				continue;
			}
			if ((token.equals(quotes)||token.equals("null")) && logic2.matcher(sqlStack.peek()).find()) {//大写的NULL将不进行处理
				if (sqlStack.isEmpty()) {
					continue;
				}
				sqlStack.pop();
				popLeftOperation();//增加去掉左边的and or的代码
				if (st.hasMoreElements()) {
					token = st.nextToken();
					if (!sqlStack.isEmpty() && andorcomma.matcher(token).matches() && andorcomma2.matcher(sqlStack.peek()).matches()) {
						continue;
					}
					else if (!sqlStack.isEmpty() && andorcomma.matcher(sqlStack.peek()).matches() && andorcomma3.matcher(token).matches()) {
						sqlStack.pop();
						sqlStack.push(token);
						continue;
					}
					else if("and".equalsIgnoreCase(token) || "or".equalsIgnoreCase(token)){//在左边已经去掉，右边的就不用去掉
						sqlStack.push(token);
					}
					else{
						popAndOrOperation(token);
					}
				} else {
					popAndOrOperation(sqlStack.pop());
				}				
			}
			else if (!sqlStack.isEmpty() && andorcomma.matcher(token).matches() && andorcomma2.matcher(sqlStack.peek()).matches()) {
				continue;
			}
			else if (!sqlStack.isEmpty() && andorcomma.matcher(sqlStack.peek()).matches() && andorcomma3.matcher(token).matches()) {
				sqlStack.pop();
				sqlStack.push(token);
				continue;
			}
			else {
				sqlStack.push(token);
			}
		}

		if (sqlStack.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		while (!sqlStack.isEmpty()) {
			sb.insert(0, " ").insert(0, sqlStack.pop());
		}
		sqlStack.clear();
		sqlStack = null;
		
		String result = sb.toString().replace("''", NULL);
		result = startWithConnectByNull.matcher(result).replaceAll(" ");
		while (lastone2.matcher(result).matches()) {
			result = lastone.matcher(result).replaceAll("");
		}
		while (lasttwo2.matcher(result).matches()) {
			result = lasttwo.matcher(result).replaceAll(" $2 ");
		}
		while (lastthree2.matcher(result).matches()) {
			result = lastthree.matcher(result).replaceAll(" $2 ");
		}
		return trimSpace(result);
	}

	private String sqlReplace(String sql) {
		String result = sql;
		while (lastone2.matcher(result).matches()) {
			result = lastone.matcher(result).replaceAll("");
		}
		result = betweenAndBothNull.matcher(result).replaceAll(" = '' ");
		result = betweenNull.matcher(result).replaceAll(" <= ");
		result = betweenAndNull.matcher(result).replaceAll(" >= $1");
		result = inNull.matcher(result).replaceAll(" = '' ");
		result = likeNull.matcher(result).replaceAll(" = '' ");
		result = logic.matcher(result).replaceAll(" $0 ");
		result = otherNull.matcher(result).replaceAll(" $0 ");
		result = otherNull2.matcher(result).replaceAll("$1 $2 ");
		result = otherNull3.matcher(result).replaceAll(" $1 $2 $3 ");
		result = otherNull4a.matcher(result).replaceAll("$1$2 $3$4");
		result = otherNull4b.matcher(result).replaceAll("$1$2 $3$4");
		if(!isFuncNull.matcher(result).matches()){
			result = funcNull.matcher(result).replaceAll(" '' ");//函数参数为''的去掉，为null的则不去掉
		}
		
		return result;
	}

	private void popLeftOperation() {// 删除= < <= > >= 等操作符左边的表达式
		if (sqlStack.isEmpty()) {
			return;
		}
		String token = sqlStack.pop();
		int idx = token.indexOf(')');
		if (idx < 0) {
			//去掉AND OR逻辑符
			String andor = sqlStack.peek();
			while("and".equalsIgnoreCase(andor) || "or".equalsIgnoreCase(andor)){
				sqlStack.pop();
				if (sqlStack.isEmpty()) {
					return;
				}
				andor = sqlStack.peek();
			}
			return;
		}

		int cnt = 0;
		do {
			for (int i = token.length() - 1; i >= 0; i--) {
				if (token.indexOf(')') >= 0)
					cnt++;
				else if (token.indexOf('(') >= 0)
					cnt--;
			}
			if(cnt <= 0 && sqlkey.matcher(sqlStack.peek()).matches()){
				break;
			}
			token = sqlStack.pop();
		} while (cnt > 0 || sqlStack.isEmpty());
		
		//去掉AND OR逻辑符
		String andor = sqlStack.peek();
		while("and".equalsIgnoreCase(andor) || "or".equalsIgnoreCase(andor)){
			sqlStack.pop();
			if (sqlStack.isEmpty()) {
				return;
			}
			andor = sqlStack.peek();
		}
	}
	
	private void popSubSelectOperation() {// 删除not/ in(select * from a where )
		if (sqlStack.isEmpty()) {
			return;
		}
		String token = sqlStack.pop();
		int idx = token.indexOf(')');
		if (idx < 0) {
			return;
		}
		int cnt = 0;
		do {
			if(token.indexOf('(') < 0 && token.indexOf(')') < 0){
				token = sqlStack.pop();
				continue;
			}
			for (int i = token.length() - 1; i >= 0; i--) {
				if (token.charAt(i) == ')')
					cnt++;
				else if (token.charAt(i) == '(')
					cnt--;
			}
			if(cnt > 0){
				token = sqlStack.pop();
			}
		} while (cnt > 0 || sqlStack.isEmpty());
		
		if (!sqlStack.isEmpty() && ("not".equalsIgnoreCase(sqlStack.peek()) || "in".equalsIgnoreCase(sqlStack.peek()))) {
			sqlStack.pop();
		}
		
		popLeftOperation();
	}

	private void popAndOrOperation(String logic) {// 删除操作符后面多余的and or ,
		if (!hasText(logic) || sqlStack.isEmpty()) {
			return;
		}
		Pattern pattern = Pattern.compile("^or$|^and$", Pattern.CASE_INSENSITIVE);
		Pattern pattern2 = Pattern.compile("^\\)$|^where$|^from$|^select$|^order$|^group$", Pattern.CASE_INSENSITIVE);
		if (!andorcomma.matcher(logic).matches()) {
			if (andorcomma.matcher(sqlStack.peek()).matches() && pattern2.matcher(logic).matches()) {
				sqlStack.pop();
			}
			if (sqlStack.isEmpty()) {
				return;
			}
			if("(".equals(sqlStack.peek()) && ")".equals(logic)){//删除and ()  或 or () 或 where ()
				int len = sqlStack.size();
				if(len<2){
					sqlStack.pop();
					return;
				}
				String key = sqlStack.get(len-2);
				if("(".equals(key) || andorcomma.matcher(key).matches() || andorcomma2.matcher(key).matches()){
					sqlStack.pop();
					if (sqlStack.isEmpty()) {
						return;
					}
					if(pattern.matcher(sqlStack.peek()).matches()){
						sqlStack.pop();
					}
				}
			}
			else if("where".equalsIgnoreCase(sqlStack.peek()) && (")".equals(logic)|| logic.indexOf(')')>=0)){//删除 in(select * from a where )
				sqlStack.push(logic);
				popSubSelectOperation();
			}
			else{
				sqlStack.push(logic);
			}
		}
	}
}