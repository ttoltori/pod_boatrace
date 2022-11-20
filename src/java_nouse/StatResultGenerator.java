package com.pengkong.boatrace.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatProperty;
import com.pengkong.boatrace.mybatis.client.ProcedureExecutorMapper;
import com.pengkong.boatrace.mybatis.entity.ParamGenerateStat;
import com.pengkong.boatrace.mybatis.entity.ParamGenerateStatLevelRank;

public class StatResultGenerator {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private Logger logger = LoggerFactory.getLogger(StatResultGenerator.class);

	public StatResultGenerator() {
	}

	public void execute(ParamGenerateStat param) throws Exception {
		logger.debug("StatResultGenerator execute" + param.getYmd());

		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

		// open session with auto-transaction mode = false
		try (SqlSession session = sqlSessionFactory.openSession(false)) {
			ProcedureExecutorMapper procedureExecutorMapper = session.getMapper(ProcedureExecutorMapper.class);

			procedureExecutorMapper.executeGenerateStat(param);
			session.commit();
		}
	}

	public static void main(String[] args) {
		try {
			BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/race_result_rebuild.properties");
			new StatResultGenerator().execute(
					new ParamGenerateStat("20170504", "2F", 
							10, 9999, 
							BigDecimal.valueOf(0.2), BigDecimal.valueOf(1),
							BigDecimal.valueOf(1.3), BigDecimal.valueOf(9999), 
							BoatProperty.DIRECTORY_STASTICS));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
