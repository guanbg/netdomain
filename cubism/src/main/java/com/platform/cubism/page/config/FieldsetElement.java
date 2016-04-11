package com.platform.cubism.page.config;

import java.io.Serializable;

import com.platform.cubism.base.Json;
import com.platform.cubism.page.Component;

public class FieldsetElement implements Element, Serializable{
	private static final long serialVersionUID = -4241145363397725583L;
	private String id;
	private String text;
	private String border;
	private String tip;
	private String status;
	private String position;
	
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public Component getComponent(Json in) {
		// TODO Auto-generated method stub
		return null;
	}
}
