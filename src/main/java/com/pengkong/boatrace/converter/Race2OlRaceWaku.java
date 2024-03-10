package com.pengkong.boatrace.converter;

import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.RaceInfo;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.mybatis.entity.OlRaceWaku;
import com.pengkong.boatrace.service.manager.JyoManager;
import com.pengkong.boatrace.util.BoatUtil;

public class Race2OlRaceWaku {

	public static OlRaceWaku convert(Race race) {
		OlRaceWaku rec = new OlRaceWaku();
		
		RaceInfo ri = race.raceInfo;
		rec.setYmd(ri.ymd);
		rec.setJyocd(race.setu.jyoCd);
		rec.setRaceno(ri.no);
		
		Waku waku1 = race.wakuList.get(0);
		Waku waku2 = race.wakuList.get(1);
		Waku waku3 = race.wakuList.get(2);
		Waku waku4 = race.wakuList.get(3);
		Waku waku5 = race.wakuList.get(4);
		Waku waku6 = race.wakuList.get(5);

		rec.setEntry1(BoatUtil.toInt(waku1.entry));
		rec.setEntry2(BoatUtil.toInt(waku2.entry));
		rec.setEntry3(BoatUtil.toInt(waku3.entry));
		rec.setEntry4(BoatUtil.toInt(waku4.entry));
		rec.setEntry5(BoatUtil.toInt(waku5.entry));
		rec.setEntry6(BoatUtil.toInt(waku6.entry));
		
		rec.setMotorno1(BoatUtil.toInt(waku1.motorNo));
		rec.setMotorno2(BoatUtil.toInt(waku2.motorNo));
		rec.setMotorno3(BoatUtil.toInt(waku3.motorNo));
		rec.setMotorno4(BoatUtil.toInt(waku4.motorNo));
		rec.setMotorno5(BoatUtil.toInt(waku5.motorNo));
		rec.setMotorno6(BoatUtil.toInt(waku6.motorNo));
		
		rec.setAvgtime1(Double.valueOf(waku1.exhibitTime));
		rec.setAvgtime2(Double.valueOf(waku2.exhibitTime));
		rec.setAvgtime3(Double.valueOf(waku3.exhibitTime));
		rec.setAvgtime4(Double.valueOf(waku4.exhibitTime));
		rec.setAvgtime5(Double.valueOf(waku5.exhibitTime));
		rec.setAvgtime6(Double.valueOf(waku6.exhibitTime));
		
		// 平均ST 2018/10/15 追加 
		rec.setAvgst1(Double.valueOf(waku1.averageStart));
		rec.setAvgst2(Double.valueOf(waku2.averageStart));
		rec.setAvgst3(Double.valueOf(waku3.averageStart));
		rec.setAvgst4(Double.valueOf(waku4.averageStart));
		rec.setAvgst5(Double.valueOf(waku5.averageStart));
		rec.setAvgst6(Double.valueOf(waku6.averageStart));
		
		// 節平均ST1 2018/10/23
		rec.setSetuavgst1(Double.valueOf(Waku.getSetuAverageStart(waku1)));
		rec.setSetuavgst2(Double.valueOf(Waku.getSetuAverageStart(waku2)));
		rec.setSetuavgst3(Double.valueOf(Waku.getSetuAverageStart(waku3)));
		rec.setSetuavgst4(Double.valueOf(Waku.getSetuAverageStart(waku4)));
		rec.setSetuavgst5(Double.valueOf(Waku.getSetuAverageStart(waku5)));
		rec.setSetuavgst6(Double.valueOf(Waku.getSetuAverageStart(waku6)));
		
		// 節平均着順 2018/10/23
		rec.setSetuavgwin1((int)Waku.getSetuAvgWin(waku1));
		rec.setSetuavgwin2((int)Waku.getSetuAvgWin(waku2));
		rec.setSetuavgwin3((int)Waku.getSetuAvgWin(waku3));
		rec.setSetuavgwin4((int)Waku.getSetuAvgWin(waku4));
		rec.setSetuavgwin5((int)Waku.getSetuAvgWin(waku5));
		rec.setSetuavgwin6((int)Waku.getSetuAvgWin(waku6));
		
		// F回数＋L回数 20181118
		rec.setFlcount1((int)(waku1.flying + waku1.late));
		rec.setFlcount2((int)(waku2.flying + waku2.late));
		rec.setFlcount3((int)(waku3.flying + waku3.late));
		rec.setFlcount4((int)(waku4.flying + waku4.late));
		rec.setFlcount5((int)(waku5.flying + waku5.late));
		rec.setFlcount6((int)(waku6.flying + waku6.late));
		
		// チルト 20181118
		rec.setTilt1(Double.valueOf(waku1.tilt));
		rec.setTilt2(Double.valueOf(waku2.tilt));
		rec.setTilt3(Double.valueOf(waku3.tilt));
		rec.setTilt4(Double.valueOf(waku4.tilt));
		rec.setTilt5(Double.valueOf(waku5.tilt));
		rec.setTilt6(Double.valueOf(waku6.tilt));
		
		rec.setHomeyn1((int)((race.setu.jyoCd.equals(JyoManager.getJyocdByBranch(waku1.branch))) ? 1 : 0));
		rec.setHomeyn2((int)((race.setu.jyoCd.equals(JyoManager.getJyocdByBranch(waku2.branch))) ? 1 : 0));
		rec.setHomeyn3((int)((race.setu.jyoCd.equals(JyoManager.getJyocdByBranch(waku3.branch))) ? 1 : 0));
		rec.setHomeyn4((int)((race.setu.jyoCd.equals(JyoManager.getJyocdByBranch(waku4.branch))) ? 1 : 0));
		rec.setHomeyn5((int)((race.setu.jyoCd.equals(JyoManager.getJyocdByBranch(waku5.branch))) ? 1 : 0));
		rec.setHomeyn6((int)((race.setu.jyoCd.equals(JyoManager.getJyocdByBranch(waku6.branch))) ? 1 : 0));
		
		return rec;
	}
}
