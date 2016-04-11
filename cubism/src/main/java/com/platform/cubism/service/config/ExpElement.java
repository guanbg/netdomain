package com.platform.cubism.service.config;

import static com.platform.cubism.util.StringUtils.getTabSpace;
import static com.platform.cubism.util.StringUtils.hasText;

public class ExpElement {
	private String id;
	private String value;
	private String errorcode;
	private String errormsg;
	private String desc;
	private String condition;
	private String each;
	private String processor;
	private boolean async = true;
	private int fetch = 20;
	private int sequence;

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public int getFetch() {
		return fetch;
	}

	public void setFetch(int fetch) {
		this.fetch = fetch;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getEach() {
		return each;
	}

	public void setEach(String each) {
		this.each = each;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public int getSequence() {
		return sequence;
	}

	public ExpElement setSequence(int sequence) {
		this.sequence = sequence;
		return this;
	}

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
		sb.append("<exp ");
		if (hasText(id)) {
			sb.append("id=\"").append(id).append("\" ");
		}
		if (hasText(desc)) {
			sb.append("desc=\"").append(desc).append("\" ");
		}
		if (hasText(condition)) {
			sb.append("condition=\"").append(condition).append("\" ");
		}
		if (fetch>0) {
			sb.append("fetch=\"").append(fetch).append("\" ");
		}
		if (hasText(processor)) {
			sb.append("processor=\"").append(processor).append("\" ");
		}
		sb.append("async=\"").append(async).append("\" ");
		sb.append(">");
		if (hasText(value)) {
			sb.append(getTabSpace(level + 1));
			sb.append(value);
		}
		sb.append(getTabSpace(level)).append("</exp>");
		return sb.toString();
	}

	public String toString() {
		return toString(0);
	}
}