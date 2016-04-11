package com.platform.cubism.service;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.Json;

public interface Service extends CustomService{
	public Json execute(Json in, boolean isDirect) throws CubismException;
}