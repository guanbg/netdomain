package com.platform.cubism.service.validate;

import static com.platform.cubism.util.StringUtils.hasText;

public class DefaultValueValidate implements ValueValidate {

	public boolean isRule(String rule) {
		return true;
	}

	public boolean validateRule(String value, String rule) {
		if (!hasText(rule)) {// 不验证
			return true;
		}
		if (!hasText(value)) {
			return false;
		}

		return true;
	}

}
