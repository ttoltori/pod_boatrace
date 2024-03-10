package com.pengkong.boatrace.online.tohyo;

import com.pengkong.boatrace.mybatis.entity.OlRace;

public interface TohyoRunnerListener {
	/** 投票エラー通知 */
	void notifyTohyoFailure(OlRace race);
	
	/** 投票成功通知 */
	void notifyTohyoSuccess(OlRace race);
	
	/** 投票スキップ通知 */
	void notifyTohyoSkipped(OlRace race);
	
	/** 投票時間超過 */
	void notifyTohyoOutOfTime(OlRace race);
	
	/** Unrecoverableエラーによる投票中止 */
	void notifyTohyoRunnerFailure();
}
