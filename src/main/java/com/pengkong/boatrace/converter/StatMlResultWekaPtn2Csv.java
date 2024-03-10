package com.pengkong.boatrace.converter;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.mybatis.entity.StatMlResultWekaptn;

public class StatMlResultWekaPtn2Csv {

	public static String convert(StatMlResultWekaptn rec) {
		StringBuilder sb = new StringBuilder();
		sb.append(rec.getYmd());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getJyocd());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getRaceno());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getBettype());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getBetKumiban());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getDescription());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getPatternName());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(rec.getPatternValue());

		return sb.toString();
	}
}
