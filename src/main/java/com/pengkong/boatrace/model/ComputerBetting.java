package com.pengkong.boatrace.model;

import com.pengkong.boatrace.common.BoatTemplate;

/**
 * コンピュータ予測情報
 * @author qwerty
 *
 */
public class ComputerBetting {
	public String predict;
	/**
	 * 自信度の星個数（1~5)
	 */
	public int confidence;
	/**
	 * ベッティングする・しない（する=Y, しない=N)
	 */
	public String betYn = "";
	
	public String toCsv() {
		StringBuilder sb = new StringBuilder();
		sb.append(predict);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(confidence);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(betYn);

		return sb.toString();
	}
}
