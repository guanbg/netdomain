package com.platform.cubism.front;

import static com.platform.cubism.util.CubismHelper.getServiceName;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.util.HeadHelper;
import com.platform.cubism.util.MultipartUtils;

public abstract class Configure extends HttpServlet {
	private static final long serialVersionUID = 2692730536970348940L;
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected static final ThreadLocal<HttpServletRequest> serviceRequest = new ThreadLocal<HttpServletRequest>();
	protected static final ThreadLocal<HttpServletResponse> serviceResponse = new ThreadLocal<HttpServletResponse>();
	private boolean isAsync = false;
	private boolean dispatchOptionsRequest = false;

	private boolean dispatchTraceRequest = false;

	@Override
	protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (MultipartUtils.isMultipart(request)) {
			request.getRequestDispatcher("upload").forward(request, response);
			return;
		}
		processRequest(request, response);
	}

	@Override
	protected final void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected final void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		super.doOptions(request, response);
		if (this.dispatchOptionsRequest) {
			processRequest(request, response);
		}
	}

	@Override
	protected void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		super.doTrace(request, response);
		if (this.dispatchTraceRequest) {
			processRequest(request, response);
		}
	}

	@Override
	public void destroy() {
		getServletContext().log("Destroying '"  + "'");
		logger.debug("the servlet is destroying " + getServletName());
	}
	
	public static HttpServletRequest getRequest(){
		return serviceRequest.get();
	}
	
	public static HttpServletResponse getResponse(){
		return serviceResponse.get();
	}
	
	protected final void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		serviceRequest.set(request);
		serviceResponse.set(response);

		response.setHeader("Cache-Control", "private");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Connection", "Keep-Alive");
		response.setHeader("Proxy-Connection", "Keep-Alive");
		long startTime = System.currentTimeMillis();
		try {
			isAsync = request.isAsyncSupported();
			if (!isAsync) {
				logger.info("the servlet is not supported Async");
				doService(request, response);
			} else {
				logger.debug("the servlet is supported Async");
				doAsyncService(request, response);
			}
		} catch (CubismException ce) {
			Json ret = JsonFactory.create().addStruc(
					HeadHelper.createRetHead(getServiceName(request.getServletPath()), "40004", "服务执行时发生系统级错误:" + ce.getMessage(), MsgLevel.D));
			response.getWriter().print(ret.toJson());
			response.flushBuffer();
		} catch (Throwable e) {
			Json ret = JsonFactory.create().addStruc(
					HeadHelper.createRetHead(getServiceName(request.getServletPath()), "40007", "系统请求时发生系统级错误:" + e.getMessage(), MsgLevel.D));
			response.getWriter().print(ret.toJson());
			response.flushBuffer();
		} finally {
			if (!isAsync && logger.isInfoEnabled()) {
				long processingTime = System.currentTimeMillis() - startTime;
				logger.info("===>>>Service total spend time:" + processingTime + "ms(毫秒)");
			}
		}
	}

	protected abstract void doService(HttpServletRequest request, HttpServletResponse response) throws Exception;

	protected abstract void doAsyncService(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
