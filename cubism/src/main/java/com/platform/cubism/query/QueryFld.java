package com.platform.cubism.query;

import static com.platform.cubism.util.StringUtils.getTabSpace;
import static com.platform.cubism.util.StringUtils.hasText;

public class QueryFld {
	private String id;
	private String text;
	private String type;
	private String data;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	public String toString(int level) {
		StringBuilder sb = new StringBuilder(getTabSpace(level));
		sb.append("<fld ");
		if (hasText(id)) {
			sb.append("id=\"").append(id).append("\" ");
		}
		if (hasText(text)) {
			sb.append("text=\"").append(text).append("\" ");
		}
		if (hasText(type)) {
			sb.append("type=\"").append(type).append("\" ");
		}
		sb.append(">");
		if (hasText(data)) {
			sb.append(getTabSpace(level + 1));
			sb.append(data);
		}
		sb.append(getTabSpace(level)).append("</fld>");
		return sb.toString();
	}

	public String toString() {
		return toString(0);
	}
}