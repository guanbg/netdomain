package com.platform.cubism.service.config;

import static com.platform.cubism.util.StringUtils.getTabSpace;
import static com.platform.cubism.util.StringUtils.hasText;

public class FldElement {
	private String name;
	private String value;
	private String type;
	private String format;
	private boolean required;
	private String check;
	private String desc;

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getType() {
		return type;
	}

	public String getFormat() {
		return format;
	}

	public String getCheck() {
		return check;
	}

	public String getDesc() {
		return desc;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String toString(int level) {
		StringBuilder sb = new StringBuilder(getTabSpace(level));
		sb.append("<fld ");
		if (hasText(name)) {
			sb.append("name=\"").append(name).append("\" ");
		}
		if (hasText(value)) {
			sb.append("value=\"").append(value).append("\" ");
		}
		if (hasText(type)) {
			sb.append("type=\"").append(type).append("\" ");
		}
		if (hasText(desc)) {
			sb.append("desc=\"").append(desc).append("\" ");
		}
		if (required) {
			sb.append("required=\"").append(required).append("\" ");
		}
		if (hasText(format)) {
			sb.append("format=\"").append(format).append("\" ");
		}
		if (hasText(check)) {
			sb.append("check=\"").append(check).append("\" ");
		}
		sb.append("/>");

		return sb.toString();
	}

	public String toString() {
		return toString(0);
	}
}
