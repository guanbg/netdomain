package com.platform.cubism.jdbc.conn;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.LoggerFactory;

public class JndiDataSourceLookup {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(JndiDataSourceLookup.class);

	public static DataSource getDataSource(String dataSourceName) throws NamingException {
		final String PREFIX = "java:comp/env/";
		try {
			Object obj = null;
			if (!dataSourceName.startsWith(PREFIX)) {
				obj = lookup(PREFIX + dataSourceName);
			}
			if (obj == null) {
				obj = lookup(dataSourceName);
			}
			if (obj == null) {
				throw new NameNotFoundException("JNDI object with [" + dataSourceName + "] not found: JNDI implementation returned null");
			}
			return (DataSource) obj;
		} catch (NamingException e) {
			if (logger.isErrorEnabled()) {
				logger.error("JNDI没有查找到数据源" + dataSourceName + "，出错信息为:" + e.getMessage());
			}
			throw e;
		}
	}

	private static Object lookup(String name) {
		if (logger.isDebugEnabled()) {
			logger.debug("需要查找的资源:" + name);
		}
		try {
			Context ctx = new InitialContext();
			Object obj = ctx.lookup(name);
			if (logger.isDebugEnabled()) {
				logger.debug("成功查找到资源:" + name);
			}
			return obj;
		} catch (NamingException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(name + "没有找到该资源:" + e.getMessage());
			}
		}
		return null;
	}
}
