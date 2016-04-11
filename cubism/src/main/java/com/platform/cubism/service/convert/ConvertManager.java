package com.platform.cubism.service.convert;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;
import org.slf4j.LoggerFactory;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CField;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.io.AbstractXmlLoader;
import com.platform.cubism.service.config.ArrElement;
import com.platform.cubism.service.config.FldElement;
import com.platform.cubism.service.config.StcElement;
import com.platform.cubism.util.Assert;

import static com.platform.cubism.util.CubismHelper.isAssignable;
import static com.platform.cubism.util.ReflectionUtils.instantiateClass;
import static com.platform.cubism.util.StringUtils.hasText;

public class ConvertManager {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ConvertManager.class);
	private final static String REGISTER_PATHNAME = "classpath:com/**/ConvertRegister.xml;"+ConvertManager.class.getResource("ConvertRegister.xml");
	private static List<FldElementConvert> FldElementConvertList;
	private static List<StcElementConvert> StcElementConvertList;
	private static List<ArrElementConvert> ArrElementConvertList;
	private static List<FldElementConvertToArray> FldElementConvertToArrayList;
	private static List<StcElementConvertToArray> StcElementConvertToArrayList;
	private static List<ArrElementConvertToArray> ArrElementConvertToArrayList;

	public static String IN = "in";
	public static String OUT = "out";
	public static String IN_PREFIX = "in.";
	public static String OUT_PREFIX = "out.";

	private static void resolveConverter(Object obj) {
		if (obj == null) {
			return;
		}
		if (isAssignable(obj.getClass(), FldElementConvert.class)) {
			if (FldElementConvertList == null) {
				FldElementConvertList = new LinkedList<FldElementConvert>();
			}
			FldElementConvertList.add((FldElementConvert) obj);
		} else if (isAssignable(obj.getClass(), StcElementConvert.class)) {
			if (StcElementConvertList == null) {
				StcElementConvertList = new LinkedList<StcElementConvert>();
			}
			StcElementConvertList.add((StcElementConvert) obj);
		} else if (isAssignable(obj.getClass(), ArrElementConvert.class)) {
			if (ArrElementConvertList == null) {
				ArrElementConvertList = new LinkedList<ArrElementConvert>();
			}
			ArrElementConvertList.add((ArrElementConvert) obj);
		} else if (isAssignable(obj.getClass(), FldElementConvertToArray.class)) {
			if (FldElementConvertToArrayList == null) {
				FldElementConvertToArrayList = new LinkedList<FldElementConvertToArray>();
			}
			FldElementConvertToArrayList.add((FldElementConvertToArray) obj);
		} else if (isAssignable(obj.getClass(), StcElementConvertToArray.class)) {
			if (StcElementConvertToArrayList == null) {
				StcElementConvertToArrayList = new LinkedList<StcElementConvertToArray>();
			}
			StcElementConvertToArrayList.add((StcElementConvertToArray) obj);
		} else if (isAssignable(obj.getClass(), ArrElementConvertToArray.class)) {
			if (ArrElementConvertToArrayList == null) {
				ArrElementConvertToArrayList = new LinkedList<ArrElementConvertToArray>();
			}
			ArrElementConvertToArrayList.add((ArrElementConvertToArray) obj);
		}
	}

	private static void createConverter(Element root) {
		Assert.notNull(root);
		for (Element regElem : root.elements()) {
			String clazz = regElem.attributeValue("class");
			if (!hasText(clazz)) {
				continue;
			}
			try {
				resolveConverter(instantiateClass(clazz));
			} catch (Exception e) {
				logger.error(clazz + " 注册数据转换器类错误:" + e.getMessage());
			}
		}
	}

	public static void registConverter() {
		AbstractXmlLoader xml = new AbstractXmlLoader() {
			public void onLoad(Element root) {
				createConverter(root);
			}
		};
		logger.info("开始加载数据转换器...");
		xml.loadXml(REGISTER_PATHNAME);
		logger.info("数据转换器加载完毕");
	}

	public static FldElementConvert[] getFldElementConvert() {
		return FldElementConvertList == null ? null : FldElementConvertList.toArray(new FldElementConvert[0]);
	}

	public static StcElementConvert[] getStcElementConvert() {
		return StcElementConvertList == null ? null : StcElementConvertList.toArray(new StcElementConvert[0]);
	}

	public static ArrElementConvert[] getArrElementConvert() {
		return ArrElementConvertList == null ? null : ArrElementConvertList.toArray(new ArrElementConvert[0]);
	}

	public static FldElementConvertToArray[] getFldElementConvertToArray() {
		return FldElementConvertToArrayList == null ? null : FldElementConvertToArrayList.toArray(new FldElementConvertToArray[0]);
	}

	public static StcElementConvertToArray[] getStcElementConvertToArray() {
		return StcElementConvertToArrayList == null ? null : StcElementConvertToArrayList.toArray(new StcElementConvertToArray[0]);
	}

	public static ArrElementConvertToArray[] getArrElementConvertToArray() {
		return ArrElementConvertToArrayList == null ? null : ArrElementConvertToArrayList.toArray(new ArrElementConvertToArray[0]);
	}

	public static CField fldConvert(FldElement fldElem, CStruc data) {
		FldElementConvert[] flds = getFldElementConvert();
		if (flds == null) {
			return null;
		}

		for (FldElementConvert fld : flds) {
			if (fld.isConvert(fldElem, data)) {
				return fld.convert(fldElem, data);
			}
		}

		return null;
	}

	public static CStruc stcConvert(StcElement stcElem, CStruc data) {
		StcElementConvert[] stcs = getStcElementConvert();
		if (stcs == null) {
			return null;
		}
		for (StcElementConvert stc : stcs) {
			if (stc.isConvert(stcElem, data)) {
				return stc.convert(stcElem, data);
			}
		}

		return null;
	}

	public static CArray arrConvert(ArrElement arrElem, CStruc data) {
		ArrElementConvert[] arrs = getArrElementConvert();
		if (arrs == null) {
			return null;
		}
		for (ArrElementConvert arr : arrs) {
			if (arr.isConvert(arrElem, data)) {
				return arr.convert(arrElem, data);
			}
		}
		return null;
	}

	public static CField[] fldConvertToArray(FldElement fldElem, CStruc data) {
		FldElementConvertToArray[] flds = getFldElementConvertToArray();
		if (flds == null) {
			return null;
		}

		for (FldElementConvertToArray fld : flds) {
			if (fld.isConvert(fldElem, data)) {
				return fld.convert(fldElem, data);
			}
		}

		return null;
	}

	public static CStruc[] stcConvertToArray(StcElement stcElem, CStruc data) {
		StcElementConvertToArray[] stcs = getStcElementConvertToArray();
		if (stcs == null) {
			return null;
		}
		for (StcElementConvertToArray stc : stcs) {
			if (stc.isConvert(stcElem, data)) {
				return stc.convert(stcElem, data);
			}
		}

		return null;
	}

	public static CArray[] arrConvertToArray(ArrElement arrElem, CStruc data) {
		ArrElementConvertToArray[] arrs = getArrElementConvertToArray();
		if (arrs == null) {
			return null;
		}
		for (ArrElementConvertToArray arr : arrs) {
			if (arr.isConvert(arrElem, data)) {
				return arr.convert(arrElem, data);
			}
		}
		return null;
	}
}