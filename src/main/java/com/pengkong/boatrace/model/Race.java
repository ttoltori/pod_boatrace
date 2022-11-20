package com.pengkong.boatrace.model;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.common.BoatTemplate;

/**
 * レース情報
 * @author qwerty
 *
 */
public class Race {
	/**
	 * レース情報
	 */
	public RaceInfo raceInfo = new RaceInfo();
	
	/**
	 * 節情報
	 */
	public Setu setu = new Setu();
	
	/**
	 * 枠別情報一覧
	 */
	public List<Waku> wakuList = new ArrayList<>();
	
	/**
	 * レース結果
	 */
	public RaceResult raceResult = new RaceResult();
	
	/**
	 * 順位算出
	 */
	public CalculatedInfo calculatedInfo = new CalculatedInfo();
	
	/**
	 * コンピュータbetting
	 */
	public ComputerBetting computerBetting = new ComputerBetting();
	
	/**
	 * bettingした結果
	 */
	public Prize prizeObj;
	
	public RaceInfo2 raceInfo2 = new RaceInfo2();
	
	/**
	 * レースの場コードとレース番号を取得する.
	 * @param race
	 * @return
	 */
	public static String getDescription(Race race) {
		return race.setu.jyoCd + "," + race.raceInfo.no;
	}
	
	public String toCsv() {
		StringBuilder sb = new StringBuilder();
		sb.append(raceInfo.toCsv());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(setu.toCsv());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(Waku.toCsv(wakuList));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(raceResult.toCsv());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(calculatedInfo.toCsv());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(computerBetting.toCsv());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(raceInfo2.toCsv());
		return sb.toString();
	}
}
