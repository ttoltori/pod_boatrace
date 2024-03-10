package com.pengkong.boatrace.service.manager;

import java.util.Iterator;
import java.util.List;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.converter.Csv2StatMlPtnFiltered;
import com.pengkong.boatrace.converter.RaceEx2Pattern;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.mybatis.entity.StatMlPtnFiltered;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.boatrace.weka.classifiers.BoatClassifierDefault;
import com.pengkong.boatrace.weka.classifiers.manager.ClassifierManager;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.collection.HashMapList;

public class WekaPatternManager {

	private ClassifierManager cfm = new ClassifierManager();
	
	// key = bettype
	private HashMapList<StatMlPtnFiltered> mapBettype = new HashMapList<>();
	
	private RaceEx2Pattern race2Pattern = new RaceEx2Pattern();
	
	private static WekaPatternManager instance;
	static { 
		try {
			instance = new WekaPatternManager();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public WekaPatternManager() throws Exception {
		loadCsv();
		cfm.loadClassifierInfo(PropertyUtil.getInstance().getString("DIRECTORY_WEKAMODELS"), "model_release", "_entry_");
	}
	
	public static WekaPatternManager getInstance() {
		return instance;
	}
	
	/**
	 * 구미방단위의 패턴리스트를 반환한다.
	 * 
	 * @param bettype
	 * @return HashMapList<Kumiban, StatMlPtnFiltered[]>
	 */
	public HashMapList<StatMlPtnFiltered> getPatterns(String bettype, RaceEx race) throws Exception {
		HashMapList<StatMlPtnFiltered> mapResult = new HashMapList<>();
		HashMapList<StatMlPtnFiltered> mapResultTmp = new HashMapList<>();
		
		// 레이스 정보 파싱
		race2Pattern.setRaceEx(race);
		
		// 요청 bettype의 패턴리스트 취득
		List<StatMlPtnFiltered> listPtnByBettype = mapBettype.get(bettype);
		
		// 현재 레이스의 패턴과 일치하는 구미방 셋을 추출한다.
		for (StatMlPtnFiltered ptn : listPtnByBettype) {
			if (ptn.getPatternValue().equals(race2Pattern.getPatternValue(ptn.getPatternName()))) {
				mapResultTmp.addItem(ptn.getKumiban(), ptn);
			}
		}

		Iterator<String> it = mapResultTmp.keySet().iterator();
		// 구미방셋으로부터 classifier의 예측과 일치하는 구미방셋에 대해 소속 패턴들을 추출한다.
		while(it.hasNext()) {
			String kumiban = it.next();
			BoatClassifierDefault classifier = cfm.getClassifierList(bettype, kumiban, BoatUtil.currentYmd()).get(0);
			String predictKumiban = classifier.predictFromHashmap(RaceEx.getWekaInputEntry(race));
			// classifierの予測と一致する
			if (predictKumiban.equals(kumiban)) {
				mapResult.put(kumiban, mapResultTmp.get(kumiban));
			} 
		}
		
		return mapResult;
	}
	
	private void loadCsv() throws Exception {
		String filepath = PropertyUtil.getInstance().getString("DIRECTORY_PROPERTY") + BoatTemplate.CSV_FILE_WEkA_PATTERNS;
		List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF-8");
		StatMlPtnFiltered dto;
		for (String line : lines) {
			dto = Csv2StatMlPtnFiltered.convert(line);
			mapBettype.addItem(dto.getBettype(), dto);
		}
	}
}
