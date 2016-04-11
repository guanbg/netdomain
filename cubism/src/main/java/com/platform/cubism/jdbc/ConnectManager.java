package com.platform.cubism.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import com.platform.cubism.jdbc.conn.DataSourceManager;
import com.platform.cubism.jdbc.tran.ConnectionHolder;
import com.platform.cubism.jdbc.tran.TransactionSynchronizationManager;
import com.platform.cubism.util.Assert;

public class ConnectManager {
	public static Connection getConnection() throws SQLException {
		return getConnection(DataSourceManager.getDataSource());
	}

	public static Connection getConnection(DataSource dataSource) throws SQLException {
		Assert.notNull(dataSource, "No DataSource specified");
		ConnectionHolder conHolder = TransactionSynchronizationManager.getResource(dataSource);
		if (conHolder != null && conHolder.hasConnection()) {
			conHolder.requested();
			return conHolder.getConnection();
		}
		return null;
	}
} 