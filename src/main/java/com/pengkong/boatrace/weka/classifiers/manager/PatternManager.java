package com.pengkong.boatrace.weka.classifiers.manager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.pengkong.boatrace.converter.RaceEx2PatternConverter;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.mybatis.client.StPtnFinalMapper;
import com.pengkong.boatrace.mybatis.entity.StPtnFinal;
import com.pengkong.boatrace.mybatis.entity.StPtnFinalExample;
import com.pengkong.common.collection.HashMapList;

public class PatternManager {

	// key = bettype + "_" + kumiban
	private HashMapList<StPtnFinal> mapFinal = new HashMapList<>();
	
	private RaceEx2PatternConverter race2Pattern = new RaceEx2PatternConverter();
	
	private static PatternManager instance;
	static { 
		try {
			instance = new PatternManager();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public PatternManager() throws Exception {
		loadDB();
	}
	
	public static PatternManager getInstance() {
		return instance;
	}
	
	/**
	 * 지정 RaceEx에 대해 구미방단위의 패턴리스트를 반환한다.
	 * 
	 * @param bettype
	 * @return HashMapList<Kumiban, StatMlPtnFiltered[]>
	 */
	public List<StPtnFinal> getStPtnFinalList(String betType, String kumiban, RaceEx race) throws Exception {
		List<StPtnFinal> results = new ArrayList<>();
		
		// 레이스 정보 파싱
		race2Pattern.setRaceEx(race);
		
		// 요청 race의 패턴리스트 취득
		List<StPtnFinal> listStPtnFinal = mapFinal.get(betType + "_" + kumiban);
		
		// 현재 레이스의 패턴과 일치하는 구미방 셋을 추출한다.
		for (StPtnFinal stFinal : listStPtnFinal) {
			if (stFinal.getPatternValue().equals(race2Pattern.getPatternValue(stFinal.getPatternName()))) {
				results.add(stFinal);
			}
		}

		return results;
	}
	
	private void loadDB() throws Exception {
		String resource = "mybatis-config.0.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

		// open session with auto-transaction mode = false
		try (SqlSession session = sqlSessionFactory.openSession(false)) {
			StPtnFinalMapper stPtnFinalMapper = session.getMapper(StPtnFinalMapper.class);
			// final select 조건
			StPtnFinalExample example = new StPtnFinalExample();
			List<StPtnFinal> listStPtnFinal = stPtnFinalMapper.selectByExample(example);
			for (StPtnFinal stFinal : listStPtnFinal) {
				String key = stFinal.getBettype() + "_" + stFinal.getKumiban();
				mapFinal.addItem(key, stFinal);
			}
		}
	}
}
