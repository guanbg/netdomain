package com.platform.cubism.front.login;

import static com.platform.cubism.util.CubismHelper.requestStreamToJson;
import static com.platform.cubism.util.CubismHelper.getIpAddr;
import static com.platform.cubism.SystemConfig.getLoginMainPage;

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
import com.platform.cubism.front.Service;
import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.util.HeadHelper;

@WebServlet(name = "login", urlPatterns = { "/login","/admin" }/*, initParams = { @WebInitParam(name = "timeout", value = "21600") }*/)
public class Login extends HttpServlet {
	private static final long serialVersionUID = 4178391964214994182L;
	private static final Logger logger = LoggerFactory.getLogger(Login.class);
	public static final String MAIN_JSP = getLoginMainPage();
	public static final String USER_STRUC_SESSION = "_user_info_";
	public static final String USER_LOGINNAME_SESSION = "_login_name_";
	public static final String VALIDATE_CODE_SESSION = "validatecode";
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String loginname = (String) request.getSession().getAttribute(Login.USER_LOGINNAME_SESSION);
		if (loginname == null || loginname.length() <= 0) {
			request.getSession().invalidate();
			getServletContext().getRequestDispatcher("/login.html").forward(request, response);
			return;
		}
		if(MAIN_JSP == null || MAIN_JSP.length() <= 0){
			getServletContext().getRequestDispatcher("/main.jsp").forward(request, response);
		}
		else{
			getServletContext().getRequestDispatcher(MAIN_JSP).forward(request, response);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String serviceName = "sys.login";
		Json in = requestStreamToJson(request);		
		if(in == null || in.isEmpty() || in.getFieldValue("userpswd").length() <= 0 || in.getFieldValue("username").length() <= 0){
			getServletContext().getRequestDispatcher("/login.html").forward(request, response);
			return;
		}
		in.addField("ip", getIpAddr(request));
		in.addField("agent", request.getHeader("USER-AGENT"));
		
		if(in.getField(VALIDATE_CODE_SESSION) != null){
			String ValidateCode = in.getField(VALIDATE_CODE_SESSION).getValue();
			String ValidateCodeSession = (String)request.getSession(true).getAttribute(VALIDATE_CODE_SESSION);
			if(ValidateCodeSession == null || ValidateCodeSession.length() <= 0 || !ValidateCodeSession.equalsIgnoreCase(ValidateCode)){
				Json err = JsonFactory.create();
				err.addStruc(HeadHelper.createRetHead(serviceName, "1080", "验证码不正确,请重新输入!", MsgLevel.D));
				response.getWriter().print(err.toJson());
				response.flushBuffer();
				request.getSession().invalidate();
				return;
			}
		}
		else{
			Json err = JsonFactory.create();
			err.addStruc(HeadHelper.createRetHead(serviceName, "1081", "请输入验证码!", MsgLevel.D));
			response.getWriter().print(err.toJson());
			response.flushBuffer();
			request.getSession().invalidate();
			return;
		}
		request.getSession().invalidate();
		
		Json ret = ServiceFactory.executeService(serviceName, in);
		if (logger.isDebugEnabled()) {
			logger.debug("===login===>" + ret);
		}
		if (ret != null && HeadHelper.isSuccess(ret) && ret.getStruc("user") != null && !ret.getStruc("user").isEmpty()) {
			String loginname = ret.getFieldValue("user.loginname");
			request.getSession(true).setAttribute(USER_STRUC_SESSION, ret.getStruc("user"));
			request.getSession().setAttribute(USER_LOGINNAME_SESSION, loginname);
			//request.getSession().setAttribute( "LOGOUTLISTENER",new LogoutListener(ret.getField("user.id").getValue(),loginname)); 

			ret.getObject().setName(null);// 返回报文的顶层直接为:{......}的形式
			response.getWriter().print(ret.toJson());
		}
		else if (HeadHelper.isFailed(ret)) {
			ret.getObject().setName(null);
			response.getWriter().print(ret.toJson());
		}
		else {
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
		
		CStruc user = (CStruc)request.getSession(false).getAttribute(USER_STRUC_SESSION);
		if(user == null || user.isEmpty()){
			return "";
		}
		if(filedName == null || filedName.length()<=0){
			return user.getFieldValue("username");
		}
		return user.getFieldValue(filedName);
	}
	
	public static void updateSessionUser(Json in){
		HttpServletRequest request = Service.getRequest();
		if(request == null){
			if (logger.isDebugEnabled()) {
				logger.debug("===updateSessionUser===> not found any request!!!!!");
			}
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("===updateSessionUser===>" + in);
		}
		CStruc user = (CStruc) request.getSession().getAttribute(Login.USER_STRUC_SESSION);//系统管理登录
		if(user != null){
			Json ret = ServiceFactory.executeService("sys.login.user.get", in);
			request.getSession(true).setAttribute(Login.USER_STRUC_SESSION, ret.getStruc("user"));
			if (logger.isDebugEnabled()) {
				logger.debug("===updateSessionUser===>" + ret);
			}
			return;
		}
		
		user = (CStruc) request.getSession().getAttribute(Logon.USER_STRUC_SESSION_LOGON);//参建单位登录
		if(user != null){
			Json ret = ServiceFactory.executeService("sys.logon.user.get", in);
			request.getSession(true).setAttribute(Logon.USER_STRUC_SESSION_LOGON, ret.getStruc("user"));
			if (logger.isDebugEnabled()) {
				logger.debug("===updateSessionUser===>" + ret);
			}
			return;
		}
	}
}
