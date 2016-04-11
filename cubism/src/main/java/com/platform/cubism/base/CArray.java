package com.platform.cubism.base;

import static com.platform.cubism.base.Json.DOT_SIGN;
import static com.platform.cubism.util.StringUtils.getTabSpace;
import static com.platform.cubism.util.StringUtils.hasText;
import com.platform.cubism.util.CubismHelper;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collections;
import java.util.List;

public class CArray implements Serializable {
	private static final long serialVersionUID = -1005981101927874714L;
	private String name;
	private List<CStruc> records;

	public CArray() {
		;
	}

	public CArray(String name) {
		setName(name);
	}

	private void init() {
		if (records == null) {
			records = new CopyOnWriteArrayList<CStruc>();
		}
	}

	public boolean isEmpty() {
		if (records == null || records.isEmpty()) {
			return true;
		}
		for (CStruc stc : records) {
			if (stc.getNames().length > 0) {
				return false;
			}
		}
		return true;
	}

	private boolean hasColumn(String column) {
		if (isEmpty()) {
			return false;
		}
		for (CStruc stc : records) {
			if (stc.getObject(column) != null) {
				return true;
			}
		}
		return false;
	}

	public CStruc getColumn() {
		if (isEmpty()) {
			return null;
		}
		for (CStruc stc : records) {
			if (stc.getNames().length > 0) {
				return stc;
			}
		}
		return null;
	}

	public String[] getColumNames() {
		CStruc stc = getColumn();
		if (stc != null) {
			return stc.getNames();
		} else {
			return null;
		}
	}

	public Class<? extends Object> getColumnType(String columnName) {
		if (hasColumn(columnName)) {
			return getColumn().getType(columnName);
		}

		return null;
	}

	public boolean isFieldColumn(String columnName) {
		if (hasColumn(columnName)) {
			return getColumn().isField(columnName);
		} else {
			return false;
		}
	}

	public boolean isStrucColumn(String columnName) {
		if (hasColumn(columnName)) {
			return getColumn().isStruc(columnName);
		} else {
			return false;
		}
	}

	public boolean isArrayColumn(String columnName) {
		if (hasColumn(columnName)) {
			return getColumn().isArray(columnName);
		} else {
			return false;
		}
	}

	public int size() {
		return (records != null) ? records.size() : 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null && name.indexOf(DOT_SIGN) >= 0) {
			throw new IllegalArgumentException("[Assertion failed] - the name can't include a dot.");
		}
		this.name = name;
	}

	public List<CStruc> getRecords() {
		if (records == null) {
			return null;
		}
		return Collections.unmodifiableList(records);
	}

	public CStruc getRecord(int idx) {
		if (idx < 0 || idx >= records.size()) {
			return null;
		}
		return records.get(idx);
	}

	public CField[] removeFieldColumn(String columnName) {
		if (records == null || records.isEmpty()) {
			return null;
		}
		int idx = columnName.indexOf(DOT_SIGN);
		if (idx < 0) {
			if (isFieldColumn(columnName)) {
				CField[] cfs = new CField[records.size()];
				int i = 0;
				for (CStruc cs : records) {
					cfs[i++] = cs.removeField(columnName);
				}
				return cfs;
			}
			return null;
		}

		String first = columnName.substring(0, idx);
		String second = columnName.substring(idx + 1);

		Object obj;
		CField[] fieldarr;
		List<CField> result = new CopyOnWriteArrayList<CField>();

		CStruc[] strucs = getStrucColumn(first);
		if (strucs != null) {
			for (CStruc struc : strucs) {
				if (struc == null) {
					continue;
				}
				obj = struc.removeFieldObject(second);
				if (obj == null) {
					continue;
				}
				if (obj.getClass().isArray()) {
					fieldarr = (CField[]) obj;
					for (CField cf : fieldarr) {
						result.add(cf);
					}
				} else {
					result.add((CField) obj);
				}
			}

			if (!result.isEmpty()) {
				return result.toArray(new CField[0]);
			} else {
				return null;
			}
		}

		CArray[] arrays = getArrayColumn(first);
		if (arrays != null) {
			for (CArray array : arrays) {
				if (array == null) {
					continue;
				}
				fieldarr = array.removeFieldColumn(second);
				if (fieldarr == null) {
					continue;
				}
				for (CField cf : fieldarr) {
					result.add(cf);
				}
			}
			if (!result.isEmpty()) {
				return result.toArray(new CField[0]);
			} else {
				return null;
			}
		}

		return null;
	}

	public CStruc[] removeStrucColumn(String columnName) {
		if (records == null || records.isEmpty()) {
			return null;
		}
		int idx = columnName.indexOf(DOT_SIGN);
		if (idx < 0) {
			if (isStrucColumn(columnName)) {
				CStruc[] css = new CStruc[records.size()];
				int i = 0;
				for (CStruc cs : records) {
					css[i++] = cs.removeStruc(columnName);
				}
				return css;
			}
			return null;
		}

		String first = columnName.substring(0, idx);
		String second = columnName.substring(idx + 1);

		Object obj;
		CStruc[] strucarr;
		List<CStruc> result = new CopyOnWriteArrayList<CStruc>();

		CArray[] arrays = getArrayColumn(first);
		if (arrays != null) {
			for (CArray array : arrays) {
				if (array == null) {
					continue;
				}
				strucarr = array.removeStrucColumn(second);
				if (strucarr == null) {
					continue;
				}
				for (CStruc cs : strucarr) {
					result.add(cs);
				}
			}
			if (!result.isEmpty()) {
				return result.toArray(new CStruc[0]);
			} else {
				return null;
			}
		}

		CStruc[] strucs = getStrucColumn(first);
		if (strucs != null) {
			for (CStruc struc : strucs) {
				if (struc == null) {
					continue;
				}
				obj = struc.removeStrucObject(second);
				if (obj == null) {
					continue;
				}
				if (obj.getClass().isArray()) {
					strucarr = (CStruc[]) obj;
					for (CStruc cs : strucarr) {
						result.add(cs);
					}
				} else {
					result.add((CStruc) obj);
				}
			}

			if (!result.isEmpty()) {
				return result.toArray(new CStruc[0]);
			} else {
				return null;
			}
		}

		return null;
	}

	public CArray[] removeArrayColumn(String columnName) {
		if (records == null || records.isEmpty()) {
			return null;
		}
		int idx = columnName.indexOf(DOT_SIGN);
		if (idx < 0) {
			if (isArrayColumn(columnName)) {
				CArray[] cas = new CArray[records.size()];
				int i = 0;
				for (CStruc cs : records) {
					cas[i++] = cs.removeArray(columnName);
				}
				return cas;
			}
			return null;
		}

		String first = columnName.substring(0, idx);
		String second = columnName.substring(idx + 1);

		Object obj;
		CArray[] arrarr;
		List<CArray> result = new CopyOnWriteArrayList<CArray>();

		CStruc[] strucs = getStrucColumn(first);
		if (strucs != null) {
			for (CStruc struc : strucs) {
				if (struc == null) {
					continue;
				}
				obj = struc.removeArrayObject(second);
				if (obj == null) {
					continue;
				}
				if (obj.getClass().isArray()) {
					arrarr = (CArray[]) obj;
					for (CArray ca : arrarr) {
						result.add(ca);
					}
				} else {
					result.add((CArray) obj);
				}
			}

			if (!result.isEmpty()) {
				return result.toArray(new CArray[0]);
			} else {
				return null;
			}
		}

		CArray[] arrays = getArrayColumn(first);
		if (arrays != null) {
			for (CArray array : arrays) {
				if (array == null) {
					continue;
				}
				arrarr = array.removeArrayColumn(second);
				if (arrarr == null) {
					continue;
				}
				for (CArray ca : arrarr) {
					result.add(ca);
				}
			}
			if (!result.isEmpty()) {
				return result.toArray(new CArray[0]);
			} else {
				return null;
			}
		}

		return null;
	}

	public CField[] getFieldColumn(String columnName) {
		if (records == null || records.isEmpty()) {
			return null;
		}
		int idx = columnName.indexOf(DOT_SIGN);
		if (idx < 0) {
			if (isFieldColumn(columnName)) {
				CField[] cfs = new CField[records.size()];
				int i = 0;
				for (CStruc cs : records) {
					cfs[i++] = cs.getField(columnName);
				}
				return cfs;
			}
			return null;
		}

		String first = columnName.substring(0, idx);
		String second = columnName.substring(idx + 1);

		Object obj;
		CField[] fieldarr;
		List<CField> result = new CopyOnWriteArrayList<CField>();

		CStruc[] strucs = getStrucColumn(first);
		if (strucs != null) {
			for (CStruc struc : strucs) {
				if (struc == null) {
					continue;
				}
				obj = struc.getFieldObject(second);
				if (obj == null) {
					continue;
				}
				if (obj.getClass().isArray()) {
					fieldarr = (CField[]) obj;
					for (CField cf : fieldarr) {
						result.add(cf);
					}
				} else {
					result.add((CField) obj);
				}
			}

			if (!result.isEmpty()) {
				return result.toArray(new CField[0]);
			} else {
				return null;
			}
		}

		CArray[] arrays = getArrayColumn(first);
		if (arrays != null) {
			for (CArray array : arrays) {
				if (array == null) {
					continue;
				}
				fieldarr = array.getFieldColumn(second);
				if (fieldarr == null) {
					continue;
				}
				for (CField cf : fieldarr) {
					result.add(cf);
				}
			}
			if (!result.isEmpty()) {
				return result.toArray(new CField[0]);
			} else {
				return null;
			}
		}

		return null;
	}

	public CStruc[] getStrucColumn(String columnName) {
		if (records == null || records.isEmpty()) {
			return null;
		}
		int idx = columnName.indexOf(DOT_SIGN);
		if (idx < 0) {
			if (isStrucColumn(columnName)) {
				CStruc[] css = new CStruc[records.size()];
				int i = 0;
				for (CStruc cs : records) {
					css[i++] = cs.getStruc(columnName);
				}
				return css;
			}
			return null;
		}

		String first = columnName.substring(0, idx);
		String second = columnName.substring(idx + 1);

		Object obj;
		CStruc[] strucarr;
		List<CStruc> result = new CopyOnWriteArrayList<CStruc>();

		CArray[] arrays = getArrayColumn(first);
		if (arrays != null) {
			for (CArray array : arrays) {
				if (array == null) {
					continue;
				}
				strucarr = array.getStrucColumn(second);
				if (strucarr == null) {
					continue;
				}
				for (CStruc cs : strucarr) {
					result.add(cs);
				}
			}
			if (!result.isEmpty()) {
				return result.toArray(new CStruc[0]);
			} else {
				return null;
			}
		}

		CStruc[] strucs = getStrucColumn(first);
		if (strucs != null) {
			for (CStruc struc : strucs) {
				if (struc == null) {
					continue;
				}
				obj = struc.getStrucObject(second);
				if (obj == null) {
					continue;
				}
				if (obj.getClass().isArray()) {
					strucarr = (CStruc[]) obj;
					for (CStruc cs : strucarr) {
						result.add(cs);
					}
				} else {
					result.add((CStruc) obj);
				}
			}

			if (!result.isEmpty()) {
				return result.toArray(new CStruc[0]);
			} else {
				return null;
			}
		}

		return null;
	}

	public CArray[] getArrayColumn(String columnName) {
		if (records == null || records.isEmpty()) {
			return null;
		}
		int idx = columnName.indexOf(DOT_SIGN);
		if (idx < 0) {
			if (isArrayColumn(columnName)) {
				CArray[] cas = new CArray[records.size()];
				int i = 0;
				for (CStruc cs : records) {
					cas[i++] = cs.getArray(columnName);
				}
				return cas;
			}
			return null;
		}

		String first = columnName.substring(0, idx);
		String second = columnName.substring(idx + 1);

		Object obj;
		CArray[] arrarr;
		List<CArray> result = new CopyOnWriteArrayList<CArray>();

		CStruc[] strucs = getStrucColumn(first);
		if (strucs != null) {
			for (CStruc struc : strucs) {
				if (struc == null) {
					continue;
				}
				obj = struc.getArrayObject(second);
				if (obj == null) {
					continue;
				}
				if (obj.getClass().isArray()) {
					arrarr = (CArray[]) obj;
					for (CArray ca : arrarr) {
						result.add(ca);
					}
				} else {
					result.add((CArray) obj);
				}
			}

			if (!result.isEmpty()) {
				return result.toArray(new CArray[0]);
			} else {
				return null;
			}
		}

		CArray[] arrays = getArrayColumn(first);
		if (arrays != null) {
			for (CArray array : arrays) {
				if (array == null) {
					continue;
				}
				arrarr = array.getArrayColumn(second);
				if (arrarr == null) {
					continue;
				}
				for (CArray ca : arrarr) {
					result.add(ca);
				}
			}
			if (!result.isEmpty()) {
				return result.toArray(new CArray[0]);
			} else {
				return null;
			}
		}

		return null;
	}

	public Object[] getObjectColumn(String columnName) {
		Object[] columnObj = getFieldColumn(columnName);
		if (columnObj != null && columnObj.length > 0) {
			return columnObj;
		}
		columnObj = getStrucColumn(columnName);
		if (columnObj != null && columnObj.length > 0) {
			return columnObj;
		}
		columnObj = getArrayColumn(columnName);
		if (columnObj != null && columnObj.length > 0) {
			return columnObj;
		}

		return null;
	}
	
	public CArray addRows(CField[] flds){
		if (flds == null) {
			return this;
		}
		init();
		CStruc stc;
		for(CField cf : flds){
			stc = new CStruc(name);
			stc.addField(cf);
			records.add(stc);
		}
		return this;
	}

	public CArray add(CField... flds) {
		if (flds == null) {
			return this;
		}
		init();
		CStruc stc = new CStruc(name);
		for(CField cf : flds){
			stc.addField(cf);
		}
		records.add(stc);

		return this;
	}
	public CArray add(CStruc stc) {
		if (stc == null) {
			return this;
		}
		init();
		records.add(stc);

		return this;
	}

	public CArray add(CArray arr) {
		if (arr == null || arr.getRecords() == null || arr.isEmpty()) {
			return this;
		}
		init();
		records.addAll(arr.getRecords());

		return this;
	}

	public CArray addColumn(CField[] value) {
		if (value == null) {
			return this;
		}
		init();
		int idx = 0;
		for (CStruc cs : records) {
			if (idx < value.length) {
				cs.addField(value[idx]);
				idx++;
			} else {
				cs.addField(new CField().copyOf(value[0]).reset());
			}
		}

		return this;
	}

	public CArray addColumn(CStruc[] value) {
		if (value == null) {
			return this;
		}
		init();
		int idx = 0;
		for (CStruc cs : records) {
			if (idx < value.length) {
				cs.addStruc(value[idx]);
				idx++;
			} else {
				cs.addStruc(new CStruc().copyOf(value[0]).reset());
			}
		}

		return this;
	}

	public CArray addColumn(CArray[] value) {
		if (value == null) {
			return this;
		}
		init();
		int idx = 0;
		for (CStruc cs : records) {
			if (idx < value.length) {
				cs.addArray(value[idx]);
				idx++;
			} else {
				cs.addArray(new CArray().copyOf(value[0]).reset());
			}
		}

		return this;
	}

	public CStruc removeRow(int idx) {
		if (records == null || idx < 0 || idx >= records.size()) {
			return null;
		}

		return records.remove(idx);
	}

	public CStruc removeRow(CStruc struc) {
		if (records == null) {
			return null;
		}
		if (records.remove(struc)) {
			return struc;
		}
		return null;
	}

	public CStruc removeRow(CField fld) {
		if (records == null) {
			return null;
		}
		String name = fld.getName();
		String value = fld.getValue();
		CStruc struc = null;
		for (CStruc cs : records) {
			if (value.equals(cs.getField(name).getValue())) {
				if (records.remove(cs)) {
					struc = cs;
				}
			}
		}
		return struc;
	}

	public Object[] removeColumn(int idx) {
		if (records == null || idx < 0) {
			return null;
		}
		CStruc columnName = getColumn();
		if (idx >= columnName.size()) {
			return null;
		}
		return removeColumn(columnName.getNames(idx));
	}

	public Object[] removeColumn(String columnName) {
		Object[] columnObj = removeFieldColumn(columnName);
		if (columnObj != null && columnObj.length > 0) {
			return columnObj;
		}
		columnObj = removeStrucColumn(columnName);
		if (columnObj != null && columnObj.length > 0) {
			return columnObj;
		}
		columnObj = removeArrayColumn(columnName);
		if (columnObj != null && columnObj.length > 0) {
			return columnObj;
		}

		return null;
	}

	public void clear() {
		if (records != null) {
			records.clear();
		}
	}

	public CArray reset() {
		if (records == null) {
			return this;
		}
		CStruc cs = getColumn();
		clear();
		records.add(cs.reset());
		return this;
	}

	public String getStringValues(String determine) {
		if (isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (CStruc s : getRecords()) {
			sb.append(s.getStringValues(determine)).append(determine);
		}
		if (sb.length() > 0) {
			sb.delete(sb.length() - determine.length(), sb.length());
			return sb.toString();
		} else {
			return "";
		}
	}

	public CArray copyOf(CArray arr) {
		if (arr == null) {
			return this;
		}

		init();

		name = arr.getName();
		records = CubismHelper.copyOf(arr.getRecords());
		return this;
	}

	public CArray mergeOf(CArray arr) {
		if (arr == null) {
			return this;
		}
		if (!hasText(name)) {
			name = arr.getName();
		}
		init();
		for (CStruc s : arr.getRecords()) {
			records.add(s);
		}
		return this;
	}
	
	public CArray addOf(CArray arr) {
		if (arr == null) {
			return this;
		}
		if (!hasText(name)) {
			name = arr.getName();
		}
		init();
		for (CStruc s : arr.getRecords()) {
			records.add(s);
		}
		return this;
	}

	public CArray setValue(CArray arr) {
		init();
		clear();
		records = CubismHelper.copyOf(arr.getRecords());
		return this;
	}

	public CField[] flat() {
		if (isEmpty()) {
			return new CField[0];
		}
		List<CField> all = new CopyOnWriteArrayList<CField>();
		for (CStruc s : getRecords()) {
			for (CField f : s.flat()) {
				all.add(f);
			}
		}
		return all.toArray(new CField[0]);
	}
	
	public String toUrl() {//目前只支持单层简单域
		StringBuilder sb = new StringBuilder();
		String[] colums = getColumNames();
		if (colums != null && colums.length > 0) {
			for (String colname : colums) {
				CField[] colfld = getFieldColumn(colname);
				if(colfld == null || colfld.length <= 0){
					continue;
				}
				if(!hasText(name)){
					sb.append(colname).append("=");
				}
				else{
					sb.append(name).append(".").append(colname).append("=");
				}
				for (CField fld : colfld) {
					sb.append(fld.getFormatValue()).append(",");
				}
				if (sb.charAt(sb.length() - 1) == ',') {
					sb.deleteCharAt(sb.length() - 1);
				}
				sb.append("&");
			}
		}
		if (sb.charAt(sb.length() - 1) == '&') {
			sb.deleteCharAt(sb.length() - 1);
		}
		
		return sb.toString();
	}
	
	public CArray toJson(JsonTokener tokener) {
		if (tokener == null || tokener.isNull()) {
			return this;
		}
		if (tokener.nextClean() != '[') {
			throw tokener.syntaxError("A JSONArray text must begin with '['");
		}
		char c;

		for (;;) {
			c = tokener.nextClean();
			switch (c) {
			case '[':
				throw tokener.syntaxError("A JSONArray text can't begin with '[['");
			case ']':
				return this;
			case ',':
				tokener.back();
				add(new CStruc(name));
				break;
			default:
				tokener.back();
				add(new CStruc(name).toJson(tokener));
			}

			switch (tokener.nextClean()) {
			case ';':
			case ',':
				if (tokener.nextClean() == ']') {
					return this;
				}
				tokener.back();
				break;
			case ']':
				return this;
			default:
				throw tokener.syntaxError("Expected a ',' or ']'");
			}
		}
	}

	public CArray toJson(String json) {
		return toJson(new JsonTokener(json));
	}

	public String toJson() {
		StringBuilder sb = new StringBuilder();
		if (hasText(name)) {
			sb.append("\"").append(name).append("\":");
		}
		sb.append("[");
		String perfix = "\"" + name + "\":";
		String stcstr;
		if (records != null) {
			boolean oneColum = getColumNames().length == 1; //只有一列
			for (CStruc stc : records) {
				if(oneColum && stc.isOnlyField()){//只有一列并且该列为单值域
					CField fld = stc.getField().values().iterator().next();
					sb.append(fld.toJsonValue()).append(",");
				}
				else{
					stcstr = stc.toJson(true);
					if (stcstr != null && stcstr.startsWith(perfix))
						sb.append(stcstr.substring(perfix.length())).append(",");
					else
						sb.append(stcstr).append(",");
				}				
			}
		}
		if (sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.append("]").toString();
	}

	public String toString(int level) {
		StringBuilder sb = new StringBuilder(getTabSpace(level));
		if (hasText(name)) {
			sb.append(name).append(":");
		}
		sb.append("[");
		String perfix = name + ":";
		String stcstr, s;
		int cnt = 0;
		if (records != null) {
			for (CStruc stc : records) {
				stcstr = stc.toString(level + 1, true);
				s = stcstr.trim();
				if (s != null && s.startsWith(perfix)) {
					int idx = stcstr.indexOf(perfix);
					sb.append(stcstr.substring(0, idx));
					sb.append(cnt++).append(":");
					sb.append(stcstr.substring(perfix.length() + idx)).append(",");
				} else {
					int idx = stcstr.indexOf("{");
					sb.append(stcstr.substring(0, idx));
					sb.append(cnt++).append(":");
					sb.append(stcstr.substring(idx)).append(",");
				}
			}
		}
		if (sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.append(getTabSpace(level)).append("]").toString();
	}

	public String toString() {
		return toString(0);
	}
}