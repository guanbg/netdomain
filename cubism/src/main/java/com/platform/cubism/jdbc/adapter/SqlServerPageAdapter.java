package com.platform.cubism.jdbc.adapter;

import com.platform.cubism.struc.PageHead;

public class SqlServerPageAdapter implements PageAdapter {
	private final static String ordercolnameperfix = "_xorder_";
	private static SqlServerPageAdapter instance = new SqlServerPageAdapter();

	private SqlServerPageAdapter() {
		;
	}

	public static SqlServerPageAdapter getInstance() {
		return instance;
	}

	public String getName() {
		return "sqlserver";
	}

	public boolean isSysAutoClumn(String colname) {
		if (colname.matches("^"+ordercolnameperfix + "\\d+$")) {
			return true;
		}
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
		int start = pageCount;
		int end = startPage * pageCount;
		if (start < 0) {
			start = 0;
		}
		if (end > totalrecord) {
			start = totalrecord % start;
		}
		String orderby = " order by 1 ";
		String orderby2 = " order by 1 desc ";
		String xsql = sql.toLowerCase();
		int idx = getOrderByIndex(xsql);
		if (idx > 0) {
			orderby = xsql.substring(idx);
			xsql = xsql.substring(0, idx);

			String[] goc = getOrderColName(orderby);
			if(goc != null){
				String colname = goc[1];
				String csql = comboOrderCol(xsql, colname);
				if (csql != null && !"".equals(csql)) {
					orderby = goc[0];
					xsql = csql;
				}
			}

			orderby2 = toggleOrderby(orderby);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("select * from ( select top ");
		sb.append(start).append(" * from ( select top ");
		sb.append(end).append(" * from (").append(xsql);
		sb.append(")_a_ ").append(orderby).append(")_b_ ").append(orderby2);
		sb.append(")_c_ ").append(orderby);
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
		sb.append(" ) mssqlcnt");
		return sb.toString();
	}

	public String getIdentitySelectString() {
		return "SELECT @@IDENTITY";
	}

	public String getSequenceNextValString(String sequenceName) {
		return null;
	}

	private String comboOrderCol(String sql, String col) {
		if (sql.matches("^\\s*select\\s*distinct\\s*.+")) {
			return "select distinct " + col + sql.substring(sql.indexOf("distinct") + 8);
		} else if (sql.matches("^\\s*select\\s*.+")) {
			return "select " + col + sql.substring(sql.indexOf("select") + 6);
		}
		return null;
	}

	private String[] getOrderColName(String orderby) {
		String col = orderby.replaceAll("\\s*order\\s+by\\s*", "");
		if(col == null || "".equals(col)){
			return null;
		}
		col = col.replaceAll("\\s*desc\\s*", "");
		col = col.replaceAll("\\s*asc\\s*", "");
		String[] cols = col.split(",");
		String xorder = orderby;
		StringBuilder sb = new StringBuilder();
		String oname;
		for (int i = 0; i < cols.length; i++) {
			oname = ordercolnameperfix + i;
			sb.append(cols[i]).append(" ").append(oname).append(" , ");
			xorder = xorder.replaceAll(cols[i], oname);
		}
		String[] ret = { xorder, sb.toString() };
		return ret;
	}

	private String toggleOrderby(String orderby) {
		StringBuilder sb = new StringBuilder();
		String[] ss = orderby.toLowerCase().replaceAll(",", " $0 ").split("\\s+");
		for (int i = 0; i < ss.length; i++) {
			if ("order".equals(ss[i])) {
				sb.append(ss[i]).append(" ");
				continue;
			}
			if ("by".equals(ss[i])) {
				sb.append(ss[i]).append(" ");
				continue;
			}
			if ("asc".equals(ss[i])) {
				sb.append("desc ");
				continue;
			}
			if ("desc".equals(ss[i])) {
				sb.append("asc ");
				continue;
			}
			if (",".equals(ss[i])) {
				if ("asc".equals(ss[i - 1]) || "desc".equals(ss[i - 1])) {
					sb.append(ss[i]);
					continue;
				}
				sb.append("desc ").append(ss[i]);
				continue;
			}
			sb.append(ss[i]).append(" ");

			if (i == ss.length - 1) {
				if ("asc".equals(ss[i]) || "desc".equals(ss[i])) {
					continue;
				}
				sb.append("desc ");
			}
		}
		return sb.toString();
	}
}