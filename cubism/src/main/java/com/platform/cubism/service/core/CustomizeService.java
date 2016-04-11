package com.platform.cubism.service.core;

import com.platform.cubism.CubismException;
import com.platform.cubism.service.CustomService;
import com.platform.cubism.service.HttpService;
import com.platform.cubism.service.ServiceType;
import com.platform.cubism.util.Assert;
import com.platform.cubism.util.CubismHelper;

public class CustomizeService {
	private String serviceName;
	private Class<?> serviceClazz;

	public CustomizeService(String serviceName) {
		setServiceName(CubismHelper.getServiceName(serviceName));
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		Assert.hasText(serviceName);
		this.serviceName = serviceName;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			cl = CustomizeService.class.getClassLoader();
		}
		try {
			serviceClazz = cl.loadClass(serviceName);
			Assert.notNull(serviceClazz);
		} catch (Exception e) {
			throw new CubismException("装载服务类错误:" + e.getMessage());
		}
	}

	public ServiceType getServiceType() {
		Assert.notNull(serviceClazz);
		if (CustomService.class.isAssignableFrom(serviceClazz)) {
			return ServiceType.customService;
		} else if (HttpService.class.isAssignableFrom(serviceClazz)) {
			return ServiceType.httpService;
		} else {
			return ServiceType.unknown;
		}
	}

	public Object getService() {
		Assert.notNull(serviceClazz);
		try {
			Object clazzobj = serviceClazz.newInstance();
			return clazzobj;
		} catch (Exception e) {
			throw new CubismException("实例化服务类错误:" + e.getMessage());
		}
	}

	public CustomService getCustomService() {
		return (CustomService) getService();
	}

	public HttpService getHttpService() {
		return (HttpService) getService();
	}
}