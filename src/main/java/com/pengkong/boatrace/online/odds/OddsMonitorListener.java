package com.pengkong.boatrace.online.odds;

import java.util.List;

import com.pengkong.boatrace.mybatis.entity.OlRace;

public interface OddsMonitorListener {
	/**
	 * オッズ監視item受け渡し
	 * @param item オッズ監視item
	 */
	void notifyItems(List<OddsMonitorItem> items);
	
	/** レース当たりのオッズ監視終了 */
	void notifyRaceComplete(OlRace race);

	/** 投票締切時間超過 */
	void notifyOutOfTime(OlRace race);
	
	/** オッズ監視全て終了 */
	void notifyMonitorComplete();
	
	/** オッズMonitorエラー通知 */
	void notifyMonitorFailure();
}
