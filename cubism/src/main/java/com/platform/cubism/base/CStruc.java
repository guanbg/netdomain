package com.platform.cubism.base;

import static com.platform.cubism.util.StringUtils.hasText;
import static com.platform.cubism.util.StringUtils.getTabSpace;
import static com.platform.cubism.base.Json.DOT_SIGN;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.util.Assert;

public class CStruc implements Serializable {
	private static final long serialVersionUID = 1709666693039257512L;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private String name;
	private Map<String, CField> field;
	private Map<String, CStruc> struc;
	private Map<String, CArray> array;

	public CStruc() {
		;
	}

	public CStruc(String name) {
		setName(name);
	}

	private void checkDuplicateName(String name) {
		if (containsName(name)) {
			if (logger.isDebugEnabled()) {
				logger.debug("字段名称重复：" + name);
			}
			throw new IllegalArgumentException("[Assertion failed] - duplicate name : " + name);
		}
	}

	private void init(Class<? extends Object> type) {
		if (field == null && CField.class == type) {
			field = new ConcurrentHashMap<String, CField>();
		} else if (struc == null && CStruc.class == type) {
			struc = new ConcurrentHashMap<String, CStruc>();
		} else if (array == null && CArray.class == type) {
			array = new ConcurrentHashMap<String, CArray>();
		}
	}

	public Class<? extends Object> getType(String name) {
		int idx = name.indexOf(DOT_SIGN);
		if (idx < 0) {
			if (field != null && field.containsKey(name)) {
				return CField.class;
			}
			if (struc != null && struc.containsKey(name)) {
				return CStruc.class;
			}
			if (array != null && array.containsKey(name)) {
				return CArray.class;
			}
		}

		Object obj = getObject(name);
		if (obj == null) {
			return null;
		}
		if (obj.getClass().isAssignableFrom(CField.class)) {
			return CField.class;
		}
		if (obj.getClass().isAssignableFrom(CStruc.class)) {
			return CStruc.class;
		}
		if (obj.getClass().isAssignableFrom(CArray.class)) {
			return CArray.class;
		}
		if (obj.getClass().isAssignableFrom(CField[].class)) {
			return CField[].class;
		}
		if (obj.getClass().isAssignableFrom(CStruc[].class)) {
			return CStruc[].class;
		}
		if (obj.getClass().isAssignableFrom(CArray[].class)) {
			return CArray[].class;
		}

		return null;
	}

	public boolean isField(String name) {
		if (CField.class == getType(name) || CField[].class == getType(name)) {
			return true;
		}

		return false;
	}

	public boolean isStruc(String name) {
		if (CStruc.class == getType(name) || CStruc[].class == getType(name)) {
			return true;
		}

		return false;
	}

	public boolean isArray(String name) {
		if (CArray.class == getType(name) || CArray[].class == getType(name)) {
			return true;
		}

		return false;
	}

	public boolean containsName(String name) {
		Assert.notNull(name);

		int idx = name.indexOf(DOT_SIGN);
		if (idx < 0) {
			if ((field != null && field.containsKey(name)) || (struc != null && struc.containsKey(name))
					|| (array != null && array.containsKey(name))) {
				return true;
			}

			return false;
		}

		String first = name.substring(0, idx);
		String second = name.substring(idx + 1);

		if (struc != null && !struc.isEmpty()) {
			CStruc cs = struc.get(first);
			if (cs != null) {
				return cs.containsName(second);
			}
		}

		return false;
	}

	public String getName() {
		return name;
	}

	public Map<String, CField> getField() {
		if (field == null) {
			return null;
		}
		return Collections.unmodifiableMap(field);
	}

	public Map<String, CStruc> getStruc() {
		if (struc == null) {
			return null;
		}
		return Collections.unmodifiableMap(struc);
	}

	public Map<String, CArray> getArray() {
		if (array == null) {
			return null;
		}
		return Collections.unmodifiableMap(array);
	}
	public String getFieldValue(String name){
		CField fld = getField(name);
		return fld == null?"":fld.getValue();
	}
	public CField getField(String name) {
		Assert.notNull(name);

		int idx = name.indexOf(DOT_SIGN);
		if (idx < 0) {
			if (field != null && !field.isEmpty() && field.containsKey(name)) {
				return field.get(name);
			}
			return null;
		}

		String first = name.substring(0, idx);
		String second = name.substring(idx + 1);

		if (struc != null && !struc.isEmpty() && struc.containsKey(first)) {
			CStruc cs = struc.get(first);
			if (cs != null) {
				return cs.getField(second);
			}
		}
		else if(array != null && !array.isEmpty() && array.containsKey(first)){
			CArray ca = array.get(first);
			if(ca != null && !ca.isEmpty() && ca.size() == 1){
				return ca.getRecord(0).getField(second);
			}
		}

		return null;
	}

	public CField[] getFieldInArray(String name) {
		Assert.notNull(name);

		int idx = name.indexOf(DOT_SIGN);
		if (idx < 0) {
			return null;
		}

		String first = name.substring(0, idx);
		String second = name.substring(idx + 1);

		if (array != null && !array.isEmpty() && array.containsKey(first)) {
			CArray ca = array.get(first);
			if (ca != null) {
				return ca.getFieldColumn(second);
			}
		}
		if (struc != null && !struc.isEmpty() && struc.containsKey(first)) {
			CStruc cs = struc.get(first);
			if (cs != null) {
				return cs.getFieldInArray(second);
			}
		}

		return null;
	}

	public Object getFieldObject(String name) {
		CField cf = getField(name);
		if (cf != null) {
			return cf;
		}
		CField[] cfs = getFieldInArray(name);
		if (cfs != null) {
			return cfs;
		}
		return null;
	}

	public CStruc getStruc(String name) {
		Assert.notNull(name);
		int idx = name.indexOf(DOT_SIGN);
		if (idx < 0) {
			if (struc != null && !struc.isEmpty() && struc.containsKey(name)) {
				return struc.get(name);
			}
			return null;
		}

		String first = name.substring(0, idx);
		String second = name.substring(idx + 1);

		if (struc != null && !struc.isEmpty() && struc.containsKey(first)) {
			CStruc cs = struc.get(first);
			if (cs != null) {
				return cs.getStruc(second);
			}
		}

		return null;
	}

	public CStruc[] getStrucInArray(String name) {
		Assert.notNull(name);
		int idx = name.indexOf(DOT_SIGN);
		if (idx < 0) {
			return null;
		}

		String first = name.substring(0, idx);
		String second = name.substring(idx + 1);

		if (array != null && !array.isEmpty() && array.containsKey(first)) {
			CArray ca = array.get(first);
			if (ca != null) {
				return ca.getStrucColumn(second);
			}
		}

		return null;
	}

	public Object getStrucObject(String name) {
		CStruc cs = getStruc(name);
		if (cs != null) {
			return cs;
		}
		CStruc[] css = getStrucInArray(name);
		if (css != null) {
			return css;
		}
		return null;
	}

	public CArray getArray(String name) {
		Assert.notNull(name);
		int idx = name.indexOf(DOT_SIGN);
		if (idx < 0) {
			if (array != null && !array.isEmpty() && array.containsKey(name)) {
				return array.get(name);
			}
			return null;
		}
		String first = name.substring(0, idx);
		String second = name.substring(idx + 1);

		if (struc != null && !struc.isEmpty() && struc.containsKey(first)) {
			CStruc cs = struc.get(first);
			if (cs != null) {
				return cs.getArray(second);
			}
		}

		return null;
	}

	public CArray[] getArrayInArray(String name) {
		Assert.notNull(name);
		int idx = name.indexOf(DOT_SIGN);
		if (idx < 0) {
			return null;
		}

		String first = name.substring(0, idx);
		String second = name.substring(idx + 1);

		if (array != null && !array.isEmpty() && array.containsKey(first)) {
			CArray ca = array.get(first);
			if (ca != null) {
				return ca.getArrayColumn(second);
			}
		}
		if (struc != null && !struc.isEmpty() && struc.containsKey(first)) {
			CStruc cs = struc.get(first);
			if (cs != null) {
				return cs.getArrayInArray(second);
			}
		}

		return null;

	}

	public Object getArrayObject(String name) {
		CArray ca = getArray(name);
		if (ca != null) {
			return ca;
		}
		CArray[] cas = getArrayInArray(name);
		if (cas != null) {
			return cas;
		}
		return null;
	}

	public Object getObject(String name) {
		Object obj = getFieldObject(name);
		if (obj != null) {
			return obj;
		}
		obj = getStrucObject(name);
		if (obj != null) {
			return obj;
		}
		obj = getArrayObject(name);
		if (obj != null) {
			return obj;
		}

		return null;
	}

	public int size() {
		int sz = 0;

		sz += (field != null) ? field.size() : 0;
		sz += (struc != null) ? struc.size() : 0;
		sz += (array != null) ? array.size() : 0;

		return sz;
	}

	public String[] getNames() {
		List<String> names = new CopyOnWriteArrayList<String>();
		if (field != null) {
			names.addAll(field.keySet());
		}
		if (struc != null) {
			names.addAll(struc.keySet());
		}
		if (array != null) {
			names.addAll(array.keySet());
		}

		return names.toArray(new String[0]);
	}

	public String getNames(int idx) {
		String[] names = getNames();
		if (idx < 0 || names == null || idx >= names.length) {
			return null;
		}
		return names[idx];
	}

	public CStruc setName(String name) {
		if (name != null && name.indexOf(DOT_SIGN) >= 0) {
			throw new IllegalArgumentException("[Assertion failed] - the name can't include a dot.");
		}
		this.name = name;
		return this;
	}

	public CStruc addField(CField fld) {
		Assert.notNull(fld);
		checkDuplicateName(fld.getName());
		init(CField.class);
		field.put(fld.getName(), fld);
		return this;
	}
	public CStruc addField(String name, String value) {
		checkDuplicateName(name);
		init(CField.class);
		field.put(name, new CField(name,value));
		return this;
	}

	public CStruc addStruc(CStruc stc) {
		Assert.notNull(stc);
		//checkDuplicateName(stc.getName());//服务之间继承的时候 系统头写入时会重复，所以去掉验证，直接覆盖
		init(CStruc.class);
		if (containsName(stc.getName())) {
			removeStruc(stc.getName());
		}
		struc.put(stc.getName(), stc);

		return this;
	}

	public CStruc addArray(CArray arr) {
		Assert.notNull(arr);
		checkDuplicateName(arr.getName());
		init(CArray.class);
		array.put(arr.getName(), arr);

		return this;
	}

	public CStruc addArray(CArray[] arrs) {
		for (CArray a : arrs) {
			addArray(a);
		}
		return this;
	}

	public CField removeField(String name) {
		if (!hasText(name) || !containsName(name)) {
			return null;
		}

		int idx = name.indexOf(DOT_SIGN);
		if (idx < 0) {
			if (field != null && field.containsKey(name)) {
				return field.remove(name);
			}

			return null;
		}

		String first = name.substring(0, idx);
		String second = name.substring(idx + 1);

		if (struc != null && !struc.isEmpty()) {
			CStruc cs = struc.get(first);
			if (cs != null) {
				return cs.removeField(second);
			}
		}
		return null;
	}

	public CField[] removeFieldInArray(String name) {
		if (!hasText(name) || !containsName(name)) {
			return null;
		}

		int idx = name.indexOf(DOT_SIGN);
		if (idx < 0) {
			return null;
		}

		String first = name.substring(0, idx);
		String second = name.substring(idx + 1);

		if (array != null && !array.isEmpty() && array.containsKey(first)) {
			CArray ca = array.get(first);
			if (ca != null) {
				return ca.removeFieldColumn(second);
			}
		}
		if (struc != null && !struc.isEmpty() && struc.containsKey(first)) {
			CStruc cs = struc.get(first);
			if (cs != null) {
				return cs.removeFieldInArray(second);
			}
		}
		return null;
	}

	public Object removeFieldObject(String name) {
		CField cf = removeField(name);
		if (cf != null) {
			return cf;
		}
		CField[] cfs = removeFieldInArray(name);
		if (cfs != null) {
			return cfs;
		}
		return null;
	}

	public CStruc removeStruc(String name) {
		if (!hasText(name) || !containsName(name)) {
			return null;
		}

		int idx = name.indexOf(DOT_SIGN);
		if (idx < 0) {
			if (struc != null && struc.containsKey(name)) {
				return struc.remove(name);
			}

			return null;
		}

		String first = name.substring(0, idx);
		String second = name.substring(idx + 1);

		if (struc != null && !struc.isEmpty()) {
			CStruc cs = struc.get(first);
			if (cs != null) {
				return cs.removeStruc(second);
			}
		}
		return null;
	}

	public CStruc[] removeStrucInArray(String name) {
		if (!hasText(name) || !containsName(name)) {
			return null;
		}

		int idx = name.indexOf(DOT_SIGN);
		if (idx < 0) {
			return null;
		}

		String first = name.substring(0, idx);
		String second = name.substring(idx + 1);

		if (array != null && !array.isEmpty() && array.containsKey(first)) {
			CArray ca = array.get(first);
			if (ca != null) {
				return ca.removeStrucColumn(second);
			}
		}
		return null;
	}

	public Object removeStrucObject(String name) {
		CStruc cs = removeStruc(name);
		if (cs != null) {
			return cs;
		}
		CStruc[] css = removeStrucInArray(name);
		if (css != null) {
			return css;
		}
		return null;
	}

	public CArray removeArray(String name) {
		if (!hasText(name) || !containsName(name)) {
			return null;
		}

		int idx = name.indexOf(DOT_SIGN);
		if (idx < 0) {
			if (array != null && array.containsKey(name)) {
				return array.remove(name);
			}

			return null;
		}

		String first = name.substring(0, idx);
		String second = name.substring(idx + 1);

		if (struc != null && !struc.isEmpty()) {
			CStruc cs = struc.get(first);
			if (cs != null) {
				return cs.removeArray(second);
			}
		}
		return null;
	}

	public CArray[] removeArrayInArray(String name) {
		if (!hasText(name) || !containsName(name)) {
			return null;
		}

		int idx = name.indexOf(DOT_SIGN);
		if (idx < 0) {
			return null;
		}

		String first = name.substring(0, idx);
		String second = name.substring(idx + 1);

		if (array != null && !array.isEmpty() && array.containsKey(first)) {
			CArray ca = array.get(first);
			if (ca != null) {
				return ca.removeArrayColumn(second);
			}
		}
		if (struc != null && !struc.isEmpty() && struc.containsKey(first)) {
			CStruc cs = struc.get(first);
			if (cs != null) {
				return cs.removeArrayInArray(second);
			}
		}
		return null;
	}

	public Object removeArrayObject(String name) {
		CArray ca = removeArray(name);
		if (ca != null) {
			return ca;
		}
		CArray[] cas = removeArrayInArray(name);
		if (cas != null) {
			return cas;
		}
		return null;
	}

	public Object removeObject(String name) {
		Object obj = removeFieldObject(name);
		if (obj != null) {
			return obj;
		}
		obj = removeStrucObject(name);
		if (obj != null) {
			return obj;
		}
		obj = removeArrayObject(name);
		if (obj != null) {
			return obj;
		}

		return null;
	}

	public boolean remove(String name) {
		if (!hasText(name) || !containsName(name)) {
			return false;
		}
		return removeObject(name) != null ? true : false;
	}

	public void clearAll() {
		if (field != null) {
			field.clear();
		}
		if (struc != null) {
			struc.clear();
		}
		if (array != null) {
			array.clear();
		}
	}

	public CStruc reset() {
		if (getField() != null && getField().values() != null) {
			for (CField f : getField().values()) {
				f.reset();
			}
		}
		if (getStruc() != null && getStruc().values() != null) {
			for (CStruc s : getStruc().values()) {
				s.reset();
			}
		}
		if (getArray() != null && getArray().values() != null) {
			for (CArray a : getArray().values()) {
				a.reset();
			}
		}
		return this;
	}

	public boolean isEmpty() {
		return (field == null || field.isEmpty()) && (struc == null || struc.isEmpty()) && (array == null || array.isEmpty());
	}
	public boolean isOnlyField(){
		return (field != null) && (struc == null || struc.isEmpty()) && (array == null || array.isEmpty());
	}
	public String getStringValues(String determine) {
		if (isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		if (getField() != null && !getField().isEmpty()) {
			for (CField f : getField().values()) {
				sb.append(f.getValue()).append(determine);
			}
		}
		if (getStruc() != null && !getStruc().isEmpty()) {
			for (CStruc s : getStruc().values()) {
				sb.append(s.getStringValues(determine)).append(determine);
			}
		}
		if (getArray() != null && !getArray().isEmpty()) {
			for (CArray a : getArray().values()) {
				sb.append(a.getStringValues(determine)).append(determine);
			}
		}
		if (sb.length() > 0) {
			sb.delete(sb.length() - determine.length(), sb.length());
			return sb.toString();
		} else {
			return "";
		}
	}
	public CStruc copyOf(CStruc src) {
		if (src == null) {
			return this;
		}

		init(CField.class);
		init(CStruc.class);
		init(CArray.class);

		clearAll();

		name = src.getName();
		if (src.getField() != null) {
			field.putAll(src.getField());
		}
		if (src.getStruc() != null) {
			struc.putAll(src.getStruc());
		}
		if (src.getArray() != null) {
			array.putAll(src.getArray());
		}

		return this;
	}

	public CStruc mergeOf(CStruc stc) {
		if (stc == null) {
			return this;
		}
		if (!hasText(name)) {
			name = stc.getName();
		}
		if (stc.getField() != null) {
			mergeField(stc.getField());
		}
		if (stc.getStruc() != null) {
			mergeStruc(stc.getStruc());
		}
		if (stc.getArray() != null) {
			mergeArray(stc.getArray());
		}
		return this;
	}
	
	public CStruc addOf(CStruc stc) {
		if (stc == null) {
			return this;
		}
		if (!hasText(name)) {
			name = stc.getName();
		}
		if (stc.getField() != null) {
			addOfField(stc.getField());
		}
		if (stc.getStruc() != null) {
			addOfStruc(stc.getStruc());
		}
		if (stc.getArray() != null) {
			addOfArray(stc.getArray());
		}
		return this;
	}
	public CStruc addOfField(Map<String, CField> fld) {
		if (fld == null) {
			return this;
		}
		init(CField.class);
		for (String name : fld.keySet()) {
			if(containsName(name)){
				continue;
			}
			else{
				field.put(name, fld.get(name));
			}
		}

		return this;
	}
	public CStruc addOfStruc(Map<String, CStruc> stc) {
		if (stc == null) {
			return this;
		}
		init(CStruc.class);
		for (String name : stc.keySet()) {
			if (field != null && field.containsKey(name)){
				continue;
			}
			else if(array != null && array.containsKey(name)) {
				continue;
			}
			else if(struc != null && struc.containsKey(name)){
				struc.get(name).addOf(stc.get(name));
			}
			else{
				struc.put(name, stc.get(name));
			}
		}

		return this;
	}

	public CStruc addOfArray(Map<String, CArray> arr) {
		if (arr == null) {
			return this;
		}
		init(CArray.class);
		for (String name : arr.keySet()) {
			if (field != null && field.containsKey(name)){
				continue;
			}
			else if(struc != null && struc.containsKey(name)){
				continue;
			}
			else if(array != null && array.containsKey(name)) {
				array.get(name).addOf(arr.get(name));
			}
			else{
				array.put(name, arr.get(name));
			}
		}

		return this;
	}

	public CStruc mergeField(Map<String, CField> fld) {
		if (fld == null) {
			return this;
		}
		init(CField.class);
		for (String name : fld.keySet()) {
			remove(name);
		}
		field.putAll(fld);

		return this;
	}

	public CStruc mergeStruc(Map<String, CStruc> stc) {
		if (stc == null) {
			return this;
		}
		init(CStruc.class);
		for (String name : stc.keySet()) {
			remove(name);
		}
		struc.putAll(stc);

		return this;
	}

	public CStruc mergeArray(Map<String, CArray> arr) {
		if (arr == null) {
			return this;
		}
		init(CArray.class);
		for (String name : arr.keySet()) {
			remove(name);
		}
		array.putAll(arr);

		return this;
	}

	public CStruc setValue(CStruc stc) {
		if (stc == null) {
			return this;
		}
		if (isEmpty()) {
			mergeOf(stc);
			return this;
		}
		if (stc.getField() != null) {
			setFieldValue(stc.getField());
		}
		if (stc.getStruc() != null) {
			setStrucValue(stc.getStruc());
		}
		if (stc.getArray() != null) {
			setArrayValue(stc.getArray());
		}

		return this;
	}

	public void setFieldValue(Map<String, CField> fld) {
		if (fld == null || getField() == null) {
			return;
		}
		for (CField f : getField().values()) {
			if (fld.containsKey(f.getName()))
				f.setValue(fld.get(f.getName()));
		}
	}

	public void setStrucValue(Map<String, CStruc> stc) {
		if (stc == null || getStruc() == null) {
			return;
		}
		for (CStruc s : getStruc().values()) {
			if (stc.containsKey(s.getName()))
				s.setValue(stc.get(s.getName()));
		}
	}

	public void setArrayValue(Map<String, CArray> arr) {
		if (arr == null || getArray() == null) {
			return;
		}
		for (CArray a : getArray().values()) {
			if (arr.containsKey(a.getName()))
				a.setValue(arr.get(a.getName()));
		}
	}

	public CField[] flat() {
		if (isEmpty()) {
			return new CField[0];
		}
		List<CField> all = new CopyOnWriteArrayList<CField>();
		if (getField() != null) {
			all.addAll(getField().values());
		}
		if (getStruc() != null) {
			for (CStruc s : getStruc().values()) {
				for (CField f : s.flat()) {
					all.add(f);
				}
			}
		}
		if (getArray() != null) {
			for (CArray a : getArray().values()) {
				for (CField f : a.flat()) {
					all.add(f);
				}
			}
		}
		return all.toArray(new CField[0]);
	}
	
	public String toUrl() {
		StringBuilder sb = new StringBuilder();
		
		if (field != null && field.entrySet() != null) {
			for (Entry<String, CField> ent : field.entrySet()) {
				if (ent == null || ent.getValue() == null) {
					continue;
				}
				if(!hasText(name)){
					sb.append(ent.getValue().toUrl()).append("&");
				}
				else{
					sb.append(name).append(".").append(ent.getValue().toUrl()).append("&");
				}
			}
		}
		if (struc != null && struc.entrySet() != null) {
			for (Entry<String, CStruc> ent : struc.entrySet()) {
				if (ent == null || ent.getValue() == null) {
					continue;
				}
				if(!hasText(name)){
					sb.append(ent.getValue().toUrl()).append("&");
				}
				else{
					sb.append(name).append(".").append(ent.getValue().toUrl()).append("&");
				}
			}
		}
		if (array != null && array.entrySet() != null) {
			for (Entry<String, CArray> ent : array.entrySet()) {
				if (ent == null || ent.getValue() == null) {
					continue;
				}
				if(!hasText(name)){
					sb.append(ent.getValue().toUrl()).append("&");
				}
				else{
					sb.append(name).append(".").append(ent.getValue().toUrl()).append("&");
				}
			}
		}

		if (sb.charAt(sb.length() - 1) == '&') {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	
	public CStruc toJson(JsonTokener tokener) {
		if (tokener == null || tokener.isNull()) {
			return this;
		}
		if (tokener.nextClean() != '{') {
			tokener.back();
			tokener.back();
			if (tokener.peek() == '[' || tokener.peek() == ',') {// ["1","5"]
				tokener.next();
			} else {
				throw tokener.syntaxError("A JSONObject text must begin with '{'");
			}
		}
		try {
			char c;
			String key, value;
			for (; tokener.more();) {
				c = tokener.nextClean();
				switch (c) {
				case 0:
					throw tokener.syntaxError("A JSONObject text must end with '}'");
				case '}':
					return this;
				case '{':
					tokener.back();
					key = (new CStruc().toJson(tokener)).toString();
					break;
				case '[':
					tokener.back();
					key = (new CArray().toJson(tokener)).toString();
					break;
				case '"':
				case '\'':
					key = tokener.nextString(c);
					break;
				default:
					tokener.back();
					key = tokener.nextValue();
				}
				// ====================================================================//
				c = tokener.nextClean();
				if (c == '=') {
					if (tokener.next() != '>') {
						tokener.back();
					}
				} else if (c == ',' || c == ']') {// ["1","5"]
					addField(new CField(name, key));
					tokener.back();
					return this;
				} else if (c != ':') {
					throw tokener.syntaxError("Expected a ':' after a key");
				}
				// ====================================================================//
				c = tokener.nextClean();
				switch (c) {
				case '{':
					tokener.back();
					addStruc(new CStruc(key).toJson(tokener));
					break;
				case '[':
					tokener.back();
					addArray(new CArray(key).toJson(tokener));
					break;
				case '"':
				case '\'':
					value = tokener.nextString(c);
					addField(new CField(key, value));
					break;
				default:
					tokener.back();
					value = tokener.nextValue();
					if ("null".equalsIgnoreCase(value)) {
						value = "";
					}
					addField(new CField(key, value));
				}
				// ====================================================================//
				switch (tokener.nextClean()) {
				case ';':
				case ',':
					if (tokener.nextClean() == '}') {
						return this;
					}
					tokener.back();
					break;
				case '}':
					return this;
				default:
					throw tokener.syntaxError("Expected a ',' or '}'");
				}
			}

		} catch (JsonException jsone) {
			throw jsone;
		}

		return this;
	}

	public CStruc toJson(String json) {
		CStruc stc = null;
		try{
			stc = toJson(new JsonTokener(json));
		}catch(Throwable t){
			logger.error("==============>>JSON字符串转换错误,错误信息:" + t.getMessage());
		}
		return stc;
	}

	public String toJson() {
		String str = null;
		try{
			str = toJson(false);
		}catch(Throwable t){
			logger.error("==============>>转换为JSON字符串错误,错误信息:" + t.getMessage());
		}
		return str;
	}

	public String toJson(boolean isInArray) {
		StringBuilder sb = new StringBuilder();
		if (hasText(name) && !isInArray) {
			sb.append("\"").append(name).append("\":");
		}
		sb.append("{");
		if (field != null && field.entrySet() != null) {
			for (Entry<String, CField> ent : field.entrySet()) {
				if (ent == null || ent.getValue() == null) {
					continue;
				}
				sb.append(ent.getValue().toJson()).append(",");
			}
		}
		if (struc != null && struc.entrySet() != null) {
			for (Entry<String, CStruc> ent : struc.entrySet()) {
				if (ent == null || ent.getValue() == null) {
					continue;
				}
				sb.append(ent.getValue().toJson()).append(",");
			}
		}
		if (array != null && array.entrySet() != null) {
			for (Entry<String, CArray> ent : array.entrySet()) {
				if (ent == null || ent.getValue() == null) {
					continue;
				}
				sb.append(ent.getValue().toJson()).append(",");
			}
		}

		if (sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.append("}").toString();
	}

	public String toString(int level) {
		return toString(level, false);
	}

	public String toString(int level, boolean isInArray) {
		StringBuilder sb = new StringBuilder(getTabSpace(level));
		if (hasText(name) && !isInArray) {
			sb.append(name).append(":");
		}
		sb.append("{");

		if (field != null && field.entrySet() != null) {
			for (Entry<String, CField> ent : field.entrySet()) {
				if (ent == null || ent.getValue() == null) {
					continue;
				}
				sb.append(getTabSpace(level + 1)).append(ent.getValue().toString()).append(",");
			}
		}
		if (struc != null && struc.entrySet() != null) {
			for (Entry<String, CStruc> ent : struc.entrySet()) {
				if (ent == null || ent.getValue() == null) {
					continue;
				}
				sb.append(ent.getValue().toString(level + 1)).append(",");
			}
		}
		if (array != null && array.entrySet() != null) {
			for (Entry<String, CArray> ent : array.entrySet()) {
				if (ent == null || ent.getValue() == null) {
					continue;
				}
				sb.append(ent.getValue().toString(level + 1)).append(",");
			}
		}
		if (sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.append(getTabSpace(level)).append("}").toString();
	}

	public String toString() {
		return toString(0);
	}
}