package com.pengkong.common.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * ArrayListをバリューと持つ汎用ハッシュマップ
 * @author sujin_jeong
 * @revision Feb 7, 2014
 * @param <T>
 */
@SuppressWarnings("serial")
public class HashMapList<T> extends HashMap<String, ArrayList<T>> {
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
	
	public List<T> addItemAll(String key, List<T> values) {
		values.stream().forEach(item -> addItem(key, item));
		
		return values;
	}
	
	public Integer getItemCount(String key) {
		ArrayList<T> items = this.get(key);
		if (items == null) {
			return 0;
		} else {
			return items.size();
		}
	}
	
	public int sizeItems() {
		int sizeItems = 0;
		for (ArrayList<T> value : this.values()) {
			sizeItems += value.size();
		}
		
		return sizeItems;
	}
	
	public List<T> getAllItems() {
		List<T> result = new ArrayList<>();
		for (List<T> listItem : this.values()) {
			result.addAll(listItem);
		}
		
		return result;
	}
}
