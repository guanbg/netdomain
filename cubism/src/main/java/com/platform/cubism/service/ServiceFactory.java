package com.platform.cubism.service;

import static com.platform.cubism.service.config.ServiceConfigManager.getServiceType;
import static com.platform.cubism.util.CubismHelper.getServiceFullName;
import static com.platform.cubism.util.HeadHelper.getSysHead;
import static com.platform.cubism.util.StringUtils.hasText;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.core.CustomizeService;
import com.platform.cubism.service.core.GenericService;
import com.platform.cubism.service.core.HttpRemoteService;
import com.platform.cubism.service.core.HttpUrlRemoteService;
import com.platform.cubism.service.core.JmsRemoteService;
import com.platform.cubism.service.core.ReportService;
import com.platform.cubism.service.core.SocketRemoteService;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.struc.SysHead;
import com.platform.cubism.util.CubismHelper;
import com.platform.cubism.util.HeadHelper;

public abstract class ServiceFactory {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ServiceFactory.class);
	
	public static boolean isReportService(String servicename) {
		return getServiceType(CubismHelper.getServiceName(servicename)) == ServiceType.local_report;
	}
	
	public static Service getService(String servicename) {
		ServiceType st = getServiceType(CubismHelper.getServiceName(servicename));
		if (st == null) {
			return null;
		}
		switch (st) {
		case local:
			return new GenericService(servicename);
		case remote_url:
			return new HttpUrlRemoteService(servicename);
		case remote_http:
			return new HttpRemoteService(servicename);
		case remote_socket:
			return new SocketRemoteService(servicename);
		case remote_jms:
			return new JmsRemoteService(servicename);
		case remote_auto:
			return null;
		case customService:
			break;
		case httpService:
			break;
		case local_report:
			break;
		case unknown:
			break;
		case webService:
			break;
		default:
			break;
		}
		return null;
	}

	/***********************************************通用服务************************************************/
	public static Object getExecuteResult(Json in, String fieldname){
		String cacheKeyParam = null;
		if(in.contains(ServiceCache.IN_CACHE_KEY_PARAM)){
			cacheKeyParam = in.getFieldValue(ServiceCache.IN_CACHE_KEY_PARAM);
		}
		else{
			cacheKeyParam = in.toJson();
		}
		final String servicename = getServiceFullName(getSysHead(SysHead.SERVICENAME.value(), in));
		Json ret, cache = ServiceCache.getServiceCache(servicename, cacheKeyParam);
		if(cache != null && !cache.isEmpty()){
			ret = cache;
		}
		else{
			if(in.contains(ServiceCache.IN_CACHE_KEY_PARAM)){
				in.remove(ServiceCache.IN_CACHE_KEY_PARAM);
			}
			ret = executeService(in);
			if(HeadHelper.isSuccess(ret)){
				ServiceCache.setServiceCache(servicename, cacheKeyParam, ret);
				ServiceCache.clearServiceCache(servicename);
			}
		}
		if(ret.contains(fieldname)){
			if (ret.get(fieldname) instanceof CField) {
				return ret.getField(fieldname).getValue();
			}
			return ret.get(fieldname);
		}
		return null;
	}
	
	public static Json executeService(Json in) throws CubismException {
		String servicename = HeadHelper.getSysHead(SysHead.SERVICENAME, in);
		if (!hasText(servicename)) {
			return JsonFactory.create().addStruc(HeadHelper.createRetHead(servicename, "10002", "无此服务", MsgLevel.D));
		}
		
		return executeService(servicename, in);
	}

	public static Json executeService(String servicename, Json in) throws CubismException {
		String cacheKeyParam = null;
		if(in.contains(ServiceCache.IN_CACHE_KEY_PARAM)){
			cacheKeyParam = in.getFieldValue(ServiceCache.IN_CACHE_KEY_PARAM);
		}
		else{
			cacheKeyParam = in.toJson();
		}
		Json ret, cache = ServiceCache.getServiceCache(servicename, cacheKeyParam);
		if(cache != null && !cache.isEmpty()){
			ret = cache;
		}
		else{
			if(in.contains(ServiceCache.IN_CACHE_KEY_PARAM)){
				in.remove(ServiceCache.IN_CACHE_KEY_PARAM);
			}
			ret = executeService(servicename, in, false);
			if(HeadHelper.isSuccess(ret)){
				ServiceCache.setServiceCache(servicename, cacheKeyParam, ret);
				ServiceCache.clearServiceCache(servicename);
			}
		}
		return ret;
	}

	public static Json executeService(String servicename, Json in, boolean isDirect) throws CubismException {
		try {
			if (!hasText(servicename)) {
				return executeService(in);
			} else {
				Service srv = getService(servicename);
				if (srv == null) {
					return JsonFactory.create().addStruc(HeadHelper.createRetHead(servicename, "10002", "无此服务", MsgLevel.D));
				}
				return srv.execute(in, isDirect);
			}
		} catch (Exception e) {
			logger.error("==============>>服务执行未知错误,错误信息:" + e.getMessage());
			return JsonFactory.create().addStruc(HeadHelper.createRetHead(servicename, "10003", "服务执行未知错误", MsgLevel.D));
		} catch (Throwable t) {
			logger.error("==============>>服务执行系统级错误,错误信息:" + t.getMessage());
			return JsonFactory.create().addStruc(HeadHelper.createRetHead(servicename, "10004", "服务执行系统级错误", MsgLevel.D));
		}
	}
	
	/***********************************************自定义服务，类服务************************************************/
	public static Json executeService(Json in, HttpServletRequest request) throws CubismException {
		String servicename = HeadHelper.getSysHead(SysHead.SERVICENAME, in);
		if (!hasText(servicename)) {
			return JsonFactory.create().addStruc(HeadHelper.createRetHead(servicename, "10002", "无此服务", MsgLevel.D));
		}
		return executeService(servicename, in, request);
	}
	public static Json executeService(String servicename, Json in, HttpServletRequest request) throws CubismException {
		CustomizeService cs = new CustomizeService(servicename);
		if(cs.getServiceType() == ServiceType.customService){
			CustomService srv = cs.getCustomService();
			return srv.execute(in);
		}
		else if(cs.getServiceType() == ServiceType.httpService){
			HttpService srv = cs.getHttpService();
			return srv.execute(in, request);
		}
		return JsonFactory.create().addStruc(HeadHelper.createRetHead(servicename, "10002", "无此服务或不能识别的服务", MsgLevel.D));
	}
	
	/***********************************************报表服务，文件服务************************************************/
	public static Json executeService(Json in, HttpServletResponse response) throws CubismException {
		String servicename = HeadHelper.getSysHead(SysHead.SERVICENAME, in);
		if (!hasText(servicename)) {
			return JsonFactory.create().addStruc(HeadHelper.createRetHead(servicename, "10002", "无此服务", MsgLevel.D));
		}
		return executeService(servicename, in, response);
	}
	public static Json executeService(String servicename, Json in, HttpServletResponse response) throws CubismException {
		try {
			if (!hasText(servicename)) {
				return executeService(in, response);
			}
			else {
				Report rpt = new ReportService(servicename);
				return rpt.execute(in, response);
			}
		} catch (Throwable t) {
			logger.error("==============>>服务执行系统级错误,错误信息:" + t.getMessage());
			return JsonFactory.create().addStruc(HeadHelper.createRetHead(servicename, "10004", "服务执行系统级错误", MsgLevel.D));
		}
	}
}