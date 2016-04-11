package com.platform.cubism.service.validate;

public interface ValueValidate {
	public boolean isRule(String rule);

	public boolean validateRule(String value, String rule);

}
