package com.pengkong.boatrace.online.tohyo;

import com.pengkong.boatrace.mybatis.entity.OlRace;

public interface TohyoResultRunnerListener {
	/** 投票結果updateエラー通知 */
	void notifyTohyoResultFailure(OlRace race);
	
	/** 投票結果update成功通知 */
	void notifyTohyoResultSuccess(OlRace race);
	
	/** 投票結果updateスキップ通知 */
	void notifyTohyoResultSkipped(OlRace race);

	/** Unrecoverableエラーによるスレッド終了 */
	void notifyTohyoResultRunnerFailure();
}
