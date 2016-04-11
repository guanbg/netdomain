package com.platform.cubism.service.core;

import static com.platform.cubism.util.StringUtils.hasText;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;

import com.platform.cubism.CubismException;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.Service;
import com.platform.cubism.util.Assert;

public class RefClazzEntity extends AbstractEntity {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
	private static final Pattern hasMethod = Pattern.compile("^.*\\s*\\(\\s*(.*,.*)*\\s*\\).*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern hasMethod2 = Pattern.compile("^.*\\s*\\(\\s*(.+)*\\s*\\).*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern paramsSplit = Pattern.compile("\\s*\\(\\s*(.*)*\\s*\\)$", Pattern.CASE_INSENSITIVE);
	private String className;
	private String methodName;
	private String[] params;

	public RefClazzEntity() {
		;
	}

	public RefClazzEntity(ServiceEntity entity) {
		super(entity);
	}

	@Override
	public void setServiceEntity(ServiceEntity entity) {
		Assert.state(entity.getType() == EntityType.clazz);
		setEntity(entity);
	}

	public void parseClazz() {
		Matcher matcher = hasMethod.matcher(getValue());
		if (matcher.matches()) {
			String pp = matcher.group(1);
			String[] p = paramsSplit.split(getValue());
			className = p[0].substring(0, p[0].lastIndexOf('.'));
			methodName = p[0].substring(p[0].lastIndexOf('.') + 1);
			if (pp != null && !"".equals(pp)) {
				params = pp.split("\\s*,\\s*");
			}
		}
		else {
			Matcher matcher2 = hasMethod2.matcher(getValue());
			if (matcher2.matches()) {
				String pp = matcher2.group(1);
				String[] p = paramsSplit.split(getValue());
				className = p[0].substring(0, p[0].lastIndexOf('.'));
				methodName = p[0].substring(p[0].lastIndexOf('.') + 1);
				if (pp != null && !"".equals(pp)) {
					params = pp.split("\\s*,\\s*");
				}
			}
			else {
				className = getValue();
				methodName = null;
				params = null;
			}
		}
	}
	
	public Json execute(Json in) {
		if (!hasText(getValue())) {
			return null;
		}
		parseClazz();
		if (!hasText(className)) {
			return null;
		}
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if (cl == null) {
				cl = RefClazzEntity.class.getClassLoader();
			}
			Class<?> clazz = cl.loadClass(className);
			Object clazzobj = null;
			try {
				clazzobj = clazz.newInstance();
			} catch (InstantiationException e) {
				if (logger.isErrorEnabled()) {
					logger.error(className+"实例化类失败：" + e.getMessage());
				}
			} catch (IllegalAccessException e) {
				if (logger.isErrorEnabled()) {
					logger.error(className+"实例化类失败：" + e.getMessage());
				}
			}
			if (isService(clazz)) {
				Service srv = (Service) clazzobj;
				return srv.execute(in);
			} else {
				Class<?>[] args = null;
				Object[] argsobj = null;
				if (params != null) {
					args = new Class<?>[params.length];
					argsobj = new Object[params.length];
					for (int i = 0; i < params.length; i++) {
						if (params[i] == null) {
							args[i] = Object.class;
							argsobj[i] = null;
							continue;
						}
						if ("".equals(params[i]) || params[i].matches("^'.*'$")) {
							args[i] = String.class;
							if (params[i].length() > 2) {
								String s = params[i].substring(1, params[i].length() - 1);
								if (s.matches("^\\#\\{.*\\}$|^\\?\\{.*\\}$")) {
									argsobj[i] = in.getFieldValue(s.substring(2, s.length()-1));
								}
								else if(s.matches(".*\\#\\{.*\\}.*|.*\\?\\{.*\\}.*")){
									Pattern pattern = Pattern.compile("\\#\\{([^{}]+?)\\}|\\?\\{([^{}]+?)\\}", Pattern.CASE_INSENSITIVE);
									Matcher matcher = pattern.matcher(s);
									StringBuffer buf = new StringBuffer();
									while (matcher.find()) {
										if(matcher.group(1) != null && matcher.group(1).length() > 0){
											matcher.appendReplacement(buf, in.getFieldValue(matcher.group(1)));
										}
										else if(matcher.group(2) != null && matcher.group(2).length() > 0){
											matcher.appendReplacement(buf, in.getFieldValue(matcher.group(2)));
										}
										else{
											matcher.appendReplacement(buf, "");
										}
									}
									matcher.appendTail(buf);
									argsobj[i] = buf.toString();
								}
								else{
									argsobj[i] = s;
								}
							} else {
								argsobj[i] = "";
							}
							continue;
						}
						if ("true".equalsIgnoreCase(params[i]) || "false".equalsIgnoreCase(params[i])) {
							args[i] = Boolean.class;
							if ("true".equalsIgnoreCase(params[i])) {
								argsobj[i] = true;
							} else {
								argsobj[i] = false;
							}
							continue;
						}
						if (params[i].matches("[0-9\\.\\-]+")) {
							if (params[i].indexOf('.') != -1) {
								args[i] = double.class;
								argsobj[i] = Double.parseDouble(params[i]);
							} else {
								args[i] = int.class;
								argsobj[i] = Integer.parseInt(params[i]);
							}
							continue;
						}
						if (params[i].matches("^\\#\\{.*\\}$|^\\?\\{.*\\}$")) {
							String p = params[i].substring(2, params[i].length() - 1);
							if("in".equalsIgnoreCase(p)){
								if(in.get(p) == null){
									argsobj[i] = in;
								}
								else{
									argsobj[i] = in.get(p);
								}
							}
							else{
								argsobj[i] = in.get(p);
							}
							
							if (argsobj[i] != null) {
								args[i] = argsobj[i].getClass();
							} else {
								args[i] = Object.class;//null ?
							}
							continue;
						}
						args[i] = Object.class;
						argsobj[i] = params[i];
					}
				}
				Method method = null;
				try {
					method = clazz.getMethod(methodName, args);
				} catch (NoSuchMethodException ex) {
					if (logger.isInfoEnabled()) {
						logger.info("类名："+className+"方法名："+methodName);
						logger.info("方法查找失败，找不到该方法：" + ex.getMessage());
					}
					for(Method m:clazz.getMethods()){
						int len = m.getParameterTypes().length;
						if(m.getName().endsWith(methodName) && len == args.length){
							method = m;
							logger.info("类名："+className+"方法名："+methodName+"，开始执行中...");
							break;
						}
					}
					if(method == null){
						logger.error("方法查找失败，找不到该方法：" + ex.getMessage());
						throw new CubismException(className+"类方法找不到：" + methodName);
					}
				}
				try {
					Object o = method.invoke(clazzobj, argsobj);
					if (logger.isDebugEnabled()) {
						logger.debug("类方法执行完毕，返回结果：" + o);
					}
					if(o != null && String.class.isAssignableFrom(o.getClass())){
						Json json = JsonFactory.create(getId());
						json.addField(getId(), o.toString());
						
						CStruc inner = in.getStruc("in");
						
						if(inner != null && !inner.containsName(getId())){
							inner.addField(getId(), o.toString());
						}
						return json;
					}
					else if (o != null && Json.class.isAssignableFrom(o.getClass())) {
						Json json = (Json) o;
						json.setName(getId());
						if (logger.isDebugEnabled()) {
							logger.debug("类服务执行完毕，服务返回结果：" + o);
						}
						return json;
					}
					//return JsonFactory.create(getId());
					return null;
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.info("类名："+className+"方法名："+methodName);
						logger.error("方法调用失败：" + e.getMessage());
					}
					throw new CubismException(className+"类方法调用失败：" + e.getMessage());
				}
			}
		} catch (ClassNotFoundException e) {
			if (logger.isErrorEnabled()) {
				logger.info("类名："+className+"方法名："+methodName);
				logger.error("类查找失败，找不到该类：" + e.getMessage());
			}
			throw new CubismException(className+"类查找失败，找不到该类：" + e.getMessage());
		}
		//throw new CubismException(className+"类调用错误,服务执行失败");
	}

	private boolean isService(Class<?> clazz) {
		if (clazz == null || Object.class == clazz || Object.class.equals(clazz))
			return false;
		return Service.class.isAssignableFrom(clazz);
	}
}