package com.pengkong.boatrace.online.race;

import java.util.Arrays;

import com.pengkong.boatrace.mybatis.entity.OlRaceExample;
import com.pengkong.boatrace.online.enums.RaceStatus;

/**
 * 投票結果確認の必要があり得るレースのキュー
 * 
 * @author ttolt
 *
 */
public class DailyResultRaceQueue extends AbstractDailyRaceQueue {

	public DailyResultRaceQueue(String ymd) {
		super(ymd);
	}

	/** 締切前のレースを取得する */
	@Override
	OlRaceExample createExample() {
		OlRaceExample example = new OlRaceExample();
		example.createCriteria().andYmdEqualTo(ymd)
				.andStatusIn(Arrays.asList(RaceStatus.BEFORE_SIME.getValue(), 
								RaceStatus.TOHYO_COMPLETED.getValue()));
		example.setOrderByClause("sime");
		return example;
	}
}
