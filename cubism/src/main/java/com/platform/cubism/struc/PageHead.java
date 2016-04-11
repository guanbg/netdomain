package com.platform.cubism.struc;

/**
 * pagehead:{ currentpage:"", totalpage:"", pagecount:"", totalrecord:""}
 */
public enum PageHead {
	PAGEHEAD("pagehead"), CURRENTPAGE("currentpage"), TOTALPAGE("totalpage"), PAGECOUNT("pagecount"), TOTALRECORD("totalrecord");
	private final String value;

	public String value() {
		return this.value;
	}

	PageHead(String value) {
		this.value = value;
	}
}