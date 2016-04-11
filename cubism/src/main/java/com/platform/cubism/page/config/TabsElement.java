package com.platform.cubism.page.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.platform.cubism.base.Json;
import com.platform.cubism.page.Component;

public class TabsElement implements Element, Serializable{
	private static final long serialVersionUID = -2161206296206460217L;
	private String id;
	private String tip;
	private String border;
	private String activeidx;
	private boolean showmenu;
	private String maxtab;
	private String status;
	private String tabposition;
	private Map<String,String> event;
	private List<TabElement> tab;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public String getBorder() {
		return border;
	}

	public void setBorder(String border) {
		this.border = border;
	}

	public String getActiveidx() {
		return activeidx;
	}

	public void setActiveidx(String activeidx) {
		this.activeidx = activeidx;
	}

	public boolean isShowmenu() {
		return showmenu;
	}

	public void setShowmenu(boolean showmenu) {
		this.showmenu = showmenu;
	}

	public String getMaxtab() {
		return maxtab;
	}

	public void setMaxtab(String maxtab) {
		this.maxtab = maxtab;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTabposition() {
		return tabposition;
	}

	public void setTabposition(String tabposition) {
		this.tabposition = tabposition;
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

	public List<TabElement> getTab() {
		return tab;
	}

	public void addTab(TabElement tab) {
		if(this.tab == null){
			this.tab = new ArrayList<TabElement>(1);
		}
		this.tab.add(tab);
	}

	public static class TabElement{
		private String id;
		private String title;
		private String iconcls;
		private String border;
		private boolean active;
		private boolean canclose;
		private String status;
		private String layout;
		private List<Element> child;
		
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
		public boolean isActive() {
			return active;
		}
		public void setActive(boolean active) {
			this.active = active;
		}
		public boolean isCanclose() {
			return canclose;
		}
		public void setCanclose(boolean canclose) {
			this.canclose = canclose;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
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
	}

	public Component getComponent(Json in) {
		// TODO Auto-generated method stub
		return null;
	}
}
