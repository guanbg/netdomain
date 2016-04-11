package com.platform.cubism.jdbc.adapter;

import com.platform.cubism.struc.PageHead;

public class MySqlPageAdapter implements PageAdapter {
	private static MySqlPageAdapter instance = new MySqlPageAdapter();

	private MySqlPageAdapter() {
		;
	}

	public static MySqlPageAdapter getInstance() {
		return instance;
	}

	public String getName() {
		return "mysql";
	}

	public boolean isSysAutoClumn(String colname) {
		return false;
	}
	public int getOrderByIndex(String sql) {
		String xsql = sql.toLowerCase();
		int idx = -1;
		if (xsql.matches(".*(\\s+|\\)+)order\\s+by(\\(+|\\s+).*")) {
			xsql = xsql.replaceAll("order\\s+by", "order by");
			idx = xsql.lastIndexOf("order by");
			int i = xsql.lastIndexOf("where");
			if (i > 0 && idx < i) {
				return -1;
			}
			i = xsql.lastIndexOf(")");
			if (i > 0 && idx < i) {
				return -1;
			}
		}
		return idx;
	}

	public String getPageSql(String sql, int startPage, int pageCount, int totalrecord) {
		int start = (startPage - 1) * pageCount;
		if (start < 0) {
			start = 0;
		}
		/*
		 * select * from mytable where 条件 limit 当前页码*页面容量-1 to 页面容量
		 */
		StringBuilder sb = new StringBuilder();
		sb.append(sql).append(" limit ").append(start).append(" , ").append(pageCount);
		return sb.toString();
	}

	public String getTotalPage(String sql) {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) as ");
		sb.append(PageHead.TOTALRECORD.value());
		sb.append(" from ( ");
		int idx = getOrderByIndex(sql);
		if (idx > 0) {
			sb.append(sql.substring(0, idx));
		} else {
			sb.append(sql);
		}
		sb.append(" ) mysqlcnt");
		return sb.toString();
	}

	public String getIdentitySelectString() {
		return "select last_insert_id()";
	}

	public String getSequenceNextValString(String sequenceName) {
		return null;
	}

}
