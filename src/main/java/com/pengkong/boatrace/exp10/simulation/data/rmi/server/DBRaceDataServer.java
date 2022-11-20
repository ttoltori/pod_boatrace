package com.pengkong.boatrace.exp10.simulation.data.rmi.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.property.SqlTemplate;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.collection.HashMapList;

public class DBRaceDataServer {
	Logger logger = LoggerFactory.getLogger(DBRaceDataServer.class);
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/** sql template */
	SqlTemplate sqlTpl = SqlTemplate.getInstance();
	
	/** key=ymd, value=list of DBRecord */
	HashMapList<DBRecord> mapListYmd;
	
	public List<DBRecord> getSimulationData(String fromYmd, String toYmd) throws Exception {
		List<DBRecord> result = new ArrayList<>();
		
		int days = BoatUtil.daysBetween(fromYmd, toYmd) + 1;
		String ymd = fromYmd;
		for (int i = 0; i < days; i++) {
			List<DBRecord> dailyResult = mapListYmd.get(ymd);
			if (dailyResult != null) {
				result.addAll(dailyResult);
			} else {
				logger.warn("daily simulation data does not exist. " + ymd);
			}
			ymd = BoatUtil.daysAfterYmd(ymd, 1);
		}
		
		return result;
	}
	
	void load() throws Exception {
		mapListYmd = new HashMapList<>();
		
		String[] period = prop.getString("dataserver_simulation_period").split(Delimeter.WAVE.getValue());
		String gradeCondition = prop.getString("dataserver_simulation_grade");
		String resource = prop.getString("target_db_resource");
		
		
		List<DBRecord> listData = loadDB(period[0], period[1], resource, gradeCondition);
		
		// 결과범위내 데이터를 날짜별로 분류
		for (DBRecord rec : listData) {
			String ymd = rec.getString("ymd");
			mapListYmd.addItem(ymd, rec);
		}
		
		logger.info("race data loaded from db. " + period[0] + "~" + period[1]);
	}
	
	List<DBRecord> loadDB(String fromYmd, String toYmd, String resource, String gradeCondition) throws Exception {
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		CustomMapper customMapper = session.getMapper(CustomMapper.class);
		
		String sql = sqlTpl.get("s-all");
		sql = sql.replace("{fromYmd}", fromYmd);
		sql = sql.replace("{toYmd}", toYmd);
		sql = sql.replace("{pattern_id}", "simulation");
		sql = sql.replace("{grade_condition}", gradeCondition);
		sql = sql.replace("{pattern_sql}", "('simulation')");
		
		HashMap<String, String> mapParam = new HashMap<>();
		mapParam.put("sql", sql);
		
		// 디비 데이터 일람 취득
		List<DBRecord> results = customMapper.selectSql(mapParam);
		if (results.size() <= 0) {
			throw new Exception("db has no data. sql=" + sql);
		}

		DatabaseUtil.close(session);
		return results; 
	}
	
	public static void main(String[] args) {
		try {
			MLPropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties");
			DBRaceDataServer server = new DBRaceDataServer();
			server.load();
			List<DBRecord> result = server.getSimulationData("20220130", "20220131");
			
			System.out.println(result.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
