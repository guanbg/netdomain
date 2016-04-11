package com.platform.cubism.util;

import static com.platform.cubism.util.HeadHelper.checkSysHead;
import static com.platform.cubism.util.HeadHelper.hasRights;
import static com.platform.cubism.util.HeadHelper.getSysHead;
import static com.platform.cubism.util.StringUtils.hasText;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.front.login.Login;
import com.platform.cubism.service.ServiceCache;
import com.platform.cubism.struc.SysHead;

public class CubismHelper {
	public static boolean isAssignable(Class<?> left, Class<?> right) {
		Assert.notNull(left, "Left-hand side type must not be null");
		Assert.notNull(right, "Right-hand side type must not be null");

		if (left == right) {
			return true;
		}
		if (left.equals(right) || left.equals(Object.class)) {
			return true;
		}
		if (left.isAssignableFrom(right) || right.isAssignableFrom(left)) {
			return true;
		}

		return false;
	}

	public static boolean isMapOfType(Map<?, ?> map, Class<?> keyYype, Class<?> valueYype) {
		boolean k = isCollectionOfType(map.keySet(), keyYype);
		boolean v = isCollectionOfType(map.keySet(), valueYype);
		return k && v;
	}

	public static boolean isCollectionOfType(Collection<?> collection, Class<?> type) {
		if (type == null) {
			return false;
		}
		if (collection == null || collection.isEmpty()) {
			return false;
		}
		for (Object element : collection) {
			if (!type.isInstance(element)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> copyOf(List<T> originalList) {
		if (originalList == null || originalList.isEmpty()) {
			return new ArrayList<T>(0);
		}

		T[] arr = null;
		// arr = originalList.toArray(arr);
		try {
			arr = (T[]) originalList.toArray();
			if (arr == null) {
				return new ArrayList<T>(0);
			}
		} catch (Exception e) {
			return new ArrayList<T>(0);
		}
		T[] cpy = Arrays.copyOf(arr, arr.length);

		return Arrays.asList(cpy);
	}

	public static boolean isSerializable(Object o) throws IOException {
		try {
			serialization(o);
			return true;
		} catch (NotSerializableException ex) {
			return false;
		}
	}

	public static byte[] serialization(Object o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.flush();
		baos.flush();

		return baos.toByteArray();
	}

	public static Object deserialize(byte[] objBytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream is = new ByteArrayInputStream(objBytes);
		ObjectInputStream ois = new ObjectInputStream(is);
		Object obj = ois.readObject();
		return obj;
	}

	public static Object serializeAndDeserialize(Object o) throws IOException, ClassNotFoundException {
		return deserialize(serialization(o));
	}

	@SuppressWarnings("unchecked")
	public static <T> T deepCopy(T src) {
		try {
			return (T) serializeAndDeserialize(src);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system
			// class loader...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = CubismHelper.class.getClassLoader();
		}
		return cl;
	}
	public static String getAppRootDir() {
		return getAppRootDir(false);
	}
	public static String getAppRootDir(boolean isWebinf) {
		String classesDir = getDefaultClassLoader().getResource("").getPath();//.toString();
		try {
			classesDir = URLDecoder.decode(classesDir,"utf-8");//关键啊 ！
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int start = 0;//"file:/".length();
		int end = classesDir.indexOf("WEB-INF");
		if(isWebinf){
			return classesDir.substring(start, end+8);
		}
		else{
			return classesDir.substring(start, end);
		}
	}

	public static String streamToString(BufferedReader br) throws IOException {
		StringBuilder sb = new StringBuilder();
		try {
			for (String s = br.readLine(); s != null; s = br.readLine()) {
				sb.append(s);
			}
		} catch (Throwable t) {
			return null;
		}
		
		return sb.toString();
	}
	
	public static Json requestStreamToJson(HttpServletRequest request) {
		String json = null;
		try {
			json = streamToString(request.getReader());
		} catch (IOException e) {
			return JsonFactory.create();
		}
		if (hasText(json)) {
			return JsonFactory.create().toJson(json);
		}
		else{
			return requestParamToJson(request);
		}
	}
	public static Json requestParamToJson(HttpServletRequest request) {
		Json ret = JsonFactory.create();
		String pName;
		String[] pValue;
		for (Entry<String, String[]> en : request.getParameterMap().entrySet()) {
			pName = en.getKey();
			pValue = en.getValue();
			
			if (pValue == null || pValue.length <= 1) {
				ret.addField(pName, pValue[0]);
			} else {
				CStruc[] records = new CStruc[pValue.length];
				int i = 0;
				for (String v : pValue) {
					records[i++] = new CStruc(pName).addField(JsonFactory.createField(pName, v));
				}
				ret.addArray(JsonFactory.createArray(pName, records));
			}
		}
		
		return ret;
	}

	public static String getServiceName(String name) {
		if (!hasText(name)) {
			return null;
		}
		int idx = name.lastIndexOf('/');
		if (idx >= 0) {
			name = name.substring(idx + 1);
		}
		if (!hasText(name)) {
			return null;
		}

		if (name.endsWith(".service")) {
			return name.substring(0, name.length() - 8);
		}

		if (name.endsWith(".class")) {
			return name.substring(0, name.length() - 6);
		}

		return name;
	}

	public static String getServiceFullName(String name) {
		if (!hasText(name)) {
			return null;
		}

		String suffix = ".service";
		if (!name.endsWith(suffix)) {
			return name + suffix;
		}

		return name;
	}

	public static void getCaller() {
		StackTraceElement[] stack = (new Throwable()).getStackTrace();
		for (int i = 0; i < stack.length; i++) {
			StackTraceElement ste = stack[i];
			System.out.println(ste.getClassName() + "." + ste.getMethodName() + "(...);");
			System.out.println(i + "--" + ste.getMethodName());
			System.out.println(i + "--" + ste.getFileName());
			System.out.println(i + "--" + ste.getLineNumber());
		}
	}
	public static String getUserPath(HttpServletRequest request) {//文件上传时获取写入文件的路径
		return getUserPath(request, File.separatorChar);
	}
	public static String getUserPath(HttpServletRequest request, char separator) {//文件上传时获取写入文件的路径
		HttpSession session = request.getSession(true);
		String s = (String) session.getAttribute(Login.USER_LOGINNAME_SESSION);
		if (s == null) {
			s = "";
			return s;
		}
		return s.trim() + separator;
	}
	public static String getUserPath(CStruc sysHead) {//复制文件时获取写入文件的路径
		if (sysHead == null || sysHead.isEmpty()) {
			return "";
		}
		return sysHead.getFieldValue(SysHead.LOGINNAME.toString())+ File.separatorChar;
	}
	
	public static String getIpAddr(HttpServletRequest request) {
		String ipAddress = request.getHeader("x-forwarded-for");
		if (!hasText(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (!hasText(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (!hasText(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("HTTP_CLIENT_IP");
		}
		if (!hasText(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (!hasText(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1")) {
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();// 根据网卡取本机配置的IP
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ipAddress = inet.getHostAddress();
			}
		}

		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.indexOf(",") > 0) {
			ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
		}

		return ipAddress;
	}
	
	public static Json queryStr2Json(String queryStr) {
		Json json = JsonFactory.create();
		if(queryStr == null || queryStr.length() <= 0){
			return json;
		}
		String jsonstr = "{\""+queryStr.replaceAll("&", "\",\"").replaceAll("=", "\":\"")+"\"}";
		return json.toJson(jsonstr);
	}
	
	public static Json parseJson(String str) {
		return parseJson(str, false);
	}

	public static Json parseJson(String str, boolean isall) {
		if (str == null || "".equals(str)) {
			return null;
		}

		Json json = null;
		if (isall) {
			Pattern keyvalue = Pattern.compile(".+?=.+?&*", Pattern.CASE_INSENSITIVE);
			Matcher m = keyvalue.matcher(str);
			if (m.matches()) {
				String[] kv = str.split("&");
				if (kv != null) {
					json = JsonFactory.create();
					int i = 0;
					for (String s : kv) {
						i = s.indexOf('=');
						json.addField(s.substring(0, i), s.substring(i + 1));
					}
					return json;
				}
			}
		}

		if (str.startsWith("[")) {
			CArray ca = JsonFactory.createArray().toJson(str);
			String cn = ca.getColumNames()[0];
			ca.setName(cn);

			json = JsonFactory.create().addArray(ca);
		} else {
			if (!str.startsWith("{")) {
				str = "{" + str + "}";
			}
			json = JsonFactory.create().toJson(str);
		}

		return json;
	}

	public static String getDownloadFileName(String fileName, HttpServletRequest request) throws IOException {
		String agent = request.getHeader("USER-AGENT");
		return getDownloadFileName(fileName, agent);
	}

	public static String getDownloadFileName(String fileName, String agent) throws IOException {
		if (null != agent && -1 != agent.indexOf("MSIE")) {
			return URLEncoder.encode(fileName, "UTF8");
		} else if (null != agent && -1 != agent.indexOf("Mozilla")) {
			return new String(fileName.getBytes(), "iso-8859-1");
			// return "=?UTF-8?B?"+(new
			// String(Base64.encodeBase64(fileName.getBytes("UTF-8"))))+"?=";
		} else {
			return fileName;
		}
	}

	public static Json getRequestParam(HttpServletRequest request) {
		if (request == null) {
			throw new CubismException("无效的请求");
		}
		else if(request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid()){
			throw new CubismException("无效的会话 ");
		}
		String json = null;
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = request.getReader();
			for (String s = br.readLine(); s != null; s = br.readLine()) {
				sb.append(s);
			}
			json = sb.toString();
			sb.delete(0, sb.length());
			sb = null;
		} catch (Throwable t) {
			json = null;
		}

		Json in = null;
		if (json != null && !"".equals(json)) {
			if (json.startsWith("[")) {
				CArray ca = JsonFactory.createArray().toJson(json);
				String cn = ca.getColumNames()[0];
				ca.setName(cn);

				in = JsonFactory.create().addArray(ca);
			} else {
				if (!json.startsWith("{")) {
					if(!json.endsWith("&")){
						json += "&";
					}
					Pattern keyvalue = Pattern.compile(".+?=.+?&*", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
					Matcher m = keyvalue.matcher(json);
					if (m.matches()) {
						Pattern ptn = Pattern.compile("[^\\\\]\"", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
						Pattern ptn2 = Pattern.compile("=(.+?)&", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
						try {
							//json = URLDecoder.decode(json, "UTF-8");
							json = json.replaceAll("%22", "%5C%22");
							json = ptn.matcher(json).replaceAll("\\\\\"");
							json = ptn2.matcher(json).replaceAll("=\"$1\",");
							if(json.endsWith("&") || json.endsWith(",")){
								json = json.substring(0,json.length()-1);
							}
							json = "{" + URLDecoder.decode(json, "UTF-8") + "}";
						} catch (UnsupportedEncodingException e) {
							json = ptn.matcher(json).replaceAll("\\\\\"");
							json = ptn2.matcher(json).replaceAll("=\"$1\",");
							if(json.endsWith("&") || json.endsWith(",")){
								json = json.substring(0,json.length()-1);
							}
							json = "{" + json + "}";
						}
					}
					else{
						json = "{" + json + "}";
					}
				}
				in = JsonFactory.create().toJson(json);
			}
		} else {
			in = requestParamToJson(request);
		}
		
		if(ServiceCache.isCacheService(getSysHead(SysHead.SERVICENAME.value(), in))){
			in.addField(ServiceCache.IN_CACHE_KEY_PARAM, in.toJson());//缓存的时的上送数据
		}
		
		checkSysHead(in, request);

		if (in == null || in.isEmpty()) {
			throw new CubismException("不能解析请求的URL及参数");
		}
		
		String serviceName = getSysHead(SysHead.SERVICENAME.value(), in);
		CStruc user = (CStruc) request.getSession().getAttribute(Login.USER_STRUC_SESSION);
		if(!hasRights(serviceName,user)){
			throw new CubismException("Sorry, You haven't any Business Authoritys, for this end a employeer You must be!");
		}
		return in;
	}
}