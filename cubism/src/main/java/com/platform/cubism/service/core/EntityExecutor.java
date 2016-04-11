package com.platform.cubism.service.core;

import com.platform.cubism.jdbc.tran.TransactionStatus;

import com.platform.cubism.base.Json;

public interface EntityExecutor {
	public Json execute(Json in) throws Exception;
	public void success(Json in) throws Exception;
	public void failure(Json in) throws Exception;
	public void setTransactionStatus(TransactionStatus status);
}