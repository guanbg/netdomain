package com.platform.cubism.service.config;

import static com.platform.cubism.util.StringUtils.getTabSpace;
import static com.platform.cubism.util.StringUtils.hasText;

import java.util.ArrayList;
import java.util.List;

public class LogElement {
	private String name;
	private String value;
	private String before;
	private String after;
	private String condition;
	private String desc;
	private List<FldElement> fld;
	private List<List<FldElement>> row;

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public String getAfter() {
		return after;
	}

	public void setAfter(String after) {
		this.after = after;
	}

	public String getCondition() {
		return condition;
	}

	public String getDesc() {
		return desc;
	}

	public List<FldElement> getFld() {
		if (fld == null) {
			return new ArrayList<FldElement>(0);
		}
		return fld;
	}

	public List<List<FldElement>> getRow() {
		if (row == null) {
			return new ArrayList<List<FldElement>>(0);
		}
		return row;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public LogElement addFld(FldElement fld) {
		if (this.fld == null) {
			this.fld = new ArrayList<FldElement>();
		}
		this.fld.add(fld);
		return this;
	}

	public LogElement addRow(List<FldElement> row) {
		if (this.row == null) {
			this.row = new ArrayList<List<FldElement>>();
		}
		this.row.add(row);
		return this;
	}

	public boolean isEmpty() {
		return (fld == null || fld.isEmpty()) && (row == null || row.isEmpty());
	}

	public String toString(int level) {
		StringBuilder sb = new StringBuilder(getTabSpace(level));
		sb.append("<log ");
		if (hasText(name)) {
			sb.append("name=\"").append(name).append("\" ");
		}
		if (hasText(value)) {
			sb.append("value=\"").append(value).append("\" ");
		}
		if (hasText(before)) {
			sb.append("before=\"").append(before).append("\" ");
		}
		if (hasText(after)) {
			sb.append("after=\"").append(after).append("\" ");
		}
		if (hasText(condition)) {
			sb.append("condition=\"").append(condition).append("\" ");
		}
		if (hasText(desc)) {
			sb.append("desc=\"").append(desc).append("\" ");
		}
		sb.append(">");
		if (fld != null) {
			for (FldElement f : fld) {
				sb.append(f.toString(level + 1));
			}
		}
		if (row != null) {
			for (List<FldElement> lst : row) {
				if (lst == null) {
					continue;
				}
				sb.append(getTabSpace(level + 1)).append("<row>");
				for (FldElement f : lst) {
					sb.append(f.toString(level + 2));
				}
				sb.append(getTabSpace(level + 1)).append("</row>");
			}
		}
		sb.append(getTabSpace(level)).append("</log>");
		return sb.toString();
	}

	public String toString() {
		return toString(0);
	}
}
