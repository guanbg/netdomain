package com.platform.cubism.struc;

/**
 * rethead:{ service:"", status:"", msgarr:[ { code:"", desc:"", level:"" } ] }
 */
public enum RetHead {
	RETHEAD("rethead"), STATUS("status"), MSGARR("msgarr"), CODE("code"), DESC("desc"), LEVEL("level"), SERVICE("service");
	private final String value;

	public String value() {
		return this.value;
	}

	RetHead(String value) {
		this.value = value;
	}
}