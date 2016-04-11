package com.platform.cubism.front.session;

import static com.platform.cubism.util.StringUtils.hasText;

import javax.servlet.http.HttpServletRequest;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.Json;
import com.platform.cubism.service.HttpService;

public class SetSessionTimeout implements HttpService {
	public Json execute(Json in, HttpServletRequest request) throws CubismException {
		if(logger.isDebugEnabled()){
			logger.debug("SetSessionTimeout====>"+in);
		}
		if(!hasText(in.getField("interval").getValue())){
			return null;
		}
		
		int interval = in.getField("interval").getIntValue()*60;
		/*
		 * setMaxInactiveInterval设置的是当前会话的失效时间，不是整个web的时间，单位为以秒计算。如果设置的值为零或负数，
		 * 则表示会话将永远不会超时。常用于设置当前会话时间。
		 */
		request.getSession().setMaxInactiveInterval(interval);
		return null;
	}
}