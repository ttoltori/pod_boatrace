package com.pengkong.boatrace.converter;

import java.math.BigDecimal;

import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.RaceInfo;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.mybatis.entity.RecRaceWaku;
import com.pengkong.boatrace.service.manager.JyoManager;
import com.pengkong.boatrace.util.BoatUtil;

public class RaceEx2RecRaceWaku {

	public static RecRaceWaku convert(RaceEx race) {
		RecRaceWaku rec = new RecRaceWaku();
		
		RaceInfo ri = race.raceInfo;
		rec.setYmd(ri.ymd);
		rec.setJyocd(race.getJyoCd());
		rec.setRaceno((short)ri.no);
		
		Waku waku1 = race.wakuList.get(0);
		Waku waku2 = race.wakuList.get(1);
		Waku waku3 = race.wakuList.get(2);
		Waku waku4 = race.wakuList.get(3);
		Waku waku5 = race.wakuList.get(4);
		Waku waku6 = race.wakuList.get(5);

		rec.setEntry1(BoatUtil.toShort(waku1.entry));
		rec.setEntry2(BoatUtil.toShort(waku2.entry));
		rec.setEntry3(BoatUtil.toShort(waku3.entry));
		rec.setEntry4(BoatUtil.toShort(waku4.entry));
		rec.setEntry5(BoatUtil.toShort(waku5.entry));
		rec.setEntry6(BoatUtil.toShort(waku6.entry));
		
		rec.setMotorno1(BoatUtil.toShort(waku1.motorNo));
		rec.setMotorno2(BoatUtil.toShort(waku2.motorNo));
		rec.setMotorno3(BoatUtil.toShort(waku3.motorNo));
		rec.setMotorno4(BoatUtil.toShort(waku4.motorNo));
		rec.setMotorno5(BoatUtil.toShort(waku5.motorNo));
		rec.setMotorno6(BoatUtil.toShort(waku6.motorNo));
		
		rec.setAvgtime1(BigDecimal.valueOf(waku1.exhibitTime));
		rec.setAvgtime2(BigDecimal.valueOf(waku2.exhibitTime));
		rec.setAvgtime3(BigDecimal.valueOf(waku3.exhibitTime));
		rec.setAvgtime4(BigDecimal.valueOf(waku4.exhibitTime));
		rec.setAvgtime5(BigDecimal.valueOf(waku5.exhibitTime));
		rec.setAvgtime6(BigDecimal.valueOf(waku6.exhibitTime));
		
		// 平均ST 2018/10/15 追加 
		rec.setAvgst1(BigDecimal.valueOf(waku1.averageStart));
		rec.setAvgst2(BigDecimal.valueOf(waku2.averageStart));
		rec.setAvgst3(BigDecimal.valueOf(waku3.averageStart));
		rec.setAvgst4(BigDecimal.valueOf(waku4.averageStart));
		rec.setAvgst5(BigDecimal.valueOf(waku5.averageStart));
		rec.setAvgst6(BigDecimal.valueOf(waku6.averageStart));
		
		// 節平均ST1 2018/10/23
		rec.setSetuavgst1(BigDecimal.valueOf(Waku.getSetuAverageStart(waku1)));
		rec.setSetuavgst2(BigDecimal.valueOf(Waku.getSetuAverageStart(waku2)));
		rec.setSetuavgst3(BigDecimal.valueOf(Waku.getSetuAverageStart(waku3)));
		rec.setSetuavgst4(BigDecimal.valueOf(Waku.getSetuAverageStart(waku4)));
		rec.setSetuavgst5(BigDecimal.valueOf(Waku.getSetuAverageStart(waku5)));
		rec.setSetuavgst6(BigDecimal.valueOf(Waku.getSetuAverageStart(waku6)));
		
		// 節平均着順 2018/10/23
		rec.setSetuavgwin1(Waku.getSetuAvgWin(waku1));
		rec.setSetuavgwin2(Waku.getSetuAvgWin(waku2));
		rec.setSetuavgwin3(Waku.getSetuAvgWin(waku3));
		rec.setSetuavgwin4(Waku.getSetuAvgWin(waku4));
		rec.setSetuavgwin5(Waku.getSetuAvgWin(waku5));
		rec.setSetuavgwin6(Waku.getSetuAvgWin(waku6));
		
		// F回数＋L回数 20181118
		rec.setFlcount1((short)(waku1.flying + waku1.late));
		rec.setFlcount2((short)(waku2.flying + waku2.late));
		rec.setFlcount3((short)(waku3.flying + waku3.late));
		rec.setFlcount4((short)(waku4.flying + waku4.late));
		rec.setFlcount5((short)(waku5.flying + waku5.late));
		rec.setFlcount6((short)(waku6.flying + waku6.late));
		
		// チルト 20181118
		rec.setTilt1(BigDecimal.valueOf(waku1.tilt));
		rec.setTilt2(BigDecimal.valueOf(waku2.tilt));
		rec.setTilt3(BigDecimal.valueOf(waku3.tilt));
		rec.setTilt4(BigDecimal.valueOf(waku4.tilt));
		rec.setTilt5(BigDecimal.valueOf(waku5.tilt));
		rec.setTilt6(BigDecimal.valueOf(waku6.tilt));
		
		rec.setHomeyn1((short)((race.getJyoCd().equals(JyoManager.getJyocdByBranch(waku1.branch))) ? 1 : 0));
		rec.setHomeyn2((short)((race.getJyoCd().equals(JyoManager.getJyocdByBranch(waku2.branch))) ? 1 : 0));
		rec.setHomeyn3((short)((race.getJyoCd().equals(JyoManager.getJyocdByBranch(waku3.branch))) ? 1 : 0));
		rec.setHomeyn4((short)((race.getJyoCd().equals(JyoManager.getJyocdByBranch(waku4.branch))) ? 1 : 0));
		rec.setHomeyn5((short)((race.getJyoCd().equals(JyoManager.getJyocdByBranch(waku5.branch))) ? 1 : 0));
		rec.setHomeyn6((short)((race.getJyoCd().equals(JyoManager.getJyocdByBranch(waku6.branch))) ? 1 : 0));
		
		return rec;
	}
}
