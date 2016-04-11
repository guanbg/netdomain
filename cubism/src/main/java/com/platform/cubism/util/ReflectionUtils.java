package com.platform.cubism.util;

import static com.platform.cubism.util.StringUtils.hasText;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;

import com.platform.cubism.base.CStruc;

public abstract class ReflectionUtils {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);
	public static final String ARRAY_SUFFIX = "[]";

	public static boolean setPropertyValue(Object obj, String propertyName, String propertyValue) {
		Method writeMethod = getPropertyWriteMethod(obj.getClass(), propertyName);
		if (writeMethod == null) {
			return false;
		}
		Type type = writeMethod.getGenericParameterTypes()[0];
		if (type == boolean.class) {
			//logger.debug(propertyName+":"+propertyValue);
			invokeMethod(writeMethod, obj, Boolean.valueOf(propertyValue).booleanValue());
		} else if (type == String.class) {
			invokeMethod(writeMethod, obj, propertyValue);
		} else if (type == int.class) {
			invokeMethod(writeMethod, obj, Integer.parseInt(propertyValue));
		} else if (type == long.class) {
			invokeMethod(writeMethod, obj, Long.parseLong(propertyValue));
		} else if (type == float.class) {
			invokeMethod(writeMethod, obj, Float.parseFloat(propertyValue));
		} else if (type == double.class) {
			invokeMethod(writeMethod, obj, Double.parseDouble(propertyValue));
		} else {
			invokeMethod(writeMethod, obj, propertyValue);
		}
		return true;
	}

	public static Method getPropertyWriteMethod(Class<? extends Object> beanClass, String propertyName) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
			for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
				if (propertyName.equalsIgnoreCase(pd.getName())) {
					Method writeMethod = pd.getWriteMethod();
					if (writeMethod != null) {
						return writeMethod;
					}
				}
			}
		} catch (IntrospectionException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	public static Object invokeMethod(Method method, Object target) {
		return invokeMethod(method, target, new Object[0]);
	}

	public static Object invokeMethod(Method method, Object target, Object... args) {
		try {
			return method.invoke(target, args);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage());
		}

		throw new IllegalStateException("Should never get here");
	}

	public static Class<?>[] getAllInterfaces(Object instance) {
		Assert.notNull(instance, "Instance must not be null");
		return getAllInterfacesForClass(instance.getClass());
	}

	public static Class<?>[] getAllInterfacesForClass(Class<?> clazz) {
		return getAllInterfacesForClass(clazz, null);
	}

	public static Class<?>[] getAllInterfacesForClass(Class<?> clazz, ClassLoader classLoader) {
		Set<Class<?>> ifcs = getAllInterfacesForClassAsSet(clazz, classLoader);
		return ifcs.toArray(new Class[ifcs.size()]);
	}

	public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz, ClassLoader classLoader) {
		Assert.notNull(clazz, "Class must not be null");
		Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>();
		if (clazz.isInterface() && isVisible(clazz, classLoader)) {
			interfaces.add(clazz);
			return interfaces;
			// return Collections.singleton(clazz);
		}
		while (clazz != null) {
			Class<?>[] ifcs = clazz.getInterfaces();
			for (Class<?> ifc : ifcs) {
				interfaces.addAll(getAllInterfacesForClassAsSet(ifc, classLoader));
			}
			clazz = clazz.getSuperclass();
		}
		return interfaces;
	}

	public static boolean isVisible(Class<?> clazz, ClassLoader classLoader) {
		if (classLoader == null) {
			return true;
		}
		try {
			Class<?> actualClass = classLoader.loadClass(clazz.getName());
			return (clazz == actualClass);
			// Else: different interface class found...
		} catch (ClassNotFoundException ex) {
			// No interface class found...
			return false;
		}
	}

	public static boolean isArray(Object obj) {
		return (obj != null && obj.getClass().isArray());
	}

	public static boolean isAssignableValue(Class<?> type, Object value) {
		Assert.notNull(type, "Type must not be null");
		return (value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive());
	}

	public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
		Assert.notNull(lhsType, "Left-hand side type must not be null");
		Assert.notNull(rhsType, "Right-hand side type must not be null");
		if (lhsType.isAssignableFrom(rhsType)) {
			return true;
		}
		if (lhsType.isPrimitive()) {
			Class<?> resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
			if (resolvedPrimitive != null && lhsType.equals(resolvedPrimitive)) {
				return true;
			}
		} else {
			Class<?> resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
			if (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper)) {
				return true;
			}
		}
		return false;
	}

	private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap<Class<?>, Class<?>>(8);
	static {
		primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
		primitiveWrapperTypeMap.put(Byte.class, byte.class);
		primitiveWrapperTypeMap.put(Character.class, char.class);
		primitiveWrapperTypeMap.put(Double.class, double.class);
		primitiveWrapperTypeMap.put(Float.class, float.class);
		primitiveWrapperTypeMap.put(Integer.class, int.class);
		primitiveWrapperTypeMap.put(Long.class, long.class);
		primitiveWrapperTypeMap.put(Short.class, short.class);
	}

	private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new HashMap<Class<?>, Class<?>>(8);
	static {
		primitiveTypeToWrapperMap.put(boolean.class, Boolean.class);
		primitiveTypeToWrapperMap.put(byte.class, Byte.class);
		primitiveTypeToWrapperMap.put(char.class, Character.class);
		primitiveTypeToWrapperMap.put(double.class, Double.class);
		primitiveTypeToWrapperMap.put(float.class, Float.class);
		primitiveTypeToWrapperMap.put(int.class, Integer.class);
		primitiveTypeToWrapperMap.put(long.class, Long.class);
		primitiveTypeToWrapperMap.put(short.class, Short.class);
	}

	public static boolean isPublicStaticFinal(Field field) {
		int modifiers = field.getModifiers();
		return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
	}

	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field
				.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	public static void makeAccessible(Constructor<?> ctor) {
		if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
			ctor.setAccessible(true);
		}
	}

	public static <T> Constructor<T> getConstructorIfAvailable(Class<T> clazz, Class<?>... paramTypes) {
		Assert.notNull(clazz, "Class must not be null");
		try {
			return clazz.getConstructor(paramTypes);
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}

	public static Method getStaticMethod(Class<?> clazz, String methodName, Class<?>... args) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(methodName, "Method name must not be null");
		try {
			Method method = clazz.getMethod(methodName, args);
			return Modifier.isStatic(method.getModifiers()) ? method : null;
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}

	public static Field findField(Class<?> clazz, String name) {
		return findField(clazz, name, null);
	}

	public static Field findField(Class<?> clazz, String name, Class<?> type) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.isTrue(name != null || type != null, "Either name or type of the field must be specified");
		Class<?> searchType = clazz;
		while (!Object.class.equals(searchType) && searchType != null) {
			Field[] fields = searchType.getDeclaredFields();
			for (Field field : fields) {
				if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
					return field;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	public static Object instantiateClass(String clazz) throws RuntimeException {
		Assert.hasText(clazz, "Class must not be null");
		try {
			Class<?> cl = CubismHelper.getDefaultClassLoader().loadClass(clazz);
			return (Object) instantiateClass(cl);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Boolean clazzIsExist(String clazz) {
		Object o = instantiateClass(clazz);
		return o != null;
	}

	public static <T> T instantiateClass(Class<T> clazz) throws RuntimeException {
		Assert.notNull(clazz, "Class must not be null");
		if (clazz.isInterface()) {
			throw new RuntimeException("Specified class is an interface");
		}
		try {
			Constructor<T> ctor = clazz.getDeclaredConstructor();
			if (ctor != null) {
				return instantiateClass(ctor);
			} else {
				return clazz.newInstance();
			}
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("NoSuchMethodException, No default constructor found", e);
		} catch (InstantiationException e) {
			throw new RuntimeException("InstantiationException, No default constructor found", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("IllegalAccessException, No default constructor found", e);
		} catch (Exception e) {
			throw new RuntimeException("Exception,No default constructor found", e);
		}
	}

	public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws RuntimeException {
		Assert.notNull(ctor, "Constructor must not be null");
		try {
			makeAccessible(ctor);
			return ctor.newInstance(args);
		} catch (InstantiationException ex) {
			throw new RuntimeException("Is it an abstract class?", ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException("Is the constructor accessible?", ex);
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException("Illegal arguments for constructor", ex);
		} catch (InvocationTargetException ex) {
			throw new RuntimeException("Constructor threw exception", ex.getTargetException());
		}
	}

	private static String getQualifiedNameForArray(Class<?> clazz) {
		StringBuilder result = new StringBuilder();
		while (clazz.isArray()) {
			clazz = clazz.getComponentType();
			result.append(ARRAY_SUFFIX);
		}
		result.insert(0, clazz.getName());
		return result.toString();
	}

	public static String getQualifiedName(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		if (clazz.isArray()) {
			return getQualifiedNameForArray(clazz);
		} else {
			return clazz.getName();
		}
	}

	public static String getShortName(Class<?> clazz) {
		return getShortName(getQualifiedName(clazz));
	}

	public static String getShortName(String className) {
		Assert.hasLength(className, "Class name must not be empty");
		int lastDotIndex = className.lastIndexOf('.');
		int nameEndIndex = className.indexOf("$$");// CGLIB_CLASS_SEPARATOR
		if (nameEndIndex == -1) {
			nameEndIndex = className.length();
		}
		String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
		shortName = shortName.replace('$', '.');// INNER_CLASS_SEPARATOR
												// PACKAGE_SEPARATOR
		return shortName;
	}

	public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isPrimitive() && clazz != void.class ? primitiveTypeToWrapperMap.get(clazz) : clazz);
	}

	public static Object CreateClazz(String className) {
		if (!hasText(className)) {
			return null;
		}
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			cl = ReflectionUtils.class.getClassLoader();
		}
		Object clazzobj = null;
		try {
			Class<?> clazz = cl.loadClass(className);
			clazzobj = clazz.newInstance();
		} catch (InstantiationException e) {
			if (logger.isErrorEnabled()) {
				logger.error("实例化类失败：" + e.getMessage());
			}
			return null;
		} catch (IllegalAccessException e) {
			if (logger.isErrorEnabled()) {
				logger.error("实例化类失败：" + e.getMessage());
			}
			return null;
		} catch (ClassNotFoundException e) {
			if (logger.isErrorEnabled()) {
				logger.error("实例化类失败：" + e.getMessage());
			}
			return null;
		} catch (Exception e) {
			return null;
		}
		return clazzobj;
	}

	public static Object execClazz(String name, CStruc in) {
		final Pattern hasMethod = Pattern.compile("^.*\\s*\\(\\s*(.+)*\\s*\\).*$", Pattern.CASE_INSENSITIVE);
		final Pattern paramsSplit = Pattern.compile("\\s*\\(\\s*(.*)*\\s*\\)$", Pattern.CASE_INSENSITIVE);
		String className;
		String methodName;
		String[] params = null;

		Matcher matcher = hasMethod.matcher(name);
		if (matcher.matches()) {
			String pp = matcher.group(1);
			String[] p = paramsSplit.split(name);
			className = p[0].substring(0, p[0].lastIndexOf('.'));
			methodName = p[0].substring(p[0].lastIndexOf('.') + 1);
			if (pp != null && !"".equals(pp)) {
				params = pp.split("\\s*,\\s*");
			}
		} else {
			className = name;
			methodName = null;
			params = null;
		}
		if (!hasText(className)) {
			return null;
		}
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if (cl == null) {
				cl = ReflectionUtils.class.getClassLoader();
			}
			Class<?> clazz = cl.loadClass(className);
			Object clazzobj = null;
			try {
				clazzobj = clazz.newInstance();
			} catch (InstantiationException e) {
				if (logger.isErrorEnabled()) {
					logger.error("实例化类失败：" + e.getMessage());
				}
			} catch (IllegalAccessException e) {
				if (logger.isErrorEnabled()) {
					logger.error("实例化类失败：" + e.getMessage());
				}
			}
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
							argsobj[i] = params[i].substring(1, params[i].length() - 1);
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
						argsobj[i] = in.getObject(p);
						if (argsobj[i] != null) {
							args[i] = argsobj[i].getClass();
						} else {
							args[i] = Object.class;
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
				if (logger.isErrorEnabled()) {
					logger.error("方法查找失败，找不到该方法：" + ex.getMessage());
				}
				return null;
			}
			try {
				Object o = method.invoke(clazzobj, argsobj);
				if (logger.isDebugEnabled()) {
					logger.debug("类方法执行完毕，返回结果：" + o);
				}
				return o;
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("方法调用失败：" + e.getMessage());
				}
				return null;
			}

		} catch (ClassNotFoundException e) {
			if (logger.isErrorEnabled()) {
				logger.error("类查找失败，找不到该类：" + e.getMessage());
			}
		}

		return null;
	}
}