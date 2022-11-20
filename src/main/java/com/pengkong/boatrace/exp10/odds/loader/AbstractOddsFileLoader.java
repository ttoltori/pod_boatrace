package com.pengkong.boatrace.exp10.odds.loader;

import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.common.collection.HashMapList;

public abstract class AbstractOddsFileLoader {
	/**
	 * 指定Oddsファイルからオッズリストを取得する。 
	 * @param filepath
	 * @param betTypes ex) [1T,2T,3T]  3M等のカスタムbettypeは規定bettypeに変換して渡さないといけない。
	 * @throws Exception
	 */
	abstract public HashMapList<Odds> load(String filepath, String...betTypes) throws Exception;
}
