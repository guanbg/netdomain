package com.platform.cubism.service.convert;

import static com.platform.cubism.util.StringUtils.hasText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.config.FldElement;
import com.platform.cubism.service.validate.ValidateManager;
import com.platform.cubism.util.Assert;

public class FldElementConvertOfArrayIndex implements FldElementConvert {
	private static Pattern indexPattern = Pattern.compile("^.+\\[\\s*(\\d+)\\s*\\]\\.(.+)$");
	private static final Pattern split = Pattern.compile("\\s*\\[\\s*\\d+\\s*\\]\\s*", Pattern.CASE_INSENSITIVE);
	private static Pattern namePattern = Pattern.compile("^#\\{(.+)}$");

	public boolean isConvert(FldElement fldElem, CStruc data) {
		String name = fldElem.getName();
		String value = fldElem.getValue();
		if (!hasText(name) || !hasText(value)) {
			return false;
		}
		if (indexPattern.matcher(value).matches()) {// xx[0].xx 或者
													// xx.xx.xx[0].xx
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
		CField dt = null;
		Matcher matcher = indexPattern.matcher(value);
		if (matcher.matches()) {
			String idx = matcher.group(1);
			String last = matcher.group(2);
			String[] vls = split.split(value);
			CArray ca = data.getArray(vls[0]);
			if (ca == null || ca.isEmpty()) {
				return JsonFactory.createField(name, null, fldElem.getType(), fldElem.getFormat());
			}
			int index = Integer.parseInt(idx);
			if (index >= ca.size()) {
				return JsonFactory.createField(name, null, fldElem.getType(), fldElem.getFormat());
			}
			CStruc cs = ca.getRecord(index);
			if (cs == null || cs.isEmpty()) {
				return JsonFactory.createField(name, null, fldElem.getType(), fldElem.getFormat());
			}
			dt = cs.getField(last);
		} else {
			dt = data.getField(value);
		}

		if (fldElem.isRequired() && (dt == null || !hasText(dt.getValue()))) {// 必输验证
			Assert.notNull(null);
		}
		if (dt != null) {
			Assert.isTrue(ValidateManager.validate(dt.getValue(), fldElem.getCheck()));// 用户规则验证
		}
		return JsonFactory.createField(name, (dt == null) ? null : dt.getValue(), fldElem.getType(), fldElem.getFormat());
	}

}
