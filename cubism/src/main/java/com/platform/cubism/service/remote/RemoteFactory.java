package com.platform.cubism.service.remote;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.LoggerFactory;

public abstract class RemoteFactory {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RemoteFactory.class);
	private static ConcurrentLinkedQueue<HttpRemoteExecutor> HttpRemoteQueue = new ConcurrentLinkedQueue<HttpRemoteExecutor>();

	public static RemoteExecutor getHttpExecutor() {
		for (HttpRemoteExecutor hre : HttpRemoteQueue) {
			if (hre.isInvalidate()) {
				HttpRemoteQueue.remove(hre);
				hre = null;
				continue;
			}
		}
		for (HttpRemoteExecutor hre : HttpRemoteQueue) {
			if (hre.iFFreeThenBussy()) {
				return hre;
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("=====>>>当前活动连接数：" + HttpRemoteQueue.size());
		}
		HttpRemoteExecutor executor = new HttpRemoteExecutor();
		HttpRemoteQueue.add(executor);
		return executor;
	}
	
	public static RemoteExecutor getHttpUrlExecutor() {
		return new HttpRemoteExecutor();
	}
	
	public static RemoteExecutor getJmsExecutor() {
		RemoteExecutor executor = null;

		return executor;
	}

	public static RemoteExecutor getSocketExecutor() {
		RemoteExecutor executor = null;

		return executor;
	}
}