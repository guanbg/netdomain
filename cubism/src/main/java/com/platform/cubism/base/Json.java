package com.platform.cubism.base;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Json {
	public static final String DOT_SIGN = ".";

	public Json setName(String name);

	public String toString();

	public String toJson();

	public Json toJson(String json);

	public Json toJson(ResultSet rs, String name) throws SQLException;

	public Json toJson(ResultSet rs, String name, boolean alwayarray, boolean lowercase) throws SQLException;

	public Json toJson(ResultSet rs, String name, String[] colNames, boolean alwayarray, boolean lowercase) throws SQLException;

	public Json mergeOf(Json json);

	public Json addOf(Json json);
	
	public Json copyOf(Json json);

	public Json deepCopy();

	public Json setValue(Json json);

	public Object get(String name);
	
	public boolean contains(String name);

	public String getStringValues(Object obj, String determine);

	public boolean add(Object obj);

	public boolean add(Object obj, String name);

	public boolean remove(String name);

	public int size();

	public boolean isEmpty();

	public CStruc getObject();

	public CArray getArray(String name);

	public CStruc getStruc(String name);

	public CField getField(String name);
	
	public String getFieldValue(String name);

	public Json addArray(Json json);

	public Json addArray(CArray[] arrs);

	public Json addArray(CArray arr);

	public Json addStruc(Json json);

	public Json addStruc(CStruc[] stcs);

	public Json addStruc(CStruc stc);

	public Json addField(Json json);

	public Json addField(CField[] flds);

	public Json addField(CField fld);

	public CField addField(String name);

	public CField addField(String name, String value);

	public CField addField(String name, String value, String format);

	public CField addField(String name, String value, String format, String type);

	public CStruc addStruc(String name, CField... fields);

	public CStruc addStruc(String name, CStruc... strucs);

	public CStruc addStruc(String name, CArray... arrays);

	public CArray addArray(String name, CStruc... records);
}