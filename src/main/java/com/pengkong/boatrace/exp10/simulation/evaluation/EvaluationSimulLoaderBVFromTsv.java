package com.pengkong.boatrace.exp10.simulation.evaluation;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.exp10.property.SqlTemplate;
import com.pengkong.boatrace.exp10.util.EvaluationHelper;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.StringUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * tsvにあるevaluations_idのリストからGroupFileを生成する。
 * これに統合して使う 2023/11/19
 * 
 * @author ttolt
 *
 */
public class EvaluationSimulLoaderBVFromTsv extends AbstractEvaluationLoader {
	/** sql template */
	SqlTemplate sqlTpl = SqlTemplate.getInstance();

	/** group定義DBロード */
	@Override
	void initialize() throws Exception {
		mapListEval = new HashMapList<>();

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

			if (prop.getString("optional_sql").equals("yes")) {
				sql = sql.replace("<optional>", "");
				sql = sql.replace("</optional>", "");
			} else {
				sql = StringUtil.replaceBetween(sql, "<optional>", "</optional>", true, true, "");
			}
			sql = sql.replace("{kumiban}", prop.getString("kumiban"));
			
			sql = sql.replaceAll("\\{factor\\}", prop.getString("factor"));
			sql = sql.replaceAll("\\{factor2\\}", prop.getString("factor2"));
			
			sql = sql.replace("{cond_field}", prop.getString("cond_field"));

			String[] condMinMax = prop.getString("cond_range").split(Delimeter.WAVE.getValue());
			sql = sql.replace("{cond_min}", condMinMax[0]);

			// minだけ記載された場合
			if (condMinMax.length >= 2) {
				sql = sql.replace("{cond_max}", condMinMax[1]);
			} else {
				sql = sql.replace("{cond_max}", "999999999");
			}
			
			sql = sql.replace("{modelno}", prop.getString("models"));

			sql = sql.replace("{limit}", prop.getString("limit"));
			
			sql = sql.replace("{custom}", prop.getString("custom"));

			HashMap<String, String> mapParam = new HashMap<>();
			mapParam.put("sql", sql);

			// 디비 데이터 일람 취득
			List<DBRecord> results = customMapper.selectSql(mapParam);
			if (results.size() <= 0) {
				throw new WarningException("db has no data.");
			}

			// group file 保存 2022/08/12
			if (prop.getString("save_group").equals("yes")) {
				new EvaluationSimulGroupFileGenerator().generateWithDBResults(sql, results);
			}
			
//			// Evaluation生成
//			HashMap<String, String> mapDuplicateCheck = new HashMap<>();
//			for (DBRecord rec : results) {
//				Evaluation eval = createEvauation(rec);
//				
//				String unkqueKey = eval.getUniqueKey();
//				// 重複あり
//				if (mapDuplicateCheck.containsKey(unkqueKey)) {
//					logger.warn("Duplicate evaluation exists. " + eval);
//					continue;
//				}
//				mapDuplicateCheck.put(unkqueKey, unkqueKey);
//				
//				mapListEval.addItem(eval.getBettypeKumibanModelNo(), eval);
//				listEval.add(eval);
//			}			
		} finally {
			DatabaseUtil.close(session);
		}
	}

	Evaluation createEvauation(DBRecord rec) {
		Evaluation eval = new Evaluation();
		rec.keySet().forEach(key -> eval.put(key, rec.getStringForced(key)));

		return EvaluationHelper.postFixRangeSelector(eval, prop.getString("range_selector"), prop.getString("bonus_pr"),
				prop.getString("bonus_bor"), prop.getString("bonus_bork"), prop.getString("bonus_borkbor"));
	}

	public void execute(String tsvFilepath) throws Exception {
		List<String> lines = FileUtil.readFileByLineArr(tsvFilepath);
		String tsvId = new File(tsvFilepath).getName().replace(".tsv", "");
		
		for (String line : lines) {
			EvaluationsId eid = new EvaluationsId(line);
			prop.putProperty("target_db_resource", "mybatis-config.0.xml");
			prop.putProperty("dir_sql", "C:/Dev/github/pod_boatrace/properties/expr10/");
			prop.putProperty("files_sql", "sqls_1.sql");
			
			prop.putProperty("group_sql_id", eid.sqlid );
			prop.putProperty("grade_type", eid.grade );
			prop.putProperty("bettype", eid.bettype );
			prop.putProperty("kumiban", eid.kumiban );
			prop.putProperty("optional_sql", "yes" );
			prop.putProperty("factor", eid.factor );
			prop.putProperty("factor2", "hitrate" );
			prop.putProperty("cond_field", "incrate" );
			prop.putProperty("cond_range", eid.condmin );
			String model = "";
			if (eid.modelno.equals("9")) {
				model = "99100";
			} else if (eid.modelno.equals("1")) {
				model = "11609";
			} else if (eid.modelno.equals("2")) {
				model = "21609";
			} else if (eid.modelno.equals("7")) {
				model = "79101";
			} 
			prop.putProperty("models", model );
			prop.putProperty("limit", eid.limit );
			prop.putProperty("custom", "%wk%" );
			
			prop.putProperty("evaluations_id", eid.getKeyIgnoreTerm());
			prop.putProperty("bonus_pr", "x");
			prop.putProperty("bonus_bor", "x");
			prop.putProperty("bonus_bork", "x");
			String simType;
			if (eid.sqlid.startsWith("BIG5")) {
				simType = "BIG5";
			} else {
				simType = "VIC2";
			}
			prop.putProperty("dir_groups_output_step1", "D:/simul_step1/candidates_groups/" + simType + "/" + tsvId + "/");
			prop.putProperty("save_group", "yes" );
			
			loadDB();
			logger.info(eid.getKeyIgnoreTerm() + " group saved.");
		}
	}
	
	/**
	 * ex) ip_1T_1_i02_hitrate_1.0_30_1_70161_BIG5-77_5-3_r1_80_5.73_12.2.png
	 * @author ttolt
	 *
	 */
	private class EvaluationsId {
		public String grade;
		public String bettype;
		public String kumiban;	
		public String factor;
		public String factor2;
		public String condmin;
		public String limit;
		public String modelno;
		public String resultno;
		public String sqlid;	
		public String termtype;
		
		public EvaluationsId(String evalId) {
			parse(evalId);
		}
		
		private void parse(String evalId) {
			String[] token = evalId.split(Delimeter.UNDERBAR.getValue());
			grade = token[0];
			bettype = token[1];
			kumiban = token[2];
			factor = token[3];
			factor2 = token[4];
			condmin = token[5];
			limit = token[6];
			modelno = token[7];
			resultno = token[8];
			sqlid = token[9];
			termtype = token[10];
		}

		//SG_1T_*-*-*_wk12+jyo+race_i09-100_0_79100_JSJ-S2_77_12_10150_detail1_detail2
		public String getKeyIgnoreTerm() {
			return String.join(Delimeter.UNDERBAR.getValue(), grade, bettype, kumiban, factor, factor2, condmin, limit, 
					modelno, resultno, sqlid, termtype);
		}
	}	
	public static void main(String[] args) {
		//String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
		String tsvFilepath = args[0];
		
		try {
			new EvaluationSimulLoaderBVFromTsv().execute(tsvFilepath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
