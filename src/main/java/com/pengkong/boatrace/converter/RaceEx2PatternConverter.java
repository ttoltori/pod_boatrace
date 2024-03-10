package com.pengkong.boatrace.converter;

import java.util.HashMap;

import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.util.BoatUtil;

/**
 * race情報からパタン値を取得する
 * @author qwerty
 *
 */
public class RaceEx2PatternConverter {

	private HashMap<String, String> mapPatterns = new HashMap<>();
	public RaceEx2PatternConverter() {
	}

	public String getPatternValue(String patternName) throws Exception {
		String[] tokenPtn = patternName.split("_");
		StringBuilder sb = new StringBuilder();
		for (String ptn : tokenPtn) {
			String value = mapPatterns.get(ptn);
			if (value == null) {
				throw new Exception("undefined pattern name unit. : " + ptn);
			}
			
			sb.append(value); sb.append("_");
		}
		
		
		return sb.substring(0, sb.length()-1);
	}
	
	public void setRaceEx(RaceEx race) {
		mapPatterns.put("jyocd", race.getJyoCd());
		mapPatterns.put("turn", BoatUtil.getSetuNumber(race.setu.turn));
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
		mapPatterns.put("level4", level.substring(9,11));
		
		String levelrank = race.calculatedInfo.levelRank;
		mapPatterns.put("levelrank1", levelrank.substring(0,1));
		mapPatterns.put("levelrank2", levelrank.substring(1,2));
		mapPatterns.put("levelrank3", levelrank.substring(2,3));
		mapPatterns.put("levelrank4", levelrank.substring(3,4));
		
		String nationrank = race.calculatedInfo.nationWiningRank;
		mapPatterns.put("nationrank1", nationrank.substring(0,1));
		mapPatterns.put("nationrank2", nationrank.substring(1,2));
		mapPatterns.put("nationrank3", nationrank.substring(2,3));
		mapPatterns.put("nationrank4", nationrank.substring(3,4));
		
		String nation2rank = race.calculatedInfo.nation2WiningRank;
		mapPatterns.put("nation2rank1", nation2rank.substring(0,1));
		mapPatterns.put("nation2rank2", nation2rank.substring(1,2));
		mapPatterns.put("nation2rank3", nation2rank.substring(2,3));
		mapPatterns.put("nation2rank4", nation2rank.substring(3,4));
		
		String nation3rank = race.calculatedInfo.nation3WiningRank;
		mapPatterns.put("nation3rank1", nation3rank.substring(0,1));
		mapPatterns.put("nation3rank2", nation3rank.substring(1,2));
		mapPatterns.put("nation3rank3", nation3rank.substring(2,3));
		mapPatterns.put("nation3rank4", nation3rank.substring(3,4));
		
		String localrank = race.calculatedInfo.localWiningRank;
		mapPatterns.put("localrank1", localrank.substring(0,1));
		mapPatterns.put("localrank2", localrank.substring(1,2));
		mapPatterns.put("localrank3", localrank.substring(2,3));
		mapPatterns.put("localrank4", localrank.substring(3,4));
		
		String local2rank = race.calculatedInfo.local2WiningRank;
		mapPatterns.put("local2rank1", local2rank.substring(0,1));
		mapPatterns.put("local2rank2", local2rank.substring(1,2));
		mapPatterns.put("local2rank3", local2rank.substring(2,3));
		mapPatterns.put("local2rank4", local2rank.substring(3,4));
		
		String local3rank = race.calculatedInfo.local3WiningRank;
		mapPatterns.put("local3rank1", local3rank.substring(0,1));
		mapPatterns.put("local3rank2", local3rank.substring(1,2));
		mapPatterns.put("local3rank3", local3rank.substring(2,3));
		mapPatterns.put("local3rank4", local3rank.substring(3,4));
		
		String motor2rank = race.calculatedInfo.motor2Rank;
		mapPatterns.put("motor2rank1", motor2rank.substring(0,1));
		mapPatterns.put("motor2rank2", motor2rank.substring(1,2));
		mapPatterns.put("motor2rank3", motor2rank.substring(2,3));
		mapPatterns.put("motor2rank4", motor2rank.substring(3,4));
		
		String motor3rank = race.calculatedInfo.motor3Rank;
		mapPatterns.put("motor3rank1", motor3rank.substring(0,1));
		mapPatterns.put("motor3rank2", motor3rank.substring(1,2));
		mapPatterns.put("motor3rank3", motor3rank.substring(2,3));
		mapPatterns.put("motor3rank4", motor3rank.substring(3,4));

		mapPatterns.put("nationrate1", String.valueOf((int)race.wakuList.get(0).nationWiningRate));
		mapPatterns.put("nationrate2", String.valueOf((int)race.wakuList.get(1).nationWiningRate));
		mapPatterns.put("nationrate3", String.valueOf((int)race.wakuList.get(2).nationWiningRate));
		mapPatterns.put("nationrate4", String.valueOf((int)race.wakuList.get(3).nationWiningRate));
		
		mapPatterns.put("nation2rate1", String.valueOf((int)race.wakuList.get(0).nation2WiningRate));
		mapPatterns.put("nation2rate2", String.valueOf((int)race.wakuList.get(1).nation2WiningRate));
		mapPatterns.put("nation2rate3", String.valueOf((int)race.wakuList.get(2).nation2WiningRate));
		mapPatterns.put("nation2rate4", String.valueOf((int)race.wakuList.get(3).nation2WiningRate));
		
		mapPatterns.put("nation3rate1", String.valueOf((int)race.wakuList.get(0).nation3WiningRate));
		mapPatterns.put("nation3rate2", String.valueOf((int)race.wakuList.get(1).nation3WiningRate));
		mapPatterns.put("nation3rate3", String.valueOf((int)race.wakuList.get(2).nation3WiningRate));
		mapPatterns.put("nation3rate4", String.valueOf((int)race.wakuList.get(3).nation3WiningRate));
		
		mapPatterns.put("localrate1", String.valueOf((int)race.wakuList.get(0).localWiningRate));
		mapPatterns.put("localrate2", String.valueOf((int)race.wakuList.get(1).localWiningRate));
		mapPatterns.put("localrate3", String.valueOf((int)race.wakuList.get(2).localWiningRate));
		mapPatterns.put("localrate4", String.valueOf((int)race.wakuList.get(3).localWiningRate));
		
		mapPatterns.put("local2rate1", String.valueOf((int)race.wakuList.get(0).local2WiningRate));
		mapPatterns.put("local2rate2", String.valueOf((int)race.wakuList.get(1).local2WiningRate));
		mapPatterns.put("local2rate3", String.valueOf((int)race.wakuList.get(2).local2WiningRate));
		mapPatterns.put("local2rate4", String.valueOf((int)race.wakuList.get(3).local2WiningRate));
		
		mapPatterns.put("local3rate1", String.valueOf((int)race.wakuList.get(0).local3WiningRate));
		mapPatterns.put("local3rate2", String.valueOf((int)race.wakuList.get(1).local3WiningRate));
		mapPatterns.put("local3rate3", String.valueOf((int)race.wakuList.get(2).local3WiningRate));
		mapPatterns.put("local3rate4", String.valueOf((int)race.wakuList.get(3).local3WiningRate));
		
		mapPatterns.put("motor2rate1", String.valueOf((int)race.wakuList.get(0).motor2WiningRate));
		mapPatterns.put("motor2rate2", String.valueOf((int)race.wakuList.get(1).motor2WiningRate));
		mapPatterns.put("motor2rate3", String.valueOf((int)race.wakuList.get(2).motor2WiningRate));
		mapPatterns.put("motor2rate4", String.valueOf((int)race.wakuList.get(3).motor2WiningRate));
		
		mapPatterns.put("motor3rate1", String.valueOf((int)race.wakuList.get(0).motor3WiningRate));
		mapPatterns.put("motor3rate2", String.valueOf((int)race.wakuList.get(1).motor3WiningRate));
		mapPatterns.put("motor3rate3", String.valueOf((int)race.wakuList.get(2).motor3WiningRate));
		mapPatterns.put("motor3rate4", String.valueOf((int)race.wakuList.get(3).motor3WiningRate));
		
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
