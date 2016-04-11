package com.platform.cubism.service.config;

import static com.platform.cubism.util.StringUtils.getTabSpace;
import static com.platform.cubism.util.StringUtils.hasText;

public class QuitElement {
	private String type;
	private String errorcode;
	private String errormsg;
	private String desc;
	private String condition;
	private int sequence;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public int getSequence() {
		return sequence;
	}

	public QuitElement setSequence(int sequence) {
		this.sequence = sequence;
		return this;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String toString(int level) {
		StringBuilder sb = new StringBuilder(getTabSpace(level));
		sb.append("<quit ");
		if (hasText(type)) {
			sb.append("type=\"").append(type).append("\" ");
		}
		if (hasText(desc)) {
			sb.append("desc=\"").append(desc).append("\" ");
		}
		if (hasText(condition)) {
			sb.append("condition=\"").append(condition).append("\" ");
		}
		sb.append("/>");

		return sb.toString();
	}

	public String toString() {
		return toString(0);
	}
}