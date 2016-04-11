package com.platform.cubism.service.config;

import static com.platform.cubism.util.StringUtils.getTabSpace;
import static com.platform.cubism.util.StringUtils.hasText;

public class RefElement {
	private String id;
	private String srv;
	private String clazz;
	private String each;
	private String errorcode;
	private String errormsg;
	private String desc;
	private String condition;
	private int sequence;

	public String getEach() {
		return each;
	}

	public void setEach(String each) {
		this.each = each;
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

	public RefElement setSequence(int sequence) {
		this.sequence = sequence;
		return this;
	}

	public String getId() {
		return id;
	}

	public String getSrv() {
		return srv;
	}

	public String getClazz() {
		return clazz;
	}

	public String getDesc() {
		return desc;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSrv(String srv) {
		this.srv = srv;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String toString(int level) {
		StringBuilder sb = new StringBuilder(getTabSpace(level));
		sb.append("<ref ");
		if (hasText(id)) {
			sb.append("id=\"").append(id).append("\" ");
		}
		if (hasText(srv)) {
			sb.append("srv=\"").append(srv).append("\" ");
		}
		if (hasText(clazz)) {
			sb.append("clazz=\"").append(clazz).append("\" ");
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