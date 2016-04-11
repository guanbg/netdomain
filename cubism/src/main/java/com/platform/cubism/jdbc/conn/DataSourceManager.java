package com.platform.cubism.jdbc.conn;

import com.platform.cubism.jdbc.conn.JndiDataSourceLookup;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.LoggerFactory;

public class DataSourceManager {
	public final static org.slf4j.Logger logger = LoggerFactory.getLogger(DataSourceManager.class);
	private static Map<String, DataSource> datasources;

	public static DataSource getDataSource() {
		return getDataSource(0);
	}

	public static DataSource getDataSource(int idx) {
		init();
		if (idx < 0 || idx > datasources.size()) {
			return null;
		}
		for (DataSource ds : datasources.values()) {
			if (idx == 0) {
				return ds;
			}
			idx--;
		}
		return null;
	}

	public static DataSource getDataSource(String name) {
		init();
		
		if(datasources.containsKey(name)){
			return datasources.get(name);
		}
		else{
			return getDataSource();
		}
	}

	private static void init() {
		if (datasources == null) {
			datasources = new LinkedHashMap<String, DataSource>();
			createDataSource();
		}
	}

	private static void createDataSource() {
		Map<String, Properties> names = DataSourceConfig.getInstance().getDataSourceNames();
		for (Entry<String, Properties> ent : names.entrySet()) {
			if (ent.getValue() == null || ent.getValue().containsKey("datasource")) {
				String ds = ent.getKey();
				if (ent.getValue() != null) {
					ds = ent.getValue().getProperty("datasource");
				}
				try {
					datasources.put(ds, new DelegatingDataSource(JndiDataSourceLookup.getDataSource(ds)));
					logger.debug(ds+"datasource create success");
				} catch (Exception e) {
					logger.error("datasource create faild:"+e.getMessage());
					if(logger.isDebugEnabled()){
						e.printStackTrace();
					}
				}
			} else if (ent.getValue() != null && ent.getValue().containsKey("driver")) {
				datasources.put(ent.getKey(), new SimpleDriverDataSource(ent.getValue()));
			} else if (ent.getValue() != null) {
				datasources.put(ent.getKey(), new DriverManagerDataSource(ent.getValue()));
			}
		}
	}
}