package com.pengkong.boatrace.converter;

import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.mybatis.entity.RecFame;
import com.pengkong.boatrace.mybatis.entity.RecFameWaku;

public class RecFameList2RecFameWaku {
	
	public static RecFameWaku convert(List<RecFame> recFameList) {
		RecFameWaku result = new RecFameWaku();
		RecFame recFameFirst = recFameList.get(0);
		// 共通項目
		result.setYmd(recFameFirst.getYmd());
		result.setJyocd(recFameFirst.getJyocd());
		result.setRaceno(recFameFirst.getRaceno());
		
		HashMap<String, RecFame> mapRecFame = new HashMap<>();
		String key;
		for (RecFame recFame : recFameList) {
			key = recFame.getWaku() + "_" + recFame.getWin();
			mapRecFame.put(key, recFame);
		}
		
		result.setWaku1Fameodds(mapRecFame.get("1_1").getFameodds());
		result.setWaku1Famerate(mapRecFame.get("1_1").getFamerate());
		result.setWaku2Fameodds(mapRecFame.get("2_1").getFameodds());
		result.setWaku2Famerate(mapRecFame.get("2_1").getFamerate());
		result.setWaku3Fameodds(mapRecFame.get("3_1").getFameodds());
		result.setWaku3Famerate(mapRecFame.get("3_1").getFamerate());
		result.setWaku4Fameodds(mapRecFame.get("4_1").getFameodds());
		result.setWaku4Famerate(mapRecFame.get("4_1").getFamerate());
		result.setWaku5Fameodds(mapRecFame.get("5_1").getFameodds());
		result.setWaku5Famerate(mapRecFame.get("5_1").getFamerate());
		result.setWaku6Fameodds(mapRecFame.get("6_1").getFameodds());
		result.setWaku6Famerate(mapRecFame.get("6_1").getFamerate());
		
		return result;
	}
}
