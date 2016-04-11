package com.platform.cubism.front.auth;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.front.login.Login;

//@WebFilter(urlPatterns = "*.files", asyncSupported = true)
public class FileAuthFilter implements Filter {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String excludefiles;

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession(true);
		String loginname = (String) session.getAttribute(Login.USER_LOGINNAME_SESSION);
		
		ServletContext sc = request.getServletContext();	
		String username;
		for (Enumeration<String> e = sc.getAttributeNames(); e.hasMoreElements();){
			username = e.nextElement();
			if(username != null && username.equalsIgnoreCase(loginname)){
				loginname = null;
				sc.removeAttribute(username);
			}
		}
			
		if (!isAllow(req.getServletPath()) && (loginname == null || "".equals(loginname))) {
			if (logger.isDebugEnabled()) {
				logger.debug("=========================>>>用户非法，请重新登录系统<<<=========================");
			}
			session.invalidate();
		} else {
			chain.doFilter(request, response);
		}
	}

	private boolean isAllow(String uri) {
		if ((this.excludefiles == null) || ("".equals(this.excludefiles))) {
			return false;
		}
		String[] fns = excludefiles.split(",");
		for (String s : fns) {
			if (uri.endsWith(s)) {
				return true;
			}
		}
		return false;
	}

	public void init(FilterConfig config) throws ServletException {
		this.excludefiles = config.getInitParameter("excludefiles");
	}
}