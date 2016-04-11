package com.platform.cubism.query;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.util.Assert;

public class QueryManager {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ServiceFactory.class);
	private static Map<String, QueryConfig> querysConfigs;
	private static SoftReference<Map<String, QueryConfig>> querysCache;

	public static void addQuerys(Map<String, QueryConfig> querys) {
		if (querysConfigs == null) {
			querysConfigs = new HashMap<String, QueryConfig>();
		}
		querysConfigs.putAll(querys);
	}

	public static QueryConfig getQuery(String queryId) {
		Assert.hasText(queryId);
		Map<String, QueryConfig> cache;
		if (querysCache == null || querysCache.get() == null) {
			cache = new HashMap<String, QueryConfig>();
			querysCache = new SoftReference<Map<String, QueryConfig>>(cache);
		} else {
			cache = querysCache.get();
		}
		if (cache.containsKey(queryId)) {
			if (logger.isDebugEnabled()) {
				logger.debug("从缓存获取返回查询：" + queryId);
			}
			return cache.get(queryId);
		}

		QueryConfig qc = querysConfigs.get(queryId);
		Assert.isNull(qc);
		cache.put(queryId, qc);
		if (logger.isDebugEnabled()) {
			logger.debug("读取需要执行的查询信息:\n" + qc.toString());
		}

		return qc;
	}
}