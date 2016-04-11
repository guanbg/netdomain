package com.platform.cubism.expression.ast;

public abstract class Operator extends SpelNodeImpl {
	String operatorName;

	public Operator(String payload, int pos, SpelNodeImpl... operands) {
		super(pos, operands);
		this.operatorName = payload;
	}

	public SpelNodeImpl getLeftOperand() {
		return children[0];
	}

	public SpelNodeImpl getRightOperand() {
		return children[1];
	}

	public final String getOperatorName() {
		return operatorName;
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(getChild(0).toStringAST());
		for (int i = 1; i < getChildCount(); i++) {
			sb.append(" ").append(getOperatorName()).append(" ");
			sb.append(getChild(i).toStringAST());
		}
		sb.append(")");
		return sb.toString();
	}
}