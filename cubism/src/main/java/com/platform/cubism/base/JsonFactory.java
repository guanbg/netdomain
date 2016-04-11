package com.platform.cubism.base;

import static com.platform.cubism.base.Json.DOT_SIGN;

public class JsonFactory {
	public static Json create() {
		return new JsonObject();
	}

	public static Json create(String name) {
		return new JsonObject(name);
	}

	public static CField createField() {
		return new CField();
	}

	public static CField createField(String name) {
		if (name == null) {
			return createField();
		}
		int idx = name.lastIndexOf(DOT_SIGN);
		if (idx < 0) {
			return new CField(name);
		}

		return new CField(name.substring(idx + 1));
	}

	public static CField createField(String name, String value) {
		return new CField(name).setValue(value);
	}

	public static CField createField(String name, String value, String format) {
		return new CField(name).setValue(value).setFormat(format);
	}

	public static CField createField(String name, String value, String format, String type) {
		return new CField(name).setValue(value).setFormat(format).setType(type);
	}

	public static CStruc createStruc() {
		return new CStruc();
	}

	public static CStruc createStruc(String name) {
		return new CStruc(name);
	}

	public static CStruc createStruc(String name, CField[] fields) {
		CStruc struc = new CStruc(name);
		for (CField fld : fields) {
			struc.addField(fld);
		}
		return struc;
	}

	public static CStruc createStruc(String name, CField[] fields, CStruc[] strucs) {
		CStruc struc = createStruc(name, fields);
		for (CStruc stc : strucs) {
			struc.addStruc(stc);
		}
		return struc;
	}

	public static CStruc createStruc(String name, CField[] fields, CStruc[] strucs, CArray[] arrays) {
		CStruc struc = createStruc(name, fields, strucs);
		for (CArray arr : arrays) {
			struc.addArray(arr);
		}
		return struc;
	}

	public static CArray createArray() {
		return new CArray();
	}

	public static CArray createArray(String name) {
		return new CArray(name);
	}

	public static CArray createArray(String name, CStruc[] records) {
		CArray array = createArray(name);
		for (CStruc stc : records) {
			array.add(stc);
		}
		return array;
	}
}
