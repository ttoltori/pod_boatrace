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
import com.pengkong.boatrace.mybatis.client.StatMlMultiresultMapper;
import com.pengkong.boatrace.mybatis.entity.StatMlMultiresult;
import com.pengkong.boatrace.mybatis.entity.StatMlMultiresultExample;
import com.pengkong.boatrace.service.manager.BeforeOddsManager;
import com.pengkong.boatrace.weka.classifiers.BoatClassifierDefault;
import com.pengkong.common.collection.HashMapList;

public class MLMultiSimulator {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	List<BoatClassifierDefault> listClassifier = new ArrayList<>();
	
	String bettype;
	String bettypeName;
	String description;
	boolean useRankChangeFilter = false;
	
	HashMapList<HashMap<String, ?>> mapListYmd = new HashMapList<>();
	BeforeOddsManager oddsManager = BeforeOddsManager.getInstance();
	public MLMultiSimulator() {
	}

	public void initialize(String bettype, String bettypeName,String description, 
			String dir, String[] defFileNames) throws Exception {
		this.bettype = bettype;
		this.bettypeName = bettypeName;
		this.description = description;
		
		// create classifiers
		for (int i = 0; i < defFileNames.length; i++) {
			BoatClassifierDefault clf = new BoatClassifierDefault();
			clf.initialize(dir, defFileNames[i]);
			listClassifier.add(clf);
		}
		
//		initializeRankChangeClassifier();
		
		
		// initialize odds manager
		oddsManager.initialize();
	}

//	private void initializeRankChangeClassifier() throws Exception {
//		BoatClassifier classfier;
//
//		classfier = new BoatClassifierOddsRankChange(); 
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
			StatMlMultiresultMapper statMlResultMapper = session.getMapper(StatMlMultiresultMapper.class);
			StatMlMultiresultExample statMlResultExample = new StatMlMultiresultExample();

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
					// load before odds
					oddsManager.load(ymd);
					
					for (HashMap<String, ?> hashmap : listHashmapYmd) {
						StatMlMultiresult statMlResult = new StatMlMultiresult();
						String jyoCd = (String)hashmap.get("jyocd");
						Short raceNo = ((Integer)hashmap.get("raceno")).shortValue();
						statMlResult.setYmd(ymd);
						statMlResult.setJyocd(jyoCd);
						statMlResult.setRaceno(raceNo);
						statMlResult.setBettype(bettype);
						statMlResult.setDescription(description);
						
						statMlResult.setResultKumiban((String)hashmap.get(bettypeName + "no"));
						Integer prize = (Integer)hashmap.get(bettypeName + "prize");
						BigDecimal resultOdds = new BigDecimal((float)prize / 100f).setScale(2, RoundingMode.FLOOR);
						statMlResult.setResultOdds(resultOdds);
						statMlResult.setResultOddsrank((Integer)hashmap.get(bettypeName + "popular"));
						
						String kumiban = null;
						String kumibanTmp = null;
						
						String sModelNo = "";
						String sBetKumiban = "";
						String sBetOdds = "";
						String sBetOddsRank = "";
						String sHitY = "";
						String sHitN = "";
						String sBetAmt = "";
						String sHitAmt = "";
						int modelNo = 0;
						int classifierCnt = 0;
						for (BoatClassifierDefault boatClassifier : listClassifier) {
							modelNo++;
							boolean isBet = false;
							kumibanTmp = boatClassifier.predictFromHashmap(hashmap);
							if (kumibanTmp != null && !kumibanTmp.equals("else")) {
								if (useRankChangeFilter) {
									//BoatClassifier rankChangeClassifier = mapRankChangeClassfier.get(kumibanTmp);
									//String kumibanFiltered = rankChangeClassifier.predictFromHashmap(hashmap);
									//if (kumibanFiltered != null && !kumibanFiltered.equals("else") && kumibanFiltered.equals(kumibanTmp)) {
										isBet = true;
									//}
								} else {
									isBet = true;
								}
							}
							
							if (!isBet) {
								sModelNo += (modelNo + ",");
								sBetKumiban += "E,";
								sBetOdds += "0,";
								sBetOddsRank += "0,";
								sHitY += "0,";
								sHitN += "0,";
								sBetAmt += "0,";
								sHitAmt += "0,";
								continue;
							}
							
							kumiban = kumibanTmp;//.substring(0, 2);
							sModelNo += (modelNo + ",");
							sBetKumiban += (kumibanTmp + ",");
							OddsItemEx oddsItemEx = oddsManager.getOddsItemEx(jyoCd, String.valueOf(raceNo), bettype, kumiban);
							if (oddsItemEx != null) {
								sBetOdds += ( BigDecimal.valueOf(oddsItemEx.value).setScale(2, RoundingMode.FLOOR) + ",");
								sBetOddsRank += (oddsItemEx.rank + ",");
							} else {
								sBetOdds += "0,";
								sBetOddsRank += "0,";
							}
							sBetAmt += "100,";
							if (kumiban.equals((String)hashmap.get(bettypeName + "no"))) {
								sHitY += "1,";
								sHitN += "0,";
								sHitAmt += (prize + ","); 
								
							} else {
								sHitY += "0,";
								sHitN += "1,";
								sHitAmt += "0,";
							}
							classifierCnt++;
						}
						
						if (classifierCnt > 0) {
							statMlResult.setModelno(sModelNo.substring(0,sModelNo.length() - 1));
							statMlResult.setBetKumiban(sBetKumiban.substring(0,sBetKumiban.length() - 1));
							statMlResult.setBetOdds(sBetOdds.substring(0,sBetOdds.length() - 1));
							statMlResult.setBetOddsrank(sBetOddsRank.substring(0,sBetOddsRank.length() - 1));
							statMlResult.setHity(sHitY.substring(0,sHitY.length() - 1));
							statMlResult.setHitn(sHitN.substring(0,sHitN.length() - 1));
							statMlResult.setBetamt(sBetAmt.substring(0, sBetAmt.length()-1));
							statMlResult.setHitamt(sHitAmt.substring(0, sHitAmt.length()-1));
							
							statMlResultMapper.insert(statMlResult);
							betcnt++;
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
			
			procedureExecutorMapper.executeOutputMultiWeka(procParam);
			
			session.commit();
			
		}
	}
	
	public static void main(String[] args) {
		args = new String[] {
				"1T", 
				"tansyo",
				"1T_12_attr_filter",
				"C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels/work",
				"2T_12-16_else.def"
		};
		
    	if (args.length != 5) {
    		System.out.println("Usage: MLSimulator {bettype} {bettype name} {description} {work directory} {define files}");
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
			MLMultiSimulator classifier = new MLMultiSimulator();
			classifier.initialize(bettype, bettypeName, description, workdir, defFiles);
			classifier.execute("20180101", "20181231", null);
			//classifier.execute("20180101", "20181231", null);
			//classifier.execute(args[0], args[1], args[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
