package com.pengkong.pattern.observer;

public interface ThreadSafeObserver {
	void update(ThreadSafeObservable observable);
}
