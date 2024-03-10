package com.pengkong.boatrace.weka.automation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.mybatis.client.ProcedureExecutorMapper;
import com.pengkong.boatrace.weka.automation.prop.BettypePattern;
import com.pengkong.boatrace.weka.automation.prop.KumibanPattern;
import com.pengkong.boatrace.weka.automation.prop.Pattern;
import com.pengkong.boatrace.weka.automation.prop.PatternComposition;
import com.pengkong.common.Prop;
import com.pengkong.common.PropertyUtil;

public class OutputWekaPattern {

	private PropertyUtil propUtil = PropertyUtil.getInstance();
	private List<BettypePattern> listBettypePattern = new ArrayList<>();
	Logger logger = LoggerFactory.getLogger(OutputWekaPattern.class);

	public final String TYPE_ALL = "all";
	public final String TYPE_KUMIBAN = "kumiban";

	private String dbNo;
	public OutputWekaPattern(String propertyFilepath, String dbNo) throws Exception {
		propUtil.addFile(propertyFilepath);
		this.dbNo = dbNo;
		
		initialize();
	}

	/**
	 * 프로퍼티 파일에 정의된 모든 내용을 로드한다.
	 */
	private void initialize() {
		List<Prop> listPropKumiban = propUtil.getProps("KUMIBAN.");
		// loop for bettype
		for (Prop prop : listPropKumiban) {
			BettypePattern betTypePattern = new BettypePattern();
			String betType = prop.key; // ex) 2T
			betTypePattern.bettype = betType;
			String keyMinBetCount = "MIN_BETCOUNT." + betType; // ex) MINIMUM_BETCOUNT.2T
			betTypePattern.betcountLimit = propUtil.getInteger(keyMinBetCount);
			String[] tokenKumiban = prop.value.split(",");
			// loop for kumiban
			for (String kumiban : tokenKumiban) {
				KumibanPattern kumibanPattern = new KumibanPattern(betType, kumiban);
				betTypePattern.listKumibanPattern.add(kumibanPattern);
				
				addRequiredPatterns(kumibanPattern);
				addAllPatterns(kumibanPattern);
				addKumibanAllPatterns(betType, kumibanPattern);
				addKumibanPatterns(betType, kumibanPattern);
			}
			
			listBettypePattern.add(betTypePattern);
		}
	}
	
	/**
	 * KumibanPatternに対してPTN_UNIT.ALL.ALL.*のPatternを追加する.
	 * @param ptn 
	 */
	private KumibanPattern addRequiredPatterns(KumibanPattern kumibanPtn) {
		List<Pattern> result = kumibanPtn.listPattern;
		result.addAll(getPatternList("PTN_UNIT.ALL.REQUIRED."));
		
		return kumibanPtn;
	}
	
	/**
	 * KumibanPatternに対してPTN_UNIT.ALL.ALL.*のPatternを追加する.
	 * @param ptn 
	 */
	private KumibanPattern addAllPatterns(KumibanPattern kumibanPtn) {
		List<Pattern> result = kumibanPtn.listPattern;
		result.addAll(getPatternList("PTN_UNIT.ALL.ALL."));
		
		return kumibanPtn;
	}
	
	/**
	 * KumibanPatternに対してPTN_UNIT.*.ALL.*のPatternを追加する.
	 * @param ptn 
	 */
	private KumibanPattern addKumibanAllPatterns(String betType, KumibanPattern kumibanPtn) {
		List<Pattern> result = kumibanPtn.listPattern;
		result.addAll(getPatternList("PTN_UNIT." + betType + ".ALL."));
		
		return kumibanPtn;
	}

	/**
	 * KumibanPatternに対してPTN_UNIT.*.*.*のPatternを追加する.
	 * @param ptn 
	 */
	private KumibanPattern addKumibanPatterns(String betType, KumibanPattern kumibanPtn) {
		List<Pattern> result = kumibanPtn.listPattern;
		result.addAll(getPatternList("PTN_UNIT." + betType + "." + kumibanPtn.kumiban + "."));
		
		return kumibanPtn;
	}

	private List<Pattern> getPatternList(String propsKey) {
		List<Pattern> result = new ArrayList<>();
		List<Prop> props = propUtil.getProps(propsKey);
		// loop for patterns
		for (Prop prop : props) {
			String ptnName = prop.key; // ex) raceno
			String[] token = prop.value.split(",");
			Pattern ptn = new Pattern();
			ptn.name = ptnName; // ex) race
			ptn.value = token[0]; // ex) race.raceno
			ptn.count = Integer.valueOf(token[1]); // ex) 12
			result.add(ptn);
		}
		
		return result;
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
	 * @param statBettype 승식
	 * @param prediction 구미방
	 * @throws Exception
	 */
	public void execute(KumibanPattern kumibanPattern) throws Exception {
		int minDepth = propUtil.getInteger("MIN_COMPOSITION_DEPTH");
		int maxDepth = propUtil.getInteger("MAX_COMPOSITION_DEPTH");
		
		String resource = "mybatis-config." + dbNo + ".xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		
		try(SqlSession session = sqlSessionFactory.openSession(false)) {
			ProcedureExecutorMapper procedureExecutorMapper = session.getMapper(ProcedureExecutorMapper.class);
			List<PatternComposition> listPtnComp = kumibanPattern.getPatternCompositionList(minDepth, maxDepth);
			HashMap<String, Object> procParam;
			int cnt = 1;
			for(PatternComposition ptnComp : listPtnComp) {
				logger.info("progress: " + cnt + "/" + listPtnComp.size());
				logger.info("  processing: " + ptnComp + "\n");
				try {
					// execute output_weka_pattern.sql
					procParam = createParamOutputWekaPattern(ptnComp);
					procedureExecutorMapper.executeOutputWekaPattern(procParam);
					session.commit();
					cnt++;
				} catch (Exception e) {
					logger.error("error: " + ptnComp);
					cnt++;
				}
			}
		}
	}
	
	/**
	 * 지정한 승식의 패턴정보를 취득한다.
	 * 
	 * @param betType 승식
	 * @return
	 */
	public BettypePattern getBettypePattern(String betType) {
		for (BettypePattern ptn : listBettypePattern) {
			if (ptn.bettype.equals(betType)) {
				return ptn;
			}
		}
		
		return null;
	}
	
	/**
	 * 프로시져 실행 파라미터를 생성한다. (output_weka_pattern.sql)
	 * 
	 * @param ptnComp 프로퍼티파일로부터 생성한 패턴정보
	 * @return 프로시져 파라미터 맵
	 */
	private HashMap<String, Object> createParamOutputWekaPattern(PatternComposition ptnComp) {
		HashMap<String, Object> mapParam = new HashMap<>();
		mapParam.put("paramDescription", propUtil.getString("DESCRIPTION." + ptnComp.betType));
		mapParam.put("paramBettype", ptnComp.betType);
		mapParam.put("paramKumiban" , ptnComp.kumiban);
		mapParam.put("paramPatternName", ptnComp.name);
		mapParam.put("paramPatternValue", ptnComp.value);
		mapParam.put("paramMinimumBetCount", propUtil.getInteger("MIN_BETCOUNT." + ptnComp.betType));
		mapParam.put("paramMetricBiasPercent", propUtil.getFloat("METRIC_BIAS_PERCENT." + ptnComp.betType));
		mapParam.put("paramFromYmd", propUtil.getString("PATTERN_YMD_FROM"));
		mapParam.put("paramToYmd", propUtil.getString("PATTERN_YMD_TO"));
		
		return mapParam;
	}
	
	public static void main(String[] args) {
//		args = new String[] {
//				"db:2", 
//		        //"kumiban",
//				//"2T",
//				//"12",
//				 "all",
//				"C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/20190816/output_entry1-2.properties"		
//		};

		if (args.length != 3 && args.length != 5) {
			System.out.println("args error.");
			System.out.println("ex) OutputWekaPattern {db:1} {all || kumiban} {prop file path}");
			System.out.println("ex) OutputWekaPattern {db:1} {all || kumiban} {bettype} {kumiban} {prop file path}");
			System.exit(-1);
		}
		
		String dbNo = args[0].split(":")[1];
		String type = args[1];
		String propFilepath = args[args.length-1];
		
		try {
			OutputWekaPattern outputWekaPattern = new OutputWekaPattern(propFilepath, dbNo);
			if (type.equals("all")) {
				outputWekaPattern.execute();
			} else if (type.equals("kumiban")) {
				String betType = args[2];
				String kumiban = args[3];
				outputWekaPattern.execute(betType, kumiban);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
