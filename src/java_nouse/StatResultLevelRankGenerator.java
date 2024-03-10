package com.pengkong.boatrace.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatProperty;
import com.pengkong.boatrace.mybatis.client.ProcedureExecutorMapper;
import com.pengkong.boatrace.mybatis.entity.ParamGenerateStatLevelRank;

public class StatResultLevelRankGenerator {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private Logger logger = LoggerFactory.getLogger(StatResultLevelRankGenerator.class);

	public StatResultLevelRankGenerator() {
	}
	
	public void execute(String[] args) throws Exception {
		String fromYmd = args[0];
		String toYmd = args[1];
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);
		
		logger.debug("StatResultLevelRankGenerator execute" + toYmd);

		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

		// open session with auto-transaction mode = false
		try (SqlSession session = sqlSessionFactory.openSession(false)) {
			ProcedureExecutorMapper procedureExecutorMapper = session.getMapper(ProcedureExecutorMapper.class);
			ParamGenerateStatLevelRank param = null;
			
			while (currDate.compareTo(toDate) <= 0) {
				String yyyyMMdd = sdf.format(currDate);
				System.out.println(yyyyMMdd);
				
				param = new ParamGenerateStatLevelRank(
						yyyyMMdd, // ymd
						args[2], // bettype 
						args[3], // paramStatStartYmd
						Integer.parseInt(args[4]), // paramMinPatternCnt 
						Integer.parseInt(args[5]), // paramMaxPatternCnt
						new BigDecimal(args[6]),
						new BigDecimal(args[7]),
						new BigDecimal(args[8]),
						new BigDecimal(args[9]),
						BoatProperty.DIRECTORY_STASTICS);
			
				procedureExecutorMapper.executeGenerateStatLevelRank(param);
				
				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
			}

			session.commit();
		}
	}
	

	public static void main(String[] args) {
		try {
			BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/race_result_rebuild.properties");
			new StatResultLevelRankGenerator().execute(args);
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
