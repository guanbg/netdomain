package com.platform.cubism.page.config;

import java.io.Serializable;
import java.util.Map;

import com.platform.cubism.base.Json;
import com.platform.cubism.page.Component;

public class TreeElement implements Element, Serializable{
	private static final long serialVersionUID = -8089021385222375041L;
	private String id;
	private String title;
	private String border;
	private String handle;
	private String srv;
	private String textfield;
	private String valuefield;
	private String type;
	private boolean checked;
	private boolean ansy;
	private MenuElement menu;
	private Root root;
	private Children children;
	private Map<String,String> event;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBorder() {
		return border;
	}
	public void setBorder(String border) {
		this.border = border;
	}
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public boolean isAnsy() {
		return ansy;
	}
	public void setAnsy(boolean ansy) {
		this.ansy = ansy;
	}
	public MenuElement getMenu() {
		return menu;
	}
	public void setMenu(MenuElement menu) {
		this.menu = menu;
	}
	public Root getRoot() {
		return root;
	}
	public void setRoot(Root root) {
		this.root = root;
	}
	public Children getChildren() {
		return children;
	}
	public void setChildren(Children children) {
		this.children = children;
	}
	public Map<String, String> getEvent() {
		return event;
	}
	public void setEvent(Map<String, String> event) {
		this.event = event;
	}
	
	private static class Root{
		private String textfield;
		private String valuefield;
		private String text;
		private String value;
		
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
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	private static class Children{
		private String srv;
		private String textfield;
		private String valuefield;
		
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
	}

	public Component getComponent(Json in) {
		// TODO Auto-generated method stub
		return null;
	}
}
