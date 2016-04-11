package com.platform.cubism.sqlite;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.CustomService;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.struc.RetStatus;
import com.platform.cubism.util.HeadHelper;

public class ExportStatus  implements CustomService{
	private static Map<String, String> exportStatusCache = new ConcurrentHashMap<String, String>(0);
	
	@Override
	public Json execute(Json in) throws CubismException {
		Json ret = JsonFactory.create();
		String lib_id = in.getFieldValue("lib_id");
		
		if(exportStatusCache.containsKey(lib_id)){
			ret.addField("completed", "0");
		}
		else{
			ret.addField("completed", "1");
		}
		ret.addStruc(HeadHelper.createRetHead("com.platform.cubism.sqlite.ExportStatus", "00000", "执行完毕", MsgLevel.B,RetStatus.SUCCESS));
		return ret;
	}

	public static void setStatus(String key, String value){
		exportStatusCache.put(key, value);
	}
	
	public static void removeStatus(String key){
		exportStatusCache.remove(key);
	}
}
