package com.platform.cubism.page.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.platform.cubism.base.Json;
import com.platform.cubism.page.Component;

public class GridElement implements Element, Serializable{
	private static final long serialVersionUID = 4825745466316258343L;
	private String id;
	private String title;
	private String titlealign;
	private String border;
	private String position;
	private String status;
	private String srv;
	private boolean initload;
	private boolean collapsed;
	
	private Inquiry inquiry;
	private Pagation pagation;
	private List<Column> column;
	private Map<String,String> event;
	private List<ToolbarElement> toolbar;
	
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

	public String getTitlealign() {
		return titlealign;
	}

	public void setTitlealign(String titlealign) {
		this.titlealign = titlealign;
	}

	public String getBorder() {
		return border;
	}

	public void setBorder(String border) {
		this.border = border;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSrv() {
		return srv;
	}

	public void setSrv(String srv) {
		this.srv = srv;
	}

	public boolean isInitload() {
		return initload;
	}

	public void setInitload(boolean initload) {
		this.initload = initload;
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	public Inquiry getInquiry() {
		return inquiry;
	}

	public void setInquiry(Inquiry inquiry) {
		this.inquiry = inquiry;
	}

	public Pagation getPagation() {
		return pagation;
	}

	public void setPagation(Pagation pagation) {
		this.pagation = pagation;
	}

	public List<Column> getColumn() {
		return column;
	}

	public void addColumn(Column column) {
		if(this.column == null){
			this.column = new ArrayList<Column>(1);
		}
		if(this.column.contains(column)){
			return;
		}
		this.column.add(column);
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

	public List<ToolbarElement> getToolbar() {
		return toolbar;
	}

	public void setToolbar(List<ToolbarElement> toolbar) {
		this.toolbar = toolbar;
	}
	
	public void addToolbar(ToolbarElement toolbar) {
		if(this.toolbar == null){
			this.toolbar = new ArrayList<ToolbarElement>(1);
		}
		if(this.toolbar.contains(toolbar)){
			return;
		}
		this.toolbar.add(toolbar);
	}

	private static class Column{
		private String srv;
		private String name;
		private String text;
		private String width;
		private String align;
		private String type;
		private String status;
		private boolean group;
		private boolean inquiry;
		private Map<String,String> event;
		
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
		public String getSrv() {
			return srv;
		}
		public void setSrv(String srv) {
			this.srv = srv;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getWidth() {
			return width;
		}
		public void setWidth(String width) {
			this.width = width;
		}
		public String getAlign() {
			return align;
		}
		public void setAlign(String align) {
			this.align = align;
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
		public boolean isGroup() {
			return group;
		}
		public void setGroup(boolean group) {
			this.group = group;
		}
		public boolean isInquiry() {
			return inquiry;
		}
		public void setInquiry(boolean inquiry) {
			this.inquiry = inquiry;
		}
	}
	private static class Inquiry{
		private String type;
		private MenuElement menu;
		private PanelElement panel;
		
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public MenuElement getMenu() {
			return menu;
		}
		public void setMenu(MenuElement menu) {
			this.menu = menu;
		}
		public PanelElement getPanel() {
			return panel;
		}
		public void setPanel(PanelElement panel) {
			this.panel = panel;
		}
	}
	private static class Pagation{
		private String button;
		
		private boolean first=false;
		private boolean previous=false;
		private boolean next=false;
		private boolean last=false;
		private boolean jump=false;
		private boolean refresh=false;
		private boolean add=false;
		private boolean update=false;
		private boolean delete=false;
		private boolean save=false;
		
		private String firstHandle;
		private String previousHandle;
		private String nextHandle;
		private String lastHandle;
		private String jumpHandle;
		private String refreshHandle;
		private String addHandle;
		private String updateHandle;
		private String deleteHandle;
		private String saveHandle;
		
		private String addType;
		private String updateType;
		
		public String getButton() {
			return button;
		}
		public void setButton(String button) {
			this.button = button;
		}
		public boolean isFirst() {
			return first;
		}
		public void setFirst(boolean first) {
			this.first = first;
		}
		public boolean isPrevious() {
			return previous;
		}
		public void setPrevious(boolean previous) {
			this.previous = previous;
		}
		public boolean isNext() {
			return next;
		}
		public void setNext(boolean next) {
			this.next = next;
		}
		public boolean isLast() {
			return last;
		}
		public void setLast(boolean last) {
			this.last = last;
		}
		public boolean isJump() {
			return jump;
		}
		public void setJump(boolean jump) {
			this.jump = jump;
		}
		public boolean isRefresh() {
			return refresh;
		}
		public void setRefresh(boolean refresh) {
			this.refresh = refresh;
		}
		public boolean isAdd() {
			return add;
		}
		public void setAdd(boolean add) {
			this.add = add;
		}
		public boolean isUpdate() {
			return update;
		}
		public void setUpdate(boolean update) {
			this.update = update;
		}
		public boolean isDelete() {
			return delete;
		}
		public void setDelete(boolean delete) {
			this.delete = delete;
		}
		public boolean isSave() {
			return save;
		}
		public void setSave(boolean save) {
			this.save = save;
		}
		public String getFirstHandle() {
			return firstHandle;
		}
		public void setFirstHandle(String firstHandle) {
			this.firstHandle = firstHandle;
		}
		public String getPreviousHandle() {
			return previousHandle;
		}
		public void setPreviousHandle(String previousHandle) {
			this.previousHandle = previousHandle;
		}
		public String getNextHandle() {
			return nextHandle;
		}
		public void setNextHandle(String nextHandle) {
			this.nextHandle = nextHandle;
		}
		public String getLastHandle() {
			return lastHandle;
		}
		public void setLastHandle(String lastHandle) {
			this.lastHandle = lastHandle;
		}
		public String getJumpHandle() {
			return jumpHandle;
		}
		public void setJumpHandle(String jumpHandle) {
			this.jumpHandle = jumpHandle;
		}
		public String getRefreshHandle() {
			return refreshHandle;
		}
		public void setRefreshHandle(String refreshHandle) {
			this.refreshHandle = refreshHandle;
		}
		public String getAddHandle() {
			return addHandle;
		}
		public void setAddHandle(String addHandle) {
			this.addHandle = addHandle;
		}
		public String getUpdateHandle() {
			return updateHandle;
		}
		public void setUpdateHandle(String updateHandle) {
			this.updateHandle = updateHandle;
		}
		public String getDeleteHandle() {
			return deleteHandle;
		}
		public void setDeleteHandle(String deleteHandle) {
			this.deleteHandle = deleteHandle;
		}
		public String getSaveHandle() {
			return saveHandle;
		}
		public void setSaveHandle(String saveHandle) {
			this.saveHandle = saveHandle;
		}
		public String getAddType() {
			return addType;
		}
		public void setAddType(String addType) {
			this.addType = addType;
		}
		public String getUpdateType() {
			return updateType;
		}
		public void setUpdateType(String updateType) {
			this.updateType = updateType;
		}
	}

	public Component getComponent(Json in) {
		// TODO Auto-generated method stub
		return null;
	}
}
