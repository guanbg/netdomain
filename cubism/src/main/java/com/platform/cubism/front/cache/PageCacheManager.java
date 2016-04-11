package com.platform.cubism.front.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class PageCacheManager {
	private static Map<String, String> pageCache = new ConcurrentHashMap<String, String>(0);
	private static String include_page_cache_disabled = "include_page_cache_disabled";
	
	public static boolean contains(String key) {
		return pageCache.containsKey(key);
	}

	public static String getKey(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		boolean disabled = false;
		if(request.getAttribute(include_page_cache_disabled) != null){
			disabled = (Boolean)request.getAttribute(include_page_cache_disabled);//防止include死循环
		}
		
		String key = (String)request.getAttribute("javax.servlet.include.request_uri");//直接取出 include 请求的 URL 值
		String qry = (String)request.getAttribute("javax.servlet.include.query_string");//直接取出include 请求的参数
		
		if(disabled || key == null || key.length() <= 0){
			key = request.getRequestURI();
			qry = request.getQueryString();
		}
		if (qry == null) {
			return key;
		}
		return key + qry;
	}
	public static String getURI(HttpServletRequest request) {
		boolean disabled = false;
		if(request.getAttribute(include_page_cache_disabled) != null){
			disabled = (Boolean)request.getAttribute(include_page_cache_disabled);//防止include死循环
		}
		String key = (String)request.getAttribute("javax.servlet.include.request_uri");//直接取出 include 请求的 URL 值
		if(disabled || key == null || key.length() <= 0){
			key = request.getRequestURI();
		}
		return key;
	}
	public static String getPage(String key) {
		return pageCache.get(key);
	}

	public static void cache(String key, String page) {
		pageCache.put(key, page);
	}

	public static String StaticizePage(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		String contextPath = request.getContextPath();
		String uri = request.getRequestURI();
		String path = uri.substring(contextPath.length());
		RequestDispatcher dispatcher = request.getRequestDispatcher(path);
		// response.setContentType("text/html; charset=UTF-8");

		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final ServletOutputStream stream = new ServletOutputStream() {
			public void write(byte[] data, int offset, int length) {
				os.write(data, offset, length);
			}

			public void write(int b) throws IOException {
				os.write(b);
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setWriteListener(WriteListener writeListener) {
				;
			}
		};
		final PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
		HttpServletResponse resp = new HttpServletResponseWrapper(response) {
			public ServletOutputStream getOutputStream() {
				return stream;
			}

			public PrintWriter getWriter() {
				return pw;
			}
		};

		try {
			request.setAttribute(include_page_cache_disabled, true);
			dispatcher.include(request, resp);
			request.removeAttribute(include_page_cache_disabled);
			pw.flush();
		} catch (Throwable t) {
			throw t;
		}
		return os.toString();
		/*
		 * try { return os.toString("UTF-8"); } catch
		 * (UnsupportedEncodingException e) { try { return os.toString(); }
		 * catch (Throwable t) { throw t; } }
		 */
		// response.reset();
	}
}
