package com.platform.cubism.expression.ast;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.platform.cubism.expression.EvaluationException;
import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.ReflectionHelper;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.SpelMessage;
import com.platform.cubism.expression.TypeConverter;
import com.platform.cubism.expression.TypedValue;
import com.platform.cubism.util.ReflectionUtils;
import com.platform.cubism.cvt.TypeDescriptor;
import com.platform.cubism.cvt.MethodParameter;

/**
 * A function reference is of the form "#someFunction(a,b,c)". Functions may be
 * defined in the context prior to the expression being evaluated or within the
 * expression itself using a lambda function definition. For example: Lambda
 * function definition in an expression: "(#max = {|x,y|$x>$y?$x:$y};max(2,3))"
 * Calling context defined function: "#isEven(37)". Functions may also be static
 * java methods, registered in the context prior to invocation of the
 * expression.
 * 
 * <p>
 * Functions are very simplistic, the arguments are not part of the definition
 * (right now), so the names must be unique.
 */
public class FunctionReference extends SpelNodeImpl {
	private final String name;

	public FunctionReference(String functionName, int pos, SpelNodeImpl... arguments) {
		super(pos, arguments);
		name = functionName;
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		TypedValue o = state.lookupVariable(name);
		if (o == null) {
			throw new SpelEvaluationException(getStartPosition(), SpelMessage.FUNCTION_NOT_DEFINED, name);
		}

		// Two possibilities: a lambda function or a Java static method
		// registered as a function
		if (!(o.getValue() instanceof Method)) {
			throw new SpelEvaluationException(SpelMessage.FUNCTION_REFERENCE_CANNOT_BE_INVOKED, name, o.getClass());
		}
		try {
			return executeFunctionJLRMethod(state, (Method) o.getValue());
		} catch (SpelEvaluationException se) {
			se.setPosition(getStartPosition());
			throw se;
		}
	}

	private TypedValue executeFunctionJLRMethod(ExpressionState state, Method method) throws EvaluationException {
		Object[] functionArgs = getArguments(state);

		if (!method.isVarArgs() && method.getParameterTypes().length != functionArgs.length) {
			throw new SpelEvaluationException(SpelMessage.INCORRECT_NUMBER_OF_ARGUMENTS_TO_FUNCTION, functionArgs.length, method.getParameterTypes().length);
		}
		// Only static methods can be called in this way
		if (!Modifier.isStatic(method.getModifiers())) {
			throw new SpelEvaluationException(getStartPosition(), SpelMessage.FUNCTION_MUST_BE_STATIC, method.getDeclaringClass().getName() + "." + method.getName(), name);
		}

		// Convert arguments if necessary and remap them for varargs if required
		if (functionArgs != null) {
			TypeConverter converter = state.getEvaluationContext().getTypeConverter();
			ReflectionHelper.convertAllArguments(converter, functionArgs, method);
		}
		if (method.isVarArgs()) {
			functionArgs = ReflectionHelper.setupArgumentsForVarargsInvocation(method.getParameterTypes(), functionArgs);
		}

		try {
			ReflectionUtils.makeAccessible(method);
			Object result = method.invoke(method.getClass(), functionArgs);
			return new TypedValue(result, new TypeDescriptor(new MethodParameter(method, -1)).narrow(result));
		} catch (Exception ex) {
			throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.EXCEPTION_DURING_FUNCTION_CALL, this.name, ex.getMessage());
		}
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder("#").append(name);
		sb.append("(");
		for (int i = 0; i < getChildCount(); i++) {
			if (i > 0)
				sb.append(",");
			sb.append(getChild(i).toStringAST());
		}
		sb.append(")");
		return sb.toString();
	}

	private Object[] getArguments(ExpressionState state) throws EvaluationException {
		Object[] arguments = new Object[getChildCount()];
		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = children[i].getValueInternal(state).getValue();
		}
		return arguments;
	}
}