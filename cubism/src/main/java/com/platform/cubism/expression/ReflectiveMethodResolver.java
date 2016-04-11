package com.platform.cubism.expression;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.platform.cubism.cvt.MethodParameter;
import com.platform.cubism.cvt.TypeDescriptor;
import com.platform.cubism.util.CubismHelper;

public class ReflectiveMethodResolver implements MethodResolver {
	private static Method[] NO_METHODS = new Method[0];
	private Map<Class<?>, MethodFilter> filters = null;
	private boolean useDistance = false;

	public ReflectiveMethodResolver() {

	}

	public ReflectiveMethodResolver(boolean useDistance) {
		this.useDistance = useDistance;
	}

	public MethodExecutor resolve(EvaluationContext context, Object targetObject, String name, List<TypeDescriptor> argumentTypes) throws AccessException {
		try {
			TypeConverter typeConverter = context.getTypeConverter();
			Class<?> type = (targetObject instanceof Class ? (Class<?>) targetObject : targetObject.getClass());
			Method[] methods = type.getMethods();

			// If a filter is registered for this type, call it
			MethodFilter filter = (this.filters != null ? this.filters.get(type) : null);
			if (filter != null) {
				List<Method> methodsForFiltering = new ArrayList<Method>();
				for (Method method : methods) {
					methodsForFiltering.add(method);
				}
				List<Method> methodsFiltered = filter.filter(methodsForFiltering);
				if (CubismHelper.isEmpty(methodsFiltered)) {
					methods = NO_METHODS;
				} else {
					methods = methodsFiltered.toArray(new Method[methodsFiltered.size()]);
				}
			}

			Arrays.sort(methods, new Comparator<Method>() {
				public int compare(Method m1, Method m2) {
					int m1pl = m1.getParameterTypes().length;
					int m2pl = m2.getParameterTypes().length;
					return (new Integer(m1pl)).compareTo(m2pl);
				}
			});

			Method closeMatch = null;
			int closeMatchDistance = Integer.MAX_VALUE;
			int[] argsToConvert = null;
			Method matchRequiringConversion = null;
			boolean multipleOptions = false;

			for (Method method : methods) {
				if (method.isBridge()) {
					continue;
				}
				if (method.getName().equals(name)) {
					Class<?>[] paramTypes = method.getParameterTypes();
					List<TypeDescriptor> paramDescriptors = new ArrayList<TypeDescriptor>(paramTypes.length);
					for (int i = 0; i < paramTypes.length; i++) {
						paramDescriptors.add(new TypeDescriptor(new MethodParameter(method, i)));
					}
					ReflectionHelper.ArgumentsMatchInfo matchInfo = null;
					if (method.isVarArgs() && argumentTypes.size() >= (paramTypes.length - 1)) {
						// *sigh* complicated
						matchInfo = ReflectionHelper.compareArgumentsVarargs(paramDescriptors, argumentTypes, typeConverter);
					} else if (paramTypes.length == argumentTypes.size()) {
						// name and parameter number match, check the arguments
						matchInfo = ReflectionHelper.compareArguments(paramDescriptors, argumentTypes, typeConverter);
					}
					if (matchInfo != null) {
						if (matchInfo.kind == ReflectionHelper.ArgsMatchKind.EXACT) {
							return new ReflectiveMethodExecutor(method, null);
						} else if (matchInfo.kind == ReflectionHelper.ArgsMatchKind.CLOSE) {
							if (!useDistance) {
								closeMatch = method;
							} else {
								int matchDistance = ReflectionHelper.getTypeDifferenceWeight(paramDescriptors, argumentTypes);
								if (matchDistance < closeMatchDistance) {
									// this is a better match
									closeMatchDistance = matchDistance;
									closeMatch = method;
								}
							}
						} else if (matchInfo.kind == ReflectionHelper.ArgsMatchKind.REQUIRES_CONVERSION) {
							if (matchRequiringConversion != null) {
								multipleOptions = true;
							}
							argsToConvert = matchInfo.argsRequiringConversion;
							matchRequiringConversion = method;
						}
					}
				}
			}
			if (closeMatch != null) {
				return new ReflectiveMethodExecutor(closeMatch, null);
			} else if (matchRequiringConversion != null) {
				if (multipleOptions) {
					throw new SpelEvaluationException(SpelMessage.MULTIPLE_POSSIBLE_METHODS, name);
				}
				return new ReflectiveMethodExecutor(matchRequiringConversion, argsToConvert);
			} else {
				return null;
			}
		} catch (EvaluationException ex) {
			throw new AccessException("Failed to resolve method", ex);
		}
	}

	public void registerMethodFilter(Class<?> type, MethodFilter filter) {
		if (this.filters == null) {
			this.filters = new HashMap<Class<?>, MethodFilter>();
		}
		if (filter == null) {
			this.filters.remove(type);
		} else {
			this.filters.put(type, filter);
		}
	}
}