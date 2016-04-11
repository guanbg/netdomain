package com.platform.cubism.jdbc.adapter;

import java.util.regex.Pattern;

import com.platform.cubism.struc.PageHead;

public class OraclePageAdapter implements PageAdapter {
	private static OraclePageAdapter instance = new OraclePageAdapter();
	private static final Pattern orderPattern1 = Pattern.compile("(\\s+|\\))order\\s+by\\s+([^\\(]*?\\([^\\(\\)]+\\))+[^\\(\\)]+?\\)(.*?)", Pattern.CASE_INSENSITIVE);
	private static final Pattern orderPattern2 = Pattern.compile("(\\s+|\\))order\\s+by\\s+[^\\(\\)]+?\\)(.*?)", Pattern.CASE_INSENSITIVE);
	private static final Pattern orderPattern3 = Pattern.compile("(\\s+|\\))order\\s+by\\s+.+", Pattern.CASE_INSENSITIVE);

	private OraclePageAdapter() {
		;
	}

	public static OraclePageAdapter getInstance() {
		return instance;
	}

	public String getName() {
		return "oracle";
	}

	public boolean isSysAutoClumn(String colname) {
		return false;
	}

	public String getPageSql(String sql, int startPage, int pageCount, int totalrecord) {
		int start = (startPage - 1) * pageCount;
		int end = startPage * pageCount;
		if (start < 0) {
			start = 0;
		}
		/*
		 * select * from ( select mytable.*, rownum num from ( 实际传的SQL ) where
		 * rownum<=pageEnd ) where num>=pageStart
		 */
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ( select orcl_page.*, rownum row_num from ( ").append(sql);
		sb.append(" ) orcl_page ) where row_num > ").append(start).append(" and row_num <= ").append(end);
		return sb.toString();
	}

	public String getTotalPage(String sql) {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) as ").append(PageHead.TOTALRECORD.value()).append(" from ( ");
		
		if(orderPattern1.matcher(sql).find()){
			sql = orderPattern1.matcher(sql).replaceAll("$1 ) $3");//$1表示编号为1的一个组，在Matcher类中group即是组，每个group由()定义，字符串本身为一个group，编号为0
		}
		if(orderPattern2.matcher(sql).find()){
			sql=orderPattern2.matcher(sql).replaceAll("$1 ) $2");//$1表示编号为1的一个组，在Matcher类中group即是组，每个group由()定义，字符串本身为一个group，编号为0
		}
		if(orderPattern3.matcher(sql).find()){
			sql=orderPattern3.matcher(sql).replaceAll("$1");//$1表示编号为1的一个组，在Matcher类中group即是组，每个group由()定义，字符串本身为一个group，编号为0
		}
		
		sb.append(sql).append(" ) oraclecnt");
		return sb.toString();
	}

	public String getIdentitySelectString() {
		return null;
	}

	public String getSequenceNextValString(String sequenceName) {
		return "select " + sequenceName + ".nextval from dual";
	}

}
