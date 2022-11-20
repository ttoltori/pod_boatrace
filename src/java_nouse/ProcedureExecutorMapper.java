package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.ParamGenerateBoatStat;
import com.pengkong.boatrace.mybatis.entity.ParamGeneratePower;
import com.pengkong.boatrace.mybatis.entity.ParamGenerateStat;
import com.pengkong.boatrace.mybatis.entity.ParamGenerateStatLevelRank;

public interface ProcedureExecutorMapper {

	void executeGeneratePower(ParamGeneratePower param);
	
	void executeGenerateStat(ParamGenerateStat param);
	
	void executeStatMakePattern();
	
	void executeGenerateStatLevelRank(ParamGenerateStatLevelRank param);
	
	void executeGenerateBoatStat(ParamGenerateBoatStat param); // 20180909
}