package com.platform.cubism.service.core;

import static com.platform.cubism.service.CustomService.logger;
import static com.platform.cubism.util.StringUtils.hasText;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.CArray;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.jdbc.SqlExecutor;
import com.platform.cubism.jdbc.SqlFactory;
import com.platform.cubism.jdbc.conn.DataSourceManager;
import com.platform.cubism.jdbc.tran.DataSourceTransaction;
import com.platform.cubism.jdbc.tran.Transaction;
import com.platform.cubism.jdbc.tran.TransactionStatus;
import com.platform.cubism.processor.ExpProcessor;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.util.Assert;
import com.platform.cubism.util.HeadHelper;
import com.platform.cubism.util.ReflectionUtils;

public class ExpScriptEntity extends AbstractEntity {
	private TransactionStatus status;
	private ExpExecAsync expexecasync;
	
	public ExpScriptEntity() {
		;
	}

	public ExpScriptEntity(ServiceEntity entity) {
		super(entity);
	}

	@Override
	public void setServiceEntity(ServiceEntity entity) {
		Assert.state(entity.getType() == EntityType.exp);
		setEntity(entity);
	}

	@Override
	public void setTransactionStatus(TransactionStatus status) {
		this.status = status;
	}

	public Json execute(Json in) throws Exception {
		String sql = getValue();
		if (!hasText(sql)) {
			return null;
		}
		getServiceEntity().setAlwayarray(true);
		getServiceEntity().setLowercase(true);
		if(getServiceEntity().isAsync()){
			return executeAsync(in);//异步
		}
		else{
			return executeSync(in);//同步
		}
	}
	
	@Override
	public void success(Json in) throws Exception{
		if(getServiceEntity().isAsync() && expexecasync != null){
			new Thread(expexecasync).start();
		}
	}
	
	@Override
	public void failure(Json in) throws Exception{
		if(expexecasync != null){
			expexecasync.proc.end(new CubismException("create file failure"));
		}
	}

	public Json executeSync(Json in) throws Exception {//同步
		boolean processFlag = false;//是否成功处理过数据
		SqlExecutor sqlExec = SqlFactory.getSqlExecutor(getServiceEntity(), status);
		if (sqlExec == null) {
			return JsonFactory.create().addStruc(HeadHelper.createRetHead(getId(), "20005", "查询数据错误", MsgLevel.D));
		}
		//pagehead:{ currentpage:"", totalpage:"", pagecount:"", totalrecord:""}
		HeadHelper.createPageHead(in,"1","0",String.valueOf(getServiceEntity().getFetch()),"0");
		Json json = sqlExec.execute(getValue(), in);
		CArray ca = json.getArray(getId());
		if(ca == null || ca.isEmpty()){
			return JsonFactory.create().addStruc(HeadHelper.createRetHead(getId(), "20006", "没有数据", MsgLevel.D));
		}
		ExpProcessor proc = (ExpProcessor)ReflectionUtils.instantiateClass(getServiceEntity().getProcessor());
		if(proc == null){
			return JsonFactory.create().addStruc(HeadHelper.createRetHead(getId(), "20007", "找不到解析器", MsgLevel.D));
		}
		if(!proc.start(in)){
			proc.end(new CubismException("open file error"));
			return JsonFactory.create().addStruc(HeadHelper.createRetHead(getId(), "20008", "不能正确初始化文件", MsgLevel.D));
		}
		try{
			do{
				if(!proc.process(ca)){
					break;
				}
				processFlag = true;
				json = sqlExec.nextPage();
				ca = json.getArray(getId());
			}while(ca != null && !ca.isEmpty());
			
			if(processFlag){
				proc.end();
				
				Json js = proc.getResult();
				js.getObject().setName(getId());
				
				Json ret = JsonFactory.create();
				ret.addStruc(js.getObject());
				return ret;
			}
			else{
				proc.end(new CubismException("no data to process"));
				return JsonFactory.create().addStruc(HeadHelper.createRetHead(getId(), "20009", "没有数据", MsgLevel.D));
			}
		}catch(Throwable e){
			logger.error("========>>服务执行失败:"+e.getMessage());
			proc.end(e);
			throw new Exception(e);
		}
	}
	public Json executeAsync(Json in) throws Exception {//异步
		expexecasync = new ExpExecAsync(in, getServiceEntity());
		if(!expexecasync.flag){
			return JsonFactory.create().addStruc(HeadHelper.createRetHead(getId(), "20008", "初始化解析器错误", MsgLevel.D));
		}
		Json js = expexecasync.getResult();
		js.getObject().setName(getId());
		
		Json ret = JsonFactory.create();
		ret.addStruc(js.getObject());
		
		return ret;
	}
	private final static class ExpExecAsync implements Runnable{
		private Json in;
		private ServiceEntity se;
		private ExpProcessor proc;
		private boolean flag;
		
		public ExpExecAsync(Json in, ServiceEntity se){
			this.in = in;
			this.se = se;
			try{
				this.proc = (ExpProcessor)ReflectionUtils.instantiateClass(se.getProcessor());
			}catch(Throwable e){
				flag = false;
				return;
			}
			if(this.proc == null){
				flag = false;
			}
			else{
				flag = proc.start(in);
			}
			
		}
		public void run() {
			if(!flag){
				proc.end(new CubismException("open file error"));
				return;
			}
			boolean processFlag = false;//是否成功处理过数据
			Transaction tran = new DataSourceTransaction().setDataSource(DataSourceManager.getDataSource());
			TransactionStatus status = tran.getTransaction(null);
				
			try{
				SqlExecutor sqlExec = SqlFactory.getSqlExecutor(se, status);
				if (sqlExec == null) {
					return;
				}
				//pagehead:{ currentpage:"", totalpage:"", pagecount:"", totalrecord:""}
				HeadHelper.createPageHead(in,"1","0",String.valueOf(se.getFetch()),"0");
				Json json = sqlExec.execute(se.getValue(), in);
				CArray ca = json.getArray(se.getId());
				if(ca == null || ca.isEmpty()){
					return;
				}
				do{
					flag = proc.process(ca);
					if(!flag){
						break;
					}
					processFlag = true;
					json = sqlExec.nextPage();
					ca = json.getArray(se.getId());
				}while(ca != null && !ca.isEmpty());
				
				if(processFlag){
					proc.end();					
					tran.commit(status);
					return;
				}
				else{
					proc.end(new CubismException("no data to process"));
					tran.rollback(status);
					return;
				}
			}catch(Throwable e){
				tran.rollback(status);
				tran = null;
				status = null;
				proc.end(e);
				return;
			}
			finally{
				tran.rollback(status);;
				tran = null;
				status = null;
			}
		}
		public Json getResult(){
			if(flag){
				return proc.getResult();
			}
			else{
				return null;
			}
		}
	}
}