package com.platform.cubism.service;

import org.slf4j.LoggerFactory;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.Json;

public interface CustomService {
	public final org.slf4j.Logger logger = LoggerFactory.getLogger(CustomService.class);

	public Json execute(Json in) throws CubismException;
}