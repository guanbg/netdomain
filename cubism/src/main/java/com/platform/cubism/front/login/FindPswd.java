package com.platform.cubism.front.login;

import static com.platform.cubism.SystemConfig.getValue;
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
import com.platform.cubism.mail.SendMail;
import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.util.HeadHelper;

@WebServlet(name = "FindPswd", urlPatterns = { "/FindPswd" })
public class FindPswd extends HttpServlet{
	private static final long serialVersionUID = 8527880996046749548L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String serviceName = "crop.userpswd.find";
		Json in = requestStreamToJson(request);		
		if(in == null || in.isEmpty()){
			getServletContext().getRequestDispatcher("/login.html").forward(request, response);
			return;
		}
		in.addField("ip", getIpAddr(request));
		in.addField("agent", request.getHeader("USER-AGENT"));
		HeadHelper.createSysHead(in, in.getFieldValue("agent"), "", "", in.getFieldValue("login_name"), in.getFieldValue("company_name"), "", in.getFieldValue("ip"), serviceName, "");
		
		String ValidateCode = in.getFieldValue("validatecode");
		String ValidateCodeSession = (String)request.getSession(true).getAttribute(Login.VALIDATE_CODE_SESSION);
		
		if(ValidateCode == null || ValidateCode.length() <= 0 || ValidateCodeSession == null || ValidateCodeSession.length() <= 0 ||  !ValidateCodeSession.equalsIgnoreCase(ValidateCode)){
			Json err = JsonFactory.create();
			err.addStruc(HeadHelper.createRetHead(serviceName, "1080", "验证码不正确,请重新输入!", MsgLevel.D));
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
		if(!HeadHelper.isSuccess(ret) || ret.getStruc("user") == null || ret.getStruc("user").isEmpty()){
			Json err = JsonFactory.create();
			err.addStruc(HeadHelper.createRetHead("CropRegister", "1092", "用户注册信息不存在，不能发送邮件!", MsgLevel.D));
			response.getWriter().print(err.toJson());
			response.flushBuffer();
			return;
		}
		CStruc user = ret.getStruc("user");
		
		try{
			Json in2 = JsonFactory.create();
			in2.addField("tmplname","email.findpswd.tmpl");
			in2.addField("title",getValue("email.findpswd.title"));
			in2.addField("url","/crop/findPswdBack.jsp");
			in2.addField("email",user.getFieldValue("login_email"));
			in2.addField("company_name",user.getFieldValue("user_name"));
			CStruc parameters = JsonFactory.createStruc("parameters");
			parameters.addField("user_id", user.getFieldValue("user_id"));
			parameters.addField("login_password", user.getFieldValue("login_password"));
			in2.addStruc(parameters);
			Json ret2 = new SendMail().execute(in2);
			if(HeadHelper.isFailed(ret2)){
				ret.getObject().setName(null);
				response.getWriter().print(ret.toJson());
			}
		}
		catch(Throwable t){
			if (logger.isDebugEnabled()) {
				t.printStackTrace();
			}
			logger.error(t.getMessage());
			
			Json err = JsonFactory.create();
			err.addStruc(HeadHelper.createRetHead("CropRegister", "1093", "发送邮件错误，请检查电子邮箱是否正确!", MsgLevel.D));
			response.getWriter().print(err.toJson());
			response.flushBuffer();
			return;
		}
		
		ret.remove("user");
		ret.getObject().setName(null);// 返回报文的顶层直接为:{......}的形式
		response.getWriter().print(ret.toJson());
		response.flushBuffer();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user_id = request.getParameter("user_id");
		if(user_id == null || user_id.length() <= 0){
			Json err = JsonFactory.create();
			err.addStruc(HeadHelper.createRetHead("FindPswd", "1091", "用户信息不正确，不能发送邮件!", MsgLevel.D));
			response.getWriter().print(err.toJson());
			response.flushBuffer();
			return;
		}
		
		Json in = JsonFactory.create();
		in.addField("user_id", user_id);
		Json ret = ServiceFactory.executeService("crop.registeuserinfo.get", in);
		if(!HeadHelper.isSuccess(ret) || ret.getStruc("user") == null || ret.getStruc("user").isEmpty()){
			Json err = JsonFactory.create();
			err.addStruc(HeadHelper.createRetHead("FindPswd", "1092", "用户信息不存在，不能发送邮件!", MsgLevel.D));
			response.getWriter().print(err.toJson());
			response.flushBuffer();
			return;
		}
		CStruc user = ret.getStruc("user");
		try{
			Json in2 = JsonFactory.create();
			in2.addField("tmplname","email.findpswd.tmpl");
			in2.addField("title",getValue("email.findpswd.title"));
			in2.addField("url","/crop/findPswdBack.jsp");
			in2.addField("email",user.getFieldValue("login_email"));
			in2.addField("company_name",user.getFieldValue("user_name"));
			CStruc parameters = JsonFactory.createStruc("parameters");
			parameters.addField("user_id", user.getFieldValue("user_id"));
			parameters.addField("login_password", user.getFieldValue("login_password"));
			in2.addStruc(parameters);
			Json ret2 = new SendMail().execute(in2);
			if(HeadHelper.isFailed(ret2)){
				ret.getObject().setName(null);
				response.getWriter().print(ret.toJson());
			}
		}
		catch(Throwable t){
			if (logger.isDebugEnabled()) {
				t.printStackTrace();
			}
			logger.error(t.getMessage());
			
			Json err = JsonFactory.create();
			err.addStruc(HeadHelper.createRetHead("FindPswd", "1093", "发送邮件错误，请检查电子邮箱是否正确!", MsgLevel.D));
			response.getWriter().print(err.toJson());
			response.flushBuffer();
			return;
		}
		
		ret.remove("user");
		ret.getObject().setName(null);// 返回报文的顶层直接为:{......}的形式
		response.getWriter().print(ret.toJson());
		response.flushBuffer();
	}
}
