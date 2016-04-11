package com.platform.cubism.base.format;

import static com.platform.cubism.util.StringUtils.hasText;

import java.util.regex.Pattern;

import com.platform.cubism.base.CField;

public class FormatConvertOfDate implements FormatConvert {
	/**
	 * yyyy-MM-dd HH:mm:ss yyyy年MM月dd日
	 */
	private Pattern datePattern = Pattern.compile(".*yy.*MM.*dd.*HH.*mm.*ss.*");

	// private String defaultDateFormat = "yyyyMMdd";

	public boolean isFormat(CField field) {
		String format = field.getFormat();
		if (!hasText(format)) {
			return false;
		}
		if (datePattern.matcher(format).matches()) {
			return true;
		}
		return false;
	}

	public String format(CField field) {
		// TODO Auto-generated method stub
		return null;
	}

}
