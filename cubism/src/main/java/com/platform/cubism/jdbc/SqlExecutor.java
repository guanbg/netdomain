package com.platform.cubism.jdbc;

import java.sql.SQLException;

import com.platform.cubism.base.Json;

public interface SqlExecutor {
	public Json execute(String sql, Json in) throws SQLException;
	public Json nextPage() throws SQLException;
}