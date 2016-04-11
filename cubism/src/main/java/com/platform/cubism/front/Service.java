package com.platform.cubism.front;

import static com.platform.cubism.util.CubismHelper.getDownloadFileName;
import static com.platform.cubism.util.CubismHelper.getRequestParam;
import static com.platform.cubism.util.CubismHelper.getServiceFullName;
import static com.platform.cubism.util.HeadHelper.getRetHeadFirstMsg;
import static com.platform.cubism.util.HeadHelper.getRetHeadMsg;
import static com.platform.cubism.util.HeadHelper.getSysHead;
import static com.platform.cubism.util.StringUtils.hasText;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.SystemConfig;
import com.platform.cubism.base.Json;
import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.struc.RetStatus;
import com.platform.cubism.struc.SysHead;
import com.platform.cubism.util.Assert;
import com.platform.cubism.util.CubismHelper;
import com.platform.cubism.util.HeadHelper;

@WebServlet(urlPatterns = "*.service", asyncSupported = true)
public class Service extends Configure {
	private static final long serialVersionUID = -8487448425440860L;
	private static ExecutorService serviceExecutor = null;
	private static int serviceTimeOut = 120;

	@Override
	public void init(ServletConfig config) {
		int size = 100;
		String servicepoolsize = config.getServletContext().getInitParameter("servicepoolsize");
		String servicetimeout = config.getServletContext().getInitParameter("servicetimeout");
		if (servicepoolsize != null && !"".equals(servicepoolsize)) {
			size = Integer.parseInt(servicepoolsize);
		} else {
			servicepoolsize = SystemConfig.getServicePoolSize();
			if (servicepoolsize != null && !"".equals(servicepoolsize)) {
				size = Integer.parseInt(servicepoolsize);
			}
		}
		if (servicetimeout != null && !"".equals(servicetimeout)) {
			serviceTimeOut = Integer.parseInt(servicetimeout);
		} else {
			servicetimeout = SystemConfig.getServiceTimeOut();
			if (servicetimeout != null && !"".equals(servicetimeout)) {
				serviceTimeOut = Integer.parseInt(servicetimeout);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("===>>服务执行缓冲区[servicepoolsize]的初始化大小为：" + size);
			logger.info("===>>服务执行超时时长[servicetimeout]为：" + serviceTimeOut + "秒");
		}
		if (size > 0) {
			serviceExecutor = Executors.newFixedThreadPool(size);
		}
		try {
			super.init(config);
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		if (serviceExecutor != null && !serviceExecutor.isShutdown()) {
			serviceExecutor.shutdown();
			serviceExecutor = null;
		}

		super.destroy();
	}

	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Json in = getRequestParam(request);
		String serviceName = getSysHead(SysHead.SERVICENAME.value(), in);
		if (logger.isDebugEnabled()) {
			logger.debug("===>>服务名称：" + CubismHelper.getServiceFullName(serviceName));
			logger.debug("===>>上送报文：" + in);
		}
		if (ServiceFactory.isReportService(serviceName)) {
			Json ret = ServiceFactory.executeService(serviceName, in, response);
			if (!HeadHelper.isSuccess(ret)) {
				String filename = getRetHeadFirstMsg(ret);
				if (!hasText(filename)) {
					filename = "CubismError.txt";
				} else {
					filename += ".txt";
				}
				filename = getDownloadFileName(filename, request);
				// response.sendError(404, filename);
				try {
					response.setContentType("application/rtf; charset=UTF-8");
					response.setHeader("Content-Disposition", "attachment; filename=" + filename);
					response.getWriter().print(getRetHeadMsg(ret));
				} catch (Throwable t) {
					logger.info("写响应流错误：" + t.getMessage());
				}
				/*
				 * ret.getObject().setName(null);// 返回报文的顶层直接为:{......}的形式 if
				 * (logger.isDebugEnabled()) { logger.debug("===>>请求的服务 [" +
				 * CubismHelper.getServiceFullName(serviceName) + "]执行完毕，返回报文为："
				 * + ret.toString()); }
				 * response.getWriter().print(ret.toJson());
				 */
			}
			try {
				response.flushBuffer();
				response.getWriter().close();
			} catch (Throwable t) {
				logger.info("写响应流错误：" + t.getMessage());
			}
		} else {
			Json ret = ServiceFactory.executeService(serviceName, in);

			ret.getObject().setName(null);// 返回报文的顶层直接为:{......}的形式
			if (logger.isDebugEnabled()) {
				logger.debug("===>>请求的服务 [" + CubismHelper.getServiceFullName(serviceName) + "]执行完毕，返回报文为：" + ret.toString());
			}
				
			String res = ret.toJson();
			if (res == null || res.length() <= 0) {
				res = HeadHelper.createRetHead(serviceName, "10009", "服务执行后无返回信息", "D", "F");
				logger.error(res);
			}
			try {
				response.getWriter().print(res);
				// response.getWriter().print(ret.toJson());
				response.flushBuffer();
				response.getWriter().close();
			} catch (Throwable t) {
				logger.info("写响应流错误：" + t.getMessage());
			}
			ret = null;
		}
		in = null;
	}

	@Override
	protected void doAsyncService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Assert.notNull(serviceExecutor);
		Json in = getRequestParam(request);
		final String serviceName = getServiceFullName(getSysHead(SysHead.SERVICENAME.value(), in));
		
		AsyncContext asyncContext = request.startAsync();
		asyncContext.setTimeout(1000 * serviceTimeOut);// 服务超时时间设置
		asyncContext.addListener(new AsyncListener() {
			private long startTime = System.currentTimeMillis();

			public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
				// 做一些准备工作或者其他
				if (logger.isDebugEnabled()) {
					logger.debug("===>>" + serviceName + ":异步服务开始执行......");
				}
			}

			public void onComplete(AsyncEvent asyncEvent) throws IOException {
				// 做一些清理工作或者其他
				if (logger.isInfoEnabled()) {
					long processingTime = System.currentTimeMillis() - startTime;
					logger.info("3===>>" + serviceName + ":异步服务执行结束,AsyncService total spend time:" + processingTime + "ms(毫秒)");
				}
			}

			public void onError(AsyncEvent asyncEvent) throws IOException {
				if (logger.isInfoEnabled()) {
					long processingTime = System.currentTimeMillis() - startTime;
					logger.info("2===>>" + serviceName + ":服务调用错误,AsyncService total spend time:" + processingTime + "ms(毫秒)");
				}
				ServletResponse response = asyncEvent.getSuppliedResponse();
				if (response == null || response.isCommitted()) {
					return;
				}
				try {
					response.getWriter().print(
							"{" + HeadHelper.createRetHead(serviceName, "99998", "服务调用错误，请查看系统日志", MsgLevel.D, RetStatus.FAILED).toJson() + "}");
					response.flushBuffer();
					response.getWriter().close();
				} catch (Throwable e) {
					logger.error("===>>" + serviceName + ":服务运行超时,不能返回错误报文到客户端：" + e.getMessage());
				} finally {
					asyncEvent.getAsyncContext().complete();
				}
			}

			public void onTimeout(AsyncEvent asyncEvent) throws IOException {
				if (logger.isInfoEnabled()) {
					long processingTime = System.currentTimeMillis() - startTime;
					logger.info("2===>>" + serviceName + ":服务调用超时(错误), AsyncService total spend time:" + processingTime + "ms(毫秒)");
				}
				ServletResponse response = asyncEvent.getSuppliedResponse();
				if (response == null || response.isCommitted()) {
					return;
				}
				try {
					response.getWriter().print(
							"{" + HeadHelper.createRetHead(serviceName, "99999", "服务调用超时，请查询系统日志查看该请求是否执行成功", MsgLevel.D, RetStatus.TIMEOUT).toJson()
									+ "}");
					response.flushBuffer();
					response.getWriter().close();
				} catch (Throwable e) {
					logger.error("===>>" + serviceName + ":服务运行超时,不能返回错误报文到客户端：" + e.getMessage());
				} finally {
					asyncEvent.getAsyncContext().complete();
				}
			}
		});
		// serviceExecutor.execute(new AsyncServiceProcessor(asyncContext));
		serviceExecutor.submit(new AsyncServiceProcessor(asyncContext, in));
	}

	private static class AsyncServiceProcessor implements Runnable {
		private final Logger logger = LoggerFactory.getLogger(getClass());
		private long createTime;
		private AsyncContext asyncContext;
		private Json in;

		public AsyncServiceProcessor(AsyncContext asyncContext, Json in) {
			this.createTime = System.currentTimeMillis();
			this.asyncContext = asyncContext;
			this.in = in;
		}

		public void run() {
			HttpServletRequest request = (HttpServletRequest)asyncContext.getRequest();
			HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
			String serviceName = getServiceFullName(getSysHead(SysHead.SERVICENAME.value(), in));
			long startRunTime = System.currentTimeMillis();
			if (logger.isInfoEnabled()) {
				logger.info("1===>>" + serviceName + ":服务等待时长:" + (startRunTime - createTime) + "ms(毫秒)");
			}
			if ((startRunTime - createTime) > asyncContext.getTimeout()) {
				logger.error("2===>>" + serviceName + ":服务等待超时,停止执行此服务,服务已等待时长：" + (startRunTime - createTime) + "ms(毫秒):服务超时时长："
						+ asyncContext.getTimeout());
				try {
					if (response == null || response.isCommitted()) {
						// asyncContext.complete();
						return;
					}
					response.getWriter().print(
							"{" + HeadHelper.createRetHead(serviceName, "99996", "服务调用超时，请查询系统日志查看该请求是否执行成功", MsgLevel.D, RetStatus.TIMEOUT).toJson()
									+ "}");
					response.flushBuffer();
					response.getWriter().close();
				} catch (Throwable e) {
					logger.error("===>>" + serviceName + ":服务运行超时,不能返回错误报文到客户端：" + e.getMessage());
				} finally {
					asyncContext.complete();
				}
				return;
			}
			try {
				serviceRequest.set(request);
				serviceResponse.set(response);
				
				doService(in, response);
			} catch (Throwable t) {
				logger.error(in.toString());
				logger.error("===>>" + serviceName + ":服务运行错误：" + t.getMessage());
				if (response == null || response.isCommitted()) {
					// asyncContext.complete();
					return;
				}
				try {
					response.getWriter().print(
							"{" + HeadHelper.createRetHead(serviceName, "99997", t.getMessage(), MsgLevel.D, RetStatus.FAILED).toJson() + "}");
					response.flushBuffer();
					response.getWriter().close();
				} catch (IOException ex) {
					logger.error("===>>" + serviceName + ":服务运行错误,不能返回错误报文到客户端：" + ex.getMessage());
				}
			} finally {
				asyncContext.complete();
			}
		}

		private void doService(Json in, HttpServletResponse response) throws Exception {
			String serviceName = getSysHead(SysHead.SERVICENAME.value(), in);
			if (logger.isDebugEnabled()) {
				logger.debug("===>>服务名称：" + CubismHelper.getServiceFullName(serviceName));
				logger.debug("===>>上送报文：" + in);
			}
			if (ServiceFactory.isReportService(serviceName)) {
				Json ret = ServiceFactory.executeService(serviceName, in, response);
				if (response == null || response.isCommitted()) {
					return;
				}
				if (!HeadHelper.isSuccess(ret)) {
					String filename = getRetHeadFirstMsg(ret);
					if (!hasText(filename)) {
						filename = "CubismError.txt";
					} else {
						filename += ".txt";
					}
					String agent = getSysHead(SysHead.USERAGENT.value(), in);
					filename = getDownloadFileName(filename, agent);
					// response.sendError(404, filename);
					try {
						response.setContentType("application/rtf; charset=UTF-8");
						response.setHeader("Content-Disposition", "attachment; filename=" + filename);
						response.getWriter().print(getRetHeadMsg(ret));
					} catch (Throwable t) {
						logger.info("写响应流错误：" + t.getMessage());
					}
					/*
					 * ret.getObject().setName(null);// 返回报文的顶层直接为:{......}的形式
					 * if (logger.isDebugEnabled()) {
					 * logger.debug("===>>请求的服务 [" +
					 * CubismHelper.getServiceFullName(serviceName) +
					 * "]执行完毕，返回报文为：" + ret.toString()); }
					 * response.getWriter().print(ret.toJson());
					 */
				}
				try {
					response.flushBuffer();
					response.getWriter().close();
				} catch (Throwable t) {
					logger.info("写响应流错误：" + t.getMessage());
				}
				ret = null;
			} else {
				Json ret = ServiceFactory.executeService(serviceName, in);
				ret.getObject().setName(null);// 返回报文的顶层直接为:{......}的形式
				if (logger.isDebugEnabled()) {
					logger.debug("===>>请求的服务 [" + CubismHelper.getServiceFullName(serviceName) + "]执行完毕，返回报文为：" + ret.toString());
				}
				if (response == null || response.isCommitted()) {
					return;
				}
				String res = ret.toJson();
				if (res == null || res.length() <= 0) {
					res = HeadHelper.createRetHead(CubismHelper.getServiceFullName(serviceName), "10009", "服务执行后无返回信息", "D", "F");
					logger.error(res);
				}
				try {
					response.getWriter().print(res);
					response.flushBuffer();
					response.getWriter().close();
				} catch (Throwable t) {
					logger.info("写响应流错误：" + t.getMessage());
				}

				ret = null;
			}
			in = null;
		}
	}
}