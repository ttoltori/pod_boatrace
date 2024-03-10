package com.pengkong.boatrace.weka.deprecated;

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
import com.pengkong.boatrace.converter.HashMap2StatMlResult2;
import com.pengkong.boatrace.model.OddsItemEx;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.mybatis.client.StatMlResult2Mapper;
import com.pengkong.boatrace.mybatis.client.StatMlResultMapper;
import com.pengkong.boatrace.mybatis.entity.StatMlResult;
import com.pengkong.boatrace.mybatis.entity.StatMlResult2;
import com.pengkong.boatrace.mybatis.entity.StatMlResult2Example;
import com.pengkong.boatrace.mybatis.entity.StatMlResultExample;
import com.pengkong.boatrace.service.manager.BeforeOddsManager;
import com.pengkong.boatrace.weka.classifiers.BoatClassifierDefault;
import com.pengkong.boatrace.weka.classifiers.manager.ClassifierManager;
import com.pengkong.common.collection.HashMapList;

public class MLMultiSimulator2 {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	List<BoatClassifierDefault> listClassifier;
	String bettype;
	String description;
	String modelFilterStr;
	String fromYmd;
	String toYmd;
	
	HashMapList<HashMap<String, ?>> mapListYmd = new HashMapList<>();
	BeforeOddsManager oddsManager = BeforeOddsManager.getInstance();
	ClassifierManager clm = new ClassifierManager();
	
	public MLMultiSimulator2() {
	}

	public void initialize(String bettype, String description, 
			String modelFilterStr, String fromYmd, String toYmd) throws Exception {
		this.bettype = bettype;
		this.description = description;
		this.modelFilterStr = modelFilterStr; 
		this.fromYmd = fromYmd;
		this.toYmd = toYmd;
		
		clm.loadClassifierInfo(BoatProperty.DIRECTORY_WEKAMODELS, "model_release", modelFilterStr);
	}
	
	public void execute() throws Exception {
		
		String resource = "mybatis-config.0.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		
		// open session with auto-transaction mode = false
		try(SqlSession session = sqlSessionFactory.openSession(false)) {
			CustomMapper customMapper = session.getMapper(CustomMapper.class);
			StatMlResultMapper statMlResultMapper = session.getMapper(StatMlResultMapper.class);
			StatMlResult2Mapper statMlResult2Mapper = session.getMapper(StatMlResult2Mapper.class);
			StatMlResultExample statMlResultExample = new StatMlResultExample();
			StatMlResult2Example statMlResult2Example = new StatMlResult2Example();

			// delete stat_ml_result_2 by description
			statMlResult2Example.clear();
			statMlResult2Example.createCriteria().andDescriptionEqualTo(description);
			statMlResult2Mapper.deleteByExample(statMlResult2Example);
			
			HashMap<String, String> mapParam = new HashMap<>();
			mapParam.put("fromYmd", fromYmd);
			mapParam.put("toYmd", toYmd);
			mapParam.put("description", description);
			List<HashMap<String, ?>> listHashmap = customMapper.select70Step2(mapParam);
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
					// load weka models
					listClassifier = clm.getClassifierList(bettype, modelFilterStr.split("_")[2], ymd);
					
					for (HashMap<String, ?> hashmap : listHashmapYmd) {
						String kumiban = null;
						int classifierCnt = 0;
						int bettingCnt = 0;
						for (BoatClassifierDefault boatClassifier : listClassifier) {
							kumiban = boatClassifier.predictFromHashmap(hashmap);
							if (kumiban != null && kumiban.equals("1")) {
								bettingCnt++;
							}
							classifierCnt++;
						}
						
						if (classifierCnt == bettingCnt) {
							StatMlResult2 statMlResult2 = HashMap2StatMlResult2.convert(hashmap);
							statMlResult2Mapper.insert(statMlResult2);
							betcnt++;
							// daily balance
							dailyBalance -= statMlResult2.getBetamt();
							dailyBalance += statMlResult2.getHitamt();
						}
						
					}
				}
				
				System.out.println(ymd + ": " + betcnt + " bet, daily balance= " + dailyBalance);

				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
			}

/*			
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
*/			
			session.commit();
			
		}
	}
	
	public static void main(String[] args) {
		args = new String[] {
				"2T",
				"multi_week_103_2T_1241_entry", // description
				"201_2T_12-41", // 콤마구분 복수지정 가능
				"20190101", // test from ymd
				"20190510" //test to ymd
		};
		
    	if (args.length != 5) {
    		System.out.println("Usage: MLMultiSimulator2 {description} {model filter string} {fromYmd} {toYmd}");
    		System.out.println("ex: MLSimulator2 2F model17 \"55_,56_\" 20190110 20190322");
    		System.exit(1);
    	}
		
		try {
			String bettype = args[0];
			String description = args[1];
			String strModelFilter = args[2];
			String fromYmd = args[3];
			String toYmd = args[4];
			
			BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/race_result_rebuild.properties");
			MLMultiSimulator2 simulator = new MLMultiSimulator2();
			
			simulator.initialize(bettype, description, strModelFilter, fromYmd, toYmd);
			simulator.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
