package com.pengkong.boatrace.weka.automation.regression;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.model.OddsItemEx;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.mybatis.client.StatMlResultRgrMapper;
import com.pengkong.boatrace.mybatis.entity.StatMlResultRgr;
import com.pengkong.boatrace.mybatis.entity.StatMlResultRgrExample;
import com.pengkong.boatrace.service.manager.BeforeOddsManager;
import com.pengkong.boatrace.weka.classifiers.BoatClassifierDefault;
import com.pengkong.boatrace.weka.classifiers.manager.ClassifierManager;
import com.pengkong.boatrace.weka.classifiers.manager.regression.RegressionManager;
import com.pengkong.boatrace.weka.classifiers.regression.BoatRegressionDefault;
import com.pengkong.common.Prop;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.collection.HashMapList;

public class MLSimulatorWithRegression {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	List<BoatClassifierDefault> listClassifier;
	String bettype;
	String bettypeName;
	String description;
	String modelFilterStr;
	String regressionFilterStr;
	String fromYmd;
	String toYmd;
	
	HashMapList<HashMap<String, ?>> mapListYmd = new HashMapList<>();
	BeforeOddsManager oddsManager = BeforeOddsManager.getInstance();
	ClassifierManager clm = ClassifierManager.getInstance();
	
	/** key = bettype.kumiban, value = classifier hitrate */ 
	Map<String, Double> mapHitRate = new HashMap<>();
	RegressionManager rgm = new RegressionManager();
	PropertyUtil prop = PropertyUtil.getInstance();
	
	public MLSimulatorWithRegression() {
	}

	public void initialize(String bettype, String bettypeName,String description, 
			String modelFilterStr, String regressionFilterStr, String fromYmd, String toYmd) throws Exception {
		this.bettype = bettype;
		this.bettypeName = bettypeName;
		this.description = description;
		this.modelFilterStr = modelFilterStr; 
		this.regressionFilterStr = regressionFilterStr; 
		this.fromYmd = fromYmd;
		this.toYmd = toYmd;
		
		clm.loadClassifierInfo( PropertyUtil.getInstance().getString("DIRECTORY_WEKAMODELS") , "model_release", modelFilterStr);
		loadHitrateMap();
		rgm.loadClassifierInfo(PropertyUtil.getInstance().getString("DIRECTORY_WEKAMODELS"), "regression_release", regressionFilterStr);
	}
	
	private void loadHitrateMap() {
		List<Prop> props = prop.getProps("Regression.hitrate.");
		for (Prop prop : props) {
			mapHitRate.put(prop.key, Double.valueOf(prop.value));
		}
	}
	
	public void execute() throws Exception {
		
		String resource = "mybatis-config.0.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		
		// open session with auto-transaction mode = false
		try(SqlSession session = sqlSessionFactory.openSession(false)) {
			CustomMapper customMapper = session.getMapper(CustomMapper.class);
			StatMlResultRgrMapper statMlResultMapper = session.getMapper(StatMlResultRgrMapper.class);
			StatMlResultRgrExample statMlResultExample = new StatMlResultRgrExample();

			// delete stat_ml_result by description
			statMlResultExample.clear();
			statMlResultExample.createCriteria().andDescriptionEqualTo(description);
			statMlResultMapper.deleteByExample(statMlResultExample);
			
			// select 70
			HashMap<String, String> mapParam = new HashMap<>();
			mapParam.put("fromYmd", fromYmd);
			mapParam.put("toYmd", toYmd);
			List<HashMap<String, ?>> listHashmap = customMapper.select70(mapParam);
			for (HashMap<String, ?> hashmap : listHashmap) {
				String ymd = (String)hashmap.get("ymd");
				mapListYmd.addItem(ymd, hashmap);
			}
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(fromYmd));
			Date currDate = calendar.getTime();
			Date toDate = sdf.parse(toYmd);
			
			while (currDate.compareTo(toDate) <= 0) {
				int dailyBalance = 0;
				int betcnt = 0;
				String ymd = sdf.format(currDate);
				
				List<HashMap<String, ?>> listHashmapYmd = mapListYmd.get(ymd);
				if (listHashmapYmd != null) {
					int intYmd = Integer.valueOf(ymd);
					
					// 시뮬레이션 결과의 월단위 옺즈통계에 오차를 발생시키므로
					// 20190101-20190109(確定オッズ） 20190117-20190124(オッズ取得間隔変更） 사이는 아예 투표를 하지 않게 하자.
					if ( (intYmd >= 20190101 && intYmd <= 20190109) || 
							(intYmd >= 20190117 && intYmd <= 20190124) ||
							(intYmd >= 20190611 && intYmd <= 20190623) 
							) {
						// 1日増加
						calendar.add(Calendar.DATE, 1);
						currDate = calendar.getTime();
						continue;
					}
					
					// 2019/1/10이후부터 직전옺즈가 존재하므로 실제 직전옺즈는 이날 이후부터만 로딩한다.
					if (intYmd >= 20190110) {
						// load odds
						oddsManager.load(ymd);
					}
					
					// load weka models
					BoatClassifierDefault classifier = clm.getClassifierList(bettype, modelFilterStr.split("_")[2], ymd).get(0);
					BoatRegressionDefault regression = rgm.getClassifierList(bettype, regressionFilterStr.split("_")[2], ymd).get(0);
							
					for (HashMap<String, ?> hashmap : listHashmapYmd) {
						StatMlResultRgr statMlResult = new StatMlResultRgr();
						String jyoCd = (String)hashmap.get("jyocd");
						Short raceNo = ((Integer)hashmap.get("raceno")).shortValue();
						String sime = (String)hashmap.get("sime");
						statMlResult.setYmd(ymd);
						statMlResult.setSime(sime);
						statMlResult.setJyocd(jyoCd);
						statMlResult.setRaceno(raceNo);
						statMlResult.setDescription(description);
						statMlResult.setBettype(bettype);
						
						statMlResult.setResultKumiban((String)hashmap.get(bettypeName + "no"));
						Integer prize = (Integer)hashmap.get(bettypeName + "prize");
						BigDecimal resultOdds = new BigDecimal((float)prize / 100f).setScale(2, RoundingMode.FLOOR);
						statMlResult.setResultOdds(resultOdds);
						statMlResult.setResultOddsrank((Integer)hashmap.get(bettypeName + "popular"));
						statMlResult.setResultAmt(prize); // 20190817추가 복수투표를 위해 비적중회수금도 기록한다. 
						String kumiban = null;
						boolean isClassifierOk = false;
						boolean isRegressionOk = false;
						kumiban = classifier.predictFromHashmap(hashmap);
						if (kumiban != null && !kumiban.equals("else")) {
							isClassifierOk = true;
							double predictAmt = regression.predictFromHashmap(bettypeName, kumiban, hashmap);
							double predictHitRate = mapHitRate.get(bettype + "." + kumiban);
							double amt = predictAmt * predictHitRate;
							if (predictAmt < 400 || predictAmt > 800) {
								statMlResult.setRgramt((int)predictAmt);
								isRegressionOk = true;
							}
						}
						
						if (isClassifierOk && isRegressionOk) {
							statMlResult.setBetKumiban(kumiban);
							statMlResult.setBetamt(100);
							// hit!
							if (statMlResult.getBetKumiban().equals(statMlResult.getResultKumiban())) {
								statMlResult.setHity(1);
								statMlResult.setHitn(0);
								statMlResult.setHitamt(prize);
							} else {
								statMlResult.setHity(0);
								statMlResult.setHitn(1);
								statMlResult.setHitamt(0);
							}
							
							// 2019/1/10이후부터 직전옺즈가 존재하므로 결과옺즈를 대신 직전옺즈로 설정한다.
							statMlResult.setBetOdds(statMlResult.getResultOdds());
							statMlResult.setBetOddsrank(statMlResult.getResultOddsrank());
							if (intYmd >= 20190110 && (intYmd <= 20190611 || intYmd >= 20190623)) {
								OddsItemEx oddsItemEx = oddsManager.getOddsItemEx(jyoCd, String.valueOf(raceNo), bettype, kumiban);
								// 직전 옺즈 설정
								if (oddsItemEx != null) {
									statMlResult.setBetOdds( BigDecimal.valueOf(oddsItemEx.value).setScale(2, RoundingMode.FLOOR) );
									statMlResult.setBetOddsrank(oddsItemEx.rank);
									statMlResultMapper.insert(statMlResult);
									betcnt++;
								}
							} else {
								// 2019/1/9이전이면 직전옺즈는 0설정하고 옺즈상관없이 베팅한다.
								statMlResultMapper.insert(statMlResult);
								betcnt++;
							}
							
							// daily balance
							dailyBalance -= statMlResult.getBetamt();
							dailyBalance += statMlResult.getHitamt();
						}
					}
				}
				
				System.out.println(ymd + ": " + betcnt + " bet, daily balance= " + dailyBalance);

				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
			}

			session.commit();
		}
	}
	
	public static void main(String[] args) {
		args = new String[] {
				"2T", // bettype
				"nirentan", // bettype name 
				"203_209regression", // description
				"203_2T_12-31", // strModelFilter
				"209_2T_12-31", // strRegressionFilter
				"20170101", // test from ymd
				"20181231" //test to ymd
		};
		
    	if (args.length != 7) {
    		System.out.println("Usage: MLMultiSimulator {bettype} {bettype name} {description} {model filter string} {regression filter string} {fromYmd} {toYmd}");
    		System.out.println("ex: MLSimulator 2F nirenhuku model17 \"55_,56_\" 20190110 20190322");
    		System.exit(1);
    	}
		
		try {
			String bettype = args[0];
			String bettypeName = args[1];
			String description = args[2];
			String strModelFilter = args[3];
			String strRegressionFilter = args[4];
			String fromYmd = args[5];
			String toYmd = args[6];
			
			PropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/race_result_rebuild.properties");
			MLSimulatorWithRegression simulator = new MLSimulatorWithRegression();
			
			simulator.initialize(bettype, bettypeName, description, strModelFilter, strRegressionFilter, fromYmd, toYmd);
			simulator.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
