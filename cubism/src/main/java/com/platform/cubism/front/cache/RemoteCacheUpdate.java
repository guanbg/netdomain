package com.platform.cubism.front.cache;

import static com.platform.cubism.service.ServiceCache.clearCache;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "RemoteCacheUpdate", urlPatterns = { "/removecache" })
public class RemoteCacheUpdate extends HttpServlet {
	private static final long serialVersionUID = 3402159908288988760L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String keyPerfix = request.getQueryString();
		logger.debug("接收到的缓存清除参数keyPerfix====>>>"+keyPerfix);
		clearCache(keyPerfix);
	}
}
