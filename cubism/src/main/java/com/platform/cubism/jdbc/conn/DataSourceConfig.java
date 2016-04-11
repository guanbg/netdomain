package com.platform.cubism.jdbc.conn;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.platform.cubism.util.Assert;

public class DataSourceConfig {
	private static DataSourceConfig instance;
	private Properties properties = null;

	public static DataSourceConfig getInstance() {
		if (instance == null) {
			instance = new DataSourceConfig();
		}

		return instance;
	}

	public DataSourceConfig() {
		try {
			String path = getClass().getPackage().getName();
			String configname = path.replaceAll("\\.", "/") + "/DataSourceConfig.properties";
			loadProperties(configname);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Map<String, Properties> getDataSourceNames() {
		Map<String, Properties> names = new LinkedHashMap<String, Properties>();
		String dsnm = getValue("datasource");
		Assert.hasText(dsnm);
		String[] ds = dsnm.split(",|;");
		for (String s : ds) {
			names.put(s, getPerfixProp(s+"."));
		}
		return names;
	}

	public String getValue(String key) {
		Assert.hasText(key);
		Assert.notNull(properties);

		return properties.getProperty(key);
	}

	private Properties getPerfixProp(String prefix) {
		Properties prop = null;
		String key;
		for (Entry<Object, Object> ent : properties.entrySet()) {
			key = (String) ent.getKey();
			if (key.startsWith(prefix)) {
				if (prop == null) {
					prop = new Properties();
				}
				prop.put(key.substring(prefix.length()), getValue(key));
			}
		}
		return prop;
	}

	public Properties getProperties() {
		return properties;
	}

	private void loadProperties(String path) throws IOException {
		Assert.hasText(path);
		InputStream in = getClass().getClassLoader().getResourceAsStream(path);
		Assert.notNull(in, "找不到需要装载的文件：" + path);
		properties = new Properties();
		properties.load(in);
	}
}