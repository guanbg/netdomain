package com.platform.cubism.query;

import static com.platform.cubism.util.ReflectionUtils.setPropertyValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.slf4j.LoggerFactory;

import com.platform.cubism.io.AbstractXmlLoader;
import com.platform.cubism.util.Assert;

public class QueryConfigLoader {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
	
	public static void loadQuery(String locationPatterns) {
		new QueryConfigLoader(locationPatterns);
	}
	private QueryConfigLoader(String locationPatterns) {
		AbstractXmlLoader xml = new AbstractXmlLoader() {
			public void onLoad(Element root) {
				loadResource(root);
			}
		};
		logger.info("开始装载综合查询配置信息...");
		xml.loadXml(locationPatterns);
		logger.info("综合查询配置信息装载完毕");
	}

	private void loadResource(Element root) {
		Assert.notNull(root);
		Map<String, QueryConfig> querys = new HashMap<String, QueryConfig>();
		for (Element elem : root.elements()) {
			QueryConfig qc = getQueryConfig(elem);
			querys.put(qc.getId(), qc);
		}
		QueryManager.addQuerys(Collections.unmodifiableMap(querys));
	}
	private QueryConfig getQueryConfig(Element qElem) {
		QueryConfig qc = new QueryConfig();
		setAttributes(qc, qElem);
		for (Element elem : qElem.elements()) {
			QueryFld qf = new QueryFld();
			setAttributes(qf, elem);
			String value = elem.getTextTrim();
			if (value != null && !"".equals(value))
				qf.setData(value);
			qc.addFld(qf);
		}
		return qc;
	}
	private void setAttributes(Object obj, Element element) {
		for (Attribute attribute : element.attributes()) {
			String attr = attribute.getName();
			setPropertyValue(obj, attr, attribute.getValue());
		}
	}
}