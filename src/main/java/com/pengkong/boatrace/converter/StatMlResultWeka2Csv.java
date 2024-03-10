package com.pengkong.boatrace.converter;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.mybatis.entity.StatMlResultWeka;

public class StatMlResultWeka2Csv {

	public static String convert(StatMlResultWeka rec) {
		StringBuilder sb = new StringBuilder();
		sb.append(rec.getYmd());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getJyocd());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getRaceno());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getDescription());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getBettype());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getBetKumiban());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getBetOdds());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getBetOddsrank());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getResultKumiban());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getResultOdds());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getResultOddsrank());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getHity());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getHitn());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getBetamt());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getHitamt());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getPatternNameCount());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getPatternValueCount());

		return sb.toString();
	}
}
