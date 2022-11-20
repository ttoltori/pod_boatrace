package com.pengkong.common.pattern.observer;

import java.util.HashSet;
import java.util.Set;

public abstract class ThreadSafeObservable {
	// this is the object we will be synchronizing on ("the monitor")
	private final Object MONITOR = new Object();

	private Set<ThreadSafeObserver> mObservers;

	/**
	 * This method adds a new Observer - it will be notified when Observable changes
	 */
	public void addObserver(ThreadSafeObserver observer) {
		if (observer == null)
			return;

		synchronized (MONITOR) {
			if (mObservers == null) {
				mObservers = new HashSet<>(1);
			}
			if (mObservers.add(observer) && mObservers.size() == 1) {
				performInit(); // some initialization when first observer added
			}
		}
	}

	/**
	 * This method removes an Observer - it will no longer be notified when
	 * Observable changes
	 */
	public void removeObserver(ThreadSafeObserver observer) {
		if (observer == null)
			return;

		synchronized (MONITOR) {
			if (mObservers != null && mObservers.remove(observer) && mObservers.isEmpty()) {
				performCleanup(); // some cleanup when last observer removed
			}
		}
	}

	private void performInit() {
	}

	private void performCleanup() {
	}

	/**
	 * This method notifies currently registered observers about Observable's change
	 */
	protected void notifyObservers() {
		Set<ThreadSafeObserver> observersCopy;

		synchronized (MONITOR) {
			if (mObservers == null)
				return;
			observersCopy = new HashSet<>(mObservers);
		}

		for (ThreadSafeObserver observer : observersCopy) {
			observer.update(this);
		}
	}
}
