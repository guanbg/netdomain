package com.platform.cubism.front.login;

import static com.platform.cubism.SystemConfig.getLogonMainPage;
import static com.platform.cubism.util.CubismHelper.getIpAddr;
import static com.platform.cubism.util.CubismHelper.requestStreamToJson;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.util.HeadHelper;

@WebServlet(name = "logon", urlPatterns = { "/logon","/crop" })
public class Logon extends HttpServlet {
	private static final long serialVersionUID = -4457446894893444163L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	public static final String MAIN_JSP = getLogonMainPage();
	public static final String USER_STRUC_SESSION_LOGON = "_user_info_logon_";
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String loginname = (String) request.getSession().getAttribute(Login.USER_LOGINNAME_SESSION);
		if (loginname == null || loginname.length() <= 0) {
			request.getSession().invalidate();
			getServletContext().getRequestDispatcher("/").forward(request, response);
			return;
		}
		
		CStruc user = (CStruc)request.getSession().getAttribute(USER_STRUC_SESSION_LOGON);
		if (user == null || user.isEmpty()) {
			request.getSession().invalidate();
			getServletContext().getRequestDispatcher("/").forward(request, response);
			return;
		}
		
		Json in = JsonFactory.create();
		in.addField("user_id", user.getFieldValue("user_id"));
		Json ret = ServiceFactory.executeService("sys.logon.refresh", in);
		if (ret != null && HeadHelper.isSuccess(ret) && ret.getStruc("user") != null && !ret.getStruc("user").isEmpty()) {
			request.getSession().setAttribute(USER_STRUC_SESSION_LOGON, ret.getStruc("user"));
		}

		if(MAIN_JSP == null || MAIN_JSP.length() <= 0){
			getServletContext().getRequestDispatcher("/crop/main.jsp").forward(request, response);
		}
		else{
			getServletContext().getRequestDispatcher(MAIN_JSP).forward(request, response);
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String serviceName = "sys.logon";
		Json in = requestStreamToJson(request);
		if(in == null || in.isEmpty() || in.getFieldValue("userpswd").length() <= 0 || in.getFieldValue("username").length() <= 0){
			getServletContext().getRequestDispatcher("/").forward(request, response);
			return;
		}
		in.addField("ip", getIpAddr(request));
		in.addField("agent", request.getHeader("USER-AGENT"));
		
		request.getSession().invalidate();
		
		Json ret = ServiceFactory.executeService(serviceName, in);
		if (logger.isDebugEnabled()) {
			logger.debug("===logon===>" + ret);
		}
		
		if (ret != null && HeadHelper.isSuccess(ret) && ret.getStruc("user") != null && !ret.getStruc("user").isEmpty()) {
			String loginname = ret.getFieldValue("user.login_name");
			if(loginname == null || loginname.length() <= 0){
				loginname = ret.getFieldValue("user.login_email");
			}
			if(loginname == null || loginname.length() <= 0){
				loginname = ret.getFieldValue("user.login_mobile");
			}
			request.getSession(true).setAttribute(USER_STRUC_SESSION_LOGON, ret.getStruc("user"));
			request.getSession().setAttribute(Login.USER_LOGINNAME_SESSION, loginname);//供权限管理使用
			ret.getObject().setName(null);// 返回报文的顶层直接为:{......}的形式
			response.getWriter().print(ret.toJson());
		}
		else if (HeadHelper.isFailed(ret)) {
			ret.getObject().setName(null);
			response.getWriter().print(ret.toJson());
		}
		else{
			Json err = JsonFactory.create();
			err.addStruc(HeadHelper.createRetHead(serviceName, "1082", "用户验证不通过，请确认用户名称及密码是否正确!", MsgLevel.D));
			response.getWriter().print(err.toJson());
		}
		response.flushBuffer();
	}
	
	public static String getUser(HttpServletRequest request, String filedName){
		if(request.getSession(false) == null){
			return "";
		}
		CStruc user = (CStruc)request.getSession(false).getAttribute(USER_STRUC_SESSION_LOGON);
		if(user == null || user.isEmpty()){
			return "";
		}
		if(filedName == null || filedName.length()<=0){
			return user.getFieldValue("username");
		}
		return user.getFieldValue(filedName);
	}
}
