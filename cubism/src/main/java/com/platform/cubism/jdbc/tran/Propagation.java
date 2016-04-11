package com.platform.cubism.jdbc.tran;

/**
 * 事务传播行为
 * 
 * @see org.springframework.transaction.TransactionDefinition
 * @author guanbg
 */
public enum Propagation {
	REQUIRED(0), // 如果存在一个事务，则支持当前事务，如果没有事务则开启一个新的事务
	SUPPORTS(1), // 如果存在一个事务，支持当前事务，如果没有事务，则以非事务的方式执行
	MANDATORY(2), // 如果已经存在一个事务，支持当前事务，如果没有一个活动的事务，则抛出异常
	REQUIRES_NEW(3), // 总是开启一个新的事务，如果一个事务已经存在，则将这个存在的事务挂起
	NOT_SUPPORTED(4), // 总是非事务地执行，并挂起任何存在的事务
	NEVER(5), // 总是非事务地执行，如果存在一个活动事务，则抛出异常
	NESTED(6);// 如果一个活动的事务存在，则运行在一个嵌套的事务中. 如果没有活动事务, 则开启一个新的事务（同REQUIRED属性）

	private final int value;

	Propagation(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}
}