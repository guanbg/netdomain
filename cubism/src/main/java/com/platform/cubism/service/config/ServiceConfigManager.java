package com.platform.cubism.service.config;

import static com.platform.cubism.util.StringUtils.hasText;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.service.ServiceType;
import com.platform.cubism.util.Assert;
import com.platform.cubism.util.CubismHelper;

public abstract class ServiceConfigManager {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ServiceFactory.class);
	private static Map<String, ServiceConfig> serviceConfigs;
	private static SoftReference<Map<String, ServiceConfig>> servicesCache;
	
	public static void release(){
		if(serviceConfigs != null){
			serviceConfigs.clear();
		}
		serviceConfigs = null;
		
		if(servicesCache != null){
			servicesCache.clear();
		}
		servicesCache = null;
	}
	
	public static ServiceConfig getService(String serviceId) {
		Assert.hasText(serviceId);
		Map<String, ServiceConfig> cache;
		if (servicesCache == null || servicesCache.get() == null) {
			cache = new HashMap<String, ServiceConfig>();
			servicesCache = new SoftReference<Map<String, ServiceConfig>>(cache);
		} else {
			cache = servicesCache.get();
		}
		if (cache.containsKey(serviceId)) {
			if (logger.isDebugEnabled()) {
				logger.debug("从缓存获取返回服务：" + CubismHelper.getServiceFullName(serviceId));
			}
			return cache.get(serviceId);
		}

		ServiceConfig sc = parseServices(serviceId);
		if (sc == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("没有找到指定的服务:" + serviceId);
			}
			return null;
		}
		cache.put(serviceId, sc);
		if (logger.isDebugEnabled()) {
			if (logger.isDebugEnabled()) {
				logger.debug("读取需要执行的服务信息:\n" + sc.toString());
			}
		}

		return sc;
	}

	public static boolean hasService(String serviceId) {
		if (serviceConfigs != null && serviceConfigs.containsKey(serviceId)) {
			return true;
		}
		return false;
	}

	public static ServiceType getServiceType(String serviceId) {
		ServiceConfig sc = getService(serviceId);
		if (sc == null) {
			return null;
		}
		String type = sc.getType();
		if (!hasText(type) || type.equalsIgnoreCase(ServiceType.local.name())) {
			return ServiceType.local;
		} else if (type.equalsIgnoreCase(ServiceType.local_report.name())) {
			return ServiceType.local_report;
		} else if (type.equalsIgnoreCase(ServiceType.remote_url.name())) {
			return ServiceType.remote_url;
		} else if (type.equalsIgnoreCase(ServiceType.remote_http.name())) {
			return ServiceType.remote_http;
		} else if (type.equalsIgnoreCase(ServiceType.remote_auto.name())) {
			return ServiceType.remote_auto;
		} else if (type.equalsIgnoreCase(ServiceType.remote_socket.name())) {
			return ServiceType.remote_socket;
		} else if (type.equalsIgnoreCase(ServiceType.remote_jms.name())) {
			return ServiceType.remote_jms;
		}else if (type.equalsIgnoreCase(ServiceType.webService.name())) {
			return ServiceType.webService;
		}

		return ServiceType.unknown;
	}

	public static Map<String, ServiceConfig> getServices() {
		return serviceConfigs;
	}

	public static void addServices(Map<String, ServiceConfig> services) {
		if (serviceConfigs == null) {
			serviceConfigs = new HashMap<String, ServiceConfig>();
		}
		serviceConfigs.putAll(services);
	}

	private static ServiceConfig parseServices(String serviceId) {
		Assert.hasLength(serviceId);
		Assert.notNull(serviceConfigs);

		ServiceConfig sc = serviceConfigs.get(serviceId);
		if (sc == null) {
			return null;
		}
		String extend = sc.getExtend();

		if (hasText(extend)) {// 单一继承，以后再支持多继承
			ServiceConfig ret = new ServiceConfig();
			ret.overrideFrom(parseServices(extend));
			ret.overrideFrom(sc);
			return ret;
		}
		logger.debug(sc.toString());
		return sc;
	}
}