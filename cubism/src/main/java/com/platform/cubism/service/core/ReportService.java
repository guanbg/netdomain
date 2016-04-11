package com.platform.cubism.service.core;

import javax.servlet.http.HttpServletResponse;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.jdbc.tran.Transaction;
import com.platform.cubism.jdbc.tran.TransactionStatus;
import com.platform.cubism.processor.RptProcessor;
import com.platform.cubism.service.Report;
import com.platform.cubism.service.config.ServiceConfig;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.struc.PageHead;
import com.platform.cubism.struc.RetStatus;
import com.platform.cubism.util.CubismHelper;
import com.platform.cubism.util.HeadHelper;
import com.platform.cubism.util.ReflectionUtils;

public class ReportService extends GenericService implements Report {
	private Transaction tran = null;
	private TransactionStatus status = null;
	
	public ReportService(String serviceId) {
		super(serviceId);
	}

	public Json execute(Json in, HttpServletResponse response) throws CubismException {
		boolean processFlag = false;//是否成功处理过数据
		RptProcessor proc = (RptProcessor)ReflectionUtils.instantiateClass(getServiceProcessor());
		if(proc == null){
			return JsonFactory.create().addStruc(HeadHelper.createRetHead(CubismHelper.getServiceFullName(getServiceId()), "20000", "找不到解析器", MsgLevel.D));
		}
		if(!proc.start(in)){
			proc.end(new CubismException("open file error"));
			return JsonFactory.create().addStruc(HeadHelper.createRetHead(CubismHelper.getServiceFullName(getServiceId()), "20001", "Report服务处理器初始化错误", MsgLevel.D));
		}
		
		try{
			Json ret = null;
			int curr = 1;
			do{
				if(curr == 1){//第一次
					//pagehead:{ currentpage:"", totalpage:"", pagecount:"", totalrecord:""}
					HeadHelper.createPageHead(in,String.valueOf(curr),"0","100","0");
				}
				else{
					in.getStruc(PageHead.PAGEHEAD.value()).getField(PageHead.CURRENTPAGE.value()).setValue(String.valueOf(curr));
				}
				
				curr++;
				ret = execute(in, false);
				if (logger.isDebugEnabled()) {
					logger.debug("==>ReportService返回结果集：" + ret);
				}
				if (ret == null || ret.isEmpty()){
					rolback();
					return JsonFactory.create().addStruc(HeadHelper.createRetHead(CubismHelper.getServiceFullName(getServiceId()), "20002", "没有数据", MsgLevel.D));
				}else if(HeadHelper.isFailed(ret)) {
					rolback();
					return ret;
				}
				if(!proc.process(ret)){//无需要处理的数据
					break;
				}
				processFlag = true;
			}while(hasNext(ret));
			
			if(processFlag){
				proc.end(response);//写数据并返回到浏览器
				commit();
				
				return JsonFactory.create().addStruc(HeadHelper.createRetHead(CubismHelper.getServiceFullName(getServiceId()), "000000", "Report服务执行完毕", MsgLevel.B, RetStatus.SUCCESS));
			}
			else{
				proc.end(new CubismException("no data to process"));
				rolback();
				return JsonFactory.create().addStruc(HeadHelper.createRetHead(CubismHelper.getServiceFullName(getServiceId()), "20002", "没有数据", MsgLevel.D));
			}
		}
		catch(Throwable e){
			logger.error("========>>Report服务执行失败:"+e.getMessage());
			if(logger.isDebugEnabled()){
				e.printStackTrace();
			}
			proc.end(e);
			throw new CubismException(e.getMessage());
		}
		finally{
			rolback();
		}
	}
	
	@Override
	protected void commit(Transaction tran,TransactionStatus status){
		if (logger.isDebugEnabled()) {
			logger.debug("ReportService========>>准备提交数据库事务");
		}
		if (tran != null) {
			if(this.tran != null){
				commit();////连接计数器减一，连接不会释放，事物不会提交
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
				logger.debug("ReportService========>>已提交数据库事务");
			}
		}
	}
	
	private void rolback(){
		if (tran != null) {
			tran.rollback(status);
			tran = null;
			status = null;
			if (logger.isDebugEnabled()) {
				logger.debug("ReportService========>>已回滚数据库事务");
			}
		}
	}
	
	private String getServiceProcessor(){
		ServiceConfig sc = getServiceConfig();
		return sc.getProcessor();
	}
	
	private boolean hasNext(Json ret){
		CStruc pagehead = ret.getStruc(PageHead.PAGEHEAD.value());
		if(pagehead == null || pagehead.isEmpty()){
			return false;
		}
		
		int currp = Integer.parseInt(pagehead.getField(PageHead.CURRENTPAGE.value()).getValue());
		int count = Integer.parseInt(pagehead.getField(PageHead.PAGECOUNT.value()).getValue());
		int ctotal = currp * count;
		int rtotal = Integer.parseInt(pagehead.getField(PageHead.TOTALRECORD.value()).getValue());
		
		if (rtotal < 1) {
			return false;
		}
		if (ctotal >= rtotal) {
			return false;
		}
		return true;
	}
}