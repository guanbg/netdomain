package com.platform.cubism.service.convert;

import static com.platform.cubism.util.StringUtils.hasText;
import static com.platform.cubism.service.convert.ConvertManager.IN_PREFIX;

import org.slf4j.LoggerFactory;

import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.config.StcElement;

public class StcElementConvertOfEmpty implements StcElementConvert {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(StcElementConvertOfEmpty.class);
	public boolean isConvert(StcElement stcElem, CStruc data) {
		if (hasText(stcElem.getName()) && stcElem.isEmpty()) {
			return true;
		}
		return false;
	}

	public CStruc convert(StcElement stcElem, CStruc data) {
		String name = stcElem.getName();
		String value = stcElem.getValue();
		if (!hasText(value)) {
			value = IN_PREFIX + name;
		}
		CStruc dt;
		
		if(logger.isDebugEnabled()){
			logger.debug("name="+name+"\t\tvalue="+value);
			logger.debug(data.toString());
		}
		if(name.equalsIgnoreCase(value) || (IN_PREFIX+name).equalsIgnoreCase(value)){
			dt = data.getStruc(value);
			if(dt != null && !dt.isEmpty()){
				return dt;
			}
			
			dt = data.getStruc(IN_PREFIX + value);
			if(dt != null && !dt.isEmpty()){
				return dt;
			}
		}
		dt = data.getStruc(value);

		if (dt == null) {
			return null;
		}

		CStruc ret = JsonFactory.createStruc(name);
		ret.copyOf(dt);

		return ret;
	}

}
