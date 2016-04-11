package com.platform.cubism.query;

import static com.platform.cubism.util.StringUtils.hasText;

import java.util.ArrayList;
import java.util.List;

public class QueryConfig {
	private String id;
	private String srvid;
	private String desc;
	private List<QueryFld> flds;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSrvid() {
		return srvid;
	}

	public void setSrvid(String srvid) {
		this.srvid = srvid;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<QueryFld> getFlds() {
		return flds;
	}

	public void setFlds(List<QueryFld> flds) {
		this.flds = flds;
	}

	public QueryConfig addFld(QueryFld fld) {
		if (this.flds == null) {
			this.flds = new ArrayList<QueryFld>();
		}
		this.flds.add(fld);
		return this;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<qry ");
		if (hasText(id)) {
			sb.append("id=\"").append(id).append("\" ");
		}
		if (hasText(desc)) {
			sb.append("desc=\"").append(desc).append("\" ");
		}
		sb.append(">");

		int level = 1;
		if (flds != null) {
			for (QueryFld fld : flds) {
				sb.append(fld.toString(level));
			}
		}
		sb.append("\n</qry>");
		return sb.toString();
	}
}