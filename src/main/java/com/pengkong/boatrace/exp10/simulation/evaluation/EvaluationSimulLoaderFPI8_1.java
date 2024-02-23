package com.pengkong.boatrace.exp10.simulation.evaluation;

import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.property.SqlTemplate;
import com.pengkong.boatrace.exp10.util.EvaluationHelper;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.StringUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * evaluationリストファイルをロードして保持する. ！！！simul_2に特化する
 * 
 * @author ttolt
 *
 */
public class EvaluationSimulLoaderFPI8_1 extends AbstractEvaluationLoader {
	/** sql template */
	SqlTemplate sqlTpl = SqlTemplate.getInstance();

	EvaluationSimulGroupFileGenerator groupFileGenerator;
	
	/** group定義DBロード */
	@Override
	void initialize() throws Exception {
		mapListEval = new HashMapList<>();
		groupFileGenerator = new EvaluationSimulGroupFileGenerator();
		
		loadDB();
	}

	/**
	 * group
	 * 
	 * @throws Exception
	 */
	void loadDB() throws Exception {
		session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		try {
			CustomMapper customMapper = session.getMapper(CustomMapper.class);

			String sql = sqlTpl.get(prop.getString("group_sql_id"));

			if (prop.getString("grade_type").equals("ip")) {
				sql = sql.replace("{result_type}", "1");
			} else if (prop.getString("grade_type").equals("SG")) {
				sql = sql.replace("{result_type}", "11");	
			} else {
				throw new IllegalStateException("undefined grade type.");
			}
			sql = sql.replace("{bettype}", prop.getString("bettype"));
			sql = sql.replace("{kumiban}", prop.getString("kumiban"));
			// sql = sql.replaceAll("\\{kumiban\\}", prop.getString("kumiban"));
			String incr = prop.getString("incr");
			if (!incr.contains(Delimeter.WAVE.getValue())) {
				throw new IllegalStateException("incrは「~」で分けて表現してください。");
			}
			String[] incrToken = incr.split(Delimeter.WAVE.getValue());
			sql = sql.replace("{incrmin}", incrToken[0]);
			sql = sql.replace("{incrmax}", incrToken[1]);

			sql = sql.replace("{modelno}", prop.getString("modelno"));			
			sql = sql.replace("{patternid}", prop.getString("patternid"));			
			
			HashMap<String, String> mapParam = new HashMap<>();
			mapParam.put("sql", sql);

			// 디비 데이터 일람 취득
			List<DBRecord> results = customMapper.selectSql(mapParam);
			if (results.size() <= 0) {
				throw new WarningException("db has no evaluation data.");
			}

			// Evaluation生成
			HashMap<String, String> mapDuplicateCheck = new HashMap<>();
			for (DBRecord rec : results) {
				// Evaluationファイル１行生成
				Evaluation eval = EvaluationHelper.createEvaluation(rec);
				
				eval =  EvaluationHelper.postFixRangeSelector(eval, prop.getString("range_selector"), prop.getString("bonus_pr"),
						prop.getString("bonus_bor"), prop.getString("bonus_bork"), prop.getString("bonus_borkbor"));

				String unkqueKey = eval.getUniqueKey();
				// 重複あり
				if (mapDuplicateCheck.containsKey(unkqueKey)) {
					logger.warn("Duplicate evaluation exists. " + eval);
					continue;
				}
				mapDuplicateCheck.put(unkqueKey, unkqueKey);
				
				mapListEval.addItem(eval.getBettypeKumibanModelNo(), eval);
				listEval.add(eval);
			}
			
			// group file 保存
			groupFileGenerator.generateWithEvaluations(sql, listEval);			
		} finally {
			DatabaseUtil.close(session);
		}
	}

	
	String convertModelInClause(String[] modelNos) {
		StringBuilder sb = new StringBuilder();
				
		for (String model : modelNos) {
			sb.append(StringUtil.quote(model.split(Delimeter.COMMA.getValue())));
			sb.append(",");
		}
		
		return sb.substring(0, sb.length()-1);
	}

	public static void main(String[] args) {
		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
		
		try {
			MLPropertyUtil.getInstance().addFile(propertyFilepath);
			MLPropertyUtil prop = MLPropertyUtil.getInstance();
			prop.putProperty("term", "833_1");
			prop.putProperty("bettype", "3T");
			prop.putProperty("kumiban", "123");
			prop.putProperty("factor", "i59");
			prop.putProperty("limit", "50");
			prop.putProperty("bonus_pr", "1~1=1");
			prop.putProperty("bonus_bor", "2~2=2");
			prop.putProperty("bonus_bork", "3~3=3");
			prop.putProperty("probability_type", "digit1");
			prop.putProperty("grade_type", "SG");
			prop.putProperty("group_sql_id", "grp_1");
			prop.putProperty("models", "100,80");
			
			List<Evaluation> evals = new EvaluationSimulLoaderFPI8_1().getEvaluations();
			System.out.println(evals.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}