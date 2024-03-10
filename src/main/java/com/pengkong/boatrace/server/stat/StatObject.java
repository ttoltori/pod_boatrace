package com.pengkong.boatrace.server.stat;

import java.util.Map;
import java.util.TreeMap;

public abstract class StatObject {
	public StatObject parent;
	Map<String, StatObject> childMap = new TreeMap<String, StatObject>();
	
	public String key;

	public StatObject(String key) {
		this.key = key;
	}
	
	public StatObject(StatObject parent, String key) {
		this.parent = parent;
		this.key = key;
	}

	public StatObject getChild(String key) {
		return childMap.get(key);
	}
	
	/**
	 * obj에 대해 depth에서 지정한 하부 단계만큼 아래의 자식객체를 반환한다.
	 * @param obj 대상 객체
	 * @param depth 지정 하부 단계. keys의 길이와 일치해야한다.
	 * @param keys 자식객체를 취득할 하부 단계별 키값 리스트
	 * @return
	 */
	public StatObject getChild(StatObject obj, int depth, String ... keys) {
		if (obj == null) {
			return null;
		}
		
		if (depth <= 1) {
			return obj.getChild(keys[keys.length - depth]);
		}
		
		return getChild(obj.getChild(keys[keys.length - depth]), depth -1, keys); 
	}
	
	
	public StatObject addChild(String key, StatObject child) {
		return childMap.put(key, child);
	}
	
	public Map<String, StatObject> getChildMap() {
		return childMap;
	}
	
	public void addRecord(Object rec) throws Exception {
		addRecord(rec, null);
	}
	
	public void addRecord(Object rec, StatObject child) throws Exception {
		calculate(rec);
		
		if (child != null) {
			childMap.put(child.key, child);
		}
		
		if (parent != null) {
			parent.addRecord(rec, this);
		}
	}
	
	abstract void calculate(Object record) throws Exception;
	
	public String toString() {
		return key;
	}
}
