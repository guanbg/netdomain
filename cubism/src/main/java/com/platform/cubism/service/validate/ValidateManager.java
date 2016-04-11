package com.platform.cubism.service.validate;

import static com.platform.cubism.util.CubismHelper.isAssignable;
import static com.platform.cubism.util.ReflectionUtils.instantiateClass;
import static com.platform.cubism.util.StringUtils.hasText;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;
import org.slf4j.LoggerFactory;

import com.platform.cubism.io.AbstractXmlLoader;
import com.platform.cubism.util.Assert;

public class ValidateManager {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ValidateManager.class);
	private final static String REGISTER_PATHNAME = "classpath:com/**/ValidateRegister.xml;"+ValidateManager.class.getResource("ValidateRegister.xml");
	private static List<ValueValidate> ValueValidateList;

	private static void resolveConverter(Object obj) {
		if (obj == null) {
			return;
		}
		if (isAssignable(obj.getClass(), ValueValidate.class)) {
			if (ValueValidateList == null) {
				ValueValidateList = new LinkedList<ValueValidate>();
			}
			ValueValidateList.add((ValueValidate) obj);
		}
	}

	private static void createValidate(Element root) {
		Assert.notNull(root);
		for (Element regElem : root.elements()) {
			String clazz = regElem.attributeValue("class");
			if (!hasText(clazz)) {
				continue;
			}
			try {
				resolveConverter(instantiateClass(clazz));
			} catch (Exception e) {
				logger.error(clazz + " 注册验证器类错误:" + e.getMessage());
			}
		}
	}

	public static void registValidate() {
		AbstractXmlLoader xml = new AbstractXmlLoader() {
			public void onLoad(Element root) {
				createValidate(root);
			}
		};
		logger.info("开始加载数据验证器...");
		xml.loadXml(REGISTER_PATHNAME);
		logger.info("数据验证器加载完毕");
	}

	public static ValueValidate[] getValueValidate() {
		return ValueValidateList == null ? null : ValueValidateList.toArray(new ValueValidate[0]);
	}

	public static boolean validate(String value, String rule) {
		ValueValidate[] validates = getValueValidate();
		if (validates == null) {
			logger.info("数据验证-没有加载验证器，不进行数据验证失败.");
			return true;
		}
		for (ValueValidate vv : validates) {
			if (vv.isRule(rule) && !vv.validateRule(value, rule)) {
				return false;
			}
		}
		return true;
	}

	public static boolean radioValidate(String value, String rule) {
		ValueValidate[] validates = getValueValidate();
		if (validates == null) {
			logger.info("数据验证-没有加载验证器，不进行数据验证失败.");
			return true;
		}

		for (ValueValidate vv : validates) {
			if (vv.isRule(rule) && vv.validateRule(value, rule)) {
				return true;
			}
		}
		return false;
	}
}