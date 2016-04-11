package com.platform.cubism.expression;
public interface TypeLocator {

	/**
	 * Find a type by name. The name may or may not be fully qualified (eg. String or java.lang.String)
	 * @param typename the type to be located
	 * @return the class object representing that type
	 * @throws EvaluationException if there is a problem finding it
	 */
	Class<?> findType(String typename) throws EvaluationException;

}
