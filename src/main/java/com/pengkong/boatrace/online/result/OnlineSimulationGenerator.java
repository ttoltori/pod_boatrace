package com.pengkong.boatrace.online.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.property.SqlTemplate;
import com.pengkong.boatrace.exp10.simulation.AbstractSimulationCreator;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.mybatis.entity.OlRace;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.DatabaseUtil;

public class OnlineSimulationGenerator {
	Logger logger = LoggerFactory.getLogger(OnlineSimulationGenerator.class);
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	/** sql template */
	SqlTemplate sqlTpl = SqlTemplate.getInstance();
	
	/** simulation結果生成クラス */
	AbstractSimulationCreator simulationCreator;
	
	public OnlineSimulationGenerator() {
		simulationCreator = new OnlineSimulationCreator();
		simulationCreator.setResultCreator(new OnlineResultCreator());
	}
	
	public List<MlResult> execute(OlRace race) throws Exception {
		DBRecord rec = loadDB(race.getYmd(), race.getJyocd(), race.getRaceno().toString());
		
		if (rec == null) {
			return new ArrayList<>();	
		}
		
		return simulationCreator.execute(rec);
	}
	
	/** レース１件の情報を取得する */
	DBRecord loadDB(String ymd, String jyoCd, String raceNo) throws Exception {
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		try {
			CustomMapper customMapper = session.getMapper(CustomMapper.class);
			
			String sql = sqlTpl.get("s-online");
			sql = sql.replace("{ymd}", ymd);
			sql = sql.replace("{jyocd}", jyoCd);
			sql = sql.replace("{raceno}", raceNo);
			HashMap<String, String> mapParam = new HashMap<>();
			mapParam.put("sql", sql);

			// 디비 데이터 일람 취득
			List<DBRecord> results = customMapper.selectSql(mapParam);
			if (results.size() != 1) {
				logger.error("online race has no data. results.size()=" + results.size() + ", sql=" + sql );
				return null;
			}

			return results.get(0);
		} finally {
			DatabaseUtil.close(session);
		}
	}
}
