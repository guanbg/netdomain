package com.platform.cubism.page.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.platform.cubism.base.Json;
import com.platform.cubism.page.Component;

public class ToolbarElement  implements Element, Serializable{
	private static final long serialVersionUID = -600419119256622438L;
	private static final String split="-";
	private String id;
	private String dock;
	private String align;
	private String border;
	private String status;
	private List<Object> child;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDock() {
		return dock;
	}
	public void setDock(String dock) {
		this.dock = dock;
	}
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public String getBorder() {
		return border;
	}
	public void setBorder(String border) {
		this.border = border;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<Object> getChild() {
		return child;
	}
	public void addButton(ButtonElement button) {
		if(this.child == null){
			this.child = new ArrayList<Object>(1);
		}
		this.child.add(button);
	}
	public void addImg(ImgElement img) {
		if(this.child == null){
			this.child = new ArrayList<Object>(1);
		}
		this.child.add(img);
	}
	public void addSplit() {
		if(this.child == null){
			this.child = new ArrayList<Object>(1);
		}
		this.child.add(split);
	}
	
	public Component getComponent(Json in) {
		// TODO Auto-generated method stub
		return null;
	}
}
