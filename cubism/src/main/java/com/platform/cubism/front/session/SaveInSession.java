package com.platform.cubism.front.session;

import static com.platform.cubism.util.CubismHelper.streamToString;

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
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.util.HeadHelper;

@WebServlet(name = "save", urlPatterns = { "/sys.save.do" }/*, initParams = { @WebInitParam(name = "timeout", value = "21600") }*/)
public class SaveInSession extends HttpServlet {
	private static final long serialVersionUID = 4178391964214994182L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	public static final String SAVE_NAME_SESSION = "sessionname";
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String json = streamToString(request.getReader());
		Json in = JsonFactory.create().toJson(json);
		
		if(in.getField(SAVE_NAME_SESSION) != null){
			String name = in.getField(SAVE_NAME_SESSION).getValue();
			request.getSession(true).setAttribute(name, json);
		}
		else{
			request.getSession(true).setAttribute(SAVE_NAME_SESSION, json);
		}
		
		Json msg = JsonFactory.create();
		msg.addStruc(HeadHelper.createRetHead("sys.save.do", "00000", "保存成功", MsgLevel.A));
		response.getWriter().print(msg.toJson());
		logger.info("========================提交服务器端保存成功========================");
		response.flushBuffer();
	}
}
