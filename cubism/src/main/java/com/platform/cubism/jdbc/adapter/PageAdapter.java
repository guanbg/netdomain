package com.platform.cubism.jdbc.adapter;

public interface PageAdapter {
	public abstract String getName();

	public boolean isSysAutoClumn(String colname);

	public abstract String getPageSql(String sql, int startPage, int pageCount, int totalrecord);

	public abstract String getTotalPage(String sql);

	public abstract String getIdentitySelectString();

	public abstract String getSequenceNextValString(String sequenceName);
}