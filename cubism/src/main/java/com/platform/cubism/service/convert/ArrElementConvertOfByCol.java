package com.platform.cubism.service.convert;

import static com.platform.cubism.util.StringUtils.hasText;

import java.util.HashMap;
import java.util.Map;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.config.ArrElement;
import com.platform.cubism.service.config.FldElement;
import com.platform.cubism.service.config.StcElement;
import com.platform.cubism.util.Assert;

public class ArrElementConvertOfByCol implements ArrElementConvert {

	public boolean isConvert(ArrElement arrElem, CStruc data) {
		String value = arrElem.getValue();
		StcElement column = arrElem.getStc();
		if (!hasText(value) && column != null && !column.isEmpty()) {// 按列解析时value必须为空
			return true;
		}
		return false;
	}

	public CArray convert(ArrElement arrElem, CStruc data) {
		Assert.notNull(arrElem);
		String name = arrElem.getName();
		Assert.hasText(name);
		
		if(namePattern.matcher(name).matches()){
			try{
				name = data.getField(namePattern.matcher(name).replaceAll("$1")).getValue();
			}catch(Throwable t){
				name = namePattern.matcher(name).replaceAll("$1");
			}
		}
		
		CArray array = JsonFactory.createArray(name);
		StcElement column = arrElem.getStc();

		if (data == null || data.isEmpty()) {
			return array;
		}

		Map<String, CField> fldCol = null;
		Map<String, CField[]> fldCols = null;
		if (column.getFld() != null) {
			for (FldElement fld : column.getFld()) {
				CField cf = ConvertManager.fldConvert(fld, data);
				if (cf == null) {
					CField[] cfs = ConvertManager.fldConvertToArray(fld, data);
					if (fldCols == null) {
						fldCols = new HashMap<String, CField[]>();
					}
					fldCols.put(fld.getName(), cfs);
				} else {
					if (fldCol == null) {
						fldCol = new HashMap<String, CField>();
					}
					fldCol.put(fld.getName(), cf);
				}
			}
		}

		Map<String, CStruc> stcCol = null;
		Map<String, CStruc[]> stcCols = null;
		if (column.getStc() != null) {
			for (StcElement stc : column.getStc()) {
				CStruc cs = ConvertManager.stcConvert(stc, data);
				if (cs == null) {
					CStruc[] css = ConvertManager.stcConvertToArray(stc, data);
					if (stcCols == null) {
						stcCols = new HashMap<String, CStruc[]>();
					}
					stcCols.put(stc.getName(), css);
				} else {
					if (stcCol == null) {
						stcCol = new HashMap<String, CStruc>();
					}
					stcCol.put(stc.getName(), cs);
				}
			}
		}

		Map<String, CArray> arrCol = null;
		Map<String, CArray[]> arrCols = null;
		if (column.getArr() != null) {
			for (ArrElement arr : column.getArr()) {
				CArray ca = ConvertManager.arrConvert(arr, data);
				if (ca == null) {
					CArray[] cas = ConvertManager.arrConvertToArray(arr, data);
					if (arrCols == null) {
						arrCols = new HashMap<String, CArray[]>();
					}
					arrCols.put(arr.getName(), cas);
				} else {
					if (arrCol == null) {
					}
					arrCol = new HashMap<String, CArray>();
					arrCol.put(arr.getName(), ca);
				}
			}
		}

		if ((fldCol == null || fldCol.isEmpty()) && (fldCols == null || fldCols.isEmpty()) && (stcCol == null || stcCol.isEmpty()) && (stcCols == null || stcCols.isEmpty())
				&& (arrCol == null || arrCol.isEmpty()) && (arrCols == null || arrCols.isEmpty())) {
			return array;
		}

		int cnt = 0;

		if (fldCols != null && fldCols.values() != null) {
			for (CField[] flds : fldCols.values()) {
				if (flds != null && cnt < flds.length) {
					cnt = flds.length;
				}
			}
		}
		if (stcCols != null && stcCols.values() != null) {
			for (CStruc[] stcs : stcCols.values()) {
				if (stcs != null && cnt < stcs.length) {
					cnt = stcs.length;
				}
			}
		}
		if (arrCols != null && arrCols.values() != null) {
			for (CArray[] arrs : arrCols.values()) {
				if (arrs != null && cnt < arrs.length) {
					cnt = arrs.length;
				}
			}
		}

		if (cnt <= 0) {
			CStruc dtStruc = JsonFactory.createStruc(name);
			String nm, vl;
			if (column != null && column.getFld() != null) {
				for (FldElement fld : column.getFld()) {
					nm = fld.getName();
					if (fldCol != null && fldCol.containsKey(nm)) {
						vl = fldCol.get(nm).getValue();
						dtStruc.addField(JsonFactory.createField(nm, vl, fld.getFormat(), fld.getType()));
					} else {
						dtStruc.addField(JsonFactory.createField(nm, null, fld.getFormat(), fld.getType()));
					}
				}
			}
			if (column != null && column.getStc() != null) {
				for (StcElement stc : column.getStc()) {
					nm = stc.getName();
					if (stcCol != null && stcCol.containsKey(nm)) {
						dtStruc.addStruc(JsonFactory.createStruc(nm).setValue(stcCol.get(nm)));
					} else {
						dtStruc.addStruc(JsonFactory.createStruc(nm));
					}

				}
			}
			if (column != null && column.getArr() != null) {
				for (ArrElement arr : column.getArr()) {
					nm = arr.getName();
					if (arrCol != null && arrCol.containsKey(nm)) {
						dtStruc.addArray(JsonFactory.createArray(nm).setValue(arrCol.get(nm)));
					} else {
						dtStruc.addArray(JsonFactory.createArray(nm));
					}
				}
			}
			
			return array.add(dtStruc);
		}

		for (int i = 0; i < cnt; i++) {
			CStruc dtStruc = JsonFactory.createStruc(name);
			CField cf;
			String nm, vl;
			if (column != null && column.getFld() != null) {
				for (FldElement fld : column.getFld()) {
					nm = fld.getName();
					cf = null;
					if (fldCols.get(nm) != null && i < fldCols.get(nm).length) {
						cf = fldCols.get(nm)[i];
						vl = cf == null ? null : cf.getValue();
						dtStruc.addField(JsonFactory.createField(nm, vl, fld.getFormat(), fld.getType()));
					} else if (fldCol.get(nm) != null) {
						cf = fldCol.get(nm);
						vl = cf == null ? null : cf.getValue();
						dtStruc.addField(JsonFactory.createField(nm, vl, fld.getFormat(), fld.getType()));
					} else {
						dtStruc.addField(JsonFactory.createField(nm, null, fld.getFormat(), fld.getType()));
					}
				}
			}
			if (column != null && column.getStc() != null) {
				for (StcElement stc : column.getStc()) {
					nm = stc.getName();
					if (stcCols.get(nm) != null && i < stcCols.get(nm).length) {
						dtStruc.addStruc(JsonFactory.createStruc(nm).setValue(stcCols.get(nm)[i]));
					} else if (stcCol != null && stcCol.containsKey(nm)) {
						dtStruc.addStruc(JsonFactory.createStruc(nm).setValue(stcCol.get(nm)));
					} else {
						dtStruc.addStruc(JsonFactory.createStruc(nm));
					}
				}
			}
			if (column != null && column.getArr() != null) {
				for (ArrElement arr : column.getArr()) {
					nm = arr.getName();
					if (arrCols.get(nm) != null && i < arrCols.get(nm).length) {
						dtStruc.addArray(JsonFactory.createArray(nm).setValue(arrCols.get(nm)[i]));
					} else if (arrCol != null && arrCol.containsKey(nm)) {
						dtStruc.addArray(JsonFactory.createArray(nm).setValue(arrCol.get(nm)));
					} else {
						dtStruc.addArray(JsonFactory.createArray(nm));
					}
				}
			}

			array.add(dtStruc);
		}

		return array;
	}
}