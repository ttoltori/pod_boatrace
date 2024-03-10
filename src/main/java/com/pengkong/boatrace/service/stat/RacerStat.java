package com.pengkong.boatrace.service.stat;

import java.util.List;

import com.pengkong.boatrace.common.BoatConst;
import com.pengkong.boatrace.exception.FatalException;

/**
 * 選手毎の統計情報
 * @author qwerty
 *
 */
public class RacerStat extends RacerStatBase {

	public RacerStat() {
	}

	/**
	 * rec_waku_recentの全国・当地の勝率、２連帯率、３連帯率を求める。
	 * @param baseYmd 集計基準日
	 * @param daysBefore 集計基準日の前日から過去daysBeforeまで集計する
	 * @param rateType BoatConst.RATE_TYPE_NATION or BoatConst.RATE_TYPE_LOCAL
	 * @return
	 * @throws FatalException
	 */
	public AverageWiningRate getRecWakuRecentWiningRate(String baseYmd, int daysBefore, int rateType) {
		// 範囲インデックス(startIndex, endIdx)を設定する
		setRangeIndex(baseYmd, daysBefore);
		
		List<Float> listRate1 = null;
		List<Float> listRate2 = null;
		List<Float> listRate3 = null;
		if (rateType == BoatConst.RATE_TYPE_NATION) {
			listRate1 = mapFloatList.get(KEY_NATION_WININGRATE);
			listRate2 = mapFloatList.get(KEY_NATION_2WININGRATE);
			listRate3 = mapFloatList.get(KEY_NATION_3WININGRATE);
		} else { 
			listRate1 = mapFloatList.get(KEY_LOCAL_WININGRATE);
			listRate2 = mapFloatList.get(KEY_LOCAL_2WININGRATE);
			listRate3 = mapFloatList.get(KEY_LOCAL_3WININGRATE);
		} 
		
		float sum1 = 0; // 得点合計
		float sum2 = 0; // ２連帯回数合計
		float sum3 = 0; // 3連帯回数合計
		float count = 0; // 出走回数合計
		for (int i = startIdx; i <= endIdx; i++) {
			sum1 += listRate1.get(i).floatValue();
			sum2 += listRate2.get(i).floatValue();
			sum3 += listRate3.get(i).floatValue();
			count++;
		}
		
		return AverageWiningRate.createInstance(sum1, sum2, sum3, count);
	}
	
	/**
	 * stat_waku_recentの当地勝率、２連帯率、３連帯率を求める。
	 * @param baseYmd 集計基準日
	 * @param daysBefore 集計基準日の前日から過去daysBeforeまで集計する
	 * @return
	 * @throws FatalException
	 */
	public AverageWiningRate getStatWakuRecentLocalWiningRate(String baseYmd, int daysBefore, String jyoCd) {
		// 範囲インデックス(startIndex, endIdx)を設定する
		setRangeIndex(baseYmd, daysBefore);

		List<Float> listJyoCd = mapFloatList.get(KEY_JYOCD);
		List<Float> listPoint = mapFloatList.get(KEY_POINT);
		List<Float> listRank = mapFloatList.get(KEY_RANK);

		float sum1 = 0; // 得点合計
		float sum2 = 0; // ２連帯回数合計
		float sum3 = 0; // 3連帯回数合計
		float count = 0; // 出走回数合計
		float jyoCdFloat = Float.valueOf(jyoCd);
		for (int i = startIdx; i <= endIdx; i++) {
			if (jyoCdFloat == listJyoCd.get(i)) {
				sum1 += listPoint.get(i);
				if (listRank.get(i) <= 2) {
					sum2 ++;
				} 
				if (listRank.get(i) <= 3) {
					sum3 ++;
				}
				count++;
			}
		}
		
		return AverageWiningRate.createInstance(sum1, sum2, sum3, count);
	}
	
	/**
	 * rec_waku_recentの平均ST、平均タイムを求める。
	 * @param baseYmd 集計基準日
	 * @param daysBefore 集計基準日の前日から過去daysBeforeまで集計する
	 * @return
	 */
	public AverageTime getRecWakuRecentTime(String baseYmd, int daysBefore) {
		// 範囲インデックス(startIndex, endIdx)を設定する
		setRangeIndex(baseYmd, daysBefore);
		
		List<Float> listStart = mapFloatList.get(KEY_AVGSTART);
		List<Float> listTime = mapFloatList.get(KEY_AVGTIME);;
		
		float sumStart = 0; // 平均ST合計
		float sumTime = 0; // 平均タイム合計
		int count = 0;
		for (int i = startIdx; i <= endIdx; i++) {
			sumStart += listStart.get(i).floatValue();
			sumTime += listTime.get(i).floatValue();
			count++;
		}
		
		return AverageTime.createInstance(sumStart, sumTime, count);
	}
	
	/**
	 * stat_waku_recentの全国勝率、２連帯率、３連帯率を求める。
	 * @param baseYmd 集計基準日
	 * @param daysBefore 集計基準日の前日から過去daysBeforeまで集計する
	 * @return
	 * @throws FatalException
	 */
	public AverageWiningRate getStatWakuRecentNationWiningRate(String baseYmd, int daysBefore) {
		// 範囲インデックス(startIndex, endIdx)を設定する
		setRangeIndex(baseYmd, daysBefore);
		
		List<Float> listPoint = mapFloatList.get(KEY_POINT);
		List<Float> listRank = mapFloatList.get(KEY_RANK);

		float sum1 = 0; // 得点合計
		float sum2 = 0; // ２連帯回数合計
		float sum3 = 0; // 3連帯回数合計
		float count = 0; // 出走回数合計
		for (int i = startIdx; i <= endIdx; i++) {
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
	 * stat_waku_recentの平均STを求める。
	 * @param baseYmd 集計基準日
	 * @param daysBefore 集計基準日の前日から過去daysBeforeまで集計する
	 * @return
	 */
	public AverageTime getStatWakuRecentTime(String baseYmd, int daysBefore) {
		// 範囲インデックス(startIndex, endIdx)を設定する
		setRangeIndex(baseYmd, daysBefore);
		
		List<Float> listStart = mapFloatList.get(KEY_START);
		
		float sumStart = 0; // 平均ST合計
		int count = 0;
		for (int i = startIdx; i <= endIdx; i++) {
			sumStart += listStart.get(i).floatValue();
			count++;
		}
		
		return AverageTime.createInstance(sumStart, 0, count);
	}
	
	public static void main(String[] args) {
		System.out.println(Float.valueOf("02"));
	}
	
	/**
	 * stat_waku_winの当該枠での選手勝率 ,２連帯率、3連帯率
	 * @param baseYmd 集計基準日
	 * @param daysBefore 集計基準日の前日から過去daysBeforeまで集計する
	 * @param waku 基準枠
	 * @return 選手勝率 ,２連帯率、3連帯率
	 */
	public AverageWiningRate getStatRacerWakuWiningRate(String baseYmd, int daysBefore, Short waku) {
		// 範囲インデックス(startIndex, endIdx)を設定する
		setRangeIndex(baseYmd, daysBefore);
		
		float wakuFloat = waku.floatValue();
		List<Float> listWaku = mapFloatList.get(KEY_WAKU);
		List<Float> listPoint = mapFloatList.get(KEY_POINT);
		List<Float> listRank = mapFloatList.get(KEY_RANK);

		float sum1 = 0; // 得点合計
		float sum2 = 0; // ２連帯回数合計
		float sum3 = 0; // 3連帯回数合計
		float count = 0; // 出走回数合計
		for (int i = startIdx; i <= endIdx; i++) {
			if (wakuFloat == listWaku.get(i)) {
				sum1 += listPoint.get(i);
				if (listRank.get(i) <= 2) {
					sum2 ++;
				} 
				if (listRank.get(i) <= 3) {
					sum3 ++;
				}
				count++;
			}
		}
		
		return AverageWiningRate.createInstance(sum1, sum2, sum3, count);
	}
	
}
