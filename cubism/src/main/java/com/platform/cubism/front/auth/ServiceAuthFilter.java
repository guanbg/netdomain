package com.platform.cubism.front.auth;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.SystemConfig;
import com.platform.cubism.front.login.Login;

@WebFilter(urlPatterns = "*.service", asyncSupported = true)
public class ServiceAuthFilter implements Filter {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String excludeservices;
	private String rethead;

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession(true);
		String loginname = (String) session.getAttribute(Login.USER_LOGINNAME_SESSION);
					
		if (SystemConfig.isCheckUserSession() && !isAllow(req.getServletPath()) && (loginname == null || loginname.length() <= 0)) {
			response.getWriter().print(rethead);
			response.flushBuffer();
			session.invalidate();
			if (logger.isDebugEnabled()) {
				logger.debug("=========================>>>服务调用非法，请重新登录系统<<<=========================");
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	private boolean isAllow(String uri) {
		if ((this.excludeservices == null) || ("".equals(this.excludeservices))) {
			return false;
		}
		String[] fns = excludeservices.split(",");
		for (String s : fns) {
			if (uri.endsWith(s)) {
				return true;
			}
		}
		return false;
	}

	public void init(FilterConfig config) throws ServletException {
		this.rethead = config.getInitParameter("rethead");
		if ((this.rethead == null) || ("".equals(this.rethead))) {
			this.rethead = "{\"rethead\":{ \"status\":\"F\", \"msgarr\":[ { \"code\":\"99999\", \"desc\":\"invalid user，please login\", \"level\":\"D\" } ] }}";
		}
		this.excludeservices = config.getInitParameter("excludeservices");
		if ((this.excludeservices == null) || ("".equals(this.excludeservices))) {
			this.excludeservices = "sys.logon.services,sys.login.service";
		}
	}
}