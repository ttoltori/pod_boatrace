package com.pengkong.boatrace.weka;

import java.io.InputStream;
import java.lang.reflect.Constructor;
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
import com.pengkong.boatrace.weka.classifiers.BoatClassifier;
import com.pengkong.common.collection.HashMapList;

public class TestParallelPredictionWithOddsRank {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	List<BoatClassifier> listClassifier = new ArrayList<>();
	
	String bettype;
	String bettypeName;
	String description;
	String jyoCd;
	
	HashMapList<HashMap<String, ?>> mapListYmd = new HashMapList<>();
	BeforeOddsManager oddsManager = BeforeOddsManager.getInstance();
	public TestParallelPredictionWithOddsRank() {
	}

	public void initialize(String bettype, String bettypeName,String description, 
			String jyoCd, String[][] classVal, String[] modelClassNames, String[] arrModelFile) throws Exception {
		this.bettype = bettype;
		this.bettypeName = bettypeName;
		this.description = description;
		this.jyoCd = jyoCd;
		
		// create classifiers
		for (int i = 0; i < modelClassNames.length; i++) {
			Constructor<?> c = Class.forName(modelClassNames[i]).getConstructor();
			BoatClassifier classifier = (BoatClassifier) c.newInstance();
			classifier.initialize(classVal[i], arrModelFile[i]);
			listClassifier.add(classifier);
		}
		
		// initialize odds manager
		oddsManager.initialize();
	}

	public void execute(String fromYmd, String toYmd, String wrWaku) throws Exception {
		
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		
		// open session with auto-transaction mode = false
		try(SqlSession session = sqlSessionFactory.openSession(false)) {
			CustomMapper customMapper = session.getMapper(CustomMapper.class);
			StatMlResultMapper statMlResultMapper = session.getMapper(StatMlResultMapper.class);
			StatMlResultExample statMlResultExample = new StatMlResultExample();

			// delete stat_ml_result
			statMlResultExample.clear();
			statMlResultExample.createCriteria().andYmdBetween(fromYmd, toYmd).andDescriptionEqualTo(description);
			statMlResultMapper.deleteByExample(statMlResultExample);
			
			// select 66
			HashMap<String, String> mapParam = new HashMap<>();
			mapParam.put("fromYmd", fromYmd);
			mapParam.put("toYmd", toYmd);
			List<HashMap<String, ?>> listHashmap = customMapper.select66(mapParam);
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
					// load before odds
					oddsManager.load(ymd);
					
					for (HashMap<String, ?> hashmap : listHashmapYmd) {
						StatMlResult statMlResult = new StatMlResult();
						String jyoCd = (String)hashmap.get("jyocd");
						Short raceNo = ((Integer)hashmap.get("raceno")).shortValue();
						statMlResult.setYmd(ymd);
						statMlResult.setJyocd(jyoCd);
						statMlResult.setRaceno(raceNo);
						statMlResult.setBettype(bettype);
						statMlResult.setDescription(description);
						String kumibanTmp = listClassifier.get(0).predictFromHashmap(hashmap);
						String kumiban;
						if (kumibanTmp.equals("else")) {
							kumiban = kumibanTmp;
						} else {
							kumiban = kumibanTmp.substring(0, 2);
						}
						
						String rank = listClassifier.get(1).predictFromHashmap(hashmap);
						if (!kumiban.equals("else") && !rank.equals("else")) {
							OddsItemEx oddsItemEx = oddsManager.getOddsItemEx(jyoCd, String.valueOf(raceNo), bettype, kumiban);
							if (oddsItemEx != null && oddsItemEx.rank == Integer.parseInt(rank)) {
								statMlResult.setBetKumiban(kumibanTmp);
								if (oddsItemEx != null) {
									statMlResult.setBetOdds(BigDecimal.valueOf(oddsItemEx.value).setScale(2, RoundingMode.FLOOR));
									statMlResult.setBetOddsrank(oddsItemEx.rank);
								}
								statMlResult.setResultKumiban((String)hashmap.get(bettypeName + "no"));
								Integer prize = (Integer)hashmap.get(bettypeName + "prize");
								BigDecimal resultOdds = new BigDecimal((float)prize / 100f).setScale(2, RoundingMode.FLOOR);
								statMlResult.setResultOdds(resultOdds);
								statMlResult.setResultOddsrank((Integer)hashmap.get(bettypeName + "popular"));
								if (kumiban.equals((String)hashmap.get(bettypeName + "no"))) {
									statMlResult.setHityn("Y");
								} else {
									statMlResult.setHityn("N");
								}
								
								statMlResultMapper.insert(statMlResult);
								betcnt++;
							}
						} else {
							// logger.info("prediction failure:" + kumiban);
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
			procParam.put("yearsCount", new Integer(2));
			procParam.put("betType", bettype);
			procParam.put("rankCols", new Integer(2));
			procParam.put("description", description);
			procParam.put("path", "C:\\weka_training\\result\\");
			
			procedureExecutorMapper.executeOutputWeka(procParam);
			
			session.commit();
			
		}
	}
	
	public static void main(String[] args) {
		try {
			BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/stat_3T.properties");
			TestParallelPredictionWithOddsRank classifier = new TestParallelPredictionWithOddsRank();
			classifier.initialize(
					"2F", 
					"nirenhuku",
					"70-123_2F_12oddsrank1-4else_BayesNet", 
					"", 
					new String[][] {
						{"121","122","123","124","else"},
						{"121","122","123","124","else"},
						{"121","122","123","124","else"}
					},
					new String[] {
						"com.pengkong.boatrace.weka.classifiers.BoatClassifier70_1",
						"com.pengkong.boatrace.weka.classifiers.BoatClassifier70_2",
						"com.pengkong.boatrace.weka.classifiers.BoatClassifier70_3"
						// "com.pengkong.boatrace.weka.classifiers.Boat65_2",
						//"com.pengkong.boatrace.weka.classifiers.Boat65_3"
					},
					new String[] {
							"C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels/70-1_2F_12oddsrank1-4else_BayesNet.model",
							"C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels/70-2_2F_12oddsrank1-4else_BayesNet.model",
							"C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels/70-3_2F_12oddsrank1-4else_BayesNet.model"
							//"C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels/66_2F_oddsrank1-9_NaiveBayes.model",
							//"C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels/65-3_2T_12else_50000.AutoWeka.model"
					}
			);
			
			classifier.execute("20170101", "20181231", null);
			//classifier.execute("20180101", "20181231", null);
			//classifier.execute(args[0], args[1], args[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
