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
import com.pengkong.boatrace.weka.classifiers.nouse.Boat54;
import com.pengkong.common.collection.HashMapList;

public class TestClassifier54 {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	Boat54 boatClassifier = new Boat54();
	String bettype;
	String description;
	
	HashMapList<HashMap<String, ?>> mapListYmd = new HashMapList<>();
	BeforeOddsManager beforeOddsManager = BeforeOddsManager.getInstance();
	public TestClassifier54() {
	}

	public void initialize(String bettype, String description, String[] classVal, String modelFilepath) throws Exception {
		this.bettype = bettype;
		this.description = description;
		boatClassifier.initialize(classVal, modelFilepath);
		// initialize odds manager
		beforeOddsManager.initialize();
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
			
			// select 54
			HashMap<String, String> mapParam = new HashMap<>();
			mapParam.put("fromYmd", fromYmd);
			mapParam.put("toYmd", toYmd);
			mapParam.put("wrWaku", wrWaku);
			List<HashMap<String, ?>> listHashmap = customMapper.select54(mapParam);
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
				beforeOddsManager.load(ymd);
				
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
							OddsItemEx oddsItemEx = beforeOddsManager.getOddsItemEx(jyoCd, String.valueOf(raceNo), "2F", kumiban); 
							statMlResult.setBetKumiban(kumiban);
							if (oddsItemEx != null) {
								statMlResult.setBetOdds(BigDecimal.valueOf(oddsItemEx.value).setScale(2, RoundingMode.FLOOR));
								statMlResult.setBetOddsrank(oddsItemEx.rank);
							}
							statMlResult.setResultKumiban((String)hashmap.get("nirenhukuno"));
							Integer prize = (Integer)hashmap.get("nirenhukuprize");
							BigDecimal resultOdds = new BigDecimal((float)prize / 100f).setScale(2, RoundingMode.FLOOR);
							statMlResult.setResultOdds(resultOdds);
							statMlResult.setResultOddsrank((Integer)hashmap.get("nirenhukupopular"));
							if (kumiban.equals((String)hashmap.get("nirenhukuno"))) {
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
			procParam.put("yearsCount", new Integer(1));
			procParam.put("betType", "2F");
			procParam.put("rankCols", new Integer(2));
			procParam.put("description", "54_2F_1414_NB_R");
			procParam.put("path", "C:\\weka_training\\result\\");
			
			procedureExecutorMapper.executeOutputWeka(procParam);
			
			session.commit();
			
		}
	}
	
	public static void main(String[] args) {
		try {
			BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/stat_2F.properties");
			TestClassifier54 classifier = new TestClassifier54();
			classifier.initialize(
					"2F", 
					"54_2F_1414_NB_R", 
					new String[] {"12","13","14","15","16","45","34","24","else"},
					"C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels/54_2F_rank3ptn-1414_NaiveBayes_Resample.model" );
			
			classifier.execute("20170101", "20171231", "1414");
			//classifier.execute(args[0], args[1], args[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
