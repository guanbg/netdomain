package com.platform.cubism.jdbc.tran;

public enum CompletionStatus {
	STATUS_COMMITTED, // 已提交状态
	STATUS_ROLLED_BACK, // 已回滚状态
	STATUS_UNKNOWN;// 已提交或已回滚状态
}