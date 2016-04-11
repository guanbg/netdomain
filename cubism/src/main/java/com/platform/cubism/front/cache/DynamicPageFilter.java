package com.platform.cubism.front.cache;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.DispatcherType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet Filter implementation class DynamicPageFilter
 */
/*
@WebFilter(
	description = "cache html", 
	filterName="DynamicPageFilter",
	urlPatterns = { "*.jsp" }, 
	dispatcherTypes={
		DispatcherType.FORWARD,
		DispatcherType.INCLUDE,
		DispatcherType.REQUEST,
		DispatcherType.ERROR, 
		DispatcherType.ASYNC
	}, 
	initParams = { 
		@WebInitParam(name = "cacheLevel", value = "page") 
	}, 
	asyncSupported = true
)*/
public class DynamicPageFilter implements Filter {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String excludefiles;
	private String[] exfiles;

	public void destroy() {
		;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		response.setContentType("text/html; charset=UTF-8");
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		String key = PageCacheManager.getKey(httpRequest);
		String uri = PageCacheManager.getURI(httpRequest);
		logger.info(key);
		if (PageCacheManager.contains(key)) {
			response.getWriter().print(PageCacheManager.getPage(key));
			//response.flushBuffer();
			//response.getWriter().close();
			logger.info("cached:"+key);
		} else if (isCache(uri)){
			try {
				String page = PageCacheManager.StaticizePage((HttpServletRequest) request, (HttpServletResponse) response);
				//System.out.print(page);
				PageCacheManager.cache(key, page);
				response.getWriter().print(page);
				//response.flushBuffer();
				//response.getWriter().close();
				logger.info("cache:"+key);
			} catch (Throwable e) {
				chain.doFilter(request, response);
				logger.info("no cache");
			}
		}
		else{
			chain.doFilter(request, response);
			logger.info("no cache");
		}
	}

	public void init(FilterConfig config) throws ServletException {
		this.excludefiles = config.getInitParameter("excludefiles");
		if ((this.excludefiles == null) || (this.excludefiles.length()<=0)) {
			this.excludefiles = "index.jsp";
		}
		this.exfiles = excludefiles.split(",");
	}

	private boolean isCache(String filename) {
		if ((this.exfiles == null) || (this.exfiles.length <= 0)) {
			return false;
		}
		if(filename.endsWith("_cache.jsp")){
			return true;
		}
		String lname = filename.toLowerCase();
		for (String s : exfiles) {
			if (lname.indexOf(s.toLowerCase()) >= 0) {
				return true;
			}
		}
		return false;
	}
}
