package com.platform.cubism.jdbc.tran;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import org.slf4j.LoggerFactory;

public class ConnectionHolder {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
	public static final String SAVEPOINT_NAME_PREFIX = "SAVEPOINT_";

	private Connection connection;
	private int referenceCount = 0;
	private boolean transactionActive;
	private Boolean savepointsSupported;
	private int savepointCounter;

	public ConnectionHolder(Connection conn) {
		setConnection(conn);
	}

	public Connection getConnection() {
		return connection;
	}

	public boolean hasConnection() {
		return connection != null;
	}

	public boolean isTransactionActive() {
		return transactionActive;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void setTransactionActive(boolean transactionActive) {
		this.transactionActive = transactionActive;
	}

	public void requested() {
		this.referenceCount++;
	}

	public boolean isOpen() {
		return (this.referenceCount > 0);
	}

	public boolean supportsSavepoints() throws SQLException {
		if (this.savepointsSupported == null) {
			this.savepointsSupported = new Boolean(getConnection().getMetaData().supportsSavepoints());
		}
		return this.savepointsSupported.booleanValue();
	}

	public Savepoint createSavepoint() throws SQLException {
		this.savepointCounter++;
		return getConnection().setSavepoint(SAVEPOINT_NAME_PREFIX + this.savepointCounter);
	}

	public void rollbackToSavepoint(Savepoint savepoint) throws SQLException {
		getConnection().rollback(savepoint);
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		getConnection().releaseSavepoint(savepoint);
	}

	public void released() {
		logger.info("当前连接引用数：" + referenceCount);
		if (!isOpen() && this.connection != null) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				logger.error("关闭当前连接出错：" + e.getMessage());
			}
			logger.info("关闭当前连接");
			this.connection = null;
		}
		this.referenceCount--;
	}

	public void clear() {
		this.transactionActive = false;
		this.savepointsSupported = null;
		this.savepointCounter = 0;
	}

}