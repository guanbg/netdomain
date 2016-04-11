package com.platform.cubism.service.config;

import static com.platform.cubism.util.StringUtils.getTabSpace;
import static com.platform.cubism.util.StringUtils.hasText;

import java.util.ArrayList;
import java.util.List;

public class StcElement {
	private String name;
	private String value;
	private String desc;
	private List<FldElement> fld;
	private List<StcElement> stc;
	private List<ArrElement> arr;

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

	public List<FldElement> getFld() {
		if (fld == null) {
			return new ArrayList<FldElement>(0);
		}
		return fld;
	}

	public List<StcElement> getStc() {
		if (stc == null) {
			return new ArrayList<StcElement>(0);
		}
		return stc;
	}

	public List<ArrElement> getArr() {
		if (arr == null) {
			return new ArrayList<ArrElement>(0);
		}
		return arr;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public StcElement addFld(FldElement fld) {
		if (this.fld == null) {
			this.fld = new ArrayList<FldElement>();
		}
		this.fld.add(fld);
		return this;
	}

	public StcElement addStc(StcElement stc) {
		if (this.stc == null) {
			this.stc = new ArrayList<StcElement>();
		}
		this.stc.add(stc);
		return this;
	}

	public StcElement addArr(ArrElement arr) {
		if (this.arr == null) {
			this.arr = new ArrayList<ArrElement>();
		}
		this.arr.add(arr);
		return this;
	}

	public boolean isEmpty() {
		return (fld == null || fld.isEmpty()) && (stc == null || stc.isEmpty()) && (arr == null || arr.isEmpty());
	}

	public String[] getNames() {
		int size = 0, idx = 0;
		if (this.fld != null && !this.fld.isEmpty()) {
			size += this.fld.size();
		}
		if (this.stc != null && !this.stc.isEmpty()) {
			size += this.stc.size();
		}
		if (this.arr != null && !this.arr.isEmpty()) {
			size += this.arr.size();
		}

		String[] names = new String[size];
		if (fld != null) {
			for (FldElement fld : this.fld) {
				names[idx++] = fld.getName();
			}
		}
		if (stc != null) {
			for (StcElement stc : this.stc) {
				names[idx++] = stc.getName();
			}
		}
		if (arr != null) {
			for (ArrElement arr : this.arr) {
				names[idx++] = arr.getName();
			}
		}

		return names;
	}

	public String toString(int level) {
		StringBuilder sb = new StringBuilder(getTabSpace(level));
		sb.append("<stc ");
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

		if (fld != null) {
			for (FldElement f : fld) {
				sb.append(f.toString(level + 1));
			}
		}
		if (stc != null) {
			for (StcElement s : stc) {
				sb.append(s.toString(level + 1));
			}
		}
		if (arr != null) {
			for (ArrElement a : arr) {
				sb.append(a.toString(level + 1));
			}
		}

		return sb.append(getTabSpace(level)).append("</stc>").toString();
	}

	public String toString() {
		return toString(0);
	}
}
