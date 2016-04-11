package com.platform.cubism.page.base;

import com.platform.cubism.CubismException;

public class Button {
	String name;
	String text;
	String icon;
	String iconAlign;
	String tip;

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

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public String getIcon() {
		return icon;
	}

	public void setIconAlign(String iconAlign) {
		this.iconAlign = iconAlign;
	}

	public String getIconAlign() {
		return iconAlign;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getType() {
		if ((icon == null || "".equals(icon)) && (text != null && !"".equals(text))) {// 仅文本
			return 0;
		} else if ((text == null || "".equals(text)) && (icon != null && !"".equals(icon))) {// 仅图标
			return 1;
		} else if ((text != null && !"".equals(text)) && (icon != null && !"".equals(icon))) {// 文本和图标
			return 2;
		}
		return 0;
	}

	public String textButtonHTML() {
		return "";
	}

	public String iconButtonHTML() {
		return "";
	}

	public String textIconButtonHTML() {
		return "";
	}

	public String toHTML() {
		switch (getType()) {
		case 0:
			return textButtonHTML();
		case 1:
			return iconButtonHTML();
		case 2:
			return textIconButtonHTML();
		}
		throw new CubismException("button type error");
	}

	public String toString() {
		return toHTML();
	}
}
