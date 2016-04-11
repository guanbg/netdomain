package com.platform.cubism.processor;

import java.sql.Connection;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.Json;

public interface ImpProcessor {
	public boolean start();
	public boolean process(CArray rs, Connection conn);
	public void end();
	public void end(Throwable e);
	public Json getResult();
}
