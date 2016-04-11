package com.platform.cubism.jdbc.tran;

import java.io.Serializable;

public class DefaultTransactionDefinition implements TransactionDefinition, Serializable {
	private static final long serialVersionUID = 2523827791262279450L;

	private Propagation propagationBehavior = Propagation.REQUIRED;

	private Isolation isolationLevel = Isolation.DEFAULT;

	private int timeout = -1;

	private boolean readOnly = false;

	public DefaultTransactionDefinition() {
	}

	public DefaultTransactionDefinition(boolean readOnly) {
		setReadOnly(readOnly);
	}

	public DefaultTransactionDefinition(boolean readOnly, int timeout) {
		setReadOnly(readOnly);
		setTimeout(timeout);
	}

	public DefaultTransactionDefinition(boolean readOnly, int timeout, Propagation propagation) {
		setReadOnly(readOnly);
		setTimeout(timeout);
		setPropagationBehavior(propagation);
	}

	public DefaultTransactionDefinition(boolean readOnly, int timeout, Propagation propagation, Isolation isolation) {
		setReadOnly(readOnly);
		setTimeout(timeout);
		setPropagationBehavior(propagation);
		setIsolationLevel(isolation);
	}

	public DefaultTransactionDefinition(TransactionDefinition other) {
		this.propagationBehavior = other.getPropagationBehavior();
		this.isolationLevel = other.getIsolationLevel();
		this.timeout = other.getTimeout();
		this.readOnly = other.isReadOnly();
	}

	public void setPropagationBehavior(Propagation propagationBehavior) {
		if (propagationBehavior == null)
			this.propagationBehavior = Propagation.REQUIRED;
		else
			this.propagationBehavior = propagationBehavior;
	}

	public void setIsolationLevel(Isolation isolationLevel) {
		if (isolationLevel == null)
			this.isolationLevel = Isolation.DEFAULT;
		else
			this.isolationLevel = isolationLevel;
	}

	public void setTimeout(int timeout) {
		if (timeout < -1) {
			this.timeout = -1;
		} else {
			this.timeout = timeout;
		}
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Propagation getPropagationBehavior() {
		return this.propagationBehavior;
	}

	public Isolation getIsolationLevel() {
		return this.isolationLevel;
	}

	public int getTimeout() {
		return this.timeout;
	}

	public boolean isReadOnly() {
		return this.readOnly;
	}
}