package com.platform.cubism.page.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.platform.cubism.base.Json;
import com.platform.cubism.page.Component;

public class ComboxElement implements Element, Serializable {
	private static final long serialVersionUID = 6790076207701257652L;
	private String id;
	private String text;
	private String tip;
	private String handle;
	
	private String show;
	private String type;
	private String status;

	private String srv;
	private String textfield;
	private String valuefield;

	private boolean multiple;
	private boolean firstempty;
	private boolean required;

	private Map<String, String> event;
	private List<Map<String, String>> item;

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

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
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

	public String getSrv() {
		return srv;
	}

	public void setSrv(String srv) {
		this.srv = srv;
	}

	public String getTextfield() {
		return textfield;
	}

	public void setTextfield(String textfield) {
		this.textfield = textfield;
	}

	public String getValuefield() {
		return valuefield;
	}

	public void setValuefield(String valuefield) {
		this.valuefield = valuefield;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public boolean isFirstempty() {
		return firstempty;
	}

	public void setFirstempty(boolean firstempty) {
		this.firstempty = firstempty;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Map<String, String> getEvent() {
		return event;
	}

	public void addEvent(String name, String event) {
		if (this.event == null) {
			this.event = new HashMap<String, String>(1);
		}
		if (this.event.containsKey(name)) {
			return;
		}
		this.event.put(name, event);
	}

	public List<Map<String, String>> getItem() {
		return item;
	}

	public void setItem(List<Map<String, String>> item) {
		this.item = item;
	}

	public Component getComponent(Json in) {
		// TODO Auto-generated method stub
		return null;
	}
}
