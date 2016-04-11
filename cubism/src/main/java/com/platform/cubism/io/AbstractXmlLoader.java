package com.platform.cubism.io;

import static com.platform.cubism.util.StringUtils.hasText;

import java.io.IOException;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractXmlLoader {
	private final static Logger logger = LoggerFactory.getLogger(AbstractXmlLoader.class);

	public void loadXml(String locationPattern) {
		String[] locations = locationPattern.split(",|;");
		for (String pathname : locations) {
			if (!hasText(pathname) || pathname.trim().length() <= 0) {
				continue;
			}
			try {
				load(pathname.trim());
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error(pathname + " 文件装载错误：" + e.toString());
				}
			}
		}
	}

	private void load(String locationPattern) {
		try {
			Resource[] resources = new ResourcePatternResolver().getResources(locationPattern);
			if (resources == null || resources.length <= 0) {
				if (logger.isErrorEnabled()) {
					logger.error("指定路径下符合条件的文件不存在，忽略不装载，搜索路径为：" + locationPattern);
				}
				return;
			}
			SAXReader reader;
			Document document;
			InputStream in = null;
			for (Resource res : resources) {
				if (logger.isInfoEnabled()) {
					logger.info("开始解析：" + res.getURL());
				}
				reader = new SAXReader();
				try {
					in = res.getInputStream();
					document = reader.read(in);//.read(new InputStreamReader(in, "UTF-8"));
					Element root = document.getRootElement();
					onLoad(root);
					if (logger.isInfoEnabled()) {
						logger.info(res.getURL() + " 解析完毕!");
					}
				} catch (DocumentException e) {
					if (logger.isErrorEnabled()) {
						logger.error(res.getURL() + "配置文件读取错误:" + e.getMessage());
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error(res.getURL() + " load Exception:" + e.getMessage());
					}
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							if (logger.isErrorEnabled()) {
								logger.error("关闭流错误:" + e.toString());
							}
						}
					}
				}
			}
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error(locationPattern + "装载文件出错：" + e.toString());
			}
		}
	}

	abstract public void onLoad(Element root);
}
