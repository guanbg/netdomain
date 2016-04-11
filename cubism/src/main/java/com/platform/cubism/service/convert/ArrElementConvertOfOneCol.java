package com.platform.cubism.service.convert;

import static com.platform.cubism.util.StringUtils.hasText;
import static com.platform.cubism.base.Json.DOT_SIGN;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.config.ArrElement;
import com.platform.cubism.service.config.StcElement;
import com.platform.cubism.util.Assert;

public class ArrElementConvertOfOneCol implements ArrElementConvert {

	public boolean isConvert(ArrElement arrElem, CStruc data) {
		String name = arrElem.getName();
		String value = arrElem.getValue();
		StcElement column = arrElem.getStc();
		if (hasText(name) && hasText(value) && value.indexOf(DOT_SIGN)>0 && (column == null || column.isEmpty())) {//单列映射为数组，即把原数组每个列可以单独拆分成一个数组
			return true;
		}
		return false;
	}

	public CArray convert(ArrElement arrElem, CStruc data) {
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
		
		int idx = value.indexOf(DOT_SIGN);
		String arrname = value.substring(0, idx);
		String colname = value.substring(idx + 1);
		
		CArray arr = data.getArray(arrname);
		if(arr == null){
			idx = value.lastIndexOf(DOT_SIGN);
			arrname = value.substring(0, idx);
			colname = value.substring(idx + 1);
			
			arr = data.getArray(arrname);
		}
		if(arr == null){
			arr = data.getArray(value);
			if(arr != null){
				return JsonFactory.createArray(name).add(arr);
			}
		}
		if(arr == null){
			//解析不到数据，
			return JsonFactory.createArray(name);
		}
		
		CArray ret = JsonFactory.createArray(name);
		ret.addRows(arr.getFieldColumn(colname));
		return ret;
	}
}