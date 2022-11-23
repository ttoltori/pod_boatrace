package com.pengkong.boatrace.exp10.simulation.evaluation;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.property.SqlTemplate;
import com.pengkong.boatrace.exp10.simulation.evaluation.trim.BorkRangeTrim;
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
public class EvaluationSimulLoaderBork extends AbstractEvaluationLoader {
	/** sql template */
	SqlTemplate sqlTpl = SqlTemplate.getInstance();

	BorkRangeTrim borkRangeTrim;
	
	EvaluationSimulGroupFileGenerator groupFileGenerator;
	
	/** group定義DBロード */
	@Override
	void initialize() throws Exception {
		mapListEval = new HashMapList<>();
		borkRangeTrim = new BorkRangeTrim();
		groupFileGenerator = new EvaluationSimulGroupFileGenerator();
		
		loadDB();
	}

	/**
	 * group
	 * 
	 * @throws Exception
	 */
	void loadDB() throws Exception {
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		CustomMapper customMapper = session.getMapper(CustomMapper.class);

		String sql = sqlTpl.get(prop.getString("group_sql_id"));

		// ex) term_833_1=20180601~20220131
		String[] tokenTerm = prop.getString("term_" + prop.getString("term")).split(Delimeter.WAVE.getValue());
		sql = sql.replace("{fromYmd}", tokenTerm[0]);
		sql = sql.replace("{toYmd}", tokenTerm[1]);

		sql = sql.replace("{bettype}", prop.getString("bettype"));
		sql = sql.replace("{kumiban}", prop.getString("kumiban"));
		String incr = prop.getString("incr");
		if (!incr.contains(Delimeter.WAVE.getValue())) {
			throw new IllegalStateException("incrは「~」で分けて表現してください。");
		}
		String[] incrToken = incr.split(Delimeter.WAVE.getValue());
		sql = sql.replace("{incrmin}", incrToken[0]);
		if (incrToken[1].equals("x")) {
			sql = sql.replace("{incrmax}", "999");
		} else {
			sql = sql.replace("{incrmax}", incrToken[1]);
		}

		if (prop.getString("grade_type").equals("ip")) {
			sql = sql.replace("{result_type}", "1");
		} else if (prop.getString("grade_type").equals("SG")) {
			sql = sql.replace("{result_type}", "11");	
		} else {
			throw new IllegalStateException("undefined grade type.");
		}

		String model = prop.getString("model");
		// "90" -> "99090"
		model = "99" + StringUtil.leftPad(model, 3, "0");
		sql = sql.replace("{model}", model);

//		String patternid = prop.getString("patternid");
//		sql = sql.replace("{patternid}", patternid);
		String custom = prop.getString("custom");
		sql = sql.replace("{custom}", custom);
		
		HashMap<String, String> mapParam = new HashMap<>();
		mapParam.put("sql", sql);

		// 디비 데이터 일람 취득
		List<DBRecord> results = customMapper.selectSql(mapParam);
		if (results.size() <= 0) {
			throw new WarningException("db has no evaluation data.");
		}

		DatabaseUtil.close(session);
		
		// Evaluation生成
		HashMap<String, String> mapDuplicateCheck = new HashMap<>();
		for (DBRecord rec : results) {
			// Evaluationファイル１行生成
			Evaluation eval = EvaluationHelper.createEvaluation(rec);
			
			// borkをborに変換するか
			String borConvert = prop.getString("bork_bor_convert");
			if (borConvert.equals("yes")) {
				String borString = borkRangeTrim.createBorString(rec);
				if (borString == null) {
					continue;
				}
				eval.put("bonus_bor", borString);	
			} else {
				String borkString = borkRangeTrim.createBorkString(rec);
				if (borkString == null) {
					continue;
				}
				eval.put("bonus_bork", borkString);
			}
			
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
	}

	/** ex) ["83,94"] -> "99083,99094" */
	String convertModelInClause(String[] modelNos) {
		StringBuilder sb = new StringBuilder();
		
		for (String model : modelNos) {
			String combined = "99" + StringUtil.leftPad(model, 3, "0");
			sb.append(combined);
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
			
			List<Evaluation> evals = new EvaluationSimulLoaderBork().getEvaluations();
			System.out.println(evals.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}