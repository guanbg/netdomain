package com.platform.cubism.service.convert;

import static com.platform.cubism.util.StringUtils.hasText;

import java.util.regex.Pattern;

import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.config.FldElement;
import com.platform.cubism.service.validate.ValidateManager;
import com.platform.cubism.util.Assert;
import com.platform.cubism.util.ReflectionUtils;

public class FldElementConvertOfClass implements FldElementConvert {
	private static Pattern classPattern = Pattern.compile("^[^\"']*.+\\..+\\(.*\\)[^\"']*$");
	private static Pattern namePattern = Pattern.compile("^#\\{(.+)}$");

	private String getClassName(String name) {
		return name.substring(0, name.lastIndexOf('.'));
	}

	public boolean isConvert(FldElement fldElem, CStruc data) {
		String name = fldElem.getName();
		String value = fldElem.getValue();
		if (!hasText(name) || !hasText(value)) {
			return false;
		}
		if (classPattern.matcher(value).matches()) {// xx.xx.xx() 或者 // xx.xx.xx(ss,ss)
			return ReflectionUtils.clazzIsExist(getClassName(value));
		}

		return false;
	}

	public CField convert(FldElement fldElem, CStruc data) {
		String name = fldElem.getName();
		String value = fldElem.getValue();
		Assert.hasText(name);
		Assert.hasText(value);
		if (value.length() < 2) {
			return null;
		}
		if(namePattern.matcher(name).matches()){
			try{
				name = data.getField(namePattern.matcher(name).replaceAll("$1")).getValue();
			}catch(Throwable t){
				name = namePattern.matcher(name).replaceAll("$1");
			}
		}

		value = (String) ReflectionUtils.execClazz(value, data);// 去掉引号
		if (fldElem.isRequired() && !hasText(value)) {// 必输验证
			Assert.notNull(null);
		}
		Assert.isTrue(ValidateManager.validate(value, fldElem.getCheck()));// 用户规则验证

		return JsonFactory.createField(name, value, fldElem.getType(), fldElem.getFormat());
	}
}