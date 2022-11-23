package com.pengkong.boatrace.exp10.simulation.data;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.property.PatternTemplate;
import com.pengkong.boatrace.exp10.property.SqlTemplate;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.DatabaseUtil;

public class SimulationDBRaceDataLoader extends AbstractRaceDataLoader {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/** sql template */
	SqlTemplate sqlTpl = SqlTemplate.getInstance();
	
	/** pattern template */
	PatternTemplate ptnTpl = PatternTemplate.getInstance();
	
	@Override
	protected List<DBRecord> excute(String fromYmd, String toYmd) throws Exception {
		
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		CustomMapper customMapper = session.getMapper(CustomMapper.class);
		
		String sql = sqlTpl.get(prop.getString("result_sql_id"));
		sql = sql.replace("{pattern_id}", "simulation");
		sql = sql.replace("{pattern_sql}", "'simulation'");
		sql = sql.replace("{fromYmd}", fromYmd);
		sql = sql.replace("{toYmd}", toYmd);
		sql = sql.replace("{grade_condition}", prop.getString("grade_condition"));
		
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
}