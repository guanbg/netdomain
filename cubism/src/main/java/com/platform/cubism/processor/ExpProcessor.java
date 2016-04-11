package com.platform.cubism.processor;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.Json;

public interface ExpProcessor {
	public boolean start(Json in);
	public boolean process(CArray rs) throws Exception;//处理成功返回 true,无需要处理的数据返回 false,失败或出错则抛出异常
	public void end();
	public void end(Throwable e);
	public Json getResult();
}
