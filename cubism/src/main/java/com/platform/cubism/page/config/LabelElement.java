package com.platform.cubism.page.config;

import java.io.Serializable;

import com.platform.cubism.base.Json;
import com.platform.cubism.page.Component;
import com.platform.cubism.page.base.Label;
import com.platform.cubism.page.parser.ParamFactory;

public class LabelElement implements Element, Serializable{
	private static final long serialVersionUID = 8761614707836409666L;
	private Component component = null;
	private boolean isDynamic;
	
	private String id;
	private String text;
	private String cls;
	private String status;
	private String handle;
	
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
	
	public String getCls() {
		return cls;
	}
	
	public void setCls(String cls) {
		this.cls = cls;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getHandle() {
		return handle;
	}
	
	public void setHandle(String handle) {
		this.handle = handle;
	}
	
	public Component getComponent(Json in) {
		if(!isDynamic && component != null){
			return component;
		}
		
		Label label = new Label();
		
		if(!isDynamic){
			isDynamic = ParamFactory.hasParam(text);
		}		
		if(!isDynamic){
			isDynamic = ParamFactory.hasParam(id);
		}		
		if(!isDynamic){
			isDynamic = ParamFactory.hasParam(cls);
		}		
		if(!isDynamic){
			isDynamic = ParamFactory.hasParam(status);
		}		
		if(!isDynamic){
			isDynamic = ParamFactory.hasParam(handle);
		}
		
		label.setText(ParamFactory.getText(text, in));
		label.setHandle(ParamFactory.getText(id, in));
		label.setCls(ParamFactory.getText(cls, in));
		label.setStatus(ParamFactory.getText(status, in));
		label.setHandle(ParamFactory.getText(handle, in));

		if(!isDynamic){
			component = label;
		}
		return label;
	}
}