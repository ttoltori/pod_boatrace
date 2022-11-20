package com.pengkong.boatrace.exp02;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp02.result.RankResultCreator;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.mybatis.client.RankResultMapper;
import com.pengkong.boatrace.mybatis.entity.RankResult;
import com.pengkong.boatrace.mybatis.entity.RankResultExample;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.service.manager.BeforeOddsManager;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.boatrace.weka.automation.DefineFileParser;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.collection.HashMapList;

public class RankResultGenerator {
	Logger logger = LoggerFactory.getLogger(RankResultGenerator.class);

	private DefineFileParser def  = new DefineFileParser();

	/** 결과 생성 개시 일자 */
	private String resultStartYmd;
	
	/** 결과 생성 종료 일자 */
	private String resultEndYmd;
	
	private String defFilepath;

	PropertyUtil prop = PropertyUtil.getInstance();
	
	public RankResultGenerator() {
	}
	
	public void initialize(String defFilepath) throws Exception {
		this.defFilepath = defFilepath;
		def.parseFile(this.defFilepath);
		
		this.resultStartYmd = def.getValue("resultStartYmd");
		this.resultEndYmd = def.getValue("resultEndYmd");;
	}

	public void execute() throws Exception {
		// 설명= 모델번호
		String description = def.getValue("no");
		String resource = prop.getString("target_db");
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		
		// 랭크모델 초기화
		RankClassifierManager rankClsMgr = new RankClassifierManager();
		rankClsMgr.initialize(this.defFilepath);
		
		// 결과생성모듈 reflection
		// 생성자 아규먼트는 없음.
		Constructor<?> c = Class.forName(prop.getString("result_creator")).getConstructor();
		RankResultCreator resultCreator = (RankResultCreator) c.newInstance();
		
		// open session with auto-transaction mode = false
		try(SqlSession session = sqlSessionFactory.openSession(false)) {
			RankResultMapper mapper = session.getMapper(RankResultMapper.class);
			RankResultExample example = new RankResultExample();

			// 기존 번호의 날짜 범위의 데이터를 삭제
			example.clear();
			example.createCriteria().andDescriptionEqualTo(description).andYmdBetween(resultStartYmd, resultEndYmd);
			mapper.deleteByExample(example);
			
			// 결과생성 대상데이터 취득
			List<DBRecord> listData = loadData();
			
			// 결과범위내 데이터를 날짜별로 분류
			HashMapList<DBRecord> mapListYmd = new HashMapList<>();
			for (DBRecord rec : listData) {
				String ymd = rec.getString("ymd");
				mapListYmd.addItem(ymd, rec);
			}
			
			DateTime currDate = BoatUtil.parseYmd(this.resultStartYmd);
			DateTime toDate = BoatUtil.parseYmd(this.resultEndYmd);
			// 결과 생성 날짜범위 루프
			while (currDate.compareTo(toDate) <= 0) {
				int dailyBetCount = 0;
				String ymd = BoatUtil.formatYmd(currDate);
				if (!isValidDay(ymd)) {
					currDate = currDate.plusDays(1);
					continue;
				}
				
				// 해당날짜의 데이터 취득
				List<DBRecord> listRec = mapListYmd.get(ymd);
				if (listRec == null) {
					currDate = currDate.plusDays(1);
					continue;
				}
				
				// 직전옺즈 로딩
				BeforeOddsManager.getInstance().load(ymd);
				
				// 해당날짜의 레이스 데이터 루프
				for (DBRecord dbRec : listRec) {
					
					String pattern = dbRec.getString("pattern");
					
					// 모델 로딩 및 예측랭크1,2,3취득
					RankBoatClassifier cls = rankClsMgr.getClassifier(pattern, ymd);
					if (cls == null) {
						// 패턴에 대한 예측모델이 없다면 레이스를 스킵한다.
						continue;
					}
					
					String predictRank = cls.predictFromHashmap(dbRec);
					
					// 투표결과생성
					List<RankResult> results = resultCreator.create(description, predictRank, dbRec);
					for (RankResult result : results) {
						mapper.insert(result);
						dailyBetCount++;
					}
				}
				session.commit();
				
				logger.debug(ymd + "/" + this.resultEndYmd + " bet count:" + dailyBetCount);
				
				// 하루 증가
				currDate = currDate.plusDays(1);
			}
			logger.debug(resultCreator.getStat());
			
		}
	}

	/** 직전옺즈가 잘못 취득된 날짜들이 있다.
	 * 20191116 옺즈는 나중에 취급하기로 하고 일단 투표는 실행시킨다.  
	 * @param ymd
	 * @return
	 */
	private boolean isValidDay(String ymd) {
/*		int intYmd = Integer.valueOf(ymd);
		
		// 시뮬레이션 결과의 월단위 옺즈통계에 오차를 발생시키므로
		// 20190101-20190109(確定オッズ） 20190117-20190124(オッズ取得間隔変更） 사이는 아예 투표를 하지 않게 하자.
		if ( (intYmd >= 20190101 && intYmd <= 20190109) || 
				(intYmd >= 20190117 && intYmd <= 20190124) ||
				(intYmd >= 20190611 && intYmd <= 20190623) 
				) {
			return false;
		}
	*/	
		return true;
	}
	
	private List<DBRecord> loadData() throws Exception {
		String resource = PropertyUtil.getInstance().getString("target_db");
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		// open session with auto-transaction mode = false
		try (SqlSession session = sqlSessionFactory.openSession(false)) {
			CustomMapper customMapper = session.getMapper(CustomMapper.class);
			String sql = def.getValue("sql_result");
			sql = sql.replace("{fromYmd}", resultStartYmd);
			sql = sql.replace("{toYmd}", resultEndYmd);
			HashMap<String, String> mapParam = new HashMap<>();
			mapParam.put("sql", sql);
			
			// 디비 데이터 일람 취득
			List<DBRecord> results = customMapper.selectSql(mapParam);
			if (results.size() <= 0) {
				throw new Exception("db has no data. sql=" + sql);
			}
			
			return results; 
		}
	}
	
	public static void main(String[] args) {
		// def파일패스, 프로퍼티파일패스
		String defFilepath = args[0];
		String propFilepath = args[1];
//		String defFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace_test/experiment/expr06/2001_rank_nopattern.def";
//		String propFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace_test/experiment/expr06/expr06.properties";
		
		try {
			PropertyUtil.getInstance().addFile(propFilepath);

			RankResultGenerator generator = new RankResultGenerator();
			generator.initialize(defFilepath);
			generator.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
