package com.platform.cubism.jdbc.tran;

public enum TranSync {
	// 总是启用事物同步，不管是否存在数据库实际事物
	SYNCHRONIZATION_ALWAYS(0),
	// 当事物传播属性为REQUIRED，MANDATORY，REQUIRES_NEW时，即存在实际的数据库事物时才使用事物同步
	SYNCHRONIZATION_ACTUAL(1),
	// 从不使用事物同步
	SYNCHRONIZATION_NEVER(2);

	private final int value;

	TranSync(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}
}
