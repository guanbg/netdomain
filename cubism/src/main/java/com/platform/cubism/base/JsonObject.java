package com.platform.cubism.base;

import static com.platform.cubism.util.CubismHelper.serializeAndDeserialize;
import static com.platform.cubism.util.CubismHelper.isMapOfType;
import static com.platform.cubism.util.StringUtils.hasText;
import static com.platform.cubism.util.StringUtils.isEncoding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.platform.cubism.jdbc.adapter.PageAdapterManager;
import com.platform.cubism.util.Assert;

public class JsonObject implements Json, Serializable {
	private static final long serialVersionUID = -6275065061736734705L;
	private CStruc struc;

	public JsonObject() {
		struc = new CStruc();
	}

	public JsonObject(String name) {
		struc = new CStruc(name);
	}

	public Json setName(String name) {
		if (struc == null) {
			throw new IllegalArgumentException("[setName failed] - the struc is null");
		}
		this.struc.setName(name);
		return this;
	}

	public String toString() {
		return struc.toString();
	}

	public String toJson() {
		return struc.toJson();
	}

	public Json toJson(String json) {
		if (!hasText(json)) {
			return this;
		}
		struc.toJson(json);
		return this;
	}

	public Json toJson(ResultSet rs, String name) throws SQLException {
		toJson(rs, name, null, false, true);
		return this;
	}

	public Json toJson(ResultSet rs, String name, boolean alwayarray, boolean lowercase) throws SQLException {
		toJson(rs, name, null, alwayarray, lowercase);
		return this;
	}

	public Json toJson(ResultSet rs, String name, String[] colNames, boolean alwayarray, boolean lowercase) throws SQLException {
		if (rs == null || struc.containsName(name)) {
			return this;
		}
		try {// 有些数据库驱动不支持此方法
			if (rs.isClosed()) {
				return this;
			}
		} catch (Error e) {
		}

		ResultSetMetaData md = rs.getMetaData();
		int len = md.getColumnCount();
		String[] columns = new String[len];
		String[] types = new String[len];
		String str;
		for (int i = 0; i < len; i++) {
			if (colNames == null || i >= colNames.length) {
				str = md.getColumnLabel(i + 1);
				if (PageAdapterManager.isSysAutoClumn(str)) {
					continue;
				}
				columns[i] = str;
			} else {
				columns[i] = colNames[i];
			}
			types[i] = md.getColumnTypeName(i + 1);
		}

		CStruc cs = null;
		CArray ca = null;
		int cnt = 0;
		String columnName = null;
		while (rs.next()) {
			if (cs != null && cnt >= 1) {
				if (ca == null) {
					ca = new CArray(name);
				}
				ca.add(cs);
			}
			cs = new CStruc(name);
			for (int i = 0; i < columns.length; i++) {
				columnName = columns[i];
				if (columns[i] == null || "".equals(columns[i])) {
					continue;
				}
				if (lowercase) {
					columnName = columnName.toLowerCase();
				}
				if (rs.getObject(i + 1) != null && "datetime".equalsIgnoreCase(types[i])) {
					String vt = SimpleDateFormat.getTimeInstance().format(rs.getObject(i + 1));
					if (vt.matches(".*0+.0+.0+$")) {
						// String vd =
						// SimpleDateFormat.getDateInstance().format(rs.getObject(i
						// + 1));
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String vd = sdf.format(rs.getObject(i + 1));
						cs.addField(new CField(columnName, vd, "", types[i]));
					} else {
						String vdt = SimpleDateFormat.getDateTimeInstance().format(rs.getObject(i + 1));
						cs.addField(new CField(columnName, vdt, "", types[i]));
					}
				} else if (rs.getObject(i + 1) != null && "varbinary".equalsIgnoreCase(types[i])) {
					cs.addField(new CField(columnName, rs.getString(i + 1), "", "String"));
				} else if (rs.getObject(i + 1) != null && ("nclob".equalsIgnoreCase(types[i]) || "clob".equalsIgnoreCase(types[i]))) {
					StringBuilder sb = new StringBuilder();
					try {
						String s = null;
						BufferedReader br = new BufferedReader(rs.getClob(i + 1).getCharacterStream());
						while ((s = br.readLine()) != null) {
							if (isEncoding(s, "ISO8859_1")) {//以流的方式写入时oracle的字符编码格式
								sb.append(new String(s.getBytes("ISO8859_1"), "UTF-8"));
							} else {//普通方式插入时的字符编码格式
								sb.append(s);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					cs.addField(new CField(columnName, sb.toString(), "", "String"));
					sb = null;
				} else {
					cs.addField(new CField(columnName, rs.getObject(i + 1), "", types[i]));
				}
			}
			cnt++;
		}
		if (cnt == 1) {
			if (alwayarray) {
				if (ca == null) {
					ca = new CArray(name);
				}
				ca.add(cs);
				addArray(ca);
			} else {
				addStruc(cs);
			}

		} else {
			if (ca == null) {
				return this;
			}
			if (cs != null) {
				ca.add(cs);
				addArray(ca);
			}
		}

		return this;
	}

	public int size() {
		return struc.size();
	}

	public boolean contains(String name) {
		return struc.containsName(name);
	}

	public CStruc getObject() {
		return struc;
	}

	public Json addField(CField fld) {
		if (fld != null)
			struc.addField(fld);
		return this;
	}

	public Json addField(CField[] flds) {
		for (CField f : flds) {
			addField(f);
		}
		return this;
	}

	public Json addField(Json json) {
		struc.mergeField(json.getObject().getField());
		return this;
	}

	public Json addStruc(CStruc stc) {
		if (stc != null)
			struc.addStruc(stc);

		return this;
	}

	public Json addStruc(CStruc[] stcs) {
		for (CStruc s : stcs) {
			addStruc(s);
		}
		return this;
	}

	public Json addStruc(Json json) {
		struc.mergeStruc(json.getObject().getStruc());
		return this;
	}

	public Json addArray(CArray arr) {
		if (arr != null)
			struc.addArray(arr);

		return this;
	}

	public Json addArray(CArray[] arrs) {
		for (CArray a : arrs) {
			addArray(a);
		}
		return this;
	}

	public Json addArray(Json json) {
		struc.mergeArray(json.getObject().getArray());
		return this;
	}

	public CField getField(String name) {
		Assert.notNull(name);
		return struc.getField(name);
	}

	public CStruc getStruc(String name) {
		Assert.notNull(name);
		return struc.getStruc(name);
	}

	public CArray getArray(String name) {
		Assert.notNull(name);
		return struc.getArray(name);
	}

	public Object get(String name) {
		if(name == null || name.length() <= 0){
			return null;
		}
		//Assert.notNull(name);
		return struc.getObject(name);
	}

	public boolean remove(String name) {
		Assert.notNull(name);
		return struc.remove(name);
	}

	public boolean add(Object obj, String name) {
		if (obj instanceof CStruc) {
			((CStruc) obj).setName(name);
		} else if (obj instanceof CField) {
			((CField) obj).setName(name);
		} else if (obj instanceof CArray) {
			((CArray) obj).setName(name);
		}
		return add(obj);
	}

	@SuppressWarnings("unchecked")
	public boolean add(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.getClass().isInstance(JsonObject.class)) {
			mergeOf((JsonObject) obj);
		} else if (obj instanceof CStruc) {
			struc.addStruc((CStruc) obj);
		} else if (obj instanceof CField) {
			struc.addField((CField) obj);
		} else if (obj instanceof CArray) {
			struc.addArray((CArray) obj);
		} else if (obj instanceof CField[]) {
			addField((CField[]) obj);
		} else if (obj instanceof CStruc[]) {
			addStruc((CStruc[]) obj);
		} else if (obj instanceof CArray[]) {
			addArray((CArray[]) obj);
		} else if (obj instanceof List) {
			Object[] objarr = ((List<?>) obj).toArray();
			if (objarr instanceof CField[]) {
				addField((CField[]) objarr);
			} else if (objarr instanceof CStruc[]) {
				addStruc((CStruc[]) objarr);
			} else if (objarr instanceof CArray[]) {
				addArray((CArray[]) objarr);
			} else
				return false;
		} else if (obj instanceof Map) {
			if (isMapOfType((Map<?, ?>) obj, String.class, CField.class)) {
				Map<String, CField> fld = (Map<String, CField>) obj;
				struc.mergeField(fld);
			} else if (isMapOfType((Map<?, ?>) obj, String.class, CStruc.class)) {
				Map<String, CStruc> stc = (Map<String, CStruc>) obj;
				struc.mergeStruc(stc);
			} else if (isMapOfType((Map<?, ?>) obj, String.class, CArray.class)) {
				Map<String, CArray> arr = (Map<String, CArray>) obj;
				struc.mergeArray(arr);
			} else {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	public String getStringValues(Object obj, String determine) {
		if (obj == null) {
			return "";
		}
		if (obj.getClass().isInstance(JsonObject.class)) {
			return ((JsonObject) obj).getObject().getStringValues(determine);
		} else if (obj instanceof CStruc) {
			return ((CStruc) obj).getStringValues(determine);
		} else if (obj instanceof CField) {
			return ((CField) obj).getValue();
		} else if (obj instanceof CArray) {
			return ((CArray) obj).getStringValues(determine);
		} else if (obj instanceof CField[]) {
			StringBuilder sb = new StringBuilder();
			for (CField cf : ((CField[]) obj)) {
				sb.append(cf.getValue()).append(determine);
			}
			return sb.toString();
		} else if (obj instanceof CStruc[]) {
			StringBuilder sb = new StringBuilder();
			for (CStruc cs : ((CStruc[]) obj)) {
				sb.append(cs.getStringValues(determine));
			}
			return sb.toString();
		} else if (obj instanceof CArray[]) {
			StringBuilder sb = new StringBuilder();
			for (CArray ca : ((CArray[]) obj)) {
				sb.append(ca.getStringValues(determine));
			}
			return sb.toString();
		}

		return obj.toString();
	}

	public Json deepCopy() {
		try {
			return (JsonObject) serializeAndDeserialize(this);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Json mergeOf(Json json) {
		if (json == null) {
			return this;
		}
		struc.mergeOf(json.getObject());
		return this;
	}

	public Json addOf(Json json) {
		if (json == null) {
			return this;
		}
		struc.addOf(json.getObject());
		return this;
	}

	public Json copyOf(Json json) {
		if (json == null) {
			return this;
		}
		struc.copyOf(json.getObject());
		return this;
	}

	public Json setValue(Json json) {
		if (json == null) {
			return this;
		}
		struc.setValue(json.getObject());
		return this;
	}

	public boolean isEmpty() {
		return struc.isEmpty();
	}

	public CField addField(String name) {
		if (get(name) != null) {
			remove(name);
		}
		CField fld = new CField(name);
		addField(fld);

		return fld;
	}

	public CField addField(String name, String value) {
		if (get(name) != null) {
			remove(name);
		}
		CField fld = new CField(name, value);
		addField(fld);

		return fld;
	}

	public CField addField(String name, String value, String format) {
		if (get(name) != null) {
			remove(name);
		}
		CField fld = new CField(name, value, format);
		addField(fld);

		return fld;
	}

	public CField addField(String name, String value, String format, String type) {
		if (get(name) != null) {
			remove(name);
		}
		CField fld = new CField(name, value, format, type);
		addField(fld);

		return fld;
	}

	public CStruc addStruc(String name, CField... fields) {
		if (get(name) != null) {
			remove(name);
		}
		CStruc stc = new CStruc(name);
		if (fields != null) {
			for (CField fld : fields) {
				stc.addField(fld);
			}
		}
		addStruc(stc);
		return stc;
	}

	public CStruc addStruc(String name, CStruc... strucs) {
		if (get(name) != null) {
			remove(name);
		}
		CStruc stc = new CStruc(name);
		if (strucs != null) {
			for (CStruc s : strucs) {
				stc.addStruc(s);
			}
		}
		addStruc(stc);
		return stc;
	}

	public CStruc addStruc(String name, CArray... arrays) {
		if (get(name) != null) {
			remove(name);
		}
		CStruc stc = new CStruc(name);
		if (arrays != null) {
			for (CArray a : arrays) {
				stc.addArray(a);
			}
		}
		addStruc(stc);
		return stc;
	}

	public CArray addArray(String name, CStruc... records) {
		if (get(name) != null) {
			remove(name);
		}
		CArray arr = new CArray(name);
		if (records != null) {
			for (CStruc stc : records) {
				arr.add(stc);
			}
		}
		addArray(arr);
		return arr;
	}

	public String getFieldValue(String name) {
		return struc.getFieldValue(name);
	}
}