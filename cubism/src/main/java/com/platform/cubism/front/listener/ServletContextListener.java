package com.platform.cubism.front.listener;

import javax.servlet.ServletContextEvent;
//import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.ServiceFactory;

//@WebListener("logout user when start")
public class ServletContextListener implements javax.servlet.ServletContextListener {
	private final static Logger logger = LoggerFactory.getLogger(ServletContextListener.class);
	
	public void contextDestroyed(ServletContextEvent sce) {
		try{
			ServiceFactory.executeService("sys.logout", JsonFactory.create());
		}catch(Throwable t){
			logger.error(t.getMessage());
		}
	}

	public void contextInitialized(ServletContextEvent sce) {
		//ServiceFactory.executeService("sys.ResetUserStatus", JsonFactory.create());
	}
}