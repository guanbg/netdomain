package com.platform.cubism.front.session;

import static com.platform.cubism.util.StringUtils.hasText;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.Json;
import com.platform.cubism.front.login.Login;
import com.platform.cubism.service.HttpService;

public class KickUserService implements HttpService{
	public Json execute(Json in, HttpServletRequest request) throws CubismException {
		if(logger.isDebugEnabled()){
			logger.debug("KickUserService====>"+in);
		}
		String loginname = in.getField(Login.USER_LOGINNAME_SESSION).getValue();
		if(!hasText(loginname)){
			return null;
		}
		ServletContext sc = request.getServletContext();
		sc.setAttribute(loginname, loginname);
		return null;
	}
}