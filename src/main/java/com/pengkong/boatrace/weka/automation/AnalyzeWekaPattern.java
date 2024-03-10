package com.pengkong.boatrace.weka.automation;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.mybatis.client.ProcedureExecutorMapper;
import com.pengkong.boatrace.weka.automation.prop.BettypePattern;
import com.pengkong.boatrace.weka.automation.prop.Condition;
import com.pengkong.boatrace.weka.automation.prop.FinalizeCondition;
import com.pengkong.boatrace.weka.automation.prop.KumibanPattern;
import com.pengkong.boatrace.weka.automation.prop.PatternComposition;

public class AnalyzeWekaPattern extends AnalyzeWekaPatternBase {

	Logger logger = LoggerFactory.getLogger(AnalyzeWekaPattern.class);

	public final String TYPE_ALL = "all";
	public final String TYPE_KUMIBAN = "kumiban";
	private String dbNo;
	
	public AnalyzeWekaPattern(String propertyFilepath, String dbNo) throws Exception {
		super(propertyFilepath);
		this.dbNo = dbNo;
	}
	
	/**
	 * 프로퍼티 파일에 정의되어 있는 모든 승식,구미방에 대해 패턴 및 기본분석을 출력한다.
	 * 
	 * @throws Exception
	 */
	public void execute() throws Exception {
		for (BettypePattern bettypePattern : listBettypePattern) {
			for (KumibanPattern kumibanPattern : bettypePattern.listKumibanPattern) {
				execute(kumibanPattern);
			}
		}
	}

	public void execute(String betType, String kumiban) throws Exception {
		BettypePattern bettypePattern = getBettypePattern(betType);
		KumibanPattern kumibanPattern = bettypePattern.getKumibanPattern(kumiban);
		execute(kumibanPattern);
	}

	/**
	 * 지정한 승식, 구미방에 대해 패턴 및 기본분석을 출력한다.
	 * 
	 * @param statBettype
	 *            승식
	 * @param prediction
	 *            구미방
	 * @throws Exception
	 */
	public void execute(KumibanPattern kumibanPattern) throws Exception {
		String resource = "mybatis-config." + dbNo + ".xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

		try (SqlSession session = sqlSessionFactory.openSession(false)) {
			ProcedureExecutorMapper procedureExecutorMapper = session.getMapper(ProcedureExecutorMapper.class);
			CustomMapper customMapper = session.getMapper(CustomMapper.class);
			
			// analyze할 패턴명을 stat_ml_ptn_final로부터 구한다.
			//List<PatternComposition> listPtnComp = kumibanPattern.getPatternCompositionList(minDepth, maxDepth);
			List<FinalizeCondition> listFinalizeCondition = kumibanPattern.getFinalizeConditions();
			int finalizeCondCnt = 0;
			for (FinalizeCondition finalizeCondition : listFinalizeCondition) {
				
				// 組番単位の統計テーブルを生成する（finalize_weka_pattern.sql）
				HashMap<String, Object> procParamFinal = createParamFinalizeWekaPattern(kumibanPattern, finalizeCondition);
				procedureExecutorMapper.executeFinalizeWekaPattern(procParamFinal);
				session.commit();
				finalizeCondCnt++;
				
				List<Condition> listCondition = kumibanPattern.listCondition;
				HashMap<String, Object> procParam;
				int condcnt = 1;
				for (Condition cond : listCondition) {
					int cnt = 1;
					procParam = createDefaultParamAnalyzeKumibanPattern(kumibanPattern, finalizeCondition);
					// analyze에 적용할 추가조건 한개를 설정한다. ANZ.COND.
					procParam.put(cond.name, cond.value);
					
					// analyze할 패턴명을 stat_ml_ptn_final로부터 구한다.
					// 반드시 finalize_weka_pattern.sql이 먼저 실행되어있어야 한다.
					HashMap<String, String> mapParam = new HashMap<>();
					mapParam.put("description", propUtil.getString("DESCRIPTION." + kumibanPattern.betType));
					mapParam.put("betType", kumibanPattern.betType);
					mapParam.put("kumiban", kumibanPattern.kumiban);
					List<HashMap<String, ?>> listFinalizedPtnNames = customMapper.selectFinalizedPtnNames(mapParam);
					List<PatternComposition> listPtnComp = kumibanPattern.getPatternCompositionList(listFinalizedPtnNames);
					
					for (PatternComposition ptnComp : listPtnComp) {
						try {
							// execute analyze_weka_pattern.sql
							procParam.put("paramPatternName", ptnComp.name);
							procParam.put("paramPatternValue", ptnComp.value);
							
							logger.info("progress finalize condition: " + finalizeCondCnt + "/" + listFinalizeCondition.size() + ": " + finalizeCondition);
							logger.info("  progress condition: " + condcnt + "/" + listCondition.size() + ": " + cond);
							logger.info("    progress pattern: " + cnt + "/" + listPtnComp.size() + ": " + ptnComp + "\n");
							
							procedureExecutorMapper.executeAnalyzeWekaPattern(procParam);
							session.commit();
							cnt++;
						} catch (Exception e) {
							logger.error("error: " + ptnComp);
							cnt++;
						}
					}
					
					//  組番単位の統計テーブルを生成する（analyze_weka_kumiban.sql）
					procedureExecutorMapper.executeAnalyzeWekaKumiban(procParam);
					session.commit();
					condcnt++;
				}
			}
		}
	}

	/**
	 * 지정한 승식의 패턴정보를 취득한다.
	 * 
	 * @param betType
	 *            승식
	 * @return
	 */
	private BettypePattern getBettypePattern(String betType) {
		for (BettypePattern ptn : listBettypePattern) {
			if (ptn.bettype.equals(betType)) {
				return ptn;
			}
		}

		return null;
	}
	
	private HashMap<String, Object> createParamFinalizeWekaPattern(KumibanPattern kumibanPattern, FinalizeCondition fcond) {
		HashMap<String, Object> mapParam = new HashMap<>();
		mapParam.put("paramDescription", propUtil.getString("DESCRIPTION." + kumibanPattern.betType));
		mapParam.put("paramBettype", kumibanPattern.betType);
		mapParam.put("paramKumiban", kumibanPattern.kumiban);
		
		mapParam.put("paramPercentIncomeAmtStr", fcond.paramPercentIncomeAmtStr);
		mapParam.put("paramPercentHitRateStr", fcond.paramPercentHitRateStr);
		mapParam.put("paramPercentIncomeRateStr", fcond.paramPercentIncomeRateStr);
		mapParam.put("paramPercentMinusDaysRateStr", fcond.paramPercentMinusDaysRateStr);
		mapParam.put("paramPercentIncomeAmtSlopeStr", fcond.paramPercentIncomeAmtSlopeStr);
		mapParam.put("paramPercentRecoverPlusRateStr", fcond.paramPercentRecoverPlusRateStr);
		
		return mapParam;
	}
	
	private HashMap<String, Object> createDefaultParamAnalyzeKumibanPattern(KumibanPattern kumibanPattern, FinalizeCondition finalizeCondition) {
		HashMap<String, Object> mapParam = new HashMap<>();
		mapParam.put("paramDescription", propUtil.getString("DESCRIPTION." + kumibanPattern.betType));
		mapParam.put("paramBettype", kumibanPattern.betType);
		mapParam.put("paramKumiban", kumibanPattern.kumiban);
		mapParam.put("paramArrYmdStr",
				propUtil.getString("ANALYZE_YMD_FROM") + "~" + propUtil.getString("ANALYZE_YMD_TO"));
		
		// finalize 파라미터 설정
		mapParam.put("paramPercentIncomeAmtStr", finalizeCondition.paramPercentIncomeAmtStr);
		mapParam.put("paramPercentHitRateStr", finalizeCondition.paramPercentHitRateStr);
		mapParam.put("paramPercentIncomeRateStr", finalizeCondition.paramPercentIncomeRateStr);
		mapParam.put("paramPercentMinusDaysRateStr", finalizeCondition.paramPercentMinusDaysRateStr);
		mapParam.put("paramPercentIncomeAmtSlopeStr", finalizeCondition.paramPercentIncomeAmtSlopeStr);
		mapParam.put("paramPercentRecoverPlusRateStr", finalizeCondition.paramPercentRecoverPlusRateStr);
		
		// analyze 디폴트 파라미터 취득 및 설정
		String prefix = "ANZ.COND.DEFAULT.";
		mapParam.put("paramArrOddsRankStr", propUtil.getString(prefix + "paramArrOddsRankStr"));
		mapParam.put("paramArrOddsStr", propUtil.getString(prefix + "paramArrOddsStr"));
		mapParam.put("paramArrTotalRateStr", propUtil.getString(prefix + "paramArrTotalRateStr"));
		mapParam.put("paramArrHitRateStr", propUtil.getString(prefix + "paramArrHitRateStr"));
		mapParam.put("paramArrIncomeRateStr", propUtil.getString(prefix + "paramArrIncomeRateStr"));
		mapParam.put("paramMetricBiasPercent", propUtil.getFloat("METRIC_BIAS_PERCENT." + kumibanPattern.betType));
		mapParam.put("paramArrBiasRateStr", propUtil.getString(prefix + "paramArrBiasRateStr"));
		mapParam.put("paramArrMinusDaysRateStr", propUtil.getString(prefix + "paramArrMinusDaysRateStr"));
		mapParam.put("paramArrBetCountStr", propUtil.getString(prefix + "paramArrBetCountStr"));
		mapParam.put("paramMinusChangedCountStr", propUtil.getString(prefix + "paramMinusChangedCountStr"));
		mapParam.put("paramIsFinal", propUtil.getString("paramIsFinal"));
		mapParam.put("paramIsCustom", propUtil.getString("paramIsCustom"));

		return mapParam;
	}

	public static void main(String[] args) {
//		 args = new String[] {
//		 "db=1",
//		 "kumiban",
//		 "2T",
//		 "12",
//		 "C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/weka_pattern_analyze_entry.properties"
//		 };
		 
		if (args.length != 3 && args.length != 5) {
			System.out.println("args error.");
			System.out.println("ex) AnalyzeWekaPattern {db:1} {'all'} {prop file path}");
			System.out.println("ex) AnalyzeWekaPattern {db:1} {'kumiban'} {bettype} {kumiban} {propfilepath}");
			System.exit(-1);
		}

		String dbNo = args[0].split(":")[1];
		String type = args[1];
		String propFilepath = args[args.length-1];

		try {
			AnalyzeWekaPattern analyzeWekaPattern = new AnalyzeWekaPattern(propFilepath, dbNo);
			if (type.equals("all")) {
				analyzeWekaPattern.execute();
			} else if (type.equals("kumiban")) {
				String betType = args[2];
				String kumiban = args[3];
				analyzeWekaPattern.execute(betType, kumiban);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
