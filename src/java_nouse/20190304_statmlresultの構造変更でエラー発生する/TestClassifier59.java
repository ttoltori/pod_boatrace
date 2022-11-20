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
import com.pengkong.boatrace.weka.classifiers.nouse.Boat59;
import com.pengkong.common.collection.HashMapList;

public class TestClassifier59 {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	Boat59 boatClassifier = new Boat59();
	String bettype;
	String bettypeName;
	String description;
	String jyoCd;
	
	HashMapList<HashMap<String, ?>> mapListYmd = new HashMapList<>();
	BeforeOddsManager oddsManager = BeforeOddsManager.getInstance();
	public TestClassifier59() {
	}

	public void initialize(String bettype, String bettypeName,String description, String jyoCd, String[] classVal, String modelFilepath) throws Exception {
		this.bettype = bettype;
		this.bettypeName = bettypeName;
		this.description = description;
		this.jyoCd = jyoCd;
		boatClassifier.initialize(classVal, modelFilepath);
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
			
			// select 56
			HashMap<String, String> mapParam = new HashMap<>();
			mapParam.put("fromYmd", fromYmd);
			mapParam.put("toYmd", toYmd);
			List<HashMap<String, ?>> listHashmap = customMapper.select59(mapParam);
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
							if (kumiban.length() == 3) {
								kumiban = kumiban.substring(0, 2);
							}
							
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
			BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/stat_2T.properties");
			TestClassifier59 classifier = new TestClassifier59();
			classifier.initialize(
					"2T", 
					"nirentan",
					"59_2T_12-56_decompose_80000_AutoWeka", 
					"", 
					new String[] {"121","122","123","124","125","126",
							"131","132","133","134","135","136",
							"141","142","143","144","145","146",
							"15","16","21","23","24","25","26","31","32","34","35","36","41","42","43","45","46","51","52","53","54","56","61","62","63","64","65"},
					"C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels/59_2T_12-56_decompose_80000_AutoWeka.model" );
			
			//classifier.execute("20160101", "20171231", "1414");
			classifier.execute("20160101", "20171231", null);
			//classifier.execute(args[0], args[1], args[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
