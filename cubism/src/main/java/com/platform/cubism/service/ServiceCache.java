package com.platform.cubism.service;

import static com.platform.cubism.util.StringUtils.hasText;
import static com.platform.cubism.SystemConfig.getUpdateCacheHost;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.platform.cubism.base.Json;
import com.platform.cubism.service.config.ServiceConfig;
import com.platform.cubism.service.config.ServiceConfigManager;
import com.platform.cubism.util.CubismHelper;

public class ServiceCache {
	private static final Logger logger = LoggerFactory.getLogger(ServiceCache.class);
	private static CacheManager manager;
	private static int errcounter = 5;
	public static final String IN_CACHE_KEY_PARAM = "__IN_CACHE_KEY_PARAM__";
	
	public synchronized static void init(){//系统使用
		if(manager != null){
			return;
		}
		if(errcounter < 0){
			return;
		}
		try {
			 ClassLoader classLoader = ServiceCache.class.getClassLoader();
			manager = CacheManager.newInstance(classLoader.getResource("/ehcache.xml"));
			printCacheName();
		} catch (Exception e) {
			manager = null;
			errcounter--;
			logger.error("加载缓存配置文件ehcache.xml错误：" + e.getMessage());
			if(logger.isDebugEnabled()){
				e.printStackTrace();
			}
		}
	}
	
	public static void close(){//系统使用
		if(manager != null){
			manager.shutdown();
		}
	}
	public static void printCacheName(){
		init();
		if(manager == null){
			logger.info("CacheManager not init!");
			return;
		}
		for(String name : manager.getCacheNames()){
			logger.info("cacheName===>>>"+name);
		}
	}
	public static boolean isCacheService(String serviceName){
		if(serviceName == null || serviceName.length() <= 0){
			return false;
		}
		String serviceId = CubismHelper.getServiceName(serviceName);
		if(serviceId == null || serviceId.length() <= 0){
			return false;
		}
		ServiceConfig cfg = ServiceConfigManager.getService(serviceId);
		String cacheName = cfg.getCache();
		if(cacheName == null || cacheName.length() <= 0){
			return false;
		}
		if(cacheName.toLowerCase().equals("false")){
			return false;
		}
		return true;
	}
	public static void setServiceCache(String serviceName, String cacheKeyParam, Json value){
		init();
		if(manager == null){
			logger.info("CacheManager not init!");
			return;
		}
		if(serviceName == null || serviceName.length() <= 0){
			logger.info("setServiceCache:serviceName===>>> is null");
			return ;
		}
		String serviceId = CubismHelper.getServiceName(serviceName);
		if(serviceId == null || serviceId.length() <= 0){
			logger.info("setServiceCache:serviceId===>>> is null");
			return ;
		}
		ServiceConfig cfg = ServiceConfigManager.getService(serviceId);
		String cacheName = cfg.getCache();
		if(cacheName == null || cacheName.length() <= 0){
			logger.info("setServiceCache:cacheName===>>> is null");
			return ;
		}
		Cache cache = manager.getCache(cacheName);
		if(cache == null){
			logger.info("setServiceCache:cacheName===>>>"+cacheName+" not found Cache!");
			return;
		}
		
		String key = serviceId + "==>"+cacheKeyParam;
		if(logger.isDebugEnabled()){
			logger.debug("setServiceCache:cacheName===>>>"+cacheName+" 缓存主键[put]："+key+" 已放入缓存");
		}
		cache.put(new Element(key.toLowerCase(), value));
	}
	
	public static Json getServiceCache(String serviceName, String cacheKeyParam){
		init();
		if(manager == null){
			logger.info("CacheManager not init!");
			return null;
		}
		if(serviceName == null || serviceName.length() <= 0){
			logger.info("getServiceCache:serviceName===>>> is null");
			return null;
		}
		String serviceId = CubismHelper.getServiceName(serviceName);
		if(serviceId == null || serviceId.length() <= 0){
			logger.info("getServiceCache:serviceId===>>> is null");
			return null;
		}
		ServiceConfig cfg = ServiceConfigManager.getService(serviceId);
		if(cfg == null){
			logger.info("getServiceCache:ServiceConfig===>>> is null");
			return null;
		}
		String cacheName = cfg.getCache();
		if(cacheName == null || cacheName.length() <= 0){
			logger.info("getServiceCache:cacheName===>>> is null");
			return null;
		}
		Cache cache = manager.getCache(cacheName);
		if(cache == null){
			logger.info("getServiceCache:cacheName===>>>"+cacheName+" not found Cache!");
			return null;
		}
		String key = serviceId + "==>"+cacheKeyParam;
		if(logger.isDebugEnabled()){
			logger.debug("缓存主键[get]："+key);
		}
		if(cache.get(key.toLowerCase()) == null){
			logger.info("getServiceCache:key===>>>"+key+" not found Cache key!");
			return null;
		}
		Json ret = (Json)cache.get(key.toLowerCase()).getObjectValue();
		if(ret == null || ret.isEmpty()){
			cache.remove(key.toLowerCase());
			logger.info("getServiceCache:cacheName===>>>"+cacheName+" key===>>>"+key+" is null");
			return null;
		}
		if(logger.isDebugEnabled()){
			logger.debug("getServiceCache:cacheName===>>>"+cacheName+" key===>>>"+key+" 已从缓存取到！");
		}
		return ret;
	}
	
	public static void clearServiceCache(String serviceName){//暂时用线程，后面用队列进行处理
		if(serviceName == null || serviceName.length() <= 0){
			logger.info("clearServiceCache : serviceName===>>> is null");
			return ;
		}
		String serviceId = CubismHelper.getServiceName(serviceName);
		if(serviceId == null || serviceId.length() <= 0){
			logger.info("clearServiceCache : serviceId===>>> is null");
			return ;
		}
		ServiceConfig cfg = ServiceConfigManager.getService(serviceId);
		String updateCacheName = cfg.getUpdatecache();
		if(updateCacheName == null || updateCacheName.length() <= 0){
			logger.info("clearServiceCache : updateCacheName===>>> is null");
			return ;
		}
		if(logger.isDebugEnabled()){
			logger.debug("开始更新缓存："+updateCacheName);
		}
		new Thread(new ClearCacheThread(updateCacheName.toLowerCase())).start();
	}
	
	public static void clearCache(String keyPerfix){//暂时用线程，后面用队列进行处理
		new Thread(new ClearCacheThread(keyPerfix)).start();
	}
	
	public static void setSqlCache(String cacheName, String cacheKey, Json value){
		init();
		if(manager == null){
			logger.info("CacheManager not init!");
			return;
		}
		if(cacheName == null || cacheName.length() <= 0){
			logger.info("setSqlCache:cacheName===>>> is null");
			return ;
		}
		if(cacheKey == null || cacheKey.length() <= 0){
			logger.info("setSqlCache:cacheKey===>>> is null");
			return ;
		}
		if(value == null || value.isEmpty()){
			logger.info("setSqlCache:cacheValue===>>> is null");
			return ;
		}
		Cache cache = manager.getCache(cacheName);
		if(cache == null){
			logger.info("setSqlCache:cacheName===>>>"+cacheName+" not found Cache!");
			return;
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("setSqlCache:cacheName===>>>"+cacheName+" 缓存主键[put]："+cacheKey+" 已放入缓存");
		}
		cache.put(new Element(cacheKey.toLowerCase(), value));
	}
	
	public static Json getSqlCache(String cacheName, String cacheKey){
		init();
		if(manager == null){
			logger.info("CacheManager not init!");
			return null;
		}
		if(cacheName == null || cacheName.length() <= 0){
			logger.info("getSqlCache:cacheName===>>> is null");
			return null;
		}
		if(cacheKey == null || cacheKey.length() <= 0){
			logger.info("getSqlCache:cacheKey===>>> is null");
			return null;
		}
		Cache cache = manager.getCache(cacheName);
		if(cache == null){
			logger.info("getSqlCache:cacheName===>>>"+cacheName+" not found Cache!");
			return null;
		}
		if(logger.isDebugEnabled()){
			logger.debug("缓存主键[get]："+cacheKey);
		}
		if(cache.get(cacheKey.toLowerCase()) == null){
			logger.info("getSqlCache:key===>>>"+cacheKey+" not found Cache key!");
			return null;
		}
		Json ret = (Json)cache.get(cacheKey.toLowerCase()).getObjectValue();
		if(ret == null || ret.isEmpty()){
			cache.remove(cacheKey.toLowerCase());
			logger.info("getSqlCache:cacheName===>>>"+cacheName+" key===>>>"+cacheKey+" is null");
			return null;
		}
		if(logger.isDebugEnabled()){
			logger.debug("getSqlCache:cacheName===>>>"+cacheName+" key===>>>"+cacheKey+" 已从缓存取到！");
		}
		return ret;
	}
	
	public static void setCache(String cacheName, String key, Json value){
		init();
		if(manager == null){
			logger.info("CacheManager not init!");
			return;
		}
		if (!hasText(cacheName)) {
			logger.info("setCache:cacheName===>>> is null");
			return;
		}
		if (!hasText(key)) {
			logger.info("setCache:key===>>> is null");
			return;
		}
		if(value == null || value.isEmpty()){
			logger.info("setCache:value===>>> is null");
			return ;
		}
		
		Cache cache = manager.getCache(cacheName);
		if(cache == null){
			logger.info("setCache:cacheName===>>>"+cacheName+" not found Cache!");
			return;
		}
		
		cache.put(new Element(key.toLowerCase(), value));
	}
	
	public static Json getCache(String cacheName, String key){
		init();
		if(manager == null){
			logger.info("CacheManager not init!");
			return null;
		}
		if (!hasText(cacheName)) {
			logger.info("getCache:cacheName===>>> is null");
			return null;
		}
		if (!hasText(key)) {
			logger.info("getCache:key===>>> is null");
			return null;
		}
		Cache cache = manager.getCache(cacheName);
		if(cache == null){
			logger.info("getCache:cacheName===>>>"+cacheName+" not found Cache!");
			return null;
		}
		if(cache.get(key.toLowerCase()) == null){
			logger.info("getCache:key===>>>"+key+" not found Cache!");
			return null;
		}
		Json ret = (Json)cache.get(key.toLowerCase()).getObjectValue();
		if(ret == null || ret.isEmpty()){
			cache.remove(key.toLowerCase());
			logger.info("getCache:cacheName===>>>"+cacheName+" key===>>>"+key+" is null");
			return null;
		}
		if(logger.isDebugEnabled()){
			logger.debug("getCache:cacheName===>>>"+cacheName+" key===>>>"+key+" 已从缓存取到！");
		}
		return ret;
	}
	
	private static class ClearCacheThread implements Runnable {
		private final static String REMOTE_PERFIX = "?";
		private String[] perfix;
		
		public ClearCacheThread(String perfix){
			if(!hasText(perfix)){
				this.perfix = null;
			}
			else{
				if(perfix.indexOf(";") > 0){//分号优先级最高，可以用来分隔远程调用，或者分组
					this.perfix = perfix.toLowerCase().split("\\s*;\\s*");
				}
				else if(perfix.toLowerCase().indexOf(REMOTE_PERFIX) >= 0){
					this.perfix = new String[] {perfix.toLowerCase()};
				}
				else{
					this.perfix = perfix.toLowerCase().split("\\s*,\\s*|\\s+");//组内参数分隔符
				}
			}
		}
		
		public void run() {
			if(perfix == null || perfix.length <= 0){
				return;
			}
			List<String> remotePerfix = new ArrayList<String>(0);
			List<String> localPerfix = new ArrayList<String>(0);
			for(String pfx : perfix){
				if(!hasText(pfx)){
					continue;
				}
				if(pfx.indexOf(REMOTE_PERFIX) >= 0){
					remotePerfix.add(pfx);
				}
				else{
					String [] pp = pfx.split("\\s*,\\s*|\\s+");
					for(String p : pp){
						if(!hasText(p)){
							continue;
						}
						localPerfix.add(p);
					}
				}
			}
			if(!remotePerfix.isEmpty()){
				clearRemoteCache(remotePerfix.toArray(new String[0]));
			}
			if(manager == null){
				logger.info("ClearCacheThread no Cache!");
				return;
			}
			if(!localPerfix.isEmpty()){
				String[] pfx = localPerfix.toArray(new String[0]);
				for(String name : manager.getCacheNames()){
					logger.info("cacheName===>>>"+name);
					clearLocalCache(pfx,name);
				}
			}
		}
		
		private void clearRemoteCache(String[] perfix){
			if(perfix == null || perfix.length <= 0){
				return;
			}
			String host = getUpdateCacheHost();
			for (String pfx : perfix) {
				String url = null;
				if(pfx == null || pfx.length() <= 0){
					continue;
				}
				if(pfx.toLowerCase().startsWith("http://")){
					url = pfx;
				}
				else{
					url = host+pfx;
				}
				try {
					URL remote = new URL(url);
					HttpURLConnection conn = (HttpURLConnection)remote.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5000);//5秒
					conn.connect();
					if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
						logger.debug("成功连接到："+url);
					}
					else{
						logger.error("连接失败，操作被忽略："+url);
					}
					conn.disconnect();
				} catch (Exception e) {
					logger.error("clearRemoteCache===>>>"+e.getMessage());
					if(logger.isDebugEnabled()){
						e.printStackTrace();
					}
				}
			}
		}
		private void clearLocalCache(String[] perfix, String cacheName){
			if(cacheName == null || cacheName.length() <= 0){
				return;
			}
			if(manager == null){
				logger.info("ClearCacheThread no Cache!");
				return;
			}
			Cache cache = manager.getCache(cacheName);
			if(cache == null){
				return;
			}
			if(perfix == null || perfix.length <= 0){
				return;
			}
			
			if(cache.getKeys() == null){
				return;
			}
			String key;
			for(Object obj : cache.getKeys()){
				key = obj.toString();
				for (String pfx : perfix) {
					if(key.indexOf(pfx) != -1){
						cache.remove(obj);
					}
				}
			}
		}

	}
}
