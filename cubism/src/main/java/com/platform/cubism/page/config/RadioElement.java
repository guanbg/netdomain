package com.platform.cubism.page.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.platform.cubism.base.Json;
import com.platform.cubism.page.Component;

public class RadioElement implements Element, Serializable{
	private static final long serialVersionUID = -5117449402545684640L;
	private String id;
	private String srv;
	private String border;
	private String tip;
	private String handle;
	private String textfield;
	private String valuefield;
	private String align;
	private String status;
	private List<Item> item;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSrv() {
		return srv;
	}

	public void setSrv(String srv) {
		this.srv = srv;
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

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Item> getItem() {
		return item;
	}

	public void addItem(Item item) {
		if(this.item == null){
			this.item = new ArrayList<Item>(1);
		}
		this.item.add(item);
	}

	private static class Item{
		private String id;
		private String text;
		private String value;
		private String iconcls;
		private String border;
		private String tip;
		private String handle;
		private String status;
		
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
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getIconcls() {
			return iconcls;
		}
		public void setIconcls(String iconcls) {
			this.iconcls = iconcls;
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
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	}

	public Component getComponent(Json in) {
		// TODO Auto-generated method stub
		return null;
	}
}