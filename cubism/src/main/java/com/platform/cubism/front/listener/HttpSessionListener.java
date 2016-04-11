package com.platform.cubism.front.listener;

import javax.servlet.http.HttpSessionEvent;

//@WebListener("logout user when session destroy")
public class HttpSessionListener implements javax.servlet.http.HttpSessionListener {

	public void sessionCreated(HttpSessionEvent hse) {
		;
	}

	public void sessionDestroyed(HttpSessionEvent hse) {
		;
	}
}