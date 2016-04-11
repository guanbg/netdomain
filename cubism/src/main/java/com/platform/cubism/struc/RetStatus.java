package com.platform.cubism.struc;

/**
 * S:successfully F:failed T:timeout U:unknow
 */
public enum RetStatus {
	SUCCESS("S"), FAILED("F"), TIMEOUT("T"), UNKNOW("U");

	private final String value;

	public String value() {
		return this.value;
	}

	RetStatus(String value) {
		this.value = value;
	}

	public static RetStatus valueOf(char value) {
		if ('S' == value) {
			return SUCCESS;
		}
		if ('F' == value) {
			return FAILED;
		}
		if ('T' == value) {
			return TIMEOUT;
		}
		return UNKNOW;
	}
	public static RetStatus valueOfStr(String value) {
		if ("S".equalsIgnoreCase(value)) {
			return SUCCESS;
		}
		if ("F".equalsIgnoreCase(value)) {
			return FAILED;
		}
		if ("T".equalsIgnoreCase(value)) {
			return TIMEOUT;
		}
		return UNKNOW;
	}
}