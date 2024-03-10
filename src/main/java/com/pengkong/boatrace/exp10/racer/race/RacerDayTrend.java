package com.pengkong.boatrace.exp10.racer.race;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.mybatis.entity.RecRacerTrend;
import com.pengkong.common.MathUtil;

public class RacerDayTrend {
	/** 基準日 */
	//String currentYmd;
	
	/** 選手番号 */
	Short entry;
	
	List<RecRacerTrend> records = new ArrayList<>();

	public RacerDayTrend(Short entry) {
		this.entry = entry;
	}
	
	public void add(RecRacerTrend rec) {
		records.add(rec);
	}
	
	public RacerWakuTrend getWakuTrend(int wakuIdx) {
		RacerWakuTrend dto = new RacerWakuTrend();
		dto.entry = this.entry;
		dto.waku = (short)(wakuIdx + 1);
		
		// 当日基準指数
		RecRacerTrend last = records.get(records.size()-1);
		dto.runcount = last.getRuncnt();
		dto.cond = last.getCond();
		dto.n1point = last.getN1point();
		dto.n2point = last.getN2point();
		dto.n3point = last.getN3point();
		dto.n1PointWaku = ((double[])last.getN1pointWaku())[wakuIdx];
		dto.n2PointWaku = ((double[])last.getN2pointWaku())[wakuIdx];
		dto.n3PointWaku = ((double[])last.getN3pointWaku())[wakuIdx];
		dto.avgstartWaku = ((double[])last.getAvgstartWaku())[wakuIdx];
		
		// slope計算
		List<Double> listRuncnt = new ArrayList<>();
		List<Double> listCond = new ArrayList<>();
		List<Double> listN1point = new ArrayList<>();
		List<Double> listN2point = new ArrayList<>();
		List<Double> listN3point = new ArrayList<>();
		List<Double> listN1pointWaku = new ArrayList<>();
		List<Double> listN2pointWaku = new ArrayList<>();
		List<Double> listN3pointWaku = new ArrayList<>();
		List<Double> listAvgstartWaku = new ArrayList<>();
		for (RecRacerTrend rec : records) {
			listRuncnt.add(rec.getRuncnt().doubleValue());
			listCond.add(rec.getCond());
			listN1point.add(rec.getN1point());
			listN2point.add(rec.getN2point());
			listN3point.add(rec.getN3point());
			listN1pointWaku.add( ((double[])rec.getN1pointWaku())[wakuIdx] );
			listN2pointWaku.add( ((double[])rec.getN2pointWaku())[wakuIdx] );
			listN3pointWaku.add( ((double[])rec.getN3pointWaku())[wakuIdx] );
			listAvgstartWaku.add( ((double[])rec.getAvgstartWaku())[wakuIdx] );
		}
		
		dto.runCountSlope =  MathUtil.scale3(MathUtil.getRegressionSlope(listRuncnt));
		dto.condSlope = MathUtil.scale3(MathUtil.getRegressionSlope(listCond));
		dto.n1pointSlope = MathUtil.scale3(MathUtil.getRegressionSlope(listN1point));
		dto.n2pointSlope = MathUtil.scale3(MathUtil.getRegressionSlope(listN2point));
		dto.n3pointSlope = MathUtil.scale3(MathUtil.getRegressionSlope(listN3point));
		dto.n1PointWakuSlope = MathUtil.scale3(MathUtil.getRegressionSlope(listN1pointWaku));
		dto.n2PointWakuSlope = MathUtil.scale3(MathUtil.getRegressionSlope(listN2pointWaku));
		dto.n3PointWakuSlope = MathUtil.scale3(MathUtil.getRegressionSlope(listN3pointWaku));
		dto.avgstartWakuSlope = MathUtil.scale3(MathUtil.getRegressionSlope(listAvgstartWaku));
		
		return dto;
	}
}
