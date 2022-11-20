package com.pengkong.boatrace.exp02;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.DBCol;
import com.pengkong.boatrace.common.RankType;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.server.db.dto.RankDbRecord;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.PropertyUtil;

public class RankPredictor {
	Logger logger = LoggerFactory.getLogger(RankPredictor.class);
	
	/** 모델예측이전에 레이스정보의 패턴이 해당되는 지를 체크하는 패턴관리자 */
	RankFilterManager ptnManager = RankFilterManager.getInstance();
	
	/** 패턴별로 랭크123을 예측하는 RankClassifierManager들의 관리자 */
	RankClassifierCenter clsCenter = new RankClassifierCenterMock();
	
	/** RankClassifierManager의 예측결과에 대해 ranktype별로 와꾸패턴을 추가 체크하는 와꾸패턴관리자 */
	WakuPatternManager wakuManager = WakuPatternManager.getInstance();
	
	private static class InstanceHolder {
		private static final RankPredictor INSTANCE = new RankPredictor();
	}
	
	public static RankPredictor getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	public RankPredictor(){
	}
	
	public void initialize(String rankType){
		try {
			List<RankDbRecord> listRankFilter = loadRankFilterFromDB(rankType);
			ptnManager.initialize(listRankFilter);
			clsCenter.initialize(listRankFilter);
		} catch (Exception e) {
			logger.error("RankPredictor initialization failed!", e);
			System.exit(-1);
		}
	}
	
	/**
	 * 레이스 정보를 바탕으로 베팅리스트를 반환한다.
	 * @param rec 레이스 정보
	 * @return
	 */
	public List<RankDbRecord> createRankFilterList(RankDbRecord raceRec) throws Exception {
		List<RankDbRecord> result = new ArrayList<RankDbRecord>();
		
		// 패턴 매니저를 통해 해당하는 패턴일람을 취득한다.
		List<RankDbRecord> resultsByPatternFiltered = RankFilterManager.getInstance().getRankFilterList(raceRec);
		// 패턴 일람 루프
		for (RankDbRecord recFilter : resultsByPatternFiltered) {
			// 예측치 취득을 위한 키 설정 : description, pattern 
			raceRec.put(DBCol.DESCRIPTION, recFilter.getString(DBCol.DESCRIPTION));
			raceRec.put(DBCol.PATTERN, recFilter.getString(DBCol.PATTERN));
			
			// rank1,2,3 예측치 취득
			String predictRank123 = clsCenter.predict(raceRec);
			// 레이스 정보에 예측랭크123을 저장
			raceRec.put(DBCol.PREDICT_RANK123, predictRank123);
			
			// waku필터링
			if (wakuManager.isValid(predictRank123, recFilter)) {
				result.add(recFilter);
			}
		}
		
		return result;
	}
	
	/**
	 * DB로부터 커스텀RankFilter(RankFilter+부가정보)의 일람을 취득한다.
	 * @return
	 * @throws Exception
	 */
	private List<RankDbRecord> loadRankFilterFromDB(String rankType) throws Exception {
		if (!DatabaseUtil.isOpened()) {
			DatabaseUtil.open(PropertyUtil.getInstance().getString("target_db"), false);
		}
		
		List<RankDbRecord> results;
		CustomMapper customMapper = DatabaseUtil.getSession().getMapper(CustomMapper.class);
		if (rankType.equals(RankType.ALL)) {
			results = customMapper.selectRankFilterAll();	
		} else {
			HashMap<String, String> param = new HashMap<>();
			param.put("rankType", rankType);
			results = customMapper.selectRankFilterByRankType(param);
		}
		
		return results;
	}
	
	public static void main(String[] args) {
		
	}
}
