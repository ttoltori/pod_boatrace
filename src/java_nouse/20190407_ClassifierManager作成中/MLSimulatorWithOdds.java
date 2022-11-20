package com.pengkong.boatrace.weka.classifiers.nouse;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.pengkong.boatrace.common.BoatProperty;
import com.pengkong.boatrace.model.OddsItemEx;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.mybatis.client.ProcedureExecutorMapper;
import com.pengkong.boatrace.mybatis.client.StatMlResultMapper;
import com.pengkong.boatrace.mybatis.entity.StatMlResult;
import com.pengkong.boatrace.mybatis.entity.StatMlResultExample;
import com.pengkong.boatrace.service.manager.BeforeOddsManager;
import com.pengkong.boatrace.weka.classifiers.BoatClassifierDefault;
import com.pengkong.common.collection.HashMapList;

public class MLSimulatorWithOdds {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	//BoatFilteredClassifier boatClassifier = new BoatFilteredClassifier();
	//BoatSecondClassifier boatSecondClassifier = new BoatSecondClassifier();
	List<BoatClassifierDefault> listClassifier = new ArrayList<>();
	HashMap<String, BoatClassifierDefault> mapRankChangeClassfier = new HashMap<>();
	
	String bettype;
	String bettypeName;
	String description;
	boolean useRankChangeFilter = true;
	
	HashMapList<HashMap<String, ?>> mapListYmd = new HashMapList<>();
	BeforeOddsManager oddsManager = BeforeOddsManager.getInstance();
	public MLSimulatorWithOdds() {
	}

	public void initialize(String bettype, String bettypeName,String description, 
			String dir, String[] defFileNames, String filterDefFileName) throws Exception {
		this.bettype = bettype;
		this.bettypeName = bettypeName;
		this.description = description;
			
		// create classifiers
		for (int i = 0; i < defFileNames.length; i++) {
			BoatClassifierDefault clf = new BoatClassifierDefault();
			clf.initialize(dir, defFileNames[i]);
			listClassifier.add(clf);
		}
		
		if (filterDefFileName.length() > 0) {
			this.useRankChangeFilter = true;
			
			BoatClassifierDefault oddsClf = new BoatClassifierDefault();
			oddsClf.initialize(dir, filterDefFileName);
			mapRankChangeClassfier.put("1", oddsClf);
		} else {
			this.useRankChangeFilter = false;
		}
		
//		initializeRankChangeClassifier();
		
		
		// initialize odds manager
		oddsManager.initialize();
	}

//	private void initializeRankChangeClassifier() throws Exception {
//		BoatClassifierDefault clf;
//
//		clf = new BoatClassifierDefault();
//		clf.initialize(dir, defFileNames[i]);
//		classfier.initialize(new String[]{"121","else"}, "C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels/70_2F_121_oddsrankchange.model");
//		mapRankChangeClassfier.put("121", classfier);
//
//		classfier = new BoatClassifierOddsRankChange(); 
//		classfier.initialize(new String[]{"122","else"}, "C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels/70_2F_122_oddsrankchange.model");
//		mapRankChangeClassfier.put("122", classfier);
//	
//		classfier = new BoatClassifierOddsRankChange(); 
//		classfier.initialize(new String[]{"123","else"}, "C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels/70_2F_123_oddsrankchange.model");
//		mapRankChangeClassfier.put("123", classfier);
//		
//		classfier = new BoatClassifierOddsRankChange(); 
//		classfier.initialize(new String[]{"124","else"}, "C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels/70_2F_124_oddsrankchange.model");
//		mapRankChangeClassfier.put("124", classfier);
//	}
	
	
	public void execute(String fromYmd, String toYmd, String wrWaku) throws Exception {
		
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		
		// open session with auto-transaction mode = false
		try(SqlSession session = sqlSessionFactory.openSession(false)) {
			CustomMapper customMapper = session.getMapper(CustomMapper.class);
			StatMlResultMapper statMlResultMapper = session.getMapper(StatMlResultMapper.class);
			StatMlResultExample statMlResultExample = new StatMlResultExample();

			// delete stat_ml_multiresult
			statMlResultExample.clear();
			statMlResultExample.createCriteria().andYmdBetween(fromYmd, toYmd).andDescriptionEqualTo(description);
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
				int betcnt = 0;
				String ymd = sdf.format(currDate);
				
				List<HashMap<String, ?>> listHashmapYmd = mapListYmd.get(ymd);
				if (listHashmapYmd != null) {
					// load odds
					oddsManager.load(ymd);
					
					for (HashMap<String, ?> hashmap : listHashmapYmd) {
						StatMlResult statMlResult = new StatMlResult();
						String jyoCd = (String)hashmap.get("jyocd");
						Short raceNo = ((Integer)hashmap.get("raceno")).shortValue();
						statMlResult.setYmd(ymd);
						statMlResult.setJyocd(jyoCd);
						statMlResult.setRaceno(raceNo);
						statMlResult.setDescription(description);
						statMlResult.setBettype(bettype);
						
						statMlResult.setResultKumiban((String)hashmap.get(bettypeName + "no"));
						Integer prize = (Integer)hashmap.get(bettypeName + "prize");
						BigDecimal resultOdds = new BigDecimal((float)prize / 100f).setScale(2, RoundingMode.FLOOR);
						statMlResult.setResultOdds(resultOdds);
						statMlResult.setResultOddsrank((Integer)hashmap.get(bettypeName + "popular"));
						
						String kumiban = null;
						String firstKumiban = null;
						int classifierCnt = 0;
						int bettingCnt = 0;
						for (BoatClassifierDefault boatClassifier : listClassifier) {
							kumiban = boatClassifier.predictFromHashmap(hashmap);
							if (firstKumiban == null) {
								firstKumiban = kumiban;
							}
							if (kumiban != null && !kumiban.equals("else") && kumiban.equals(firstKumiban)) {
								if (useRankChangeFilter) {
									BoatClassifierDefault oddsClassifier = mapRankChangeClassfier.get("1");
									String kumibanOdds = oddsClassifier.predictFromHashmap(hashmap);
									if (kumibanOdds != null && !kumibanOdds.equals("else") && kumibanOdds.contains(kumiban)) {
									  bettingCnt++;
									}
								} else {
									bettingCnt++;
								}
							}
							
							classifierCnt++;
						}
						
						if (classifierCnt == bettingCnt) {
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

							int intYmd = Integer.valueOf(ymd);
							// 2019/1/10이후부터 직전옺즈가 존재한다.
							statMlResult.setBetOdds(new BigDecimal(0));
							statMlResult.setBetOddsrank(0);
							if (intYmd >= 20190110) {
								OddsItemEx oddsItemEx = oddsManager.getOddsItemEx(jyoCd, String.valueOf(raceNo), bettype, kumiban);
								// 직전 옺즈 설정
								if (oddsItemEx != null) {
									statMlResult.setBetOdds( BigDecimal.valueOf(oddsItemEx.value).setScale(2, RoundingMode.FLOOR) );
									statMlResult.setBetOddsrank(oddsItemEx.rank);
									// 직전옺즈가 1.1이상일 때만 베팅한다.
									if (oddsItemEx.value >= 1.1 ) {
										statMlResultMapper.insert(statMlResult);
										betcnt++;
									}
								}
							} else {
								// 2019/1/9이전이면 직전옺즈는 0설정하고 옺즈상관없이 베팅한다.
								statMlResultMapper.insert(statMlResult);
								betcnt++;
							}
						}
						
					}
				}
				
				System.out.println(ymd + ": " + betcnt + " bet");

				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
			}
			
			ProcedureExecutorMapper procedureExecutorMapper = session.getMapper(ProcedureExecutorMapper.class);
			HashMap<String, Object> procParam = new HashMap<>();
			procParam.put("fromYmd", fromYmd);
			procParam.put("toYmd", toYmd);
			procParam.put("yearsCount", new Integer(1));
			procParam.put("betType", bettype);
			procParam.put("rankCols", new Integer(2));
			procParam.put("description", description);
			procParam.put("path", "C:\\Dev\\workspace\\Oxygen\\pod_boatrace\\test\\wekamodels\\result\\");
			procedureExecutorMapper.executeOutputWeka(procParam);
			
			session.commit();
			
		}
	}
	
	public static void main(String[] args) {
		args = new String[] {
				"1T", 
				"tansyo",
				"model17",
				"C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels",
				"17_1T_123456_racer_20171231.def", // model
				"" // odds filter model
		};
		
    	if (args.length != 6) {
    		System.out.println("Usage: MLSimulator {bettype} {bettype name} {description} {work directory} {define files} {odds define files} {filter use}");
    		System.out.println("ex: MLSimulator 2F nirenhuku 2F_01 C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels \"2F_01-01.def,2F_01-02.def,2F_01-03.def,2F_01-04.def\"");
    		System.exit(1);
    	}
		
		try {
			String bettype = args[0];
			String bettypeName = args[1];
			String description = args[2];
			String workdir = args[3];
			String strDefFiles = args[4];
			String[] defFiles = strDefFiles.split(",");
			
			BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/race_result_rebuild.properties");
			MLSimulatorWithOdds simulator = new MLSimulatorWithOdds();
			simulator.initialize(bettype, bettypeName, description, workdir, defFiles, args[5]);
			//simulator.execute("20190222", "20190303", null);
			simulator.execute("20180101", "20181231", null);
			//classifier.execute("20180101", "20181231", null);
			//classifier.execute(args[0], args[1], args[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
