package com.platform.cubism.jdbc;

import java.sql.Connection;

import com.platform.cubism.jdbc.tran.TransactionStatus;
import com.platform.cubism.service.core.ServiceEntity;

public class SqlFactory {
	public static SqlExecutor getSqlExecutor(ServiceEntity entity, TransactionStatus status) {
		GenericStatement gs = new GenericStatement();
		gs.setId(entity.getId());
		gs.setAlwayarray(entity.isAlwayarray());
		gs.setLowercase(entity.isLowercase());
		gs.setGeneratedkeys(entity.getGeneratedkeys());
		gs.setConn(status.getConnection());
		gs.setCache(entity.getCache());
		gs.setUpdatecache(entity.getUpdatecache());
		gs.setServiceId(entity.getServiceId());
		return gs;
	}
	public static SqlExecutor getSqlExecutor(ServiceEntity entity, Connection conn) {
		GenericStatement gs = new GenericStatement();
		gs.setId(entity.getId());
		gs.setAlwayarray(entity.isAlwayarray());
		gs.setLowercase(entity.isLowercase());
		gs.setGeneratedkeys(entity.getGeneratedkeys());
		gs.setConn(conn);
		gs.setCache(entity.getCache());
		gs.setUpdatecache(entity.getUpdatecache());
		gs.setServiceId(entity.getServiceId());
		return gs;
	}
}