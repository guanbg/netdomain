package com.platform.cubism.page.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.platform.cubism.base.Json;
import com.platform.cubism.page.Component;

public class UploadElement implements Element, Serializable{
	private static final long serialVersionUID = -8090671531992623141L;
	private String id;
	private String text;
	private String tip;
	private String srv;
	private String status;
	private List<Tab> tab;
	
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

	public String getSrv() {
		return srv;
	}

	public void setSrv(String srv) {
		this.srv = srv;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Tab> getTab() {
		return tab;
	}

	public void addTab(Tab tab) {
		if(this.tab == null){
			this.tab = new ArrayList<Tab>(1);
		}
		this.tab.add(tab);
	}
	private static class Tab{
		private String title;
		private String text;
		private String handle;
		private String position;
		
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
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
		public String getPosition() {
			return position;
		}
		public void setPosition(String position) {
			this.position = position;
		}
	}
	public Component getComponent(Json in) {
		// TODO Auto-generated method stub
		return null;
	}
}
