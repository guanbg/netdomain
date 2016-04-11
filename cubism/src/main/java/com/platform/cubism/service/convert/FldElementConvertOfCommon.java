package com.platform.cubism.service.convert;

import static com.platform.cubism.util.StringUtils.hasText;

import java.util.regex.Pattern;

import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.config.FldElement;
import com.platform.cubism.service.validate.ValidateManager;
import com.platform.cubism.util.Assert;

public class FldElementConvertOfCommon implements FldElementConvert {
	private static Pattern namePattern = Pattern.compile("^#\\{(.+)}$");
	public boolean isConvert(FldElement fldElem, CStruc data) {
		String name = fldElem.getName();
		String value = fldElem.getValue();
		if (!hasText(name)) {
			return false;
		}
		if (!hasText(value) || name.equals(value)) {// 为空默认映射名为name
			return true;
		}
		return false;
	}

	public CField convert(FldElement fldElem, CStruc data) {
		String name = fldElem.getName();
		String value = fldElem.getValue();
		if (!hasText(value)) {// 为空默认映射名为name
			value = name;
		}
		if(namePattern.matcher(name).matches()){
			try{
				name = data.getField(namePattern.matcher(name).replaceAll("$1")).getValue();
			}catch(Throwable t){
				name = namePattern.matcher(name).replaceAll("$1");
			}
		}

		CField dt = data.getField(value);

		if (fldElem.isRequired() && (dt == null || !hasText(dt.getValue()))) {// 必输验证
			Assert.notNull(null);
		}
		if (dt != null) {
			Assert.isTrue(ValidateManager.validate(dt.getValue(), fldElem.getCheck()));// 用户规则验证
		}
		return JsonFactory.createField(name, (dt == null) ? null : dt.getValue(), fldElem.getType(), fldElem.getFormat());
	}

}
