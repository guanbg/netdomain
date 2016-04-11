package com.platform.cubism.page.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.platform.cubism.base.Json;
import com.platform.cubism.page.Component;

public class FieldElement implements Element, Serializable{
	private static final long serialVersionUID = 8955097391502467783L;
	private String id;
	private String text;
	private String tip;
	private String type;
	private String status;
	private String mask;
	private String format;
	private boolean required;
	private boolean password;
	private String handle;
	private Map<String,String> event;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isPassword() {
		return password;
	}

	public void setPassword(boolean password) {
		this.password = password;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public Map<String, String> getEvent() {
		return event;
	}

	public void addEvent(String name, String event) {
		if(this.event == null){
			this.event = new HashMap<String, String>(1);
		}
		if(this.event.containsKey(name)){
			return;
		}
		this.event.put(name, event);
	}

	public Component getComponent(Json in) {
		// TODO Auto-generated method stub
		return null;
	}
}
