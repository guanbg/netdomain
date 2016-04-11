package com.platform.cubism.front.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = { "*.jsonp" })
public class JsonP extends HttpServlet {
	private static final long serialVersionUID = -1359030632469186772L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String url = request.getParameter("url");
		try {
			request.setCharacterEncoding("UTF-8");
			
			URL server = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) server.openConnection();
			connection.connect();
			String line = null;
			StringBuilder sb = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
			response.setContentType("text/xml");
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(sb.toString());
		} catch (IOException e) {
			if(logger.isDebugEnabled()){
				e.printStackTrace();
			}
			logger.error(e.getMessage());
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
}
