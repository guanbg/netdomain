package com.platform.cubism.service.config;

import static com.platform.cubism.util.StringUtils.getTabSpace;
import static com.platform.cubism.util.StringUtils.hasText;

public class JsonElement {
	private String id;
	private String value;
	private String desc;

	public String getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public String getDesc() {
		return desc;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String toString(int level) {
		StringBuilder sb = new StringBuilder(getTabSpace(level));
		sb.append("<json ");
		if (hasText(id)) {
			sb.append("id=\"").append(id).append("\" ");
		}
		if (hasText(desc)) {
			sb.append("desc=\"").append(desc).append("\" ");
		}
		sb.append(">");
		if (hasText(value)) {
			sb.append(getTabSpace(level + 1));
			sb.append(value);
		}
		sb.append(getTabSpace(level)).append("</json>");

		return sb.toString();
	}

	public String toString() {
		return toString(0);
	}
}