package com.platform.cubism.expression.ast;

import java.util.ArrayList;
import java.util.List;

import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.PropertyAccessor;

public class AstUtils {
	public static List<PropertyAccessor> getPropertyAccessorsToTry(Class<?> targetType, ExpressionState state) {
		List<PropertyAccessor> specificAccessors = new ArrayList<PropertyAccessor>();
		List<PropertyAccessor> generalAccessors = new ArrayList<PropertyAccessor>();
		for (PropertyAccessor resolver : state.getPropertyAccessors()) {
			Class<?>[] targets = resolver.getSpecificTargetClasses();
			if (targets == null) { // generic resolver that says it can be used
									// for any type
				generalAccessors.add(resolver);
			} else {
				if (targetType != null) {
					int pos = 0;
					for (Class<?> clazz : targets) {
						if (clazz == targetType) { // put exact matches on the
													// front to be tried first?
							specificAccessors.add(pos++, resolver);
						} else if (clazz.isAssignableFrom(targetType)) { 
							generalAccessors.add(resolver);
						}
					}
				}
			}
		}
		List<PropertyAccessor> resolvers = new ArrayList<PropertyAccessor>();
		resolvers.addAll(specificAccessors);
		resolvers.addAll(generalAccessors);
		return resolvers;
	}
}