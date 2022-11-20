package com.pengkong.boatrace.mybatis.client;

import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.mybatis.entity.ex.RaceKey;
import com.pengkong.boatrace.mybatis.entity.ex.RecRacerEx;

public interface RecRacerExMapper {
	List<RecRacerEx> selectAll(HashMap<String, String> param);
	
	List<RaceKey> selectRaceKeyList(HashMap<String, String> param);
}