package com.pengkong.boatrace.exp02.result;

import java.util.List;

import com.pengkong.boatrace.exception.NullValueException;
import com.pengkong.boatrace.mybatis.entity.RankResult;
import com.pengkong.boatrace.server.db.dto.DBRecord;

public interface RankResultCreator {
	/**
	 * StatMlResult를 생성하는 알고리즘을 제공하는 클래스.
	 * 
	 * @param desc 실행설명 (모델번호)
	 * @param predictRank123 모델이 예측한 랭크1,2,3
	 * @param dbRec 결과를 생성할 대상 레코드
	 * 
	 * @return bettype별로 취득한 StatMlResult들의 리스트
	 * 
	 * @throws NullValueException
	 */
	public List<RankResult> create(String desc, String predictRank123, DBRecord dbRec) throws NullValueException;
	
	public String getStat();
}
