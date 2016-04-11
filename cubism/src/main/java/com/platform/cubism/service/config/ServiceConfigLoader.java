package com.platform.cubism.service.config;

import static com.platform.cubism.util.ReflectionUtils.setPropertyValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.platform.cubism.util.Assert;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.slf4j.LoggerFactory;

import com.platform.cubism.io.AbstractXmlLoader;

public class ServiceConfigLoader {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
	private static final String IN = "in";
	private static final String OUT = "out";
	private static final String REF = "ref";
	private static final String SQL = "sql";
	private static final String LOG = "log";
	private static final String EXP = "exp";
	private static final String IMP = "imp";
	private static final String QUIT = "quit";
	private static final String JSON = "json";

	private static final String FLD = "fld";
	private static final String STC = "stc";
	private static final String ARR = "arr";

	public static void loadService(String locationPatterns) {
		new ServiceConfigLoader(locationPatterns);
	}

	private ServiceConfigLoader(String locationPatterns) {
		AbstractXmlLoader xml = new AbstractXmlLoader() {
			public void onLoad(Element root) {
				loadResource(root);
			}
		};
		if (logger.isInfoEnabled()) {
			logger.info("开始装载系统服务...");
		}
		xml.loadXml(locationPatterns);
		if (logger.isInfoEnabled()) {
			logger.info("系统服务装载完毕");
		}
	}

	private void loadResource(Element root) {
		Assert.notNull(root);
		Map<String, ServiceConfig> services = new HashMap<String, ServiceConfig>();
		for (Element srvElem : root.elements()) {
			ServiceConfig sc = new ServiceConfig();
			setAttributes(sc, root);
			setServiceConfig(srvElem, sc);
			services.put(sc.getId(), sc);
		}
		ServiceConfigManager.addServices(Collections.unmodifiableMap(services));
	}

	private ServiceConfig setServiceConfig(Element srvElem, ServiceConfig sc) {
		Assert.notNull(sc);
		int sequence = 0;
		setAttributes(sc, srvElem);//System.out.println(sc);
		for (Element elem : srvElem.elements()) {
			String nodeName = elem.getName();
			if (IN.equalsIgnoreCase(nodeName)) {
				sc.setIn(getInElement(elem));
			} else if (OUT.equalsIgnoreCase(nodeName)) {
				sc.setOut(getOutElement(elem));
			} else if (REF.equalsIgnoreCase(nodeName)) {
				sc.addRef(getRefElement(elem).setSequence(++sequence));
			} else if (QUIT.equalsIgnoreCase(nodeName)) {
				sc.addQuit(getQuitElement(elem).setSequence(++sequence));
			} else if (SQL.equalsIgnoreCase(nodeName)) {
				sc.addSql(getSqlElement(elem).setSequence(++sequence));
			} else if (LOG.equalsIgnoreCase(nodeName)) {
				sc.addLog(getLogElement(elem));
			} else if (EXP.equalsIgnoreCase(nodeName)) {
				sc.addExp(getExpElement(elem).setSequence(++sequence));
			} else if (IMP.equalsIgnoreCase(nodeName)) {
				sc.addImp(getImpElement(elem).setSequence(++sequence));
			} else if (JSON.equalsIgnoreCase(nodeName)) {
				sc.addJson(getJsonElement(elem));
			}
		}
		sc.setMaxSeq(sequence);
		//logger.debug(sc);
		return sc;
	}

	private InElement getInElement(Element inElem) {
		InElement in = new InElement();
		setAttributes(in, inElem);
		for (Element elem : inElem.elements()) {
			String nodeName = elem.getName();
			if (FLD.equalsIgnoreCase(nodeName)) {
				in.addFld(getFldElement(elem));
			} else if (STC.equalsIgnoreCase(nodeName)) {
				in.addStc(getStcElement(elem));
			} else if (ARR.equalsIgnoreCase(nodeName)) {
				in.addArr(getArrElement(elem));
			}
		}
		return in;
	}

	private OutElement getOutElement(Element outElem) {
		OutElement out = new OutElement();
		setAttributes(out, outElem);
		for (Element elem : outElem.elements()) {
			String nodeName = elem.getName();
			if (FLD.equalsIgnoreCase(nodeName)) {
				out.addFld(getFldElement(elem));
			} else if (STC.equalsIgnoreCase(nodeName)) {
				out.addStc(getStcElement(elem));
			} else if (ARR.equalsIgnoreCase(nodeName)) {
				out.addArr(getArrElement(elem));
			}
		}
		return out;
	}

	private RefElement getRefElement(Element refElem) {
		RefElement ref = new RefElement();
		setAttributes(ref, refElem);
		return ref;
	}
	
	private QuitElement getQuitElement(Element quitElem) {
		QuitElement quit = new QuitElement();
		setAttributes(quit, quitElem);
		return quit;
	}

	private SqlElement getSqlElement(Element sqlElem) {
		SqlElement sql = new SqlElement();
		setAttributes(sql, sqlElem);
		String value = sqlElem.getTextTrim();
		if (value != null && !"".equals(value))
			sql.setValue(value);
		//logger.debug(sql);
		return sql;
	}
	private ExpElement getExpElement(Element expElem) {
		ExpElement exp = new ExpElement();
		setAttributes(exp, expElem);
		String value = expElem.getTextTrim();
		if (value != null && !"".equals(value))
			exp.setValue(value);
		return exp;
	}
	private ImpElement getImpElement(Element impElem) {
		ImpElement imp = new ImpElement();
		setAttributes(imp, impElem);
		String value = impElem.getTextTrim();
		if (value != null && !"".equals(value))
			imp.setValue(value);
		return imp;
	}

	private LogElement getLogElement(Element logElem) {
		LogElement log = new LogElement();
		setAttributes(log, logElem);
		for (Element elem : logElem.elements()) {
			String nodeName = elem.getName();
			if (FLD.equalsIgnoreCase(nodeName)) {
				log.addFld(getFldElement(elem));
			} else {
				log.addRow(getRowElement(elem));
			}
		}
		return log;
	}

	private List<FldElement> getRowElement(Element rowElem) {
		List<FldElement> row = new ArrayList<FldElement>();
		for (Element elem : rowElem.elements()) {
			row.add(getFldElement(elem));
		}
		return row;
	}

	private JsonElement getJsonElement(Element jsonElem) {
		JsonElement json = new JsonElement();
		setAttributes(json, jsonElem);
		String value = jsonElem.getTextTrim();
		if (value != null && !"".equals(value))
			json.setValue(value);
		return json;
	}

	private FldElement getFldElement(Element fldElem) {
		FldElement fld = new FldElement();
		setAttributes(fld, fldElem);
		String value = fldElem.getTextTrim();
		if (value != null && !"".equals(value))
			fld.setValue(value);
		return fld;
	}

	private StcElement getStcElement(Element stcElem) {
		StcElement stc = new StcElement();
		setAttributes(stc, stcElem);

		for (Element elem : stcElem.elements()) {
			String nodeName = elem.getName();
			if (FLD.equalsIgnoreCase(nodeName)) {
				stc.addFld(getFldElement(elem));
			} else if (STC.equalsIgnoreCase(nodeName)) {
				stc.addStc(getStcElement(elem));
			} else if (ARR.equalsIgnoreCase(nodeName)) {
				stc.addArr(getArrElement(elem));
			}
		}

		return stc;
	}

	private ArrElement getArrElement(Element arrElem) {
		ArrElement arr = new ArrElement();
		setAttributes(arr, arrElem);
		for (Element elem : arrElem.elements()) {
			arr.setStc(getStcElement(elem));
		}
		return arr;
	}

	private void setAttributes(Object obj, Element element) {
		for (Attribute attribute : element.attributes()) {
			String attr = attribute.getName();
			//logger.debug(attr+':'+attribute.getValue());
			setPropertyValue(obj, attr, attribute.getValue());
		}
	}
}