package com.pengkong.boatrace.exp10.simulation.step2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.property.SqlTemplate;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * evaluationリストファイルをロードして保持する. ！！！simul_2に特化する
 * 
 * @author ttolt
 *
 */
public class Step2PropertyLoaderFSB  {
	/** 実験プロパティ */
	protected MLPropertyUtil prop = MLPropertyUtil.getInstance();

	protected Logger logger = LoggerFactory.getLogger(Step2PropertyLoaderFSB.class);

	/** sql template */
	SqlTemplate sqlTpl = SqlTemplate.getInstance();
	
	/** ex) key=1.01~1,19  value=TermPair */
	Map<String, TermPair> mapIncrTermpair;
	
	protected SqlSession session = null;
	
	public void setStep2CommonProperties() throws Exception {
		mapIncrTermpair = new HashMap<>();
		loadDB();
		
		TermPair first = mapIncrTermpair. values().iterator().next();
		
		prop.putProperty("group_sql_id", first.getGroupSqlId());
		prop.putProperty("factor", first.getFactor());
		prop.putProperty("simul_custom", first.getCustom());
		
		StringBuilder sb = new StringBuilder();
		for (TermPair tPair : mapIncrTermpair.values()) {
			sb.append(tPair.getIncr());
			sb.append(",");
		}
		prop.putProperty("simul_incr", sb.substring(0, sb.length()-1));
		
		prop.putProperty("result_eval_id", first.getResultEvalId());
	}
	
	public void setStep2BonusProperties() {
		prop.putProperty("bonus_bork", "x");
		prop.putProperty("bonus_bor", "x");

		// ex) x-and -> x,and
		String[] token = prop.getString("simul_bonus_term").split(Delimeter.DASH.getValue());

		prop.putProperty("bonus_bork", mapIncrTermpair.get(prop.getString("incr")).getBonusBork(token[0]));
		prop.putProperty("bonus_bor", mapIncrTermpair.get(prop.getString("incr")).getBonusBor(token[1]));
	}
	
	
	/**
	 * simulation step 1の結果から選んだevaluationをロードする。
	 * 
	 * @throws Exception
	 */
	void loadDB() throws Exception {
		
		session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		try {
			CustomMapper customMapper = session.getMapper(CustomMapper.class);

			String sql = sqlTpl.get(prop.getString("simul2_sql_id"));

			sql = sql.replace("{grade_type}", prop.getString("simul_grade_type"));
			sql = sql.replace("{simul2_limit}", prop.getString("simul2_limit"));
			sql = sql.replace("{bettype}", prop.getString("bettype"));
			sql = sql.replace("{kumiban}", prop.getString("kumiban"));
			sql = sql.replace("{ranking}", prop.getString("ranking"));
			
			String simul2Custom = prop.getString("simul2_custom");
			if (simul2Custom.equals("wk")) {
				sql = sql.replace("{simul2_custom}", "id_custom = 'wk'");
			} else {
				sql = sql.replace("{simul2_custom}", "true");
			}
			

			HashMap<String, String> mapParam = new HashMap<>();
			mapParam.put("sql", sql);

			// 디비 데이터 일람 취득
			List<DBRecord> results = customMapper.selectSql(mapParam);
			if (results.size() <= 0) {
				throw new WarningException("db has no data.");
			}

			// simulation step 2 用propertiesのlistを生成する.
			createStep2ProperteisList(results);
			
		} finally {
			DatabaseUtil.close(session);
		}
	}

	void createStep2ProperteisList(List<DBRecord> results) {
		HashMapList<DBRecord> mapIncr = new HashMapList<>();
		// id_incrで分類
		for (DBRecord rec : results) {
			mapIncr.addItem(rec.getString("id_incr"), rec);
		}
		
		for (String key : mapIncr.keySet()) {
			List<DBRecord> pair = mapIncr.get(key);  
			// term1, term2２個でないと変
			if (pair.size() != 2) {
				logger.warn("number of terms is invalid. " + mapIncr.get(key).size());
				continue;
			}
			
			if (pair.get(0).getDouble("incomerate") <= 1.0 || pair.get(1).getDouble("incomerate") <= 1.0) {
				// 収益率がterm1, term2共に黒字でなければskip
				continue;
			}
			
			mapIncrTermpair.put(key,  new TermPair(pair.get(0), pair.get(1)));
		}
	}
	
	
	
	public static void main(String[] args) {
		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
		
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
