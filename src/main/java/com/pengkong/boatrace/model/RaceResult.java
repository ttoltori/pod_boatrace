package com.pengkong.boatrace.model;

import com.pengkong.boatrace.common.BoatTemplate;

/**
 * 勝式別レース結果
 * 
 * @author qwerty
 *
 */
public class RaceResult {
	/* 結果スタート成績、結果着順はWakuに格納する */
	public String sanrentanNo;
	public int sanrentanPrize;
	public int sanrentanPopular;
	public String sanrenhukuNo;
	public int sanrenhukuPrize;
	public int sanrenhukuPopular;
	public String nirentanNo;
	public int nirentanPrize;
	public int nirentanPopular;
	public String nirenhukuNo;
	public int nirenhukuPrize;
	public int nirenhukuPopular;
	public String tansyoNo;
	public int tansyoPrize;
	public int tansyoPopular; // 20180415
	public String hukusyo1No;
	public int hukusyo1Prize;
	public int hukusyo1Popular; // 20180415
	public String hukusyo2No;
	public int hukusyo2Prize;
	public int hukusyo2Popular; // 20180415
	public String kakuren1No;
	public int kakuren1Prize;
	public int kakuren1Popular; // 20180422
	public String kakuren2No;
	public int kakuren2Prize;
	public int kakuren2Popular; // 20180422
	public String kakuren3No;
	public int kakuren3Prize;
	public int kakuren3Popular; // 20180422
	/** 決まり手 */
	public String kimariTe;

	public String toCsv() {
		StringBuilder sb = new StringBuilder();
		sb.append(sanrentanNo);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(sanrentanPrize);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(sanrentanPopular);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(sanrenhukuNo);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(sanrenhukuPrize);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(sanrenhukuPopular);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(nirentanNo);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(nirentanPrize);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(nirentanPopular);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(nirenhukuNo);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(nirenhukuPrize);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(nirenhukuPopular);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(tansyoNo);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(tansyoPrize);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(tansyoPopular);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(hukusyo1No);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(hukusyo1Prize);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(hukusyo1Popular);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(hukusyo2No);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(hukusyo2Prize);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(hukusyo2Popular);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(kakuren1No);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(kakuren1Prize);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(kakuren1Popular);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(kakuren2No);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(kakuren2Prize);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(kakuren2Popular);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(kakuren3No);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(kakuren3Prize);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(kakuren3Popular);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(kimariTe);

		return sb.toString();
	}
}
