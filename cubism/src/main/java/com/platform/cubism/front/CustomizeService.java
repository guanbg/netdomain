package com.platform.cubism.front;

import static com.platform.cubism.util.CubismHelper.getRequestParam;
import static com.platform.cubism.util.HeadHelper.getSysHead;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.struc.RetHead;
import com.platform.cubism.struc.RetStatus;
import com.platform.cubism.struc.SysHead;
import com.platform.cubism.util.HeadHelper;

@WebServlet(urlPatterns = "*.class", asyncSupported = false)
public class CustomizeService extends Configure {
	private static final long serialVersionUID = -3547603764338316027L;

	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Json in = getRequestParam(request);
		String serviceName = getSysHead(SysHead.SERVICENAME.value(),in)+".class";
		if (logger.isDebugEnabled()) {
			logger.debug("=========================>>>服务名称：" + serviceName);
			logger.debug("=========================>>>上送报文：" + in);
		}
		Json ret = ServiceFactory.executeService(serviceName, in, request);
		if (ret == null) {
			ret = JsonFactory.create().addStruc(HeadHelper.createRetHead(serviceName, "00000", "服务执行完毕", MsgLevel.B, RetStatus.SUCCESS));
		}
		else if (ret.getStruc(RetHead.RETHEAD.value()) == null){
			ret.addStruc(HeadHelper.createRetHead(serviceName, "00000", "服务执行完毕", MsgLevel.B, RetStatus.SUCCESS));
		}
		ret.getObject().setName(null);// 返回报文的顶层直接为:{......}的形式
		if (logger.isInfoEnabled()) {
			logger.info("=====>>>请求的服务 [" + serviceName + "]执行完毕，返回报文为：" + ret.toString());
		}
		try {
			response.getWriter().print(ret.toJson());
			response.flushBuffer();
			response.getWriter().close();
		} catch (Throwable e) {
			logger.error("===>>"+serviceName+":不能返回错误报文到客户端：" + e.getMessage());
		}
	}

	@Override
	protected void doAsyncService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		doService(request, response);
	}
}