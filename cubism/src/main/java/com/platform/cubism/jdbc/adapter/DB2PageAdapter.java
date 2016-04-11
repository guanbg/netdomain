package com.platform.cubism.jdbc.adapter;

import com.platform.cubism.struc.PageHead;

public class DB2PageAdapter implements PageAdapter {
	private static DB2PageAdapter instance = new DB2PageAdapter();

	private DB2PageAdapter() {
		;
	}

	public static DB2PageAdapter getInstance() {
		return instance;
	}

	public String getName() {
		return "db2";
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
		StringBuilder sb = new StringBuilder();
		sb.append(sql).append(" fetch first ").append(end).append(" rows only");
		return sb.toString();
	}

	public String getTotalPage(String sql) {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) as ");
		sb.append(PageHead.TOTALRECORD.value());
		sb.append(" from ( ");
		int idx = sql.toLowerCase().indexOf("order");
		if (idx > 0) {
			sb.append(sql.substring(0, idx));
		} else {
			sb.append(sql);
		}
		sb.append(" )a");
		return sb.toString();
	}

	public String getIdentitySelectString() {
		return "VALUES IDENTITY_VAL_LOCAL()";
	}

	public String getSequenceNextValString(String sequenceName) {
		return null;
	}

}
