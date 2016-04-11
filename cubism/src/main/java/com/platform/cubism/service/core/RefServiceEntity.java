package com.platform.cubism.service.core;

import static com.platform.cubism.util.StringUtils.hasText;
import com.platform.cubism.base.Json;
import com.platform.cubism.service.Service;
import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.util.Assert;

public class RefServiceEntity extends AbstractEntity {
	public RefServiceEntity() {
		;
	}

	public RefServiceEntity(ServiceEntity entity) {
		super(entity);
	}

	public void setServiceEntity(ServiceEntity entity) {
		Assert.state(entity.getType() == EntityType.service);
		setEntity(entity);
	}

	public Json execute(Json in) {
		String serviceId = getValue();
		if (!hasText(serviceId)) {
			return null;
		}

		Service srv = ServiceFactory.getService(serviceId);
		if (srv == null) {
			return null;
		}
		return srv.execute(in);
	}
}