package com.platform.cubism.front.login;

import static com.platform.cubism.util.CubismHelper.getIpAddr;
import static com.platform.cubism.util.CubismHelper.queryStr2Json;
import static com.platform.cubism.util.CubismHelper.requestStreamToJson;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.util.HeadHelper;
import com.platform.cubism.util.SecurityHelper;

@WebServlet(name = "CropEmailConfirm", urlPatterns = { "/CropEmailConfirm" })
public class CropEmailConfirm extends HttpServlet{
	private static final long serialVersionUID = -4352111193587058863L;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {//邮件中确认用户注册信息
		String serviceName = "crop.registeuser.confirm";
		String param = request.getQueryString();
		
		if(param == null || param.length() <= 0){
			getServletContext().getRequestDispatcher("/crop/cropEmailConfirm.jsp?error").forward(request, response);
			return;
		}
		Json in = queryStr2Json(new String(SecurityHelper.hexStringToBytes(param)));
		in.addField("ip", getIpAddr(request));
		in.addField("agent", request.getHeader("USER-AGENT"));
		HeadHelper.createSysHead(in, in.getFieldValue("agent"), "", "", in.getFieldValue("login_name"), in.getFieldValue("company_name"), "", in.getFieldValue("ip"), serviceName, "");
		
		Json ret = ServiceFactory.executeService(serviceName, in);
		if(ret != null && HeadHelper.isSuccess(ret)){
			getServletContext().getRequestDispatcher("/crop/cropEmailConfirm.jsp?success").forward(request, response);
			return;
		}
		else{
			ServiceFactory.executeService("crop.registeuser.delete", in);
		}
		getServletContext().getRequestDispatcher("/crop/cropEmailConfirm.jsp?"+HeadHelper.getRetCode(ret)).forward(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {//重新设置密码
		String serviceName = "crop.userpswd.save";
		Json in = requestStreamToJson(request);		
		if(in == null || in.isEmpty()){
			Json err = JsonFactory.create();
			err.addStruc(HeadHelper.createRetHead(serviceName, "1091", "用户信息不正确,请重新输入!", MsgLevel.D));
			response.getWriter().print(err.toJson());
			response.flushBuffer();
			request.getSession().invalidate();
			return;
		}
		in.addField("ip", getIpAddr(request));
		in.addField("agent", request.getHeader("USER-AGENT"));
		Json ret = ServiceFactory.executeService(serviceName, in);
		if (logger.isDebugEnabled()) {
			logger.debug("===login===>" + ret);
		}
		ret.getObject().setName(null);// 返回报文的顶层直接为:{......}的形式
		response.getWriter().print(ret.toJson());
		response.flushBuffer();
	}
}
