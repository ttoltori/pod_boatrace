package com.pengkong.boatrace.converter;

import java.util.HashMap;

import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.util.BoatUtil;

/**
 * race情報からパタン値を取得する
 * @author qwerty
 *
 */
public class RaceEx2Pattern {

	private String jyocd;
	private String turn;
	private String raceno;
	private String racetype;
	private String alcount;
	private String level;
	private String levelrank;
	private String sime;
	
	private HashMap<String, String> mapPatterns;
	public RaceEx2Pattern() {
	}

	public String getPatternValue(String patternName) {
		return mapPatterns.get(patternName);
	}
	
	public void setRaceEx(RaceEx race) {
		this.jyocd = race.getJyoCd();
		this.turn = BoatUtil.getSetuNumber(race.setu.turn);
		this.raceno = race.getRaceNo();
		this.racetype = BoatUtil.getRacetypeSimple(race.raceInfo2.raceType);
		this.alcount = String.valueOf(race.raceInfo2.aLevelCount);
		this.level = race.raceInfo2.wakuLevelList;
		this.levelrank = race.calculatedInfo.levelRank;
		this.sime = String.format("%04d", race.raceInfo.sime);
		
		mapPatterns = new HashMap<>();
		mapPatterns.put("jyocd_raceno_level12", jyocd_raceno_level12());
		mapPatterns.put("turn_raceno_level123", turn_raceno_level123());
		mapPatterns.put("turn_racetype_level12", turn_racetype_level12());
		mapPatterns.put("turn_alcount_level12", turn_alcount_level12());
		mapPatterns.put("jyocd_alcount_level2", jyocd_alcount_level2());
		mapPatterns.put("turn_alcount_level123", turn_alcount_level123());
		mapPatterns.put("turn_alcount_level2", turn_alcount_level2());
		mapPatterns.put("jyocd_alcount_turn", jyocd_alcount_turn());
		mapPatterns.put("levelrank12345", levelrank12345());
		mapPatterns.put("turn_racetype_alcount_level4", turn_racetype_alcount_level4());
		mapPatterns.put("jyocd_alcount_level1", jyocd_alcount_level1());
		mapPatterns.put("levelrank1234", levelrank1234());
		mapPatterns.put("jyocd_turn_level14", jyocd_turn_level14());
		mapPatterns.put("jyocd_alcount_level12", jyocd_alcount_level12());
		mapPatterns.put("turn_levelrank1234", turn_levelrank1234());
		mapPatterns.put("turn_simehh_level1_levelrank1", turn_simehh_level1_levelrank1());
		mapPatterns.put("turn_alcount_level124", turn_alcount_level124());
		mapPatterns.put("turn_alcount_level1", turn_alcount_level1());
		mapPatterns.put("turn_racetype_level124", turn_racetype_level124());
		mapPatterns.put("turn_raceno_alcount", turn_raceno_alcount());
		mapPatterns.put("turn_raceno_level12", turn_raceno_level12());
		mapPatterns.put("jyocd_raceno_level124", jyocd_raceno_level124());
		mapPatterns.put("turn_alcount_level4", turn_alcount_level4());
	}

	private String jyocd_raceno_level12() {
		return jyocd + raceno + level.substring(0, 5);
	}
	
	private String turn_raceno_level123() {
		return jyocd + raceno + level.substring(0, 8);
	}
	
	private String turn_racetype_level12() {
		return turn + racetype + level.substring(0, 5);
	}
	
	private String turn_alcount_level12() {
		return turn + alcount + level.substring(0, 5);
	}
	
	private String jyocd_alcount_level2() {
		return jyocd + alcount + level.substring(3, 5);
	}
	
	private String turn_alcount_level123() {
		return turn + alcount + level.substring(0, 8);
	}
	
	private String turn_alcount_level2() {
		return turn + alcount + level.substring(3, 5);
	}
	
	private String jyocd_alcount_turn() {
		return jyocd + alcount + turn;
	}
	
	private String levelrank12345() {
		return levelrank.substring(0,5);
	}
	
	private String turn_racetype_alcount_level4() {
		return turn + racetype + alcount + level.substring(9, 11);
	}
	
	private String jyocd_alcount_level1() {
		return jyocd + alcount + level.substring(0, 2);
	}
	
	private String levelrank1234() {
		return levelrank.substring(0,4);
	}
	
	private String jyocd_turn_level14() {
		return jyocd + turn + level.substring(0,2) + level.substring(9,11); 
	}
	
	private String jyocd_alcount_level12() {
		return jyocd + alcount + level.substring(0,5); 
	}
	
	private String turn_levelrank1234() {
		return turn + levelrank.substring(0,4);
	}

	private String turn_simehh_level1_levelrank1() {
		return turn + sime.substring(0,2) + level.substring(0,2) + levelrank.substring(0,1);
	}
	
	private String turn_alcount_level124() {
		return turn + alcount + level.substring(0,5) + level.substring(9,11);  
	}
	
	private String turn_alcount_level1() {
		return turn + alcount + level.substring(0,2);  
	}
	
	private String turn_racetype_level124() {
		return turn + racetype + level.substring(0,5) + level.substring(9,11);
	}
	
	private String turn_raceno_alcount() {
		return turn + raceno + alcount;
	}
	
	private String turn_raceno_level12() {
		return turn + raceno + level.substring(0,5); 
	}
	
	private String jyocd_raceno_level124() {
		return jyocd + raceno + level.substring(0,5) + level.substring(9,11); 
	}
	
	private String turn_alcount_level4() {
		return turn + alcount + level.substring(0,2) + level.substring(9,11); 
	}
	
	public static void main(String[] args) {
		System.out.println("A1-A2-A3-A4-A5-A6".substring(0, 11));
		Short sh = 8;
		System.out.println(sh.toString());
	}
}
