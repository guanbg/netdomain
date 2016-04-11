package com.platform.cubism.tools;

import java.util.ArrayList;
import java.util.List;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.util.CubismHelper;

public class CreateTreeFromMysql {
	private static String name = "children";
	
	public static Json toTree(CArray data, String id, String parnentid, CField fld) {
		if(fld == null || fld.isEmpty()){
			return toTree(data, id, parnentid, "");
		}
		else{
			return toTree(data, id, parnentid, fld.getValue());
		}
		
	}
	public static Json toTree(CArray data, String id, String parnentid, String rootvalue) {
		int sz = data.size();
		if (sz <= 0) {
			return null;
		}

		CArray tree = JsonFactory.createArray(data.getName());
		CArray child = null;
		CStruc cs = null;
		String value;
		for (int i = 0; i < sz; i++) {
			cs = data.getRecord(i);
			if (cs.getField(id) == null || cs.getField(parnentid) == null) {
				continue;
			}
			value = cs.getField(parnentid).getValue();
			if ((value == null || "".equals(value)) && ((rootvalue == null) || "".equals(rootvalue) || ("null".equalsIgnoreCase(rootvalue)))) {
				// data.removeRow(cs);
				child = getchildren(data, id, parnentid, cs.getField(id).getValue());
				if (child == null || child.isEmpty()) {
					tree.add(cs);
					continue;
				}
				tree.add(cs.addArray(child));
			} else if (value.equalsIgnoreCase(rootvalue)) {
				// data.removeRow(cs);
				child = getchildren(data, id, parnentid, cs.getField(id).getValue());
				if (child == null || child.isEmpty()) {
					tree.add(cs);
					continue;
				}
				tree.add(cs.addArray(child));
			}
		}

		return JsonFactory.create().addArray(tree);
	}

	public static Json toAuthTree(CArray data, String responitemid, String responid, String responname, String text, String id, String parnentid,
			String rootvalue) {
		int sz = data.size();
		if (sz <= 0) {
			return null;
		}

		List<String> idList = new ArrayList<String>();
		CArray tree = JsonFactory.createArray(data.getName());
		CArray child = null;
		CStruc cs = null;
		String value, idvalue;
		for (int i = 0; i < sz; i++) {
			cs = data.getRecord(i);
			if (cs.getField(id) == null || cs.getField(parnentid) == null || cs.getField(responitemid) == null || cs.getField(responid) == null) {
				continue;
			}
			value = cs.getField(parnentid).getValue();
			idvalue = cs.getField(id).getValue();
			if ((value == null || "".equals(value)) && ((rootvalue == null) || "".equals(rootvalue) || ("null".equalsIgnoreCase(rootvalue)))) {
				if (idList.contains(idvalue)) {
					continue;
				}
				idList.add(idvalue);
				child = getAuthChildren(data, responitemid, responid, responname, text, id, parnentid, idvalue);
				if (child == null || child.isEmpty()) {
					child = getResponChildren(data, responitemid, responid, responname, text, id, parnentid, idvalue);
					if (child == null || child.isEmpty()) {
						tree.add(cs);
						continue;
					}
				}
				tree.add(cs.addArray(child));
			} else if (value.equalsIgnoreCase(rootvalue)) {
				if (idList.contains(value)) {
					continue;
				}
				idList.add(value);
				child = getAuthChildren(data, responitemid, responid, responname, text, id, parnentid, value);
				if (child == null || child.isEmpty()) {
					tree.add(cs);
					continue;
				}
				tree.add(cs.addArray(child));
			}
		}

		return JsonFactory.create().addArray(tree);
	}

	private static CArray getAuthChildren(CArray data, String responitemid, String responid, String responname, String text, String id,
			String parnentid, String parnent) {
		int sz = data.size();
		if (sz <= 0) {
			return null;
		}
		List<String> idList = new ArrayList<String>();
		CArray children = JsonFactory.createArray(name);
		CStruc cs = null;
		String value, idvalue;
		for (int i = 0; i < sz; i++) {
			cs = data.getRecord(i);
			if (cs.getField(id) == null || cs.getField(parnentid) == null) {
				continue;
			}
			value = cs.getField(parnentid).getValue();
			if (value == null) {
				value = "";
			}
			idvalue = cs.getField(id).getValue();
			if (parnent.equalsIgnoreCase(value)) {
				if (idList.contains(idvalue)) {
					continue;
				} else
					idList.add(idvalue);
				if (cs.getField(responid).getValue() != null && !"".equals(cs.getField(responid).getValue())) {
					CArray ca = getResponChildren(data, responitemid, responid, responname, text, id, parnentid, cs.getField(id).getValue());
					if (ca != null && !ca.isEmpty()) {
						cs.addArray(ca);
						cs.getField("leaf").setValue("false");
					}
					children.add(cs);
					continue;
				}
				// data.removeRow(i);
				children.add(cs);
				CArray ca = getAuthChildren(data, responitemid, responid, responname, text, id, parnentid, cs.getField(id).getValue());
				if (ca != null && !ca.isEmpty()) {
					cs.addArray(ca);
					cs.getField("leaf").setValue("false");
				}
			}
		}
		return children;
	}

	private static CArray getResponChildren(CArray data, String responitemid, String responid, String responname, String text, String id,
			String parnentid, String idvalue) {
		int sz = data.size();
		if (sz <= 0) {
			return null;
		}
		CArray children = JsonFactory.createArray(name);
		CStruc cs = null;
		String value;
		for (int i = 0; i < sz; i++) {
			cs = data.getRecord(i);
			if (cs.getField(id) == null) {
				continue;
			}
			value = cs.getField(id).getValue();
			if (idvalue.equalsIgnoreCase(value) && cs.getField(responid).getValue() != null && !"".equals(cs.getField(responid).getValue())) {
				// data.removeRow(i);
				CStruc struc = CubismHelper.deepCopy(cs);
				cs.getField("leaf").setValue("false");
				//struc.copyOf(CubismHelper.deepCopy(cs));
				String stext = struc.getField(responname).getValue();
				struc.getField(text).setValue(stext);
				struc.getField(parnentid).setValue(struc.getField(id).getValue());
				String sid = struc.getField(id).getValue() + '_' + struc.getField(responitemid).getValue() + '_'
						+ struc.getField(responid).getValue();
				struc.getField(id).setValue(sid);
				children.add(struc);
			}
		}
		return children;
	}

	private static CArray getchildren(CArray data, String id, String parnentid, String parnent) {
		int sz = data.size();
		if (sz <= 0) {
			return null;
		}
		CArray children = JsonFactory.createArray(name);
		CStruc cs = null;
		String value;
		for (int i = 0; i < sz; i++) {
			cs = data.getRecord(i);
			if (cs.getField(parnentid) == null) {
				continue;
			}
			value = cs.getField(parnentid).getValue();
			if (parnent.equalsIgnoreCase(value)) {
				// data.removeRow(cs);
				children.add(cs);
				CArray ca = getchildren(data, id, parnentid, cs.getField(id).getValue());
				if (ca != null && !ca.isEmpty()) {
					cs.addArray(ca);
				}
			}
		}
		return children;
	}
}