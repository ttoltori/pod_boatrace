package com.pengkong.boatrace.exp10.remote.server.classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassifierQueue {
	Logger logger = LoggerFactory.getLogger(ClassifierQueue.class);
	
	/** max of queue item */ 
	int max = 30;

	/** item map  ex) key = 1_nopattern_20151231_rank1.model  value=Classifier implementation 
	 *  最初partの1はmodel_noである（exNoでない) */
	Map<String, AbstractBoatClassifier> mapItem = new HashMap<>();
	
	/** item key list */
	List<String> listKey = new ArrayList<>();
	
	public ClassifierQueue(int max) {
		this.max = max;
	}

	/**
	 * queueにclassifierを追加する
	 * @param key filename ex) 1_nopattern_20151231_rank1
	 * @param clf モデルのbinary本体
	 */
	public synchronized void add(String key, AbstractBoatClassifier clf) {
		// maxに達した
		if (listKey.size() >= max) {
			destroyOld();
		}
		
		// itemをmap, listに追加
		mapItem.put(key, clf);
		listKey.add(key);

		logger.debug("item added...queue size=" + listKey.size() + ") item: " + key);
	}

	/**
	 * 現在のアイテム数を取得する。
	 * @return queueのアイテム数
	 */
	public int size() {
		return mapItem.size();
	}
	
	/** リストの最初のitemを除去する */
	void destroyOld() {
		String key = listKey.get(0);
		
		// mapItemから除去
		// Classifier clf = mapItem.get(key);
		mapItem.remove(listKey.get(0));
		
		// listKeyから除去
		listKey.remove(0);

		logger.debug("item removed...queue size=" + listKey.size() + ") item: " + key);
	}
	
	/**
	 * classifierを取得する
	 * @param key filename ex) 00001_nopattern_20151231_rank1
	 * @return
	 */
	public AbstractBoatClassifier get(String key) {
		AbstractBoatClassifier item = mapItem.get(key); 

		return item;
	}
}
