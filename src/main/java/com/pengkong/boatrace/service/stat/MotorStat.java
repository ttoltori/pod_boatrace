package com.pengkong.boatrace.service.stat;

import java.util.List;

import com.pengkong.boatrace.exception.FatalException;

/**
 * 選手毎の統計情報
 * @author qwerty
 *
 */
public class MotorStat extends MotorStatBase {

	public MotorStat() {
	}

	/**
	 * rec_waku_recentのモータ２連帯率、３連帯率を求める。
	 * @param baseYmd 集計基準日
	 * @param daysBefore 集計基準日の前日から過去daysBeforeまで集計する
	 * @param jyoCd 場コード
	 * @return
	 * @throws FatalException
	 */
	public AverageWiningRate getRecWakuRecentMotorWiningRate(String baseYmd, int daysBefore, String jyoCd) {
		// 範囲インデックス(startIndex, endIdx)を設定する
		setRangeIndex(baseYmd, daysBefore, jyoCd);

		List<Float> listJyoCd = mapFloatList.get(KEY_JYOCD);
		List<Float> listRate2 = mapFloatList.get(KEY_MOTOR_2WININGRATE);
		List<Float> listRate3 = mapFloatList.get(KEY_MOTOR_3WININGRATE);

		float sum2 = 0; // ２連帯回数合計
		float sum3 = 0; // 3連帯回数合計
		float count = 0; // 出走回数合計
		float jyoCdFloat = Float.valueOf(jyoCd);
		
		// 集計データがなにもなければ
		if (listYmd.size() == 0) {
			return AverageWiningRate.createInstance(0, sum2, sum3, count);
		}
		
		for (int i = startIdx; i <= endIdx; i++) {
			if (jyoCdFloat == listJyoCd.get(i)) {
				sum2 += listRate2.get(i).floatValue();
				sum3 += listRate3.get(i).floatValue();
				count++;
			}
		}
		
		return AverageWiningRate.createInstance(0, sum2, sum3, count);
	}
	
	/**
	 * stat_waku_recentのモータ勝率、２連帯率、３連帯率を求める。
	 * @param baseYmd 集計基準日
	 * @param daysBefore 集計基準日の前日から過去daysBeforeまで集計する
	 * @param jyoCd 場コード
	 * @return
	 * @throws FatalException
	 */
	public AverageWiningRate getStatWakuRecentMotorWiningRate(String baseYmd, int daysBefore, String jyoCd) {
		// 範囲インデックス(startIndex, endIdx)を設定する
		setRangeIndex(baseYmd, daysBefore, jyoCd);

		List<Float> listJyoCd = mapFloatList.get(KEY_JYOCD);
		List<Float> listPoint = mapFloatList.get(KEY_POINT);
		List<Float> listRank = mapFloatList.get(KEY_RANK);

		float sum1 = 0; // 得点合計
		float sum2 = 0; // ２連帯回数合計
		float sum3 = 0; // 3連帯回数合計
		float count = 0; // 出走回数合計
		float jyoCdFloat = Float.valueOf(jyoCd);
		
		// 集計データがなにもなければ
		if (listYmd.size() == 0) {
			return AverageWiningRate.createInstance(0, sum2, sum3, count);
		}
		
		for (int i = startIdx; i <= endIdx; i++) {
			if (jyoCdFloat != listJyoCd.get(i))
				continue;
			
			sum1 += listPoint.get(i);
			if (listRank.get(i) <= 2) {
				sum2 ++;
			} 
			if (listRank.get(i) <= 3) {
				sum3 ++;
			}
			count++;
		}
		
		return AverageWiningRate.createInstance(sum1, sum2, sum3, count);
	}
	
	/**
	 * stat_waku_winの当該枠でのモータ勝率 ,２連帯率、3連帯率
	 * @param baseYmd 集計基準日
	 * @param daysBefore 集計基準日の前日から過去daysBeforeまで集計する
	 * @param jyoCd 場コード
	 * @param waku 基準枠
	 * @return 選手勝率 ,２連帯率、3連帯率
	 */
	public AverageWiningRate getStatMotorWakuWiningRate(String baseYmd, int daysBefore, String jyoCd, Short waku) {
		// 範囲インデックス(startIndex, endIdx)を設定する
		setRangeIndex(baseYmd, daysBefore, jyoCd);
		
		float jyoCdFloat = Float.valueOf(jyoCd);
		float wakuFloat = waku.floatValue();
		List<Float> listJyoCd = mapFloatList.get(KEY_JYOCD);
		List<Float> listWaku = mapFloatList.get(KEY_WAKU);
		List<Float> listPoint = mapFloatList.get(KEY_POINT);
		List<Float> listRank = mapFloatList.get(KEY_RANK);

		float sum1 = 0; // 得点合計
		float sum2 = 0; // ２連帯回数合計
		float sum3 = 0; // 3連帯回数合計
		float count = 0; // 出走回数合計
		
		// 集計データがなにもなければ
		if (listYmd.size() == 0) {
			return AverageWiningRate.createInstance(0, sum2, sum3, count);
		}
		
		for (int i = startIdx; i <= endIdx; i++) {
			if (jyoCdFloat != listJyoCd.get(i))
				continue;
			
			if (wakuFloat != listWaku.get(i))
				continue;
			
			sum1 += listPoint.get(i);
			if (listRank.get(i) <= 2) {
				sum2 ++;
			} 
			if (listRank.get(i) <= 3) {
				sum3 ++;
			}
			count++;
		}
		
		return AverageWiningRate.createInstance(sum1, sum2, sum3, count);
	}

}
