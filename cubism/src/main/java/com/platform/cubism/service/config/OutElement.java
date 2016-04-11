package com.platform.cubism.service.config;

import static com.platform.cubism.util.CubismHelper.copyOf;
import static com.platform.cubism.util.StringUtils.getTabSpace;
import static com.platform.cubism.util.StringUtils.hasText;

import java.util.ArrayList;
import java.util.List;

public class OutElement {
	private String clazz;
	private String json;
	private List<FldElement> fld;
	private List<StcElement> stc;
	private List<ArrElement> arr;

	public String getClazz() {
		return clazz;
	}

	public String getJson() {
		return json;
	}

	public List<FldElement> getFld() {
		if(fld == null){
			return new ArrayList<FldElement>(0);
		}
		return fld;
	}

	public List<StcElement> getStc() {
		if(stc == null){
			return new ArrayList<StcElement>(0);
		}
		return stc;
	}

	public List<ArrElement> getArr() {
		if(arr == null){
			return new ArrayList<ArrElement>(0);
		}
		return arr;
	}

	public int size() {
		int sz = 0;

		sz += (fld != null) ? fld.size() : 0;
		sz += (stc != null) ? stc.size() : 0;
		sz += (arr != null) ? arr.size() : 0;

		return sz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public OutElement addFld(FldElement fld) {
		if (this.fld == null) {
			this.fld = new ArrayList<FldElement>();
		}
		this.fld.add(fld);
		return this;
	}

	public OutElement addStc(StcElement stc) {
		if (this.stc == null) {
			this.stc = new ArrayList<StcElement>();
		}
		this.stc.add(stc);
		return this;
	}

	public OutElement addArr(ArrElement arr) {
		if (this.arr == null) {
			this.arr = new ArrayList<ArrElement>();
		}
		this.arr.add(arr);
		return this;
	}

	public void addOverrides(OutElement other) {
		if (other == null) {
			return;
		}
		if (hasText(other.getClazz()))
			this.setClazz(other.getClazz());
		if (hasText(other.getJson()))
			this.setJson(other.getJson());

		if (this.fld == null)
			this.fld = new ArrayList<FldElement>();
		if (this.stc == null)
			this.stc = new ArrayList<StcElement>();
		if (this.arr == null)
			this.arr = new ArrayList<ArrElement>();

		this.fld.addAll(copyOf(other.getFld()));
		this.stc.addAll(copyOf(other.getStc()));
		this.arr.addAll(copyOf(other.getArr()));
	}

	public String toString(int level) {
		StringBuilder sb = new StringBuilder(getTabSpace(level));
		sb.append("<out ");
		if (hasText(clazz)) {
			sb.append("clazz=\"").append(clazz).append("\" ");
		}
		if (hasText(json)) {
			sb.append("json=\"").append(json).append("\" ");
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

		sb.append(getTabSpace(level)).append("</out>");
		return sb.toString();
	}

	public String toString() {
		return toString(0);
	}
}