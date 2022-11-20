package com.pengkong.boatrace.converter;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.model.comparator.RecFameFameOddsComparator;
import com.pengkong.boatrace.mybatis.entity.RecFame;
import com.pengkong.boatrace.mybatis.entity.RecFameRank3;

public class RecFameList2RecFameRank3 {
	
	public static RecFameRank3 convert(List<RecFame> recFameList) {
		RecFameRank3 result = new RecFameRank3();
		RecFame recFameFirst = recFameList.get(0);
		// 共通項目
		result.setYmd(recFameFirst.getYmd());
		result.setJyocd(recFameFirst.getJyocd());
		result.setRaceno(recFameFirst.getRaceno());

		List<RecFame> recFameList1 = new ArrayList<>();
		List<RecFame> recFameList2 = new ArrayList<>();
		List<RecFame> recFameList3 = new ArrayList<>();
		for (RecFame recFame : recFameList) {
			if (recFame.getWin().equals("1")) {
				recFameList1.add(recFame);
			} else if (recFame.getWin().equals("2")) {
				recFameList2.add(recFame);
			} else if (recFame.getWin().equals("3")) {
				recFameList3.add(recFame);
			}
		}
		 
		// 1着支持率１，２，３
		RecFameFameOddsComparator comparator = new RecFameFameOddsComparator();
		recFameList1.sort(comparator);
		recFameList2.sort(comparator);
		recFameList3.sort(comparator);
		
		result.setWin1Rank1Waku(recFameList1.get(0).getWaku());
		result.setWin1Rank1Entry(recFameList1.get(0).getEntry());
		result.setWin1Rank1Fameodds(recFameList1.get(0).getFameodds());
		result.setWin1Rank1Famerate(recFameList1.get(0).getFamerate());
		result.setWin1Rank2Waku(recFameList1.get(1).getWaku());
		result.setWin1Rank2Entry(recFameList1.get(1).getEntry());
		result.setWin1Rank2Fameodds(recFameList1.get(1).getFameodds());
		result.setWin1Rank2Famerate(recFameList1.get(1).getFamerate());
		result.setWin1Rank3Waku(recFameList1.get(2).getWaku());
		result.setWin1Rank3Entry(recFameList1.get(2).getEntry());
		result.setWin1Rank3Fameodds(recFameList1.get(2).getFameodds());
		result.setWin1Rank3Famerate(recFameList1.get(2).getFamerate());

		result.setWin2Rank1Waku(recFameList2.get(0).getWaku());
		result.setWin2Rank1Entry(recFameList2.get(0).getEntry());
		result.setWin2Rank1Fameodds(recFameList2.get(0).getFameodds());
		result.setWin2Rank1Famerate(recFameList2.get(0).getFamerate());
		result.setWin2Rank2Waku(recFameList2.get(1).getWaku());
		result.setWin2Rank2Entry(recFameList2.get(1).getEntry());
		result.setWin2Rank2Fameodds(recFameList2.get(1).getFameodds());
		result.setWin2Rank2Famerate(recFameList2.get(1).getFamerate());
		result.setWin2Rank3Waku(recFameList2.get(2).getWaku());
		result.setWin2Rank3Entry(recFameList2.get(2).getEntry());
		result.setWin2Rank3Fameodds(recFameList2.get(2).getFameodds());
		result.setWin2Rank3Famerate(recFameList2.get(2).getFamerate());
		
		result.setWin3Rank1Waku(recFameList3.get(0).getWaku());
		result.setWin3Rank1Entry(recFameList3.get(0).getEntry());
		result.setWin3Rank1Fameodds(recFameList3.get(0).getFameodds());
		result.setWin3Rank1Famerate(recFameList3.get(0).getFamerate());
		result.setWin3Rank2Waku(recFameList3.get(1).getWaku());
		result.setWin3Rank2Entry(recFameList3.get(1).getEntry());
		result.setWin3Rank2Fameodds(recFameList3.get(1).getFameodds());
		result.setWin3Rank2Famerate(recFameList3.get(1).getFamerate());
		result.setWin3Rank3Waku(recFameList3.get(2).getWaku());
		result.setWin3Rank3Entry(recFameList3.get(2).getEntry());
		result.setWin3Rank3Fameodds(recFameList3.get(2).getFameodds());
		result.setWin3Rank3Famerate(recFameList3.get(2).getFamerate());

		return result;
	}
}
