package com.platform.cubism.service;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.Json;

public interface HttpService {
	final Logger logger = LoggerFactory.getLogger(HttpService.class);
	
	public Json execute(Json in, HttpServletRequest request) throws CubismException;
}
