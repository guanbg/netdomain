package com.platform.cubism.service.convert;

import static com.platform.cubism.util.StringUtils.hasText;

import java.util.Arrays;

import com.platform.cubism.base.CStruc;
import com.platform.cubism.service.config.StcElement;

public class StcElementConvertToArrayOfDefault implements StcElementConvertToArray {

	public boolean isConvert(StcElement stcElem, CStruc data) {
		String name = stcElem.getName();
		String value = stcElem.getValue();
		if (!hasText(name) || !hasText(value) || data == null) {
			return false;
		}
		if (data.getStrucInArray(value) != null) {// 数组
			return true;
		}
		return false;
	}

	public CStruc[] convert(StcElement stcElem, CStruc data) {
		String name = stcElem.getName();
		String value = stcElem.getValue();
		if (!hasText(name) || !hasText(value) || data == null) {
			return null;
		}

		CStruc[] strucs = data.getStrucInArray(value);

		return Arrays.copyOf(strucs, strucs.length);
	}
}