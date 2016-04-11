package com.platform.cubism.page.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.platform.cubism.base.Json;
import com.platform.cubism.page.Component;

public class MenuElement implements Element, Serializable{
	private static final long serialVersionUID = -8306721036136274029L;
	private String id;
	private String showseparator;
	private String srv;
	private String handle;
	private String textfield;
	private String valuefield;
	private List<MenuItemElement> menuitem;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getShowseparator() {
		return showseparator;
	}

	public void setShowseparator(String showseparator) {
		this.showseparator = showseparator;
	}

	public String getSrv() {
		return srv;
	}

	public void setSrv(String srv) {
		this.srv = srv;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
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

	public List<MenuItemElement> getMenuitem() {
		return menuitem;
	}

	public void addMenuitem(MenuItemElement menuitem) {
		if(this.menuitem == null){
			this.menuitem = new ArrayList<MenuItemElement>(0);
		}
		this.menuitem.add(menuitem);
	}

	public static class MenuItemElement implements Serializable{
		private static final long serialVersionUID = -8490060184726527978L;
		private MenuElement menu;
		private String type;
		private String text;
		private String value;
		private String checked;
		private String group;
		private String iconcls;
		private String handle;
		
		public MenuElement getMenu() {
			return menu;
		}
		public void setMenu(MenuElement menu) {
			this.menu = menu;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
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
		public String getChecked() {
			return checked;
		}
		public void setChecked(String checked) {
			this.checked = checked;
		}
		public String getGroup() {
			return group;
		}
		public void setGroup(String group) {
			this.group = group;
		}
		public String getIconcls() {
			return iconcls;
		}
		public void setIconcls(String iconcls) {
			this.iconcls = iconcls;
		}
		public String getHandle() {
			return handle;
		}
		public void setHandle(String handle) {
			this.handle = handle;
		}
	}

	public Component getComponent(Json in) {
		// TODO Auto-generated method stub
		return null;
	}
}
