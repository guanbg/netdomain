package com.platform.cubism.service.convert;

import static com.platform.cubism.util.StringUtils.hasText;
import static com.platform.cubism.service.convert.ConvertManager.IN_PREFIX;

import org.slf4j.LoggerFactory;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.config.ArrElement;
import com.platform.cubism.service.config.FldElement;
import com.platform.cubism.service.config.StcElement;

public class StcElementConvertOfDefault implements StcElementConvert {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(StcElementConvertOfDefault.class);
	public boolean isConvert(StcElement stcElem, CStruc data) {
		if (stcElem != null && hasText(stcElem.getName()) && !stcElem.isEmpty()) {
			return true;
		}
		return false;
	}

	public CStruc convert(StcElement stcElem, CStruc data) {
		if (stcElem == null || data == null) {
			return null;
		}

		String name = stcElem.getName();
		String value = stcElem.getValue();
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
		
		if (hasText(value)) {
			dt = data.getStruc(value);
		} else {
			dt = data;
		}

		if (dt == null) {
			return null;
		}

		CStruc struc = JsonFactory.createStruc(name);
		if (stcElem.getFld() != null) {
			for (FldElement fld : stcElem.getFld()) {
				CField cf = ConvertManager.fldConvert(fld, dt);
				if (cf != null) {
					struc.addField(cf);
				}
			}
		}
		if (stcElem.getStc() != null) {
			for (StcElement stc : stcElem.getStc()) {
				CStruc cs = ConvertManager.stcConvert(stc, dt);
				if (cs != null) {
					struc.addStruc(cs);
				}
			}
		}
		if (stcElem.getArr() != null) {
			for (ArrElement arr : stcElem.getArr()) {
				CArray ca = ConvertManager.arrConvert(arr, dt);
				if (ca != null) {
					struc.addArray(ca);
				}
			}
		}

		return struc;
	}
}