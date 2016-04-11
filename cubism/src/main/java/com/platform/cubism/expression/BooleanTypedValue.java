package com.platform.cubism.expression;

public class BooleanTypedValue extends TypedValue {
	public static final BooleanTypedValue TRUE = new BooleanTypedValue(true);
	public static final BooleanTypedValue FALSE = new BooleanTypedValue(false);

	private BooleanTypedValue(boolean b) {
		super(b);
	}

	public static BooleanTypedValue forValue(boolean b) {
		if (b) {
			return TRUE;
		} else {
			return FALSE;
		}
	}
}