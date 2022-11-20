package com.pengkong.boatrace.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.model.comparator.RecOddsOddsComparator;
import com.pengkong.boatrace.mybatis.entity.RecOdds;

public class Csv2RecOdds {
	public static List<RecOdds> convert(String line) {
		RecOddsOddsComparator oddsComparator = new RecOddsOddsComparator();
		
		List<RecOdds> results = new ArrayList<>();
		List<RecOdds> resultsTmp = new ArrayList<>();
		String[] token = line.split(",");
		
		for (int i = 4; i <= token.length-1; i++) {
			RecOdds odds = new RecOdds();
			odds.setYmd(token[0]);
			odds.setJyocd(token[1]);
			odds.setRaceno(Short.parseShort(token[2]));
			odds.setBettype(token[3]);
			String[] keyValue = token[i].split("=");
			odds.setKumiban(keyValue[0]);
			odds.setOdds(new BigDecimal(keyValue[1]));
			resultsTmp.add(odds);
		}

		// sort by odds ascendant
		resultsTmp.sort(oddsComparator);
		BigDecimal oddsMax = resultsTmp.get(resultsTmp.size()-1).getOdds();
		boolean isExistZeroValue = false;
		for (RecOdds odds : resultsTmp) {
			// オッズが0の場合は最大値に置き換える
			if (odds.getOdds().floatValue() == 0.0f) {
				odds.setOdds(oddsMax);
				isExistZeroValue = true;
			} else {
				break;
			}
		}
		
		if (isExistZeroValue) {
			// 再ソート
			resultsTmp.sort(oddsComparator);
		}
		
		Short rank = 1;
		int max = resultsTmp.size();
		
		for (int i = 0; i < max; i++) {
			RecOdds recOdds = resultsTmp.get(i);
			recOdds.setRank(rank++);
			results.add(recOdds);
		}
		
		return results;
	}
	
	public static List<RecOdds> getLimited(List<RecOdds> recOddsList, int max) {
		if (recOddsList.size() < max) {
			max = recOddsList.size();
		}
		List<RecOdds> results = new ArrayList<>();
		for (int i = 0; i < max; i++) {
			results.add(recOddsList.get(i));
		}

		return results;
	}
}
