package com.pengkong.boatrace.weka.automation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.mybatis.client.StPtnFinalMapper;
import com.pengkong.boatrace.mybatis.entity.StPtnFinal;
import com.pengkong.boatrace.server.PatternStatRunnerPool;
import com.pengkong.boatrace.server.db.dto.BoatDbRecord;
import com.pengkong.boatrace.server.manager.PatternStatRunner;
import com.pengkong.boatrace.server.manager.filter.FinalizeFilter;
import com.pengkong.boatrace.server.rmi.BoatDbRmiClient;
import com.pengkong.boatrace.server.stat.StStatMlPtnFinal;
import com.pengkong.boatrace.weka.automation.prop.BettypePattern;
import com.pengkong.boatrace.weka.automation.prop.KumibanPattern;
import com.pengkong.boatrace.weka.automation.prop.Pattern;
import com.pengkong.boatrace.weka.automation.prop.PatternComposition;
import com.pengkong.common.Prop;
import com.pengkong.common.PropertyUtil;

public class FinalGenerator {

	private PropertyUtil propUtil = PropertyUtil.getInstance();
	
	private List<BettypePattern> listBettypePattern = new ArrayList<>();
	private Map<String, FinalizeFilter> mapFinalizeFilter; 
	BoatDbRmiClient rmiClient = new BoatDbRmiClient();

	Logger logger = LoggerFactory.getLogger(FinalGenerator.class);

	public final String TYPE_ALL = "all";
	public final String TYPE_KUMIBAN = "kumiban";

	private String dbNo;
	public FinalGenerator(String dbNo) throws Exception {
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
			String[] tokenKumiban = prop.value.split(",");
			// loop for kumiban
			for (String kumiban : tokenKumiban) {
				KumibanPattern kumibanPattern = new KumibanPattern(betType, kumiban);
				kumibanPattern.kumiban = kumiban;
				betTypePattern.listKumibanPattern.add(kumibanPattern);
				
				addRequiredPatterns(kumibanPattern);
				addAllPatterns(kumibanPattern);
				addKumibanAllPatterns(betType, kumibanPattern);
				addKumibanPatterns(betType, kumibanPattern);
			}
			
			listBettypePattern.add(betTypePattern);
		}
		
		createFinalizeFilter();
	}
	
	/**
	 * 프로퍼티로부터 finalzie조건을 로딩한다.
	 * @return Map<String, FinalizeFilter> : key = bettype.kumiban ex) "2F.12"
	 */
	private void createFinalizeFilter() {
		mapFinalizeFilter = new HashMap<String, FinalizeFilter>();
		List<Prop> listProp = propUtil.getProps("FIN.FILTER.");
		for (Prop prop : listProp) {
			String[] valueToken = prop.value.split(",");
			FinalizeFilter filter = FinalizeFilter.create(valueToken);
			mapFinalizeFilter.put(prop.key, filter);
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
	private void execute(KumibanPattern kumibanPattern) throws Exception {
		int minDepth = propUtil.getInteger("MIN_COMPOSITION_DEPTH");
		int maxDepth = propUtil.getInteger("MAX_COMPOSITION_DEPTH");
		String fromYmd = propUtil.getString("PATTERN_YMD_FROM");
		String toYmd = propUtil.getString("PATTERN_YMD_TO");
		PatternStatRunnerPool pool = new PatternStatRunnerPool(10);

		
		String description = createDescription(kumibanPattern, String.valueOf(maxDepth));
		String resource = "mybatis-config." + dbNo + ".xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		// RMI서버로부터 DB데이터 취득
		List<BoatDbRecord> listDbRec = rmiClient.getBoatDbRecordList(fromYmd, toYmd, kumibanPattern.betType, kumibanPattern.kumiban);
		
		try(SqlSession session = sqlSessionFactory.openSession(false)) {
			StPtnFinalMapper stPtnFinalMapper = session.getMapper(StPtnFinalMapper.class);
			List<PatternComposition> listPtnComp = kumibanPattern.getPatternCompositionList(minDepth, maxDepth);
			int cnt = 1;
			for(PatternComposition ptnComp : listPtnComp) {
				//logger.info("progress: " + cnt + "/" + listPtnComp.size());
				//logger.info("  processing: " + ptnComp + "\n");
				PatternStatRunner runner = new PatternStatRunner(listDbRec, ptnComp.name, mapFinalizeFilter);
				pool.addRunner(runner);
				cnt++;
			}
			
			try {
				List<StStatMlPtnFinal> results = pool.execute();
				for (StStatMlPtnFinal stFinal : results) {
					StPtnFinal rec = StStatMlPtnFinal.createDbRecord(description, stFinal.stPatternValue);
					stPtnFinalMapper.insert(rec);
				}
			} catch (Exception e) {
				logger.error("error: ", e);
			}
			session.commit();
		}
	}
	
	private String createDescription(KumibanPattern kumibanPattern, String depth) {
		String betType = kumibanPattern.betType;
		String kumiban = kumibanPattern.kumiban;
		// key ex) 3T.123
		FinalizeFilter filter = mapFinalizeFilter.get(betType + "." + kumiban);
		return betType + "_" + kumiban + "_" + depth + "_" + filter 
				+ "_" + propUtil.getString("PATTERN_YMD_FROM") + "~" + propUtil.getString("PATTERN_YMD_TO"); 
	}
	
	/**
	 * 지정한 승식의 패턴정보를 취득한다.
	 * 
	 * @param betType 승식
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
	
	
	public static void main(String[] args) {
//		args = new String[] {
//				"db:0", 
//		        "kumiban",
//				"2T",
//				"21",
//				"C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/output2.properties"		
//		};

		if (args.length != 3 && args.length != 5) {
			System.out.println("args error.");
			System.out.println("ex) FinalGenerator {db:1} {all } {prop file path}");
			System.out.println("ex) FinalGenerator {db:1} {kumiban} {bettype} {kumiban value} {prop file path}");
			System.exit(-1);
		}
		
		String dbNo = args[0].split(":")[1];
		String type = args[1];
		String propFilepath = args[args.length-1];
		try {
			PropertyUtil.getInstance().addFile(propFilepath);
			FinalGenerator outputWekaPattern = new FinalGenerator(dbNo);
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
