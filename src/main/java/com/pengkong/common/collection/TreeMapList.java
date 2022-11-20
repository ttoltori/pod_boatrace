package com.pengkong.common.collection;

import java.util.ArrayList;
import java.util.TreeMap;

@SuppressWarnings("serial")
public class TreeMapList<T> extends TreeMap<String, ArrayList<T>> {
	/**
	 * ArrayListへItem追加
	 * @param key ArrayListへのキー
	 * @param value <T>
	 * @return <T>
	 */
	public T addItem(String key, T value) {
		ArrayList<T> al;
		if (super.containsKey(key)) {
			al = super.get(key);
			if (al == null) {
				al = new ArrayList<>();
				super.put(key, al);
			}
		} else {
			al = new ArrayList<>();
			super.put(key, al); // JSJ 20140620
		}
		al.add(value);
//JSJ 20140620		super.put(key, al);
		return value;
	}
	
	public Integer getItemCount(String key) {
		ArrayList<T> items = this.get(key);
		if (items == null) {
			return 0;
		} else {
			return items.size();
		}
	}
}
