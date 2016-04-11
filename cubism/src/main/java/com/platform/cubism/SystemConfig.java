package com.platform.cubism;

import static com.platform.cubism.util.StringUtils.hasText;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import com.platform.cubism.util.Assert;
import com.platform.cubism.util.CubismHelper;

public class SystemConfig {
	private static String[] ADMIN_RIGHTS;
	private static String charset = "UTF-8";
	private static Properties properties = null;

	public static String getSysPath() {
		return CubismHelper.getAppRootDir();
	}

	private static void loadProperties(String path) throws IOException {
		Assert.hasText(path);
		InputStreamReader in= new InputStreamReader(SystemConfig.class.getClassLoader().getResourceAsStream(path),charset);
		properties = new Properties();
		properties.load(in);
	}
	
	public static Properties getProperties(){
		if (properties == null) {
			try {
				loadProperties("cubism.properties");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties;
	}
	
	public static String getValue(String key) {
		Assert.hasText(key);
		if (properties == null) {
			try {
				loadProperties("cubism.properties");
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
		}
		return properties.getProperty(key);
	}

	public static String getJdkUploadCacheLocation() {
		String path = getValue("sys.jdkuploadcachelocation");
		if (hasText(path)) {
			if (path.endsWith(File.separatorChar+"") || path.endsWith("\\") || path.endsWith("/")) {
				return path;
			}
			return path.concat("/");
		}
		return path;
	}

	public static String getTempPath() {
		String path = getValue("sys.temppath");
		if (hasText(path)) {
			if (path.endsWith(File.separatorChar+"") || path.endsWith("\\") || path.endsWith("/")) {
				return path;
			}
			return path.concat("/");
		}
		return path;
	}

	public static String getUploadPath() {
		String path = getValue("sys.uploadpath");
		if (hasText(path)) {
			if (path.endsWith(File.separatorChar+"") || path.endsWith("\\") || path.endsWith("/")) {
				return path;
			}
			return path.concat("/");
		}
		return path;
	}
	public static String getUploadTempPath() {
		String path = "temp";
		if (hasText(path)) {
			if (path.endsWith(File.separatorChar+"") || path.endsWith("\\") || path.endsWith("/")) {
				return path;
			}
			return path.concat("/");
		}
		return path;
	}

	public static String getDownloadPath() {
		String path = getValue("sys.downloadpath");
		if (hasText(path)) {
			if (path.endsWith(File.separatorChar+"") || path.endsWith("\\") || path.endsWith("/")) {
				return path;
			}
			return path.concat("/");
		}
		return path;
	}
	public static String getUpdateCacheHost() {
		String host = getValue("sys.updatecachehost");
		if (hasText(host)) {
			if (host.endsWith("/")) {
				return host.substring(0, host.length()-2);
			}
			return host;
		}
		return "";
	}
	public static boolean isCheckUserSession() {
		String checkusersession = getValue("sys.checkusersession");
		if (hasText(checkusersession)) {
			if("false".equalsIgnoreCase(checkusersession.trim())){
				return false;
			}
		}
		return true;
	}
	public static String getServicePoolSize() {
		String servicepoolsize = getValue("sys.servicepoolsize");
		if (hasText(servicepoolsize)) {
			servicepoolsize = servicepoolsize.trim();
		}
		return servicepoolsize;
	}

	public static String getServiceTimeOut() {
		String servicetimeout = getValue("sys.servicetimeout");
		if (hasText(servicetimeout)) {
			servicetimeout = servicetimeout.trim();
		}
		return servicetimeout;
	}
	public static String getModuleAuthExcludeFiles() {
		String excludefiles = getValue("sys.moduleauth.excludefiles");
		if (!hasText(excludefiles)) {
			excludefiles = "";
		}
		return excludefiles;
	}
	public static String getLoginMainPage() {
		String main = getValue("sys.login.main.jsp");
		if (!hasText(main)) {
			main = "";
		}
		return main;
	}
	public static String getLogonMainPage() {
		String main = getValue("sys.logon.main.jsp");
		if (!hasText(main)) {
			main = "";
		}
		return main;
	}
	public static String[] getAdminRights() {
		if(ADMIN_RIGHTS != null && ADMIN_RIGHTS.length > 0){
			return ADMIN_RIGHTS;
		}
		String rights = getValue("admin.rights.prefix");
		if (!hasText(rights)) {
			return null;
		}
		ADMIN_RIGHTS = rights.split("\\s*,\\s*|\\s*;\\s*|\\s");
		return ADMIN_RIGHTS;
	}
}