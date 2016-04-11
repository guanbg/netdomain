package com.platform.cubism.page.config;

import java.io.Serializable;

import com.platform.cubism.base.Json;
import com.platform.cubism.page.Component;
import com.platform.cubism.page.base.Img;
import com.platform.cubism.page.parser.ParamFactory;

public class ImgElement implements Element, Serializable{
	private static final long serialVersionUID = 7250631736904162037L;
	private Component component = null;
	private boolean isDynamic;

	private String id;
	private String iconcls;
	private String url;
	private String tip;
	private String status;
	private String handle;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIconcls() {
		return iconcls;
	}

	public void setIconcls(String iconcls) {
		this.iconcls = iconcls;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
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
		Img img = new Img();
		
		if(!isDynamic){
			isDynamic = ParamFactory.hasParam(id);
		}		
		if(!isDynamic){
			isDynamic = ParamFactory.hasParam(tip);
		}		
		if(!isDynamic){
			isDynamic = ParamFactory.hasParam(iconcls);
		}		
		if(!isDynamic){
			isDynamic = ParamFactory.hasParam(url);
		}		
		if(!isDynamic){
			isDynamic = ParamFactory.hasParam(status);
		}		
		if(!isDynamic){
			isDynamic = ParamFactory.hasParam(handle);
		}
		
		img.setName(ParamFactory.getText(id, in));
		img.setUrl(ParamFactory.getText(url, in));
		img.setTip(ParamFactory.getText(tip, in));
		img.setIconcls(ParamFactory.getText(iconcls, in));
		img.setStatus(ParamFactory.getText(status, in));
		img.setHandle(ParamFactory.getText(handle, in));
		
		if(!isDynamic){
			component = img;
		}
		
		return img;
	}

}
