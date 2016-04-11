package com.platform.cubism.service.convert;

import static com.platform.cubism.util.StringUtils.hasText;

import org.slf4j.LoggerFactory;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.config.ArrElement;
import com.platform.cubism.service.config.FldElement;
import com.platform.cubism.service.config.StcElement;
import com.platform.cubism.util.Assert;

public class ArrElementConvertOfByRow implements ArrElementConvert {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	public boolean isConvert(ArrElement arrElem, CStruc data) {
		String value = arrElem.getValue();
		StcElement column = arrElem.getStc();
		if (hasText(value) && column != null && !column.isEmpty()) {// 按行解析时必须指定value
			logger.info("execute ArrElementConvertOfByRow...");
			return true;
		}
		return false;
	}

	public CArray convert(ArrElement arrElem, CStruc data) {
		Assert.notNull(arrElem);
		String name = arrElem.getName();
		String value = arrElem.getValue();

		Assert.hasText(name);
		Assert.hasText(value);
		
		if(namePattern.matcher(name).matches()){
			try{
				name = data.getField(namePattern.matcher(name).replaceAll("$1")).getValue();
			}catch(Throwable t){
				name = namePattern.matcher(name).replaceAll("$1");
			}
		}
		
		CArray array = JsonFactory.createArray(name);
		StcElement column = arrElem.getStc();

		if (data == null || data.isEmpty()) {
			return array;
		}

		CArray dtarr = data.getArray(value);
		if (dtarr == null || dtarr.isEmpty()) {
			return array;
		}
		CStruc struc;
		for (CStruc record : dtarr.getRecords()) {
			struc = JsonFactory.createStruc(column.getName());
			array.add(struc);

			for (FldElement fld : column.getFld()) {
				CField cf = ConvertManager.fldConvert(fld, record);
				if (cf != null) {
					struc.addField(cf);
				}
			}

			for (StcElement stc : column.getStc()) {
				CStruc cs = ConvertManager.stcConvert(stc, record);
				if (cs != null) {
					struc.addStruc(cs);
				}
			}

			for (ArrElement arr : column.getArr()) {
				CArray ca = ConvertManager.arrConvert(arr, record);
				if (ca != null) {
					struc.addArray(ca);
				}
			}
		}
		
		return array;
	}
}