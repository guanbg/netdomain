package com.platform.cubism.page.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.platform.cubism.base.Json;
import com.platform.cubism.page.Component;

public class TextareaElement implements Element, Serializable{
	private static final long serialVersionUID = -8445683877169361691L;
	private String id;
	private String text;
	private String border;
	private String tip;
	private String handle;
	private String col;
	private String row;
	private String status;
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
	public String getBorder() {
		return border;
	}
	public void setBorder(String border) {
		this.border = border;
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
	public String getCol() {
		return col;
	}
	public void setCol(String col) {
		this.col = col;
	}
	public String getRow() {
		return row;
	}
	public void setRow(String row) {
		this.row = row;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
