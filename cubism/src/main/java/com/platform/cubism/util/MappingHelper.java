package com.platform.cubism.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.JsonFactory;

public class MappingHelper {
	public static void DesEncryptMapping(CArray in, String fldname){
		if (in == null || in.isEmpty()) {
			return;
		}
		CField fld;
		CStruc cs;
		for (int i = 0, j = in.size(); i < j; i++) {
			cs = in.getRecord(i);
			if (cs == null || cs.isEmpty()) {
				continue;
			}
			if(!cs.containsName(fldname)){
				continue;
			}
			fld = cs.getField(fldname);
			fld.setValue(SecurityHelper.DesEncrypt(fld.getValue()));
		}
	}
	public static void Extjs42FilterMapping(CStruc in) {
		if (in == null || !in.containsName("filter")) {
			return;
		}
		// filter:["data":{"type":"list","value":["10kV交换机1", "10kV交换机2"]},"field":"substationname"],
		CArray filter = in.getArray("filter");
		if (filter == null || filter.isEmpty()) {
			return;
		}
		CField fld;
		CStruc cs;
		CStruc dt;
		CArray arr;
		String typ;
		for (int i = 0, j = filter.size(); i < j; i++) {
			cs = filter.getRecord(i);
			if (cs == null || cs.isEmpty()) {
				continue;
			}
			dt = cs.getStruc("data");
			typ = dt.getField("type").getValue();
			if("list".equalsIgnoreCase(typ)){
				arr = dt.getArray("value");
				if (arr == null || arr.isEmpty()) {
					continue;
				}
				arr.setName(cs.getField("field").getValue());
				in.addArray(arr);
			}
			else{
				fld = dt.getField("value");
				if (fld == null || fld.isEmpty()) {
					continue;
				}
				fld.setName(cs.getField("field").getValue());
				in.addField(fld);
			}
		}
	}
	
	public static void ExtjsFilterMapping(CStruc in) {
		if (in == null || !in.containsName("filter")) {
			return;
		}
		// filter:"[{"type":"list","value":["110kV\u6d77\u6d41\u56fe\u53d8\u7535\u7ad9"],"field":"substationname"}]",
		String filter = in.getField("filter").getValue();
		if (filter == null || filter.length() <= 0) {
			return;
		}
		try {
			filter = URLDecoder.decode(filter, "utf-8");
		} catch (UnsupportedEncodingException e) {
			;
		}
		CArray ca = JsonFactory.createArray().toJson(filter);
		if (ca == null || ca.isEmpty()) {
			return;
		}
		CField fld;
		CStruc cs;
		CArray arr;
		String typ;
		for (int i = 0, j = ca.size(); i < j; i++) {
			cs = ca.getRecord(i);
			if (cs == null || cs.isEmpty()) {
				continue;
			}
			typ = cs.getField("type").getValue();
			if("list".equalsIgnoreCase(typ)){
				arr = cs.getArray("value");
				if (arr == null || arr.isEmpty()) {
					continue;
				}
				arr.setName(cs.getField("field").getValue());
				in.addArray(arr);
			}
			else{
				fld = cs.getField("value");
				if (fld == null || fld.isEmpty()) {
					continue;
				}
				fld.setName(cs.getField("field").getValue());
				in.addField(fld);
			}
		}
	}
}
