package com.platform.cubism.page.config;

import java.io.Serializable;

import com.platform.cubism.base.Json;
import com.platform.cubism.page.Component;

public class ButtonElement implements Element, Serializable{
	private static final long serialVersionUID = 4754389580502882391L;
	private String id;
	private String text ;
	private String handle ;
	private String type ;
	private String status ;
	private String iconcls ;
	private String iconalign ;
	private String tip ;
	private MenuElement menu;
	
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

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
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

	public String getIconcls() {
		return iconcls;
	}

	public void setIconcls(String iconcls) {
		this.iconcls = iconcls;
	}

	public String getIconalign() {
		return iconalign;
	}

	public void setIconalign(String iconalign) {
		this.iconalign = iconalign;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public MenuElement getMenu() {
		return menu;
	}

	public void setMenu(MenuElement menu) {
		this.menu = menu;
	}

	public Component getComponent(Json in) {
		// TODO Auto-generated method stub
		return null;
	}

}
