package com.pengkong.boatrace.online.race;

import com.pengkong.boatrace.mybatis.entity.OlRaceExample;
import com.pengkong.boatrace.online.enums.RaceStatus;

/**
 * 投票締切前のレースのキュー
 * @author ttolt
 *
 */
public class DailyTohyoRaceQueue extends AbstractDailyRaceQueue {

	public DailyTohyoRaceQueue(String ymd) {
		super(ymd);
	}

	/** 締切前のレースを取得する */
	@Override
	OlRaceExample createExample() {
		OlRaceExample example = new OlRaceExample();
		example.createCriteria().andYmdEqualTo(ymd).andStatusEqualTo(RaceStatus.BEFORE_SIME.getValue());
		example.setOrderByClause("sime");
		
		return example;
	}
}
