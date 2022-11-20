package com.pengkong.boatrace.weka.notuse;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
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
import com.pengkong.boatrace.weka.classifiers.nouse.BoatFilteredClassifier;
import com.pengkong.boatrace.weka.classifiers.nouse.BoatSecondClassifier;
import com.pengkong.common.collection.HashMapList;

public class TestClassifier60 {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	BoatFilteredClassifier boatClassifier = new BoatFilteredClassifier();
	BoatSecondClassifier boatSecondClassifier = new BoatSecondClassifier();
	String bettype;
	String bettypeName;
	String description;
	String jyoCd;
	
	HashMapList<HashMap<String, ?>> mapListYmd = new HashMapList<>();
	BeforeOddsManager oddsManager = BeforeOddsManager.getInstance();
	public TestClassifier60() {
	}

	public void initialize(String bettype, String bettypeName,String description, String jyoCd, String[] classVal, String modelFilepath) throws Exception {
		this.bettype = bettype;
		this.bettypeName = bettypeName;
		this.description = description;
		this.jyoCd = jyoCd;
		boatClassifier.initialize(classVal, modelFilepath);
		boatSecondClassifier.initialize(classVal, modelFilepath);
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
			
			// select 56 (同じSQLなので流用する）
			HashMap<String, String> mapParam = new HashMap<>();
			mapParam.put("fromYmd", fromYmd);
			mapParam.put("toYmd", toYmd);
			List<HashMap<String, ?>> listHashmap = customMapper.select56(mapParam);
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
				
				// load before odds
				oddsManager.load(ymd);
				
				List<HashMap<String, ?>> listHashmapYmd = mapListYmd.get(ymd);
				if (listHashmapYmd != null) {
					for (HashMap<String, ?> hashmap : listHashmapYmd) {
						StatMlResult statMlResult = new StatMlResult();
						String jyoCd = (String)hashmap.get("jyocd");
						Short raceNo = ((Integer)hashmap.get("raceno")).shortValue();
						statMlResult.setYmd(ymd);
						statMlResult.setJyocd(jyoCd);
						statMlResult.setRaceno(raceNo);
						statMlResult.setBettype(bettype);
						statMlResult.setDescription(description);
						String kumiban = boatClassifier.predictFromHashmap(hashmap);
						
						if (kumiban != null && kumiban != "else") {
							
							
							String kumibanTmp;
							kumibanTmp = kumiban;
							
							OddsItemEx oddsItemEx = oddsManager.getOddsItemEx(jyoCd, String.valueOf(raceNo), bettype, kumiban); 
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
			TestClassifier60 classifier = new TestClassifier60();
			classifier.initialize(
					"3T", 
					"sanrentan",
					"63-1_3T_123-132else_40000_SimpleLogistic", 
					"", 
					// new String[] {"23","16","24","34"},
					new String[] {"123","124","132", "else"},
					"C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels/63-1_3T_123-132else_40000_SimpleLogistic.model" );
			
			//classifier.execute("20160101", "20171231", "1414");
			classifier.execute("20160101", "20171231", null);
			//classifier.execute(args[0], args[1], args[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
