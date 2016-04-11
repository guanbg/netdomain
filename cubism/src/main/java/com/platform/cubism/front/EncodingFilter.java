package com.platform.cubism.front;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter(urlPatterns = "/*", asyncSupported = true)
public class EncodingFilter implements Filter {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String encoding;
	private String contentType;

	public void init(FilterConfig config) throws ServletException {
		this.encoding = config.getInitParameter("encoding");
		this.contentType = config.getInitParameter("contentType");
		if ((this.encoding == null) || ("".equals(this.encoding))) {
			this.encoding = "UTF-8";
		}
		if ((this.contentType == null) || (this.contentType.equals(""))) {
			this.contentType = "text/x-json; charset=UTF-8";
		}
		if (logger.isDebugEnabled()) {
			logger.debug("初始化编码格式：" + this.encoding);
			logger.debug("初始化上下文类型：" + this.contentType);
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		((HttpServletResponse) response).setHeader("Cache-Control","no-cache"); 
		((HttpServletResponse) response).setHeader("Pragma","no-cache");
		((HttpServletResponse) response).setDateHeader ("Expires", -1); 
		
		request.setCharacterEncoding(this.encoding);
		response.setCharacterEncoding(this.encoding);
		response.setContentType(this.contentType);
		chain.doFilter(request, response);
	}

	public void destroy() {
		this.encoding = null;
		this.contentType = null;
	}
}