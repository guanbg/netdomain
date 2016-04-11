package com.platform.cubism.jdbc.tran;

/**
 * 事务隔离级别
 * 
 * @see org.springframework.transaction.TransactionDefinition
 * @see Connection.TRANSACTION_READ_UNCOMMITTED
 * @see Connection.TRANSACTION_READ_COMMITTED
 * @see Connection.TRANSACTION_REPEATABLE_READ
 * @see Connection.TRANSACTION_SERIALIZABLE
 * @author guanbg
 */
public enum Isolation {
	/* 使用数据库默认的事务隔离级别 */
	DEFAULT(-1),

	/*
	 * 这是事务最低的隔离级别，它充许别外一个事务可以看到这个事务未提交的数据 这种隔离级别会产生脏读，不可重复读和幻想读
	 */
	READ_UNCOMMITTED(1),

	/*
	 * 保证一个事务修改的数据提交后才能被另外一个事务读取， 另外一个事务不能读取该事务未提交的数据， 这种事务隔离级别可以避免脏读出现，
	 * 但是可能会出现不可重复读和幻想读
	 */
	READ_COMMITTED(2),

	/*
	 * 这种事务隔离级别可以防止脏读，不可重复读， 但是可能出现幻想读，它除了保证一个事务不能读取另一个事务未提交的数据外，还保证避免了 不可重复读
	 */
	REPEATABLE_READ(4),

	/*
	 * 这是花费最高代价但是最可靠的事务隔离级别， 事务被处理为顺序执行，除了防止脏读，不可重复读外，还避免了幻想读
	 */
	SERIALIZABLE(8);

	private final int value;

	Isolation(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}

	public static Isolation valueOf(int value) {
		switch (value) {
		case 1:
			return READ_UNCOMMITTED;
		case 2:
			return READ_COMMITTED;
		case 4:
			return REPEATABLE_READ;
		case 8:
			return SERIALIZABLE;
		default:
			return DEFAULT;
		}
	}
}