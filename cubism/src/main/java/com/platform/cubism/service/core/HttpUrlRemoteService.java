package com.platform.cubism.service.core;

import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.jdbc.tran.Transaction;
import com.platform.cubism.jdbc.tran.TransactionStatus;
import com.platform.cubism.service.remote.RemoteExecutor;
import com.platform.cubism.service.remote.RemoteFactory;
import com.platform.cubism.struc.RetHead;
import com.platform.cubism.util.HeadHelper;

public class HttpUrlRemoteService extends GenericService {
	private Transaction tran = null;
	private TransactionStatus status = null;
	
	public HttpUrlRemoteService(String serviceId) {
		super(serviceId);
	}

	@Override
	public Json perform(Json in) throws Exception {
		Json result = null; 
		Json ret = null;
		try{
			ret = super.perform(in);
			if (ret == null) {
				ret = in;
			} else if (HeadHelper.isFailed(ret)) {
				rolback();
				return ret;
			}
			
			RemoteExecutor remoteExecutor = RemoteFactory.getHttpUrlExecutor();
			ret.getObject().addField(RetHead.SERVICE.value(), getServiceId());
			result = remoteExecutor.execute(ret);	
			if(result == null){
				result = JsonFactory.create();
			}
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
		if(ret != null){
			result.addOf(ret);
		}
		
		return result;
	}
	
	@Override
	protected void commit(Transaction tran,TransactionStatus status){
		if (logger.isDebugEnabled()) {
			logger.debug("HttpUrlRemoteService========>>准备提交数据库事务");
		}
		if (tran != null) {
			if(this.tran != null){
				commit();//连接计数器减一，连接不会释放，事物不会提交
			}
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