package com.platform.cubism.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.base.CField;

public class SqlParameter {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private int type;
	private int index;
	private String name;
	private Object obj;
	private CField[] value;

	public SqlParameter(int idx) {
		setIndex(idx);
	}

	public int getType() {
		return type;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		if (value == null) {
			return 0;
		}
		return value.length;
	}

	public Object getValue() {
		if (value == null) {
			return null;
		}
		if(value[0] == null){
			return null;
		}
		return value[0].getObjectValue();
	}

	public Object getValue(int idx) {
		if (value == null) {
			return null;
		}
		if (idx >= value.length) {
			return null;
		}
		if(value[idx] == null){
			return null;
		}
		return value[idx].getObjectValue();
	}

	public SqlParameter setType(int type) {
		this.type = type;
		return this;
	}

	public SqlParameter setIndex(int index) {
		this.index = index;
		return this;
	}

	public SqlParameter setName(String name) {
		this.name = name;
		return this;
	}

	public SqlParameter setValue(CField value) {
		this.value = new CField[1];
		this.value[0] = value;
		return this;
	}

	public SqlParameter setValue(CField[] value) {
		this.value = value;
		return this;
	}

	public SqlParameter setValue(CField[][] value) {
		int len = 0;
		for (CField[] f : value) {
			len += f.length;
		}
		CField[] cf = new CField[len];
		int i = 0;
		for (CField[] fld : value) {
			for (CField f : fld) {
				cf[i++] = f;
			}

		}
		this.value = cf;

		if (logger.isDebugEnabled()) {
			for (CField f : this.value) {
				logger.debug(f==null?null:f.toString());
			}
			logger.debug("\n");
		}
		return this;
	}

	public Object getObjValue() {
		return obj;
	}

	public SqlParameter setObjValue(Object obj) {
		this.obj = obj;
		return this;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("name=").append(name);
		sb.append("type=").append(type);
		sb.append("index=").append(index);
		sb.append("obj=").append(obj.toString());
		
		if(value == null || value.length <= 0){
			sb.append("value=");
		}
		else{
			sb.append("value=");
			for (CField f : this.value) {
				sb.append(f.toString()).append(",");
			}
		}
		
		return sb.toString();
	}
}