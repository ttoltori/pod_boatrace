package com.pengkong.boatrace.exp02;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.server.db.dto.YmdBeforeRangeDbList;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.boatrace.weka.automation.DefineFileParser;
import com.pengkong.common.CommandExecutor;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * 모델 생성기.
 *   1. 디비데이터를 로드하여 패턴별로 격납
 *   2. arff파일 생성
 *   3. model생성
 *    
 * model_home
 *   *.def  (모델번호_*.def ex:215_20181231_730.def
 *   model_arff
 *      *.arff  (모델번호_rank[1,2,3].arff  ex:215_rank1.arff
 *      (모델번호에 대해 랭크1,2,3을 각각 재이용한다. 파일갯수를 억제하자.)
 *   model_release
 *     {pattern}
 *       *.model  (모델번호_pattern_rangeEndYmd_rank[1,2,3].model  ex:215_nopattern_20181231_rank1.model 
 *   model_evaluation
 *     {pattern}
 *     *.eval  (모델번호_pattern_rank[1,2,3].eval  ex:215_20181231_730_rank1.eval)
 *      (모델번호에 대해 랭크1,2,3을 각각 축적한다. 파일갯수를 억제하자.)
 * 
 * @author qwerty
 *
 */

public class RankModelGenerator {
	Logger logger = LoggerFactory.getLogger(RankModelGenerator.class);

	private DefineFileParser def  = new DefineFileParser();
	private PropertyUtil prop = PropertyUtil.getInstance();
	
	public String modelStartYmd;
	public String modelEndYmd;
	public int modelIntervalDays;
	public int modelDataDays;
	public String defFilepath;
	protected int rankCount = 3;
	/** 패턴별 디비 데이터 리스트 */
	private HashMapList<DBRecord> mapPattern = new HashMapList<>();
	
	/** 모델정보 관리자 */
	RankModelManager modelMgr = RankModelManager.getInstance();
	
	/** 패턴 정보 관리자 */
	RankPatternManager patternMgr = RankPatternManager.getInstance();
	
	public RankModelGenerator() {
		super();
	}
	
	public void initialize(String defFilepath) throws Exception {
		// 실험정의파일 로딩
		this.defFilepath = defFilepath;
		def.parseFile(this.defFilepath);
		
		// 결과생성 범위시작,끝의 하루전을 모델생성범위로 한다.
		String resultStartYmd = def.getValue("resultStartYmd");
		String resultEndYmd = def.getValue("resultEndYmd");
		this.modelStartYmd = BoatUtil.daysBeforeYmd(resultStartYmd, 1);
		this.modelEndYmd = BoatUtil.daysBeforeYmd(resultEndYmd, 1);
		//**********************
		this.modelIntervalDays = prop.getInteger("model_interval_days");
		//**********************
		
		this.modelDataDays = Integer.parseInt(def.getValue("model_data_days"));
	}
	
	public void execute() throws Exception {
		// 디비데이터 로딩
		loadDB();
		logger.debug("db load complete.");
		String started = BoatUtil.currentYyyyMMddHHmmSS();
		
		// 패턴 루프 
		YmdBeforeRangeDbList listRange = new YmdBeforeRangeDbList();
		RankArffCreator arff = new RankArffCreator(this.def);
		int patternCount = mapPattern.size();
		int count = 1;
		for (String pattern : mapPattern.keySet()) {
			// 모델번호
			String modelNo = def.getValue("no");
			// arff파일 디렉토리
			String dirArff = prop.getString("dir_model_arff");
			FileUtil.createDirIfNotExist(dirArff);
			// 모델 디텍토리
			String dirModel = prop.getString("dir_model_release") + modelNo + "/" + pattern + "/";
			FileUtil.createDirIfNotExist(dirModel);
			// 모델 평가 디렉토리
			String dirEval = prop.getString("dir_model_evaluation") + modelNo + "/" + pattern + "/";
			FileUtil.createDirIfNotExist(dirEval);
			
			// 배치파일 디렉토리
			String dirBatch = prop.getString("dir_batch");
			
			List<DBRecord> listDbByPattern = mapPattern.get(pattern);

			// 패턴별 디비데이터 재로딩
			listRange.initialize(modelIntervalDays, modelDataDays, modelStartYmd, modelEndYmd, listDbByPattern);
			
			// 생성할 총 모델 갯수
			int rangeModelDays = BoatUtil.daysBetween(modelStartYmd, modelEndYmd);
			int modelCount = 1 + ((rangeModelDays-1) / modelIntervalDays); 
			if (((rangeModelDays-1) % modelIntervalDays) != 0) {
				modelCount++;
			}
			int totalCount = patternCount * modelCount;
			
			// 디비데이트를 모델생성간격으로 루프
			List<DBRecord> listDb;

			//**********************
			// 데이터를 범위로 취할 것인지 누적적으로 취할 것인지의 여부 true:일정범위로 누적하지 않는다. 
			listRange.setUseRangeStartKey(prop.getString("use_rankge_startkey").equals("true"));
			
			while( (listDb = listRange.get()) != null) {
//				if (modelNo.equals("0106") && count < 1199) {
//					count++; continue;
//				}
				
				logger.debug("started:" + started + ", model data :" + listRange);
				// arff, model, evaluation파일명의 공통부분
				String rangeEndYmd = listRange.getLastRangeEndKey();
				String prefixArffFile = modelNo;
				String prefixModelFile = modelNo + "_" + pattern + "_" + rangeEndYmd;
				String prefixEvalFile = modelNo + "_" + pattern;
				
				// arff생성 (랭크1,2,3의 arff파일패스를 반환한다.)
				List<String> listArffpath = arff.create(listDb, dirArff + prefixArffFile);
				
				// 모델생성 배치파일 패스
				String batchFilepath = dirBatch + def.getValue("batch_name");
				
				// 랭크 1,2,3 루프
				for (int i = 1; i <= rankCount; i++) {
					String arffFilepath = listArffpath.get(i-1);
					String modelFilepath = dirModel + prefixModelFile + "_rank" + i + ".model";
					String evalFilepath = dirEval + prefixEvalFile + "_rank" + i + ".eval";
					
					int exitcode = CommandExecutor.execute(batchFilepath, 
							Arrays.asList(arffFilepath, modelFilepath, evalFilepath), 1000);
					if (exitcode != 0) {
						throw new Exception("model creation failed, exit code=" + exitcode);
					}
					
					logger.info(count + "/" + totalCount + ", model:" + modelFilepath);
				}
				count++;
			}
		}
	}
	
	private void loadDB() throws Exception {
		String resource = prop.getString("target_db");
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		// open session with auto-transaction mode = false
		try (SqlSession session = sqlSessionFactory.openSession(false)) {
			CustomMapper customMapper = session.getMapper(CustomMapper.class);
			// 5일치 더 과거데이터에 여유를 둔다.
			String dataStartYmd = BoatUtil.daysBeforeYmd(modelStartYmd, modelDataDays);
			String sql = def.getValue("sql_model");
			sql = sql.replace("{fromYmd}", dataStartYmd);
			sql = sql.replace("{toYmd}", modelEndYmd);
			HashMap<String, String> mapParam = new HashMap<>();
			mapParam.put("sql", sql);
			
			// 디비 데이터 일람 취득
			List<DBRecord> results = customMapper.selectSql(mapParam);
			if (results.size() <= 0) {
				throw new Exception("db has no data. sql=" + sql);
			}
			
			// 패턴 별로 분리해서 격납
			for (DBRecord rec : results) {
				String pattern = rec.getString("pattern");
				if (!isValidPattern(pattern)) {
					continue;
				}
				
				mapPattern.addItem(pattern, rec);
			}
		}
	}
	
	/**
	 * 모델생성대상 패턴인지를 판정한다.
	 * @param pattern
	 */
	private boolean isValidPattern(String pattern) throws Exception{
		String description = this.def.getValue("no");
		String patternName = modelMgr.getPatternName(description);
		
		return patternMgr.isValidForModel(patternName, pattern);
	}
	
	public static void main(String[] args) {
		// def파일패스, 프로퍼티파일패스
		String defFilepath = args[0];
		String propFilepath = args[1];
//		String defFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace_test/experiment/expr02/0090_rank_turn+apos_ranknationrate.def";
//		String propFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace_test/experiment/expr02/exp02.properties";
		
		try {
			PropertyUtil.getInstance().addFile(propFilepath);

			RankModelGenerator generator = new RankModelGenerator();
			generator.initialize(defFilepath);
			generator.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
