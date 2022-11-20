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
import com.pengkong.boatrace.mybatis.entity.ParamGenerateBoatStat;
import com.pengkong.boatrace.mybatis.entity.ParamGeneratePower;
import com.pengkong.boatrace.simulation.model.BettingRule;

/**
 * 20180909 
 * 組番固定の統計データ生成a
 * @author qwerty
 *
 */
public class BoatStatGenerator {
	private Logger logger = LoggerFactory.getLogger(BoatStatGenerator.class);
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public BettingRule rule;
	public BoatStatGenerator(BettingRule rule) {
		this.rule = rule;
	}

	public void execute(String yyyyMMdd) throws Exception {
		logger.debug("BoatStatGenerator execute" + yyyyMMdd);
		
		ParamGenerateBoatStat param = new ParamGenerateBoatStat(
				rule.getString("fromYmd"), 
				yyyyMMdd, 
				rule.getString("type"), 
				rule.getString("betTypeName"), 
				rule.getString("type2"),
				rule.getString("pattern"),
				rule.getString("table"),
				rule.getInt("minPatternCnt"), 
				rule.getBigDecimal("minIncomeRate"),
				rule.getBigDecimal("maxIncomeRate"), 
				BoatProperty.DIRECTORY_STASTICS
				);
				
		execute(param);
	}
	
	public void execute(ParamGenerateBoatStat param) throws Exception {
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

		// open session with auto-transaction mode = false
		try (SqlSession session = sqlSessionFactory.openSession(false)) {
			ProcedureExecutorMapper procedureExecutorMapper = session.getMapper(ProcedureExecutorMapper.class);

			procedureExecutorMapper.executeGenerateBoatStat(param);
			session.commit();
		}
	}

	public static void main(String[] args) {
		try {
			BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/stat_2T.properties");
			BettingRule rule = new BettingRule(BoatProperty.DIRECTORY_PROPERTY + "stat_2T.properties");
			
			new BoatStatGenerator(rule).execute("20170125");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
