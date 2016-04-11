package com.platform.cubism;

import static com.platform.cubism.util.StringUtils.hasText;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.query.QueryConfigLoader;
import com.platform.cubism.service.ServiceCache;
import com.platform.cubism.service.config.ServiceConfigLoader;
import com.platform.cubism.service.config.ServiceConfigManager;
import com.platform.cubism.service.convert.ConvertManager;
import com.platform.cubism.service.validate.ValidateManager;

@WebListener("auto load")
public class Loader implements ServletContextListener {
	private Logger logger = LoggerFactory.getLogger(getClass());

	public void contextDestroyed(ServletContextEvent sce) {
		ServiceCache.close();
		ServiceConfigManager.release();
		System.gc();
		logger.info("退出系统");
	}

	public void contextInitialized(ServletContextEvent sce) {
		logger.info("开始加载平台配置信息");
		//ServiceCache.init();
		
		String serviceConfigLocation = sce.getServletContext().getInitParameter("serviceConfigLocation");
		if (!hasText(serviceConfigLocation)) {
			serviceConfigLocation = "classpath:service*.xml; WEB-INF/service/**/service*.xml";
		}
		ServiceConfigLoader.loadService(serviceConfigLocation);

		String queryConfigLocation = sce.getServletContext().getInitParameter("queryConfigLocation");
		if (!hasText(queryConfigLocation)) {
			queryConfigLocation = "classpath:query*.xml; WEB-INF/query/**/query*.xml";
		}
		QueryConfigLoader.loadQuery(queryConfigLocation);

		ConvertManager.registConverter();
		ValidateManager.registValidate();

		logger.info("平台配置信息加载完成");
	}
}