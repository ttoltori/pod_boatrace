package com.pengkong.boatrace.exp02.simulation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.DBCol;
import com.pengkong.boatrace.common.RankType;
import com.pengkong.boatrace.exp02.RankPredictor;
import com.pengkong.boatrace.exp02.model.RankRace;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.mybatis.client.RunRankResultMapper;
import com.pengkong.boatrace.mybatis.client.RunRankResultPtnMapper;
import com.pengkong.boatrace.mybatis.entity.RunRankResultExample;
import com.pengkong.boatrace.mybatis.entity.RunRankResultPtnExample;
import com.pengkong.boatrace.server.db.dto.RankDbRecord;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.collection.HashMapList;

import lombok.Getter;

public class RankSimulationUser {
	private Logger logger = LoggerFactory.getLogger(RankSimulationUser.class);

	@Getter
	private volatile int balance = 0;
	@Getter
	private volatile int dailyStartBalance = 0;

	@Getter
	protected String todayYmd;

	private List<RankSimulationWorker> workerList = new ArrayList<>();

	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	protected String condition;
	
	PropertyUtil prop = PropertyUtil.getInstance();
	
	/** 시뮬레이션용 전체 레이스 정보를 보관한다. key=ymd, value=DB레코드*/
	protected HashMapList<RankRace> mapRace = new HashMapList<>();
	
	public synchronized void addBalance(int value) {
		this.balance += value;
	}

	public synchronized void subtractBalance(int value) {
		this.balance -= value;
	}

	public RankSimulationUser() throws Exception {
	}

	public void start(String rankType, String fromYmd, String toYmd) {
		condition = "test1"; //rankType + "_" + fromYmd + "_" + toYmd;
		SqlSession session = null;
		try {
			session = DatabaseUtil.open(prop.getString("target_db"), false);
			// 既存DB結果データ削除
			deleteResultDB(session, condition);
			
			// 레이스 정보 취득
			loadRacesFromDB(fromYmd, toYmd);
			
			// 워커 생성 (필요한 경우 복수개 생성해서 사용한다.)
			workerList.add(new RankSimulationWorker(this));

			balance = prop.getInteger("totalStartBalance"); // tohyoManager.depositIfNeeded();
			dailyStartBalance = balance;

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(fromYmd));
			Date currDate = calendar.getTime();
			Date toDate = sdf.parse(toYmd);
			while (currDate.compareTo(toDate) <= 0) {
				todayYmd = sdf.format(currDate);
				dailyStartBalance = balance;
				dailyStartBalance = 0;

				ExecutorService executorService = Executors.newFixedThreadPool(workerList.size());
				for (RankSimulationWorker worker : workerList) {
					executorService.execute(worker);
				}
				executorService.shutdown();
				try {
					executorService.awaitTermination(10, TimeUnit.MINUTES);
				} catch (InterruptedException e) {
					logger.error("worker thread failed!", e);
					executorService.shutdownNow();
				}

				dailyStartBalance = 0;

				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
			}
			
			session.commit();
		} catch (Exception e) {
			logger.error("user failed!!! ", e);
		} finally {
			DatabaseUtil.close(session);
		}
	}

	/**
	 * 기존 결과테이블 내용을 삭제한다.
	 * @param condition
	 */
	private void deleteResultDB(SqlSession session, String condition) {
		RunRankResultMapper mapper = session.getMapper(RunRankResultMapper.class);
		RunRankResultExample example = new RunRankResultExample();
		example.createCriteria().andConditionEqualTo(condition);
		mapper.deleteByExample(example);
		
		RunRankResultPtnMapper mapper2 = session.getMapper(RunRankResultPtnMapper.class);
		RunRankResultPtnExample example2 = new RunRankResultPtnExample();
		example2.createCriteria().andConditionEqualTo(condition);
		mapper2.deleteByExample(example2);
	}
	
	/**
	 * 시뮬레이션용 레이스정보를 로드한다. 
	 * @param fromYmd
	 * @param toYmd
	 * @throws Exception
	 */
	private void loadRacesFromDB(String fromYmd, String toYmd) throws Exception {
		SqlSession session = DatabaseUtil.getSession();
		CustomMapper mapper = session.getMapper(CustomMapper.class);
		HashMap<String, String> param = new HashMap<>();
		param.put("fromYmd", fromYmd);
		param.put("toYmd", toYmd);
		List<RankDbRecord> result = mapper.selectSimulationRaces(param);
		for (RankDbRecord rec : result) {
			mapRace.addItem(rec.getString(DBCol.YMD), new RankRace(rec));
		}
	}
	
	public List<RankRace> getRaces(String ymd) {
		return mapRace.get(ymd);
	}
	
	public static void main(String[] args) {
//		String rankType = args[0];
//		String fromYmd = args[1];
//		String toYmd = args[2];
//		String propFilepath = args[3];
		
		String rankType = RankType.ALL;
		String fromYmd = "20190101";
		String toYmd = "20190630";
		String propFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/rank_user.properties";
		
		try {
			// 설정로딩
			PropertyUtil.getInstance().addFile(propFilepath);
			PropertyUtil.getInstance().putProperty("rankType", rankType);
			PropertyUtil.getInstance().putProperty("simulationFromYmd", fromYmd);
			PropertyUtil.getInstance().putProperty("simulationToYmd", toYmd);
			// 예측엔진 초기화
			RankPredictor.getInstance().initialize(rankType);
			RankSimulationUser user = new RankSimulationUser();
			user.start(rankType, fromYmd, toYmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
