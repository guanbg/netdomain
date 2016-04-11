package com.platform.cubism.page.base;

import static com.platform.cubism.util.StringUtils.hasText;

import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;

public class Img  extends AbstractComponent{
	private String url;
	private String tip;
	private String name;
	private String iconcls;
	private String status;
	private String handle;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconcls() {
		return iconcls;
	}

	public void setIconcls(String iconcls) {
		this.iconcls = iconcls;
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

	public String toHTML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<IMG id=\"").append(getId()).append("\" ");
		if(hasText(name)){
			sb.append("name=\"").append(name).append("\" ");
		}
		if(hasText(url)){
			sb.append("src=\"").append(url).append("\" ");
		}
		if(hasText(tip)){
			sb.append("title=\"").append(tip).append("\" ");
			sb.append("alt=\"").append(tip).append("\" ");
		}
		if(hasText(iconcls)){
			sb.append("class=\"").append(iconcls).append("\" ");
		}
		if(hasText(status)){
			if(HIDE.equalsIgnoreCase(status)){
				sb.append("style=\"display:none\" ");
			}
			else if(DISABLED.equalsIgnoreCase(status)){
				sb.append("disabled=\"true\" ");
			}
		}
		sb.append("/>");
		return sb.toString();
	}

	public Json getComponentStruc() {//{id:'',tip:'',type:'',event:{},child:[]}
		Json cmp = JsonFactory.create();
		cmp.addField("id", getId());
		cmp.addField("tip", tip);
		cmp.addField("type", "img");
		cmp.addStruc("event", JsonFactory.createField("click", handle));
		cmp.addArray("child");
		return cmp;
	}
}