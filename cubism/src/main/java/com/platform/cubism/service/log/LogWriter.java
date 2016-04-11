package com.platform.cubism.service.log;

import com.platform.cubism.base.Json;

public interface LogWriter {
	public static final String LOGBEFORE = "logbefore";

	public Json beginLog(Json in);

	public Json endLog(Json in);
}
