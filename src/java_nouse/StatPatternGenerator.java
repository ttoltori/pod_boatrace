package com.pengkong.boatrace.util;

import java.io.InputStream;
import java.text.SimpleDateFormat;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatProperty;
import com.pengkong.boatrace.mybatis.client.ProcedureExecutorMapper;

public class StatPatternGenerator {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private Logger logger = LoggerFactory.getLogger(StatPatternGenerator.class);

	public StatPatternGenerator() {
	}

	public void execute() throws Exception {
		logger.debug("StatPatternGenerator execute");
		
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

		// open session with auto-transaction mode = false
		try (SqlSession session = sqlSessionFactory.openSession(false)) {
			ProcedureExecutorMapper procedureExecutorMapper = session.getMapper(ProcedureExecutorMapper.class);

			procedureExecutorMapper.executeStatMakePattern();
			
			session.commit();
		}
	}

	public static void main(String[] args) {
		try {
			BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/race_result_rebuild.properties");
			new StatPatternGenerator().execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
