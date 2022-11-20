package com.pengkong.boatrace.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pengkong.boatrace.mybatis.entity.RecFame;
import com.pengkong.boatrace.mybatis.entity.RecOdds;
import com.pengkong.common.collection.HashMapList;

public class RecOddsList2RecFameList {
	
	public static List<RecFame> convert(String[] entries, List<RecOdds> recOddsList) {
		List<RecFame> results = new ArrayList<>();
		HashMapList<RecOdds> mapListFame = new HashMapList<>();
		double oddsSum = 0;
		
		for (int i = 0; i < recOddsList.size(); i++) {
			String key = null; // key = waku + entry + win(着順)
			RecOdds recOdds = recOddsList.get(i);
			oddsSum += recOdds.getOdds().doubleValue();
			
			String kumiban = recOdds.getKumiban();
			for (int wakuIdx = 1; wakuIdx <= 6; wakuIdx++) {
				String waku = String.valueOf(wakuIdx);
				key = null;
				if (kumiban.substring(0, 1).equals(waku)) {
					// 1着
					key = waku + "_" + entries[wakuIdx - 1] + "_" + "1";
					mapListFame.addItem(key, recOdds);
				} else if (kumiban.substring(1, 2).equals(String.valueOf(wakuIdx))) {
					// 2着
					key = waku + "_" + entries[wakuIdx - 1] + "_" + "2";
					mapListFame.addItem(key, recOdds);
				} else if (kumiban.substring(2, 3).equals(String.valueOf(wakuIdx))) {
					// 3着
					key = waku + "_" + entries[wakuIdx - 1] + "_" + "3";
					mapListFame.addItem(key, recOdds);
				}
			}
		}
		
		for (Map.Entry<String, ArrayList<RecOdds>> entry : mapListFame.entrySet()) {
			String key = entry.getKey(); // key = waku + entry + win(着順)
			String[] token = key.split("_");
			RecFame recFame = new RecFame();
			RecOdds recOdds = entry.getValue().get(0);
			// 共通項目
			recFame.setYmd(recOdds.getYmd());
			recFame.setJyocd(recOdds.getJyocd());
			recFame.setRaceno(recOdds.getRaceno());
			recFame.setWaku(token[0]);
			recFame.setEntry(token[1]);
			recFame.setWin(token[2]);
			
			int cnt = 0;
			double rankWinOdds = 0;
			for (RecOdds odds : entry.getValue()) {
				rankWinOdds += odds.getOdds().doubleValue();
				cnt++;
			}
			
			if (rankWinOdds == 0) {
				continue;
			}
			
			recFame.setFameodds(new BigDecimal(rankWinOdds / cnt).setScale(2,RoundingMode.FLOOR));
			//recFame.setFameodds(decOdds);
			
			double fameRate = 0;
			if (recFame.getWin().equals("1")) {
				 fameRate = 100f - (rankWinOdds / oddsSum * 100f);
			} else if (recFame.getWin().equals("2")) {
				 fameRate = 100f - (rankWinOdds / oddsSum * 100f);
			} else if (recFame.getWin().equals("3")) {
				 fameRate = 100f - (rankWinOdds / oddsSum * 100f);
			}
			recFame.setFamerate(new BigDecimal(fameRate).setScale(2, RoundingMode.FLOOR));
		
			results.add(recFame);
		}
		
		
		return results;
	}
}
