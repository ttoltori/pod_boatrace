package com.pengkong.boatrace.online.odds;

import java.util.List;

import com.pengkong.boatrace.mybatis.entity.OlRace;
import com.pengkong.boatrace.scraping.scheduled.OddsParseResult;

public interface OddsParseRunnerListener {

	/** れーし当たりのリアルタイムオッズ監視結果を受け渡す */
	void notifyOddsParseResult(OlRace race, List<OddsParseResult> result);
	
	/** レース当たりのオッズ監視終了を通知する */
	void notifyOddsParseRunnerComplete(OlRace race);
	
	/** エラー通知 */
	void notifyOddsParseRunnerFailure(OlRace race);
}
