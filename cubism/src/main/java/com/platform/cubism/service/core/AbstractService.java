package com.platform.cubism.service.core;

import static com.platform.cubism.util.StringUtils.hasText;
import static com.platform.cubism.service.convert.ConvertManager.IN;
import static com.platform.cubism.service.log.LogWriter.LOGBEFORE;

import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.Service;
import com.platform.cubism.service.log.DefaultLogWriter;
import com.platform.cubism.service.log.LogWriter;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.struc.RetHead;
import com.platform.cubism.struc.RetStatus;
import com.platform.cubism.struc.SysHead;
import com.platform.cubism.util.HeadHelper;

public abstract class AbstractService extends ConfigParser implements Service {
	private LogWriter logWriter = null;

	public AbstractService(String serviceId) {
		super(serviceId);

		if (hasLog()) {
			logWriter = DefaultLogWriter.getLogger(getLogElement());
		}
	}

	public Json execute(Json in) {
		return execute(in, false);
	}

	public Json execute(Json in, boolean isDirect) {
		if(HeadHelper.isFailed(in) && !isDirect){
			if (logger.isInfoEnabled()) {
				logger.info("该服务上送报文中存在终止后继服务的头信息,终止执行服务"+getServiceId()+",直接返回。");
			}
			return JsonFactory.create();
		}
		
		Json jsonIn = inMapping(in);
		Json valiRet = validate(jsonIn);
		if (valiRet != null) {
			return valiRet;
		}

		Json result = JsonFactory.create();
		if (isDirect) {
			result.mergeOf(in);
		} else {
			if(jsonIn.contains(IN)){
				result.mergeOf(jsonIn);
			}
			else{
				result.addStruc(jsonIn.getObject());
			}
		}
		if (logWriter != null) {
			Json ret = logWriter.beginLog(jsonIn);
			if (ret == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("该服务已配置日志选项，但日志记录条件不满足，不记录before日志。");
				}
			} else if (!HeadHelper.isSuccess(ret)) {
				logger.error("before日志服务执行错误：" + ret);
				return ret;
			} else {
				ret.remove(IN);
				ret.remove(RetHead.RETHEAD.value());
				result.mergeOf(ret);
			}
		}
		try {
			Json jsonSrv = perform(result);
			if(jsonSrv == null){//空服务
				result.addStruc(HeadHelper.createRetHead(getServiceId(), "00000", "空服务执行成功", MsgLevel.B, RetStatus.SUCCESS));
			}
			else{
				if (result.getStruc(LOGBEFORE) != null && jsonSrv.getStruc(LOGBEFORE) != null) {
					jsonSrv.remove(LOGBEFORE);
				}
				result.mergeOf(jsonSrv);
				HeadHelper.addAllHead(result, jsonSrv, getServiceId());
			}
		} catch (Throwable e) {
			logger.error(getServiceId()+"服务执行错误：" + e.getMessage());
			if(logger.isDebugEnabled()){
				logger.debug(in.toString());
				logger.debug(result.toString());
				e.printStackTrace();
			}
			if(logger.isInfoEnabled()){
				logger.info(in.toString());
				logger.info(result.toString());
			}
			String errcode = "10001";
			String errmsg = e.getMessage();
			if (getCurrentServiceEntity() != null) {
				if (hasText(getCurrentServiceEntity().getErrorcode())) {
					errcode = getCurrentServiceEntity().getErrorcode();
				}
				if (hasText(getCurrentServiceEntity().getErrormsg())) {
					errmsg = getCurrentServiceEntity().getErrormsg();
				}
			}
			result.remove(RetHead.RETHEAD.value());
			result.addStruc(HeadHelper.createRetHead(getServiceId(), errcode, errmsg, MsgLevel.D));
		} finally {
			if (logWriter != null) {
				logWriter.endLog(result);
			}
		}

		Json jsonOut = outMapping(result);
		// jsonOut.remove("logbefore");
		
		if(!jsonOut.contains(SysHead.SYSHEAD.value())){
			if(jsonOut.contains(IN)){
				jsonOut.addStruc(jsonOut.getStruc(IN).getStruc(SysHead.SYSHEAD.value()));
				jsonOut.remove(IN);
			}
		}
		return jsonOut;
	}

	protected abstract Json validate(Json in);

	protected abstract Json perform(Json in) throws Exception;
}