package com.platform.cubism.front.login;

import static com.platform.cubism.util.CubismHelper.getIpAddr;
import static com.platform.cubism.util.CubismHelper.streamToString;
import static com.platform.cubism.util.StringUtils.hasText;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.util.HeadHelper;

@WebServlet(name = "offlineProgressSummaryReport", urlPatterns = { "/offline.progress.summary.report" })
public class OfflineProgressSummaryReport extends HttpServlet {
	private static final long serialVersionUID = 1565731186178172123L;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String json = null;
		try {
			json = streamToString(request.getReader());
		} catch (IOException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,"无效请求");
			logger.error("非法访问，IP："+getIpAddr(request));
			return ;
		}
		if (!hasText(json)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN,"无效请求");
			logger.error("非法访问，IP："+getIpAddr(request));
			return ;
		}
		
		Json in = JsonFactory.create().toJson(json);
		if(in == null || in.isEmpty() || in.getFieldValue("documentor_id").length() <= 0 || in.getFieldValue("documentor_name").length() <= 0){
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"不允许访问");
			logger.error("非法访问，参数错误，IP："+getIpAddr(request));
			return;
		}
		
		in.addField("ip", getIpAddr(request));
		in.addField("agent", request.getHeader("USER-AGENT"));
		if (logger.isDebugEnabled()) {
			logger.debug("===离线端调用服务===>" + in);
		}
		
		String serviceName = "offline.progress.summary.report";
		Json ret = ServiceFactory.executeService(serviceName, in);
		if(HeadHelper.isFailed(ret)){
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED,"访问错误");
			logger.error("===离线端调用服务===>："+ret);
			return;
		}
		
		ret.getObject().setName(null);
		response.getWriter().print(ret.toJson());
		response.flushBuffer();
	}
}
