package com.platform.cubism.front.compression;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//代码不兼容，在tomcat8下导致奇慢无比，特此去掉
//@WebFilter(urlPatterns = { "*.js", "*.css", "*.html","*.service" }, asyncSupported = true, initParams = { @WebInitParam(name = "compressionThreshold", value = "2048") })
public class CompressionFilter implements Filter {
	private FilterConfig config = null;
	private int minThreshold = 128;
	protected int compressionThreshold;

	public void init(FilterConfig filterConfig) {
		config = filterConfig;
		if (filterConfig != null) {
			String str = filterConfig.getInitParameter("compressionThreshold");
			if (str != null) {
				compressionThreshold = Integer.parseInt(str);
				if (compressionThreshold != 0 && compressionThreshold < minThreshold) {
					compressionThreshold = minThreshold;
				}
			} else {
				compressionThreshold = 0;
			}

		} else {
			compressionThreshold = 0;
		}
	}

	public void destroy() {
		this.config = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (compressionThreshold == 0) {
			chain.doFilter(request, response);
			return;
		}

		boolean supportCompression = false;
		if (request instanceof HttpServletRequest) {
			String s = ((HttpServletRequest) request).getParameter("gzip");
			if ("false".equals(s)) {
				chain.doFilter(request, response);
				return;
			}

			Enumeration<String> e = ((HttpServletRequest) request).getHeaders("Accept-Encoding");
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				if (name.indexOf("gzip") != -1) {
					supportCompression = true;
				}
			}
		}

		if (!supportCompression) {
			chain.doFilter(request, response);
			return;
		}

		if (response instanceof HttpServletResponse) {
			CompressionServletResponseWrapper wrappedResponse = new CompressionServletResponseWrapper((HttpServletResponse) response);
			wrappedResponse.setCompressionThreshold(compressionThreshold);
			try {
				chain.doFilter(request, wrappedResponse);
			} finally {
				wrappedResponse.finishResponse();
			}
			return;
		}
	}

	/**
	 * Set filter config This function is equivalent to init. Required by
	 * Weblogic 6.1
	 * 
	 * @param filterConfig
	 *            The filter configuration object
	 */
	public void setFilterConfig(FilterConfig filterConfig) {
		init(filterConfig);
	}

	/**
	 * Return filter config Required by Weblogic 6.1
	 */
	public FilterConfig getFilterConfig() {
		return config;
	}
}