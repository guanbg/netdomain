package com.platform.cubism.page.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.platform.cubism.base.Json;
import com.platform.cubism.page.Component;

public class PanelElement implements Element, Serializable{
	private static final long serialVersionUID = 4086383193989953186L;
	private String id;
	private String border;
	private String title;
	private String titlealign;
	private String headerposition;
	private String collapsed;
	private String collapseaction;
	private String closed;
	private String closeaction;
	private String layout;
	private List<ContextItem> contextitem;
	private List<Element> child;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBorder() {
		return border;
	}

	public void setBorder(String border) {
		this.border = border;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitlealign() {
		return titlealign;
	}

	public void setTitlealign(String titlealign) {
		this.titlealign = titlealign;
	}

	public String getHeaderposition() {
		return headerposition;
	}

	public void setHeaderposition(String headerposition) {
		this.headerposition = headerposition;
	}

	public String getCollapsed() {
		return collapsed;
	}

	public void setCollapsed(String collapsed) {
		this.collapsed = collapsed;
	}

	public String getCollapseaction() {
		return collapseaction;
	}

	public void setCollapseaction(String collapseaction) {
		this.collapseaction = collapseaction;
	}

	public String getClosed() {
		return closed;
	}

	public void setClosed(String closed) {
		this.closed = closed;
	}

	public String getCloseaction() {
		return closeaction;
	}

	public void setCloseaction(String closeaction) {
		this.closeaction = closeaction;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public List<Element> getChild() {
		return child;
	}

	public void addChild(Element child) {
		if(this.child == null){
			this.child = new ArrayList<Element>(1);
		}
		this.child.add(child);
	}
	public List<ContextItem> getContextItem() {
		return contextitem;
	}

	public void addContextItem(ContextItem contextitem) {
		if(this.contextitem == null){
			this.contextitem = new ArrayList<ContextItem>(0);
		}
		this.contextitem.add(contextitem);
	}
	
	public static class ContextItem implements Serializable{
		private static final long serialVersionUID = -3228330476019618898L;
		private String clicktype;
		private String position;
		private MenuElement menu;
		
		public String getClicktype() {
			return clicktype;
		}
		public void setClicktype(String clicktype) {
			this.clicktype = clicktype;
		}
		public String getPosition() {
			return position;
		}
		public void setPosition(String position) {
			this.position = position;
		}
		public MenuElement getMenu() {
			return menu;
		}
		public void setMenu(MenuElement menu) {
			this.menu = menu;
		}		
	}
	
	public Component getComponent(Json in) {
		// TODO Auto-generated method stub
		return null;
	}

}
