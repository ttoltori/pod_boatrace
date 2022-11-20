package com.pengkong.boatrace.scraping.scheduled;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exception.FatalException;

/**
 * 一日の間に指定時分から一定時間の間に一定間隔でタスクを繰り返し実行する。
 * 注意：1日を超えた場合は想定しない。
 * @author ttolt
 *
 */

public abstract class AbstractScheduledRunner implements Runnable {
	Logger logger = LoggerFactory.getLogger(AbstractScheduledRunner.class);

	/** ダウンロード実行間隔 */
	int intervalSec = 10;
	
	/** 開始時間 */
	DateTime startTime;
	
	/** 終了時間 */
	DateTime endTime;
	
	/** スレッド終了フラグ */
	protected boolean isStop = false;
	
	public AbstractScheduledRunner() {
	}

	@Override
	public void run() {
		while(!isStop) {
			DateTime now = DateTime.now();
			
			// 開始時間未満
			if (now.isBefore(startTime)) {
				try {
					Thread.sleep(intervalSec * 1000);
				} catch (InterruptedException e) {
					isStop = true;
				}
				continue;
			}
			
			// 終了時間未満
			if (now.isBefore(endTime)) {
				long startMil = DateTime.now().getMillis();
				try {
					execute();
				} catch (Exception e) {
					// 予期せぬエラー
					logger.error("Exception occurred.", e);
					break;
				}
				long endMil = DateTime.now().getMillis();

				// パーサー実行時間
				long executionMil = endMil - startMil;
				try {
					Thread.sleep( (intervalSec * 1000) - executionMil );
				} catch (InterruptedException e) {
					isStop = true;
				}
			} else {
				/* 終了時間以降 */
				isStop = true;
			}
		}
		destroy();
	}

	/** スレッド終了処理 */
	private void destroy() {
		startTime = null;
		endTime = null;
		notifyScheduleComplete();
	}
	
	/** 下位継承クラスで必要な後処理を行うためのoverrideメソッド */
	protected abstract void notifyScheduleComplete();
	
	/**
	 * 実行メソッド
	 */
	abstract public void execute() throws FatalException;
	

	/**
	 * 例) (10,15,10, 5) -> 10時15分の10分前から5秒間隔で作業を実行する.
	 * @param hour レース締切のhour
	 * @param minuteレース締切のminute
	 * @param term 実行を続ける時間
	 * @param interval 繰り返し実行の間隔（秒単位）
	 */
	public void setSchedule(int hour, int minute, int termMinutes, int intervalSec) {
		this.intervalSec = intervalSec;
		DateTime today = DateTime.now();
		endTime = new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), hour, minute);
		startTime = endTime.minusMinutes(termMinutes);
		startTime = startTime.minusSeconds(5);
	}
}
