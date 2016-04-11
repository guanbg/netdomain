package com.platform.cubism.jdbc.tran;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.LoggerFactory;

import com.platform.cubism.OrderComparator;
import com.platform.cubism.util.Assert;

public abstract class TransactionSynchronizationManager {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TransactionSynchronizationManager.class);
	private static final ThreadLocal<Map<DataSource, ConnectionHolder>> resources = new ThreadLocal<Map<DataSource, ConnectionHolder>>();
	private static final ThreadLocal<List<TransactionSynchronization>> synchronizations = new ThreadLocal<List<TransactionSynchronization>>();

	public static void release(){
		if(synchronizations != null){
			synchronizations.remove();
		}
		if(resources != null){
			resources.remove();
		}
	}
	
	public static boolean hasResource(Object key) {
		return (getResource(key) != null);
	}

	public static ConnectionHolder getResource(Object key) {
		Map<DataSource, ConnectionHolder> map = resources.get();
		if (map == null) {
			return null;
		}

		ConnectionHolder value = map.get(key);
		// 此处标记，确定类型后回来修改
		return value;
	}

	public static void bindResource(DataSource key, ConnectionHolder value) throws IllegalStateException {
		Assert.notNull(value, "Value must not be null");
		Map<DataSource, ConnectionHolder> map = resources.get();
		if (map == null) {
			map = new HashMap<DataSource, ConnectionHolder>();
			resources.set(map);
		}
		if (map.put(key, value) != null) {
			throw new IllegalStateException("Already value [" + map.get(key) + "] for key [" + key + "] bound to thread [" + Thread.currentThread().getName() + "]");
		}
		logger.trace("Bound value [" + value + "] for key [" + key + "] to thread [" + Thread.currentThread().getName() + "]");
	}

	public static ConnectionHolder unbindResource(DataSource key) throws IllegalStateException {
		Map<DataSource, ConnectionHolder> map = resources.get();
		if (map == null) {
			return null;
		}
		ConnectionHolder value = map.remove(key);
		// Remove entire ThreadLocal if empty...
		if (map.isEmpty()) {
			resources.remove();
		}
		if (value == null) {
			throw new IllegalStateException("No value for key [" + key + "] bound to thread [" + Thread.currentThread().getName() + "]");
		}
		logger.trace("Removed value [" + value + "] for key [" + key + "] from thread [" + Thread.currentThread().getName() + "]");

		return value;

	}

	public static boolean isSynchronizationActive() {
		return (synchronizations.get() != null);
	}

	public static void initSynchronization() throws IllegalStateException {
		if (isSynchronizationActive()) {
			throw new IllegalStateException("Cannot activate transaction synchronization - already active");
		}
		logger.trace("Initializing transaction synchronization");
		synchronizations.set(new LinkedList<TransactionSynchronization>());
	}

	public static void registerSynchronization(TransactionSynchronization synchronization) throws IllegalStateException {
		Assert.notNull(synchronization, "TransactionSynchronization must not be null");
		if (!isSynchronizationActive()) {
			throw new IllegalStateException("Transaction synchronization is not active");
		}
		synchronizations.get().add(synchronization);
	}

	public static List<TransactionSynchronization> getSynchronizations() throws IllegalStateException {
		List<TransactionSynchronization> synchs = synchronizations.get();
		if (synchs == null) {
			throw new IllegalStateException("Transaction synchronization is not active");
		}
		// Return unmodifiable snapshot, to avoid
		// ConcurrentModificationExceptions
		// while iterating and invoking synchronization callbacks that in turn
		// might register further synchronizations.
		if (synchs.isEmpty()) {
			return Collections.emptyList();
		} else {
			// Sort lazily here, not in registerSynchronization.
			OrderComparator.sort(synchs);
			return Collections.unmodifiableList(new ArrayList<TransactionSynchronization>(synchs));
		}
	}

	public static void clearSynchronization() throws IllegalStateException {
		if (!isSynchronizationActive()) {
			throw new IllegalStateException("Cannot deactivate transaction synchronization - not active");
		}
		logger.trace("Clearing transaction synchronization");
		synchronizations.remove();
	}

	public static void clear() {
		clearSynchronization();
	}
}