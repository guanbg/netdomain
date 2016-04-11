package com.platform.cubism.expression.ast;

import com.platform.cubism.expression.ExpressionState;
import com.platform.cubism.expression.InternalParseException;
import com.platform.cubism.expression.SpelEvaluationException;
import com.platform.cubism.expression.SpelMessage;
import com.platform.cubism.expression.SpelParseException;
import com.platform.cubism.expression.TypedValue;

public abstract class Literal extends SpelNodeImpl {
	protected String literalValue;

	public Literal(String payload, int pos) {
		super(pos);
		this.literalValue = payload;
	}

	public abstract TypedValue getLiteralValue();

	@Override
	public final TypedValue getValueInternal(ExpressionState state) throws SpelEvaluationException {
		return getLiteralValue();
	}

	@Override
	public String toString() {
		return getLiteralValue().getValue().toString();
	}

	@Override
	public String toStringAST() {
		return toString();
	}

	public static Literal getIntLiteral(String numberToken, int pos, int radix) {
		try {
			int value = Integer.parseInt(numberToken, radix);
			return new IntLiteral(numberToken, pos, value);
		} catch (NumberFormatException nfe) {
			throw new InternalParseException(new SpelParseException(pos >> 16, nfe, SpelMessage.NOT_AN_INTEGER, numberToken));
		}
	}

	public static Literal getLongLiteral(String numberToken, int pos, int radix) {
		try {
			long value = Long.parseLong(numberToken, radix);
			return new LongLiteral(numberToken, pos, value);
		} catch (NumberFormatException nfe) {
			throw new InternalParseException(new SpelParseException(pos >> 16, nfe, SpelMessage.NOT_A_LONG, numberToken));
		}
	}

	public static Literal getRealLiteral(String numberToken, int pos, boolean isFloat) {
		try {
			if (isFloat) {
				float value = Float.parseFloat(numberToken);
				return new RealLiteral(numberToken, pos, value);
			} else {
				double value = Double.parseDouble(numberToken);
				return new RealLiteral(numberToken, pos, value);
			}
		} catch (NumberFormatException nfe) {
			throw new InternalParseException(new SpelParseException(pos >> 16, nfe, SpelMessage.NOT_A_REAL, numberToken));
		}
	}
}