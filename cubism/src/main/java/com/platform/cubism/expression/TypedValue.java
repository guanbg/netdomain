package com.platform.cubism.expression;

import com.platform.cubism.cvt.TypeDescriptor;

public class TypedValue {
	public static final TypedValue NULL = new TypedValue(null);
	private final Object value;
	private TypeDescriptor typeDescriptor;

	public TypedValue(Object value) {
		this.value = value;
		this.typeDescriptor = null;
	}

	public TypedValue(Object value, TypeDescriptor typeDescriptor) {
		this.value = value;
		this.typeDescriptor = typeDescriptor;
	}

	public Object getValue() {
		return this.value;
	}

	public TypeDescriptor getTypeDescriptor() {
		if (this.typeDescriptor == null) {
			this.typeDescriptor = TypeDescriptor.forObject(this.value);
		}
		return this.typeDescriptor;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("TypedValue: '").append(this.value).append("' of [").append(getTypeDescriptor() + "]");
		return str.toString();
	}
}