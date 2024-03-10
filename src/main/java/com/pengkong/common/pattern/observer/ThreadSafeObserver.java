package com.pengkong.common.pattern.observer;

public interface ThreadSafeObserver {
	void update(ThreadSafeObservable observable);
}
