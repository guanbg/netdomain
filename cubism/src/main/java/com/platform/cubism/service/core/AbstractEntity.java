package com.platform.cubism.service.core;

import com.platform.cubism.base.Json;
import com.platform.cubism.jdbc.tran.TransactionStatus;
import com.platform.cubism.util.Assert;

public abstract class AbstractEntity implements EntityExecutor {
	private ServiceEntity entity;

	public AbstractEntity() {
		;
	}

	public AbstractEntity(ServiceEntity entity) {
		setServiceEntity(entity);
	}

	public String getId() {
		Assert.notNull(entity);
		return this.entity.getId();
	}

	public String getValue() {
		Assert.notNull(entity);
		return this.entity.getValue();
	}
	
	public String getErrorMessage() {
		Assert.notNull(entity);
		return this.entity.getErrorcode()+this.entity.getErrormsg();
	}
	
	public boolean isAlwayarray() {
		return this.entity.isAlwayarray();
	}
	public boolean isLowercase() {
		return this.entity.isLowercase();
	}

	protected ServiceEntity getServiceEntity() {
		return this.entity;
	}

	protected void setEntity(ServiceEntity entity) {
		this.entity = entity;
	}

	public void setTransactionStatus(TransactionStatus status) {
		// throw new IllegalArgumentException("该实体不支持数据库事物，请检查");
		return;
	}
	public void success(Json in) throws Exception{
		return;
	}
	public void failure(Json in) throws Exception{
		return;
	}

	abstract public void setServiceEntity(ServiceEntity entity);
}