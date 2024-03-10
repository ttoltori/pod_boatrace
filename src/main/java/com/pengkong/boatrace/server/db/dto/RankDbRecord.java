package com.pengkong.boatrace.server.db.dto;

/**
 * 랭크모델기반 투표의 레이스정보 전달 클래스.
 *   시뮬레이션: 레이스 csv -> Race -> DB데이터 포맷
 *   실전: Race -> DB데이터 포맷
 * @author qwerty
 *
 */
public class RankDbRecord extends BoatDbRecord {
	/** 시뮬레이션에서 사용하기 위한 소팅 시간 정보. 초기값은 rec_race.sime이다.*/
	public int sortTime;
	public RankDbRecord() {
		super();
	}
}
