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

@WebFilter(urlPatterns = {"*.js", "*.jsp", "*.html"}, asyncSupported = true)
public class ModuleAuthFilter implements Filter {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String excludefiles;
	private String[] exfiles;
	private String script;

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession(true);
		String loginname = (String) session.getAttribute(Login.USER_LOGINNAME_SESSION);
		String filepathname = req.getServletPath();
		
		if (!isAllow(filepathname) && (loginname == null || loginname.length()<=0)) {
			if (logger.isDebugEnabled()) {
				logger.debug("请求的功能模块：" + filepathname);
				logger.debug("=========================>>>用户非法，请重新登录系统<<<=========================");
			}
			
			if(filepathname != null && filepathname.toLowerCase().endsWith(".js")){
				response.getWriter().print(script);
			}
			else{
				logger.debug("=========================>>>forward："+req.getContextPath());
				request.getServletContext().getRequestDispatcher("/").forward(request, response);
			}
			
			response.flushBuffer();
			session.invalidate();
		} else {
			chain.doFilter(request, response);
		}
	}

	public void init(FilterConfig config) throws ServletException {
		this.excludefiles = config.getInitParameter("excludefiles");
		if ((this.excludefiles == null) || (this.excludefiles.length()<=0)) {
			this.excludefiles = SystemConfig.getModuleAuthExcludeFiles();
		}
		if ((this.excludefiles == null) || (this.excludefiles.length()<=0)) {
			this.excludefiles = "login.html,md5.js,html5shiv.js,respond.min.js,jquery-2.1.1.min.js,jquery-1.11.1.min.js,jquery.mobile-1.4.5.min.js";
		}
		this.exfiles = excludefiles.split(",");
		this.script = config.getInitParameter("script");
		if ((this.script == null) || (this.script.length()<=0)) {
			this.script = "alert('Access Violation,Please login or get the authority!');";
		}
	}

	private boolean isAllow(String filename) {
		if ((this.exfiles == null) || (this.exfiles.length <= 0)) {
			return false;
		}
		String lname = filename.toLowerCase();
		for (String s : exfiles) {
			if (lname.endsWith(s.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}