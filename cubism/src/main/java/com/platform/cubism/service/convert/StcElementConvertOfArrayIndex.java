package com.platform.cubism.service.convert;

import static com.platform.cubism.util.StringUtils.hasText;
import static com.platform.cubism.service.convert.ConvertManager.IN_PREFIX;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.config.StcElement;

public class StcElementConvertOfArrayIndex implements StcElementConvert {
	private static Pattern indexPattern = Pattern.compile("^.+\\[\\s*(\\d+)\\s*\\]\\s*$");
	private static Pattern indexPattern2 = Pattern.compile("^.+\\[\\s*(\\d+)\\s*\\]\\.(.+)$");
	private static final Pattern split = Pattern.compile("\\s*\\[\\s*\\d+\\s*\\]\\s*", Pattern.CASE_INSENSITIVE);

	public boolean isConvert(StcElement stcElem, CStruc data) {
		String name = stcElem.getName();
		String value = stcElem.getValue();
		if (hasText(name) && stcElem.isEmpty()) {
			if (indexPattern.matcher(value).matches()) {// xx[0] 或者  xx.xx.xx[0]
				return true;
			}
			if (indexPattern2.matcher(value).matches()) {// xx[0].xx 或者  xx.xx.xx[0].xx
				return true;
			}
		}
		return false;
	}

	public CStruc convert(StcElement stcElem, CStruc data) {
		String name = stcElem.getName();
		String value = stcElem.getValue();
		if (!hasText(value)) {
			value = IN_PREFIX + name + "[0]";
		}
		CStruc dt = null;
		Matcher matcher = indexPattern.matcher(value);
		if (matcher.matches()) {
			String idx = matcher.group(1);
			String[] vls = split.split(value);
			CArray ca = data.getArray(vls[0]);
			if (ca == null || ca.isEmpty()) {
				return JsonFactory.createStruc(name);
			}
			int index = Integer.parseInt(idx);
			if (index >= ca.size()) {
				return JsonFactory.createStruc(name);
			}
			dt = ca.getRecord(index);
		}
		else{
			matcher = indexPattern2.matcher(value);
			if (matcher.matches()) {
				String idx = matcher.group(1);
				String last = matcher.group(2);
				String[] vls = split.split(value);
				CArray ca = data.getArray(vls[0]);
				if (ca == null || ca.isEmpty()) {
					return JsonFactory.createStruc(name);
				}
				int index = Integer.parseInt(idx);
				if (index >= ca.size()) {
					return JsonFactory.createStruc(name);
				}
				dt = ca.getRecord(index);
				if (dt == null) {
					return JsonFactory.createStruc(name);
				}
				dt = dt.getStruc(last);
			}
		}
		if (dt == null) {
			return JsonFactory.createStruc(name);
		}

		CStruc ret = JsonFactory.createStruc(name);
		ret.copyOf(dt);

		return ret;
	}
}