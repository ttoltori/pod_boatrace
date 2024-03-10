package com.pengkong.boatrace.server.util;

import java.util.HashMap;

import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.BoatUtil;

/**
 * race情報からパタン値を取得する
 * @author qwerty
 *
 */
public class Db2PatternConverter {

	private HashMap<String, String> mapPatterns;
	public Db2PatternConverter() {
	}

	public String getPatternValue(String patternName) throws Exception {
		String[] tokenPtn = patternName.split("_");
		StringBuilder sb = new StringBuilder();
		for (String ptn : tokenPtn) {
			String value = mapPatterns.get(ptn);
			if (value == null) {
				throw new Exception("undefined pattern name unit. : " + ptn);
			}
			
			sb.append(value);
		}
		
		return sb.toString();
	}
	
	public void setRaceEx(RaceEx race, DBRecord rec ) {
		mapPatterns.put("jyocd", rec.getString("jyocd"));
		mapPatterns.put("turn", rec.getString("turn"));
		mapPatterns.put("raceno", rec.getString("raceno"));

		mapPatterns.put("raceno", race.getRaceNo());
		mapPatterns.put("racetype", BoatUtil.getRacetypeSimple(race.raceInfo2.raceType));
		mapPatterns.put("alevelcount", String.valueOf(race.raceInfo2.aLevelCount));
		mapPatterns.put("simehh", String.format("%04d", race.raceInfo.sime).substring(0,2));
		mapPatterns.put("femalecount", String.valueOf(race.raceInfo2.femaleCount));
		mapPatterns.put("fixedentrance", race.raceInfo2.fixedEntrance);
		mapPatterns.put("weather", race.raceInfo.weather);
		mapPatterns.put("winddirection", race.raceInfo.windDirection);
		mapPatterns.put("wave", String.valueOf(race.raceInfo.wave));
		
		String level = race.raceInfo2.wakuLevelList;
		mapPatterns.put("level1", level.substring(0,2));
		mapPatterns.put("level2", level.substring(3,5));
		mapPatterns.put("level3", level.substring(6,8));
		mapPatterns.put("level12", level.substring(0,5));
		mapPatterns.put("level13", level.substring(0,2) + level.substring(6,8));
		mapPatterns.put("level123", level.substring(0,8));
		
		String levelrank = race.calculatedInfo.levelRank;
		mapPatterns.put("levelrank1", levelrank.substring(0,1));
		
		String nationrank = race.calculatedInfo.nationWiningRank;
		mapPatterns.put("nationrank1", nationrank.substring(0,1));
		mapPatterns.put("nationrank2", nationrank.substring(1,2));
		mapPatterns.put("nationrank3", nationrank.substring(2,3));
		mapPatterns.put("nationrank12", nationrank.substring(0,2));
		mapPatterns.put("nationrank13", nationrank.substring(0,1) + nationrank.substring(2,3));
		
		mapPatterns.put("nationrate1", String.valueOf((int)race.wakuList.get(0).nationWiningRate));
		mapPatterns.put("nationrate2", String.valueOf((int)race.wakuList.get(1).nationWiningRate));
		mapPatterns.put("nationrate3", String.valueOf((int)race.wakuList.get(2).nationWiningRate));
		
		int count = 0;
		for (int i=0; i < 6; i++) {
			if ("A1".equals(race.wakuList.get(i).level)) {
				count++;
			}
		}
		mapPatterns.put("a1count", String.valueOf(count));
		
		count = 0;
		for (int i=0; i < 6; i++) {
			if ("B2".equals(race.wakuList.get(i).level)) {
				count++;
			}
		}
		mapPatterns.put("b2count", String.valueOf(count));
	}
	
	public static void main(String[] args) {
		System.out.println("A1-A2-A3-A4-A5-A6".substring(0, 11));
		Short sh = 8;
		System.out.println(sh.toString());
	}
}
