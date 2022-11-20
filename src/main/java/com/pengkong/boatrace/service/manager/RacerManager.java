package com.pengkong.boatrace.service.manager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.model.Racer;
import com.pengkong.boatrace.mybatis.entity.RecRacer;
import com.pengkong.common.FileUtil;

public class RacerManager {

	private static RacerManager instance = new RacerManager();
	private HashMap<String, HashMap<String, Racer>> mapRacerByType;
	public RacerManager() {
	}
	
	public static RacerManager getInstance() {
		return instance;
	}

	public void initialize() throws Exception {
	}
	
	/**
	 * 統計用にレース単位のrec_racer一覧を追加する
	 * @param listRecRacer
	 */
	public void addRecRacerList(List<RecRacer> listRecRacer) {
		
	}
	
	/**
	 * 直近（設定期間）の全国勝率
	 */
	public BigDecimal getRecentNationWiningRate(String ymd, Short entry) {
		return null;
	}
	
	/**
	 * 直近（設定期間）の全国2連勝率
	 */
	public BigDecimal getRecentNation2WiningRate(String ymd, Short entry) {
		return null;
	}
	
	/**
	 * 直近（設定期間）の全国3連勝率
	 */
	public BigDecimal getRecentNation3WiningRate(String ymd, Short entry) {
		return null;
	}
	
	
}
