package com.platform.cubism;

import java.util.Properties;

import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.CustomService;
import com.platform.cubism.struc.MsgLevel;
import com.platform.cubism.struc.RetStatus;
import com.platform.cubism.util.HeadHelper;

public class ConfigService implements CustomService{

	@Override
	public Json execute(Json in) throws CubismException {
		Json ret = JsonFactory.create();
		String paramName = in.getFieldValue("name");
		
		if(paramName == null || paramName.length() <= 0){
			ret.addStruc(HeadHelper.createRetHead("ConfigService", "1093", "参数名称错误!", MsgLevel.D));
			return ret;
		}
		String key = null;
		Properties prop = SystemConfig.getProperties();
		for(Object p : prop.keySet()){
			key = p.toString();
			if(key.startsWith(paramName)){
				ret.addField(key.replaceAll("\\.", "_"), prop.getProperty(key));
			}
		}
		ret.addStruc(HeadHelper.createRetHead("ConfigService", "00000", "读取参数信息成功", MsgLevel.B, RetStatus.SUCCESS));
		return ret;
	}
}
