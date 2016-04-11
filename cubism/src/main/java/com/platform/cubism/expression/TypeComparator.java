package com.platform.cubism.expression;

public interface TypeComparator {
	int compare(Object firstObject, Object secondObject) throws EvaluationException;

	boolean canCompare(Object firstObject, Object secondObject);
}