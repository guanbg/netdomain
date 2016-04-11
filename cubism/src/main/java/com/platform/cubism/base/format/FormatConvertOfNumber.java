package com.platform.cubism.base.format;

import static com.platform.cubism.util.StringUtils.hasText;

import java.util.regex.Pattern;

import com.platform.cubism.base.CField;

public class FormatConvertOfNumber implements FormatConvert {
	/**
	 * #.##% 以百分比方式计数，并取两位小数 #.#####E0 显示为科学计数法，并取五位小数 ,### 每三位以逗号进行分隔 0.00
	 * 取一位整数和两位小数 00.00 取两位整数和三位小数，整数不足部分以0填补 每秒###米 将格式嵌入文本
	 */
	private Pattern numPattern = Pattern.compile("^[0#.,]+$");

	public boolean isFormat(CField field) {
		String format = field.getFormat();
		if (!hasText(format)) {
			return false;
		}
		if (numPattern.matcher(format).matches()) {
			return true;
		}
		return false;
	}

	public String format(CField field) {
		// TODO Auto-generated method stub
		return null;
	}

}
