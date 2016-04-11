package com.platform.cubism.service.convert;

import static com.platform.cubism.util.StringUtils.hasText;

import java.util.Arrays;

import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.service.config.FldElement;
import com.platform.cubism.service.validate.ValidateManager;
import com.platform.cubism.util.Assert;

public class FldElementConvertToArrayOfDefault implements FldElementConvertToArray {

	public boolean isConvert(FldElement fldElem, CStruc data) {
		String name = fldElem.getName();
		String value = fldElem.getValue();
		if (!hasText(name) || !hasText(value) || data == null) {
			return false;
		}
		if (data.getFieldInArray(value) != null) {// 数组
			return true;
		}
		return false;
	}

	public CField[] convert(FldElement fldElem, CStruc data) {
		String name = fldElem.getName();
		String value = fldElem.getValue();
		if (!hasText(name) || !hasText(value) || data == null) {
			return null;
		}
		CField[] fields = data.getFieldInArray(value);
		String rule = fldElem.getCheck();
		boolean isRequired = fldElem.isRequired();
		if (isRequired && fields == null) {// 必输验证
			Assert.notNull(null);
		}
		fields = Arrays.copyOf(fields, fields.length);
		if (!isRequired && !hasText(rule)) {
			return fields;
		}

		for (CField cf : fields) {
			if (fldElem.isRequired() && (cf == null || !hasText(cf.getValue()))) {// 必输验证
				Assert.notNull(null);
			}
			Assert.isTrue(ValidateManager.validate(cf.getValue(), fldElem.getCheck()));// 用户规则验证
		}

		return fields;
	}
}