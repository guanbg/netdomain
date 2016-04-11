package com.platform.cubism.front.listener;

import static com.platform.cubism.util.StringUtils.hasText;

import java.util.Enumeration;

import javax.servlet.ServletContext;
//import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.ServiceFactory;

//@WebListener("logout user when session destroy")
public class LogoutListener implements HttpSessionBindingListener {
	private String userid;
	private String loginname;

	public LogoutListener() {

	}

	public LogoutListener(String userid, String loginname) {
		this.userid = userid;
		this.loginname = loginname;
	}

	public void valueBound(HttpSessionBindingEvent event) {
		;
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
		if (!hasText(this.userid)) {
			return;
		}
		
		ServletContext sc = event.getSession().getServletContext();	
		for (Enumeration<String> e = sc.getAttributeNames(); e.hasMoreElements();){
			if(loginname.equalsIgnoreCase(e.nextElement())){
				sc.removeAttribute(loginname);
			}
		}
		
		Json in = JsonFactory.create();
		in.addField("id", userid);
		in.addField("loginname", loginname);
		ServiceFactory.executeService("sys.logout", in);
	}
}