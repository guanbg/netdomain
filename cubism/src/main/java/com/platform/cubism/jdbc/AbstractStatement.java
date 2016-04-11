package com.platform.cubism.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.platform.cubism.jdbc.adapter.PageAdapterManager;

public abstract class AbstractStatement extends AbstractSqlExecutor {
	private int fetchSize = 0;
	private int maxRows = 0;
	private int queryTimeout = 0;
	private Connection conn;

	public int getFetchSize() {
		return fetchSize;
	}

	public int getMaxRows() {
		return maxRows;
	}

	public int getQueryTimeout() {
		return queryTimeout;
	}

	public Connection getConn() {
		return conn;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}

	public void setConn(Connection conn) {
		try {
			PageAdapterManager.setDatabaseProductName(conn.getMetaData().getDatabaseProductName());
			if (logger.isDebugEnabled()) {
				logger.debug("数据库类型：" + PageAdapterManager.getDatabaseProductName());
			}
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error("获取数据库类型错误：" + e.getMessage());
			}
		}
		this.conn = createConnectionProxy(conn);
	}

	private Connection createConnectionProxy(Connection con) {
		return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[] { Connection.class },
				new CloseSuppressingInvocationHandler(con));
	}

	protected void applyStatementSettings(Statement stmt) throws SQLException {
		if (getFetchSize() > 0) {
			stmt.setFetchSize(fetchSize);
		}
		if (getMaxRows() > 0) {
			stmt.setMaxRows(maxRows);
		}
		if (getQueryTimeout() > 0) {
			stmt.setQueryTimeout(getQueryTimeout());
		}
	}

	private class CloseSuppressingInvocationHandler implements InvocationHandler {

		private final Connection target;

		public CloseSuppressingInvocationHandler(Connection target) {
			this.target = target;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("equals")) {
				// Only consider equal when proxies are identical.
				return (proxy == args[0]);
			} else if (method.getName().equals("hashCode")) {
				// Use hashCode of PersistenceManager proxy.
				return System.identityHashCode(proxy);
			} else if (method.getName().equals("unwrap")) {
				if (((Class<?>) args[0]).isInstance(proxy)) {
					return proxy;
				}
			} else if (method.getName().equals("isWrapperFor")) {
				if (((Class<?>) args[0]).isInstance(proxy)) {
					return true;
				}
			} else if (method.getName().equals("close")) {
				// Handle close method: suppress, not valid.
				return null;
			} else if (method.getName().equals("isClosed")) {
				return false;
			} else if (method.getName().equals("getTargetConnection")) {
				// Handle getTargetConnection method: return underlying
				// Connection.
				return this.target;
			}

			// Invoke method on target Connection.
			try {
				Object retVal = method.invoke(this.target, args);

				// If return value is a JDBC Statement, apply statement settings
				// (fetch size, max rows, transaction timeout).
				if (retVal instanceof Statement) {
					applyStatementSettings(((Statement) retVal));
				}

				return retVal;
			} catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		}
	}
}