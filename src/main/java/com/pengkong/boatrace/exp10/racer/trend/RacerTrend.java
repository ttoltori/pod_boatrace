package com.pengkong.boatrace.exp10.racer.trend;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.exp10.racer.trend.calculator.WakuAvgStartPointCalculator;
import com.pengkong.boatrace.exp10.racer.trend.calculator.ConditionCalculator;
import com.pengkong.boatrace.exp10.racer.trend.calculator.PointCalculator;
import com.pengkong.boatrace.exp10.racer.trend.calculator.Rank123Calculator;
import com.pengkong.boatrace.exp10.racer.trend.calculator.Rank12Calculator;
import com.pengkong.boatrace.exp10.racer.trend.calculator.WakuPointCalculator;
import com.pengkong.boatrace.exp10.racer.trend.calculator.WakuRank123Calculator;
import com.pengkong.boatrace.exp10.racer.trend.calculator.WakuRank12Calculator;
import com.pengkong.boatrace.mybatis.entity.RecRacer;
import com.pengkong.boatrace.mybatis.entity.RecRacerTrend;
import com.pengkong.common.collection.TreeMapList;

public class RacerTrend {
	/** 登番 */
	public Short entry;
	
	/** 基準日 */
	public String currentYmd;
	
	/** 日付順のレース記録。直近記録保持日数分保持する key = レース日付ymd */
	TreeMapList<RecRacer> mapRecords = new TreeMapList<>();

	public RacerTrend(Short entry, String currentYmd, int daysOfTrend) {
		super();
		this.currentYmd = currentYmd;
		this.entry = entry;
	}
	
	/** 1日分のレコードを追加する */
	public void add(String ymd, RecRacer record) {
		// 事故発生した選手はランクが9になっているため、除外
		if (record.getRank() > 6) {
			return;
		}

		mapRecords.addItem(ymd, record);
	}
	
	public RecRacerTrend createRecRacerTrend() {
		ConditionCalculator conditionCalcutor = new ConditionCalculator();
		PointCalculator pointCalculator = new PointCalculator();
		Rank12Calculator rank12Calculator = new Rank12Calculator();
		Rank123Calculator rank123Calculator = new Rank123Calculator();
		WakuPointCalculator wakuPointCalculator = new WakuPointCalculator();
		WakuRank12Calculator wakuRank12Calculator = new WakuRank12Calculator();
		WakuRank123Calculator wakuRank123Calculator = new WakuRank123Calculator();
		WakuAvgStartPointCalculator avgstCalculator = new WakuAvgStartPointCalculator();
		
		RecRacerTrend trend = new RecRacerTrend();
		trend.setYmd(currentYmd);
		trend.setEntry(entry.shortValue());
		trend.setRuncnt(0);
		
		// 記録を時間順にリスト化する
		List<RecRacer> recs = new ArrayList<>();
		for (String ymd : mapRecords.keySet()) {
			recs.addAll(mapRecords.get(ymd));
		}
		
		// 集計
		for (RecRacer rec : recs) {
			// 出走回数
			trend.setRuncnt(trend.getRuncnt()+1);
			
			// 指数計算
			conditionCalcutor.add(rec);
			pointCalculator.add(rec);
			rank12Calculator.add(rec);
			rank123Calculator.add(rec);
			wakuPointCalculator.add(rec);
			wakuRank12Calculator.add(rec);
			wakuRank123Calculator.add(rec);
			avgstCalculator.add(rec);
		}

		// 結果設定
		trend.setCond(conditionCalcutor.get2ScaledResult());
		trend.setN1point(pointCalculator.get2ScaledResult());
		trend.setN2point(rank12Calculator.get2ScaledResult());
		trend.setN3point(rank123Calculator.get2ScaledResult());
		trend.setN1pointWaku(wakuPointCalculator.get2ScaledResult());
		trend.setN2pointWaku(wakuRank12Calculator.get2ScaledResult());
		trend.setN3pointWaku(wakuRank123Calculator.get2ScaledResult());
		trend.setAvgstartWaku(avgstCalculator.get2ScaledResult());
		
		return trend;
	}
}
