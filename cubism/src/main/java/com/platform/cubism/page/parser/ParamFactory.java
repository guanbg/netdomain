package com.platform.cubism.page.parser;

import static com.platform.cubism.util.StringUtils.hasText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.service.core.ConditionFactory;
import com.platform.cubism.util.CubismHelper;

public class ParamFactory {
	private static final String staticParamPrefix = "#{";
	private static final String staticParamSuffix = "}";

	private static final String expParamPrefix = "${";
	private static final String expParamSuffix = "}";

	public static boolean hasParam(String text) {
		if (text == null || text.length() <= 0) {
			return false;
		}
		if (text.indexOf(staticParamPrefix) != -1 && text.indexOf(staticParamSuffix) != -1) {
			return true;
		}
		if (text.indexOf(expParamPrefix) != -1 && text.indexOf(expParamSuffix) != -1) {
			return true;
		}
		return false;
	}

	public static String getText(String text, Json in) {
		if (!hasParam(text)) {
			return text;
		}
		return getParamParser(in).parse(text);
	}

	public static ParamParser getParamParser(Json in) {
		return new ParamParser(new PageHandle(in), new String[] { staticParamPrefix, staticParamSuffix }, new String[] { expParamPrefix,
				expParamSuffix });
	}

	public static ParamParser getParamParser(ParamHandle handle) {
		return new ParamParser(handle, new String[] { staticParamPrefix, staticParamSuffix }, new String[] { expParamPrefix, expParamSuffix });
	}

	private static class PageHandle implements ParamHandle {
		private Json in;

		public PageHandle(Json in) {
			this.in = in;
		}

		public String handleToken(String before, String openToken, String content, String closeToken, String after) {
			if (in == null) {
				return "";
			}

			if (!hasText(content)) {
				return openToken + closeToken;
			}

			if (expParamPrefix.equals(openToken) && expParamSuffix.equals(closeToken)) {
				return ConditionFactory.getStrValue(content, in);
			}

			Object obj = in.get(content);
			if (obj == null) {
				return "";
			}

			CField[] fld = null;
			if (obj.getClass().isArray()) {
				if (CubismHelper.isAssignable(CField[].class, obj.getClass())) {
					fld = (CField[]) obj;
				}
				if (CubismHelper.isAssignable(CStruc[].class, obj.getClass())) {
					List<CField> fList = new ArrayList<CField>();
					CStruc[] ss = (CStruc[]) obj;
					for (CStruc s : ss) {
						fList.addAll(Arrays.asList(s.flat()));
					}
					fld = fList.toArray(new CField[0]);
				}
				if (CubismHelper.isAssignable(CArray[].class, obj.getClass())) {
					List<CField> fList = new ArrayList<CField>();
					CArray[] aa = (CArray[]) obj;
					for (CArray a : aa) {
						fList.addAll(Arrays.asList(a.flat()));
					}
					fld = fList.toArray(new CField[0]);
				}
			} else {
				if (CubismHelper.isAssignable(CField.class, obj.getClass())) {
					fld = new CField[1];
					fld[0] = ((CField) obj);
				}
				if (CubismHelper.isAssignable(CStruc.class, obj.getClass())) {
					fld = ((CStruc) obj).flat();
				}
				if (CubismHelper.isAssignable(CArray.class, obj.getClass())) {
					fld = ((CArray) obj).flat();
				}
			}
			if (fld == null || fld.length <= 0) {
				return "";
			}
			if (fld.length == 1) {
				return fld[0].getValue();
			}
			StringBuilder sb = new StringBuilder();
			for (CField f : fld) {
				sb.append(f.getValue()).append(" ");
			}
			return sb.toString();
		}
	}
}
