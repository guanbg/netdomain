package com.platform.cubism.service.config;

import static com.platform.cubism.util.StringUtils.getTabSpace;
import static com.platform.cubism.util.StringUtils.hasText;

public class SqlElement {
	private String id;
	private String value;
	private String errorcode;
	private String errormsg;
	private String desc;
	private String condition;
	private String each;
	private String cache;//缓存名称
	private String updatecache;//需要更新的键值，多个以空白或者逗号分号隔开
	private String generatedkeys;
	private int sequence;
	private boolean alwayarray;
	private boolean lowercase = true;
	
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

	public boolean isAlwayarray() {
		return alwayarray;
	}

	public void setAlwayarray(boolean alwayarray) {
		this.alwayarray = alwayarray;
	}

	public boolean isLowercase() {
		return lowercase;
	}

	public void setLowercase(boolean lowercase) {
		this.lowercase = lowercase;
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

	public String getGeneratedkeys() {
		return generatedkeys;
	}

	public void setGeneratedkeys(String generatedkeys) {
		this.generatedkeys = generatedkeys;
	}

	public int getSequence() {
		return sequence;
	}

	public SqlElement setSequence(int sequence) {
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

	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public String getUpdatecache() {
		return updatecache;
	}

	public void setUpdatecache(String updatecache) {
		this.updatecache = updatecache;
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
		sb.append("<sql ");
		if (hasText(id)) {
			sb.append("id=\"").append(id).append("\" ");
		}
		if (hasText(generatedkeys)) {
			sb.append("generatedkeys=\"").append(generatedkeys).append("\" ");
		}
		sb.append("alwayarray=\"").append(alwayarray).append("\" ");
		sb.append("lowercase=\"").append(lowercase).append("\" ");
		if (hasText(each)) {
			sb.append("each=\"").append(each).append("\" ");
		}
		if (hasText(condition)) {
			sb.append("condition=\"").append(condition).append("\" ");
		}
		if (hasText(cache)) {
			sb.append("cache=\"").append(cache).append("\" ");
		}
		if (hasText(updatecache)) {
			sb.append("updatecache=\"").append(updatecache).append("\" ");
		}
		if (hasText(desc)) {
			sb.append("desc=\"").append(desc).append("\" ");
		}
		sb.append(">");
		if (hasText(value)) {
			sb.append(getTabSpace(level + 1));
			sb.append(value);
		}
		sb.append(getTabSpace(level)).append("</sql>");
		return sb.toString();
	}

	public String toString() {
		return toString(0);
	}
}