package com.pengkong.boatrace.weka.deprecated;

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
import com.pengkong.boatrace.service.manager.OddsManager;
import com.pengkong.boatrace.weka.classifiers.BoatClassifierDefault;
import com.pengkong.common.collection.HashMapList;

public class MLSimulator {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	List<BoatClassifierDefault> listClassifier = new ArrayList<>();
	String bettype;
	String bettypeName;
	String description;
	String fromYmd;
	String toYmd;
	
	HashMapList<HashMap<String, ?>> mapListYmd = new HashMapList<>();
	OddsManager oddsManager = OddsManager.getInstance();
	
	public MLSimulator() {
	}

	public void initialize(String bettype, String bettypeName,String description, 
			String dir, String[] defFileNames, String fromYmd, String toYmd) throws Exception {
		this.bettype = bettype;
		this.bettypeName = bettypeName;
		this.description = description;
		this.fromYmd = fromYmd;
		this.toYmd = toYmd;
		
		// create classifiers
		for (int i = 0; i < defFileNames.length; i++) {
			BoatClassifierDefault clf = new BoatClassifierDefault();
			String defFilepath = dir + "/" + defFileNames[i];
			String modelFilepath = dir + "/model_bin/" +  defFileNames[i].split("\\.")[0] + ".model";
			clf.initialize(defFilepath, modelFilepath);
			listClassifier.add(clf);
		}
		
		// initialize odds manager
		oddsManager.initialize();
		
	}
	
	public void execute() throws Exception {
		
		String resource = "mybatis-config.0.xml";
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
								bettingCnt++;
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
//		args = new String[] {
//				"2T", // bettype
//				"nirentan", // bettype name 
//				"model18", // description
//				"C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels", // workdir
//				"6_2T_12-16_20100101-20190220.def", // 콤마구분 복수지정 가능
//				"20190110", // test from ymd
//				"20190322" //test to ymd
//		};
		
    	if (args.length != 7) {
    		System.out.println("Usage: MLSimulator {bettype} {bettype name} {description} {workdir} {define files} {fromYmd} {toYmd}");
    		System.out.println("ex: MLSimulator 2F nirenhuku C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels model17 \"2F_01-01.def,2F_01-02.def,2F_01-03.def\" 20190110 20190322");
    		System.exit(1);
    	}
		
		try {
			String bettype = args[0];
			String bettypeName = args[1];
			String description = args[2];
			String workdir = args[3];
			String strDefFiles = args[4];
			String[] defFiles = strDefFiles.split(",");
			String fromYmd = args[5];
			String toYmd = args[6];
			
			BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/race_result_rebuild.properties");
			MLSimulator simulator = new MLSimulator();
			
			simulator.initialize(bettype, bettypeName, description, workdir, defFiles, fromYmd, toYmd);
			simulator.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
