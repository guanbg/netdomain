package com.platform.cubism.service.core;

import static com.platform.cubism.util.StringUtils.hasText;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.util.Assert;

public class QuitServiceEntity extends AbstractEntity {
	public static final String QUIT_FAILD = "faild";
	public static final String QUIT_SUCCES = "successful";
	
	public QuitServiceEntity() {
		;
	}

	public QuitServiceEntity(ServiceEntity entity) {
		super(entity);
	}

	public void setServiceEntity(ServiceEntity entity) {
		Assert.state(entity.getType() == EntityType.quit);
		setEntity(entity);
	}

	public Json execute(Json in) {
		if (!hasText(getValue()) || QUIT_FAILD.equalsIgnoreCase(getValue())) {
			throw new CubismException(getErrorMessage());
		}
		
		return JsonFactory.create();
	}
}