package com.platform.cubism.processor;

import javax.servlet.http.HttpServletResponse;

import com.platform.cubism.base.Json;

public interface RptProcessor {
	public boolean start(Json in);
	public boolean process(Json in) throws Exception;//处理成功返回 true,无需要处理的数据返回 false,失败或出错则抛出异常
	public void end(HttpServletResponse response);
	public void end(Throwable e);
}
