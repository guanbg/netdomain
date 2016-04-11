package com.platform.cubism.service.config;

import static com.platform.cubism.util.StringUtils.getTabSpace;
import static com.platform.cubism.util.StringUtils.hasText;

public class ArrElement {
	private String name;
	private String value;
	private String desc;
	private StcElement stc;

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDesc() {
		return desc;
	}

	public StcElement getStc() {
		return stc;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setStc(StcElement stc) {
		this.stc = stc;
	}

	public String toString(int level) {
		StringBuilder sb = new StringBuilder(getTabSpace(level));
		sb.append("<arr ");
		if (hasText(name)) {
			sb.append("name=\"").append(name).append("\" ");
		}
		if (hasText(value)) {
			sb.append("value=\"").append(value).append("\" ");
		}
		if (hasText(desc)) {
			sb.append("desc=\"").append(desc).append("\" ");
		}
		sb.append(">");
		if (stc != null) {
			sb.append(stc.toString(level + 1));
		}
		sb.append(getTabSpace(level)).append("</arr>");
		return sb.toString();
	}

	public String toString() {
		return toString(0);
	}
}