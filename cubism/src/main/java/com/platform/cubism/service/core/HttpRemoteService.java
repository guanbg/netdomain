package com.platform.cubism.service.core;

import com.platform.cubism.base.Json;
import com.platform.cubism.jdbc.tran.Transaction;
import com.platform.cubism.jdbc.tran.TransactionStatus;
import com.platform.cubism.service.remote.RemoteExecutor;
import com.platform.cubism.service.remote.RemoteFactory;
import com.platform.cubism.struc.RetHead;
import com.platform.cubism.util.HeadHelper;

public class HttpRemoteService extends GenericService {
	private Transaction tran = null;
	private TransactionStatus status = null;

	public HttpRemoteService(String serviceId) {
		super(serviceId);
	}

	@Override
	public Json perform(Json in) throws Exception {
		Json ret = super.perform(in);
		if (ret == null) {
			ret = in;
		} else if (HeadHelper.isFailed(ret)) {
			return ret;
		}
		
		Json result = null; 
		try{
			RemoteExecutor remoteExecutor = RemoteFactory.getHttpExecutor();
			ret.getObject().addField(RetHead.SERVICE.value(), getServiceId());
			result = remoteExecutor.execute(ret);			
		}
		catch(Exception e){
			logger.error("========>>远程服务执行失败:"+e.getMessage());
			throw e;
		}
		finally{
			if(result != null && HeadHelper.isSuccess(result)){
				commit();
			}
			else{
				rolback();
			}
		}
		return result;
	}

	@Override
	protected void commit(Transaction tran,TransactionStatus status){
		if (logger.isDebugEnabled()) {
			logger.debug("HttpUrlRemoteService========>>准备提交数据库事务");
		}
		if (tran != null) {
			this.tran = tran;
			this.status = status;
		}
	}

	@Override
	protected Json validate(Json in) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void commit(){
		if (tran != null) {
			tran.commit(status);
			tran = null;
			status = null;
			if (logger.isDebugEnabled()) {
				logger.debug("HttpUrlRemoteService========>>已提交数据库事务");
			}
		}
	}
	
	private void rolback(){
		if (tran != null) {
			tran.rollback(status);
			tran = null;
			status = null;
			if (logger.isDebugEnabled()) {
				logger.debug("HttpUrlRemoteService========>>已回滚数据库事务");
			}
		}
	}
}