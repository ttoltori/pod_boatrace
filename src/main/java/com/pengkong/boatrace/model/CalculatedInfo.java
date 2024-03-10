package com.pengkong.boatrace.model;

import com.pengkong.boatrace.common.BoatTemplate;

/**
 * 枠の属性から計算した各種順位情報
 * @author qwerty
 *
 */
public class CalculatedInfo {
	/** 着順枠一覧 */
	public String wakuRank;
	public String levelRank;
	/** 枠別級一覧
	 * 例）A1/B1/B1/B2/A2/A1
	 */
	public String resultLevelRank;
	/** 全国勝率の枠順の順位
	 * 例）134265 
	 */
	public String nationWiningRank;
	public String nation2WiningRank;
	public String nation3WiningRank;
	public String localWiningRank;
	public String local2WiningRank;
	public String local3WiningRank;
	public String motor2Rank;
	public String motor3Rank;
	public String boat2Rank;
	public String boat3Rank;
	public String startExhibitRank;
	public String exhibitRank;
	public String averageStartRank;
	
	public String toCsv() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(wakuRank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(levelRank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(resultLevelRank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(nationWiningRank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(nation2WiningRank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(nation3WiningRank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(localWiningRank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(local2WiningRank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(local3WiningRank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(motor2Rank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(motor3Rank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(boat2Rank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(boat3Rank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(startExhibitRank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(exhibitRank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(averageStartRank);
		
		return sb.toString();
	}
}
