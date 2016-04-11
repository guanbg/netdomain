package com.platform.cubism.service;

import javax.servlet.http.HttpServletResponse;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.Json;

public interface Report extends CustomService{
	public Json execute(Json in, HttpServletResponse response) throws CubismException;
}