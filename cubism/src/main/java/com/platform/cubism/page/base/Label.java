package com.platform.cubism.page.base;

import static com.platform.cubism.util.StringUtils.hasText;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;

public class Label extends AbstractComponent {
	private String text;
	private String name;
	private String cls;
	private String status;
	private String handle;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
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
		sb.append("<LABEL id=\"").append(getId()).append("\" ");
		if(hasText(cls)){
			sb.append("class=\"").append(cls).append("\" ");
		}
		if(hasText(status)){
			if(HIDE.equalsIgnoreCase(status)){
				sb.append("style=\"display:none\" ");
			}
			else if(DISABLED.equalsIgnoreCase(status)){
				sb.append("disabled=\"true\" ");
			}
		}
		sb.append("hidefocus=\"true\" tabindex=\"-1\">");
		sb.append(text);
		sb.append("</LABEL>");
		return sb.toString();
	}

	public Json getComponentStruc() {//{id:'',tip:'',type:'',event:{},child:[]}
		Json cmp = JsonFactory.create();
		cmp.addField("id", getId());
		cmp.addField("type", "label");
		cmp.addStruc("event", JsonFactory.createField("click", handle));
		cmp.addArray("child");
		return cmp;
	}
}