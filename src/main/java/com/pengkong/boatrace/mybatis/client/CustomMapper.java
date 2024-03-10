package com.pengkong.boatrace.mybatis.client;

import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.mybatis.entity.RecFameRank3;
import com.pengkong.boatrace.mybatis.entity.RecFameRank3Example;
import com.pengkong.boatrace.server.db.dto.BoatDbRecord;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.server.db.dto.RankDbRecord;

public interface CustomMapper {
	
	int insertRecRacerArr(HashMap<String, String> param);
	
	List<DBRecord> selectSql(HashMap<String, String> param);
	
	List<RankDbRecord> selectRankExtMonthly(HashMap<String, String> param);
	
	List<RankDbRecord> selectSimulationRaces(HashMap<String, String> param);
	
	List<RankDbRecord> selectRankFilterAll();

	List<RankDbRecord> selectRankFilterByRankType(HashMap<String, String> param);
	
	List<DBRecord> selectRankClassifierCenter(HashMap<String, String> param);
	
	List<DBRecord> selectRegressionDbList(HashMap<String, String> param);
	
	List<BoatDbRecord> selectResultWithPatterns(HashMap<String, String> param);
	
	List<HashMap<String, ?>> selectFinalizedPtnNames(HashMap<String, String> param);
	
	List<HashMap<String, ?>> select70Step2(HashMap<String, String> param);
	
	List<HashMap<String, ?>> select70(HashMap<String, String> param);
	
	List<HashMap<String, ?>> select66_RaceType(HashMap<String, String> param);
	
	List<HashMap<String, ?>> select66(HashMap<String, String> param);
	
	List<HashMap<String, ?>> select65_RaceType(HashMap<String, String> param);
	
	List<HashMap<String, ?>> select65(HashMap<String, String> param);
	
	List<HashMap<String, ?>> select59(HashMap<String, String> param);
	
	List<HashMap<String, ?>> select56(HashMap<String, String> param);
	
	List<HashMap<String, ?>> select55(HashMap<String, String> param);
	
	List<HashMap<String, ?>> select54(HashMap<String, String> param);
	
	List<HashMap<String, ?>> select52(HashMap<String, String> param);
	
	List<HashMap<String, ?>> select50(HashMap<String, String> param);
	
	List<HashMap<String, ?>> select49(HashMap<String, String> param);
	/**
	 * 機械学習投票４７番用の投票条件データをDBから種痘する
	 * @param ymd
	 * @return
	 */
	
	List<HashMap<String, ?>> select47(String ymd);
	/**
	 * Ntile投票シミュレーション用の投票条件データをDBから種痘する
	 * @param ymd
	 * @return
	 */
	List<HashMap<String, ?>> selectNtile(String ymd);
	
	/**
	 * 機械学習投票４３番用の投票条件データをDBから種痘する
	 * @param ymd
	 * @return
	 */
	List<HashMap<String, ?>> select43(String ymd);

//	/**
//	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_fame_rank3
//	 * @mbg.generated
//	 */
//	int insert(RecFameRank3 record);
//
//	/**
//	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_fame_rank3
//	 * @mbg.generated
//	 */
//	int insertSelective(RecFameRank3 record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_fame_rank3
	 * @mbg.generated
	 */
	List<RecFameRank3> selectByExample(RecFameRank3Example example);
}