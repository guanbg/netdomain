package com.platform.cubism.jdbc.adapter;

public class PageAdapterManager {
	private static String DatabaseProductName;
	private static final String sqlserver = "Microsoft SQL Server";
	private static final String mysql = "MySQL";
	private static final String oracle = "oracle";
	private static final String db2 = "db2";

	public static PageAdapter getPageAdapter() {// 通过当前数据库连接获取相应的翻页适配器，如果不能获取连接，将返回空
		if (sqlserver.equalsIgnoreCase(DatabaseProductName)) {
			return SqlServerPageAdapter.getInstance();
		} else if (mysql.equalsIgnoreCase(DatabaseProductName)) {
			return MySqlPageAdapter.getInstance();
		} else if (oracle.equalsIgnoreCase(DatabaseProductName)) {
			return OraclePageAdapter.getInstance();
		} else if (db2.equalsIgnoreCase(DatabaseProductName)) {
			return DB2PageAdapter.getInstance();
		}

		return null;
	}

	public static boolean isSysAutoClumn(String colname) {
		PageAdapter pa = getPageAdapter();
		if (pa != null && pa.isSysAutoClumn(colname)) {
			return true;
		}
		return false;
	}

	public static void setDatabaseProductName(String pname) {
		DatabaseProductName = pname;
	}

	public static String getDatabaseProductName() {
		return DatabaseProductName;
	}
}