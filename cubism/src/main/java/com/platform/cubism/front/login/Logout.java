package com.platform.cubism.front.login;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "logout", urlPatterns = { "/logout" })
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 8048337584373769987L;

	public Logout() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getQueryString();
		if(url == null || url.length() <= 0){
			url = "/login.html";
		}
		request.getSession().invalidate();
		request.getServletContext().getRequestDispatcher(url).forward(request, response);
	}

}
