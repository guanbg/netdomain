package com.platform.cubism;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrderComparator implements Comparator<Object> {
	public static OrderComparator INSTANCE = new OrderComparator();

	public int compare(Object o1, Object o2) {
		boolean p1 = (o1 instanceof PriorityOrdered);
		boolean p2 = (o2 instanceof PriorityOrdered);
		if (p1 && !p2) {
			return -1;
		} else if (p2 && !p1) {
			return 1;
		}

		// Direct evaluation instead of Integer.compareTo to avoid unnecessary
		// object creation.
		int i1 = getOrder(o1);
		int i2 = getOrder(o2);
		return (i1 < i2) ? -1 : (i1 > i2) ? 1 : 0;
	}

	protected int getOrder(Object obj) {
		return (obj instanceof Ordered ? ((Ordered) obj).getOrder() : Ordered.LOWEST_PRECEDENCE);
	}

	public static void sort(List<?> list) {
		if (list.size() > 1) {
			Collections.sort(list, INSTANCE);
		}
	}

	public static void sort(Object[] array) {
		if (array.length > 1) {
			Arrays.sort(array, INSTANCE);
		}
	}
}