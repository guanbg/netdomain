package com.platform.cubism.service.core;

public abstract class EntityFactory {
	public static EntityExecutor getExecutor(ServiceEntity entity) {
		EntityExecutor executor = null;
		switch (entity.getType()) {
		case service:
			executor = new RefServiceEntity(entity);
			break;
		case clazz:
			executor = new RefClazzEntity(entity);
			break;
		case sql:
			executor = new SqlScriptEntity(entity);
			break;
		case exp:
			executor = new ExpScriptEntity(entity);
			break;
		case imp:
			executor = new ImpScriptEntity(entity);
			break;
		case quit:
			executor = new QuitServiceEntity(entity);
			break;
		}
		return executor;
	}
}