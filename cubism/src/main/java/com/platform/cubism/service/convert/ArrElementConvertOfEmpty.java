package com.platform.cubism.service.convert;

import static com.platform.cubism.util.StringUtils.hasText;
import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.config.ArrElement;
import com.platform.cubism.service.config.StcElement;
import com.platform.cubism.util.Assert;

public class ArrElementConvertOfEmpty implements ArrElementConvert {

	public boolean isConvert(ArrElement arrElem, CStruc data) {
		StcElement column = arrElem.getStc();
		if (column == null || column.isEmpty()) {
			return true;
		}
		return false;
	}

	public CArray convert(ArrElement arrElem, CStruc data) {
		String name = arrElem.getName();
		String value = arrElem.getValue();
		Assert.hasText(name);
		if (!hasText(value)) {
			value = name;
		}
		CArray arr = data.getArray(value);
		CArray ret = JsonFactory.createArray(name);
		if (arr != null && !arr.isEmpty()) {
			ret.copyOf(arr);
		}
		ret.setName(name);
		return ret;
	}
}