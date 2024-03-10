package com.pengkong.boatrace.online.enums;

/** レース状態 */
public enum RaceStatus {
	/** 締切前 */
	BEFORE_SIME(0),
	
	/** 投票なし */
	TOHYO_SKIPPED(1),
	
	/** 投票完了 */
	TOHYO_COMPLETED(2),
	
	/** 投票結果確認完了 */
	RESULT_COMPLETED(3),
	
	/** 投票時間超過 */
	TOHYO_TIMEOUT(4),
	
	/** 投票エラー */
	TOHYO_ERROR(5),
	
	/** 投票結果確認エラー */
	RESULT_ERROR(6),
	
	/** 投票完了しているが、ml_resultにデータがない */
	RESULT_SKIPPED(7),
	
	/** レース中止 */
	TOHYO_CANCELED(8);
	
	private final int status;

	private RaceStatus(int status) {
		this.status = status;
	}
	
	public int getValue() {
		return this.status;
	}
}
