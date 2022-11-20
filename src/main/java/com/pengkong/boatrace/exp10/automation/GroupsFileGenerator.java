package com.pengkong.boatrace.exp10.automation;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatConst;
import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.enums.RangeValidationType;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.property.SqlTemplate;
import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;
import com.pengkong.boatrace.exp10.util.EvaluationHelper;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.StringUtil;

@Deprecated
public class GroupsFileGenerator {
	Logger logger = LoggerFactory.getLogger(GroupsFileGenerator.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/** sql template */
	SqlTemplate sqlTpl = SqlTemplate.getInstance();
	
	/** DB query sql */
	private String sql;
	
	public void execute(List<String> exNos) throws Exception {
		HashMap<String, String> mapExNo = new HashMap<>();
		for (String exNo : exNos) {
			mapExNo.put(exNo, exNo);
		}
		
		prop.reset("file_groups_config");
		while(prop.hasNext()) {
			prop.next();
			String exNo = prop.getString("group_no");
			if (!mapExNo.containsKey(exNo)) 
				continue;
			// 実験実行
			executeExperiment(exNo);
		}
	}
	
	public void executeExperiment(String exNo) throws Exception {
		String dir = prop.getString("dir_groups_step1");
		String fileKey = String.join("_", StringUtil.leftPad(exNo, BoatConst.LEFT_PAD6, "0"), 
				prop.getString("bettype") + "-" + prop.getString("prediction"));
		
		List<DBRecord> results = loadDB();
		if (results.size() <= 0) {
			FileUtil.writeFile(dir + fileKey + ".tsv.nodata", "");
			logger.warn(fileKey + " has no data.");

			return;
		}
		
		// 内容作成
		String content = createContent(exNo, results, fileKey);
		
		// save file
		FileUtil.writeFile(dir + fileKey + ".tsv", content);
		
		print(new File(dir + fileKey + ".tsv"));
		
		int sumOfBet = 0;
		for (DBRecord rec : results) {
			sumOfBet += rec.getInt("betcnt");
		}
		logger.info(fileKey + " has been created. ptncnt=" + results.size() + ", betcnt=" + sumOfBet);
		
		// log出力
		FileUtil.appendFileByLine(prop.getString("dir_simulation_log") + "group_generation.log", content + "\n");
	}
	
	void print(File file) throws Exception {
		List<Evaluation> evaluations = EvaluationHelper.readFile(file);
		EvaluationHelper.printTitle();
		for (Evaluation eval : evaluations) {
			EvaluationHelper.print(eval);
		}
	}
	
	String createContent(String exNo, List<DBRecord> results, String fileKey) {
		StringBuilder sb = new StringBuilder();
		
		// comment section
		sb.append("COMMENT_BEGIN " + fileKey); sb.append("\n");
		sb.append(sql.split("where")[1]); sb.append("\n");
		sb.append(prop.getString("comment", "")); sb.append("\n\n");
		sb.append("COMMENT_END"); sb.append("\n");
		// group bonus 定義 
		sb.append("RANGE_SELECTOR:x"); sb.append("\n");
		sb.append("BONUS_PR:x"); sb.append("\n");
		sb.append("BONUS_BOR:x"); sb.append("\n");
		sb.append("BONUS_BORK:x"); sb.append("\n");
		
		String titles = String.join("\t", 
				"grades", "bettype", "kumiban", "resultno", "modelno", "patternid", "pattern", 
				"range_selector", "bonus_pr", "bonus_bor", "bonus_bork", 
				"betcnt", "betrate", "incamt", "hitrate", "incomerate",
				"pr_bestmin", "pr_bestmax", "bor_bestmin", "bor_bestmax", "bork_bestmin", "bork_bestmax",
				"pr_betcnt", "pr_betrate", "pr_hitrate", "pr_income", "pr_incomerate", 
				"bor_betcnt", "bor_betrate", "bor_hitrate", "bor_income", "bor_incomerate", 
				"bork_betcnt", "bork_betrate", "bork_hitrate", "bork_income", "bork_incomerate", 
				"hodds_stable", "hodds_median", "hodds_mean", "hodds_stddev", "hodds_min", "hodds_max", 
				"bal1", "bal2", "bal3", "bal1_slope", "bal2_slope", "bal3_slope"
				);		
		sb.append(titles); sb.append("\n");
		
		// data section
		for (DBRecord rec : results) {
			sb.append(rec.getString("grades")); sb.append("\t");
			sb.append(rec.getString("bettype")); sb.append("\t");
			sb.append(rec.getString("kumiban")); sb.append("\t");
			sb.append(rec.getString("resultno")); sb.append("\t");
			sb.append(rec.getString("modelno")); sb.append("\t");
			sb.append(rec.getString("patternid")); sb.append("\t");
			sb.append(rec.getString("pattern")); sb.append("\t");
			sb.append(rec.getString("range_selector")); sb.append("\t");
			sb.append(rec.getString("bonus_pr")); sb.append("\t");
			sb.append(rec.getString("bonus_bor")); sb.append("\t");
			sb.append(rec.getString("bonus_bork")); sb.append("\t");
			sb.append(rec.getInteger("betcnt")); sb.append("\t");
			sb.append(rec.getDouble("betrate")); sb.append("\t");
			sb.append(rec.getInteger("incamt")); sb.append("\t");
			sb.append(rec.getDouble("hitrate")); sb.append("\t");
			sb.append(rec.getDouble("incomerate")); sb.append("\t");
			sb.append(rec.getDouble("pr_bestmin")); sb.append("\t");
			sb.append(rec.getDouble("pr_bestmax")); sb.append("\t");
			sb.append(rec.getDouble("bor_bestmin")); sb.append("\t");
			sb.append(rec.getDouble("bor_bestmax")); sb.append("\t");
			sb.append(rec.getDouble("bork_bestmin")); sb.append("\t");
			sb.append(rec.getDouble("bork_bestmax")); sb.append("\t");
			sb.append(rec.getInteger("pr_betcnt")); sb.append("\t");
			sb.append(rec.getDouble("pr_betrate")); sb.append("\t");
			sb.append(rec.getDouble("pr_hitrate")); sb.append("\t");
			sb.append(rec.getInteger("pr_income")); sb.append("\t");
			sb.append(rec.getDouble("pr_incomerate")); sb.append("\t");
			sb.append(rec.getInteger("bor_betcnt")); sb.append("\t");
			sb.append(rec.getDouble("bor_betrate")); sb.append("\t");
			sb.append(rec.getDouble("bor_hitrate")); sb.append("\t");
			sb.append(rec.getInteger("bor_income")); sb.append("\t");
			sb.append(rec.getDouble("bor_incomerate")); sb.append("\t");
			sb.append(rec.getInteger("bork_betcnt")); sb.append("\t");
			sb.append(rec.getDouble("bork_betrate")); sb.append("\t");
			sb.append(rec.getDouble("bork_hitrate")); sb.append("\t");
			sb.append(rec.getInteger("bork_income")); sb.append("\t");
			sb.append(rec.getDouble("bork_incomerate")); sb.append("\t");
			sb.append(rec.getDouble("hodds_stable")); sb.append("\t");
			sb.append(rec.getDouble("hodds_median")); sb.append("\t");
			sb.append(rec.getDouble("hodds_mean")); sb.append("\t");
			sb.append(rec.getDouble("hodds_stddev")); sb.append("\t");
			sb.append(rec.getDouble("hodds_min")); sb.append("\t");
			sb.append(rec.getDouble("hodds_max")); sb.append("\t");
			sb.append(rec.getInteger("bal1")); sb.append("\t");
			sb.append(rec.getInteger("bal2")); sb.append("\t");
			sb.append(rec.getInteger("bal3")); sb.append("\t");
			sb.append(rec.getDouble("bal1_slope")); sb.append("\t");
			sb.append(rec.getDouble("bal2_slope")); sb.append("\t");
			sb.append(rec.getDouble("bal3_slope")); sb.append("\n");
		}
		
		return sb.toString();
	}

	
	private List<DBRecord> loadDB() throws Exception {
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		CustomMapper customMapper = session.getMapper(CustomMapper.class);
		
		sql = sqlTpl.get(prop.getString("sql_id"));
		
		sql = sql.replace("{grades}", prop.getString("grades"));
		sql = sql.replace("{bettype}", prop.getString("bettype"));
		sql = sql.replace("{result_type}", prop.getString("result_type"));
		sql = sql.replace("{prediction}", prop.getString("prediction"));
		
		sql = sql.replaceAll("\\{bork_idx\\}", prop.getString("bork_idx"));
		sql = sql.replace("{ranking_min}", prop.getString("ranking_min"));
		sql = sql.replace("{ranking_max}", prop.getString("ranking_max"));
		sql = sql.replace("{incomerate_min}", prop.getString("incomerate_min"));
		
//		sql = sql.replaceAll("\\{bal123_min\\}", prop.getString("bal123_min"));
//		sql = sql.replaceAll("\\{bal123_max\\}", prop.getString("bal123_max"));
//		sql = sql.replaceAll("\\{slope123_min\\}", prop.getString("slope123_min"));
//		sql = sql.replaceAll("\\{slope123_max\\}", prop.getString("slope123_max"));
//		sql = sql.replace("{betcnt_min}", prop.getString("betcnt_min"));
//		sql = sql.replace("{betcnt_max}", prop.getString("betcnt_max"));
//		sql = sql.replace("{income_min}", prop.getString("income_min"));
//		sql = sql.replace("{income_max}", prop.getString("income_max"));
//		sql = sql.replace("{hitrate_min}", prop.getString("hitrate_min"));
//		sql = sql.replace("{hitrate_max}", prop.getString("hitrate_max"));
//		String[] modelNos = prop.getString("modelnos").split(Delimeter.COMMA.getValue()); 
//		sql = sql.replace("{modelnos}", convertModelInClause(modelNos));
//		
//		if (prop.getString("patternids").equals(RangeValidationType.NONE.getValue())) {
//			sql = sql.replace("{patternid_condition}", "true");
//		} else {
//			sql = sql.replace("{patternid_condition}",
//					"patternid in ("
//							+ StringUtil.quote(prop.getString("patternids").split(Delimeter.COMMA.getValue())) + ")" );
//		}

		sql = sql.replace("{custom}", prop.getString("custom"));
		
		HashMap<String, String> mapParam = new HashMap<>();
		mapParam.put("sql", sql);
		
		// 디비 데이터 일람 취득
		List<DBRecord> results = customMapper.selectSql(mapParam);
		DatabaseUtil.close(session);
		
		return results; 
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
//		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
//		String exNoList = "1";
		String propertyFilepath = args[0];
		String exNosStr = args[1];
		
		List<String> listExno = StringUtil.parseNumericOptionsString(exNosStr);
		
		try {
			// PropertyUtilを使っている箇所もあるので互換性確保のために
			PropertyUtil.getInstance().addFile(propertyFilepath);
			MLPropertyUtil.getInstance().addFile(propertyFilepath);

			GroupsFileGenerator generator = new GroupsFileGenerator(); 
			generator.execute(listExno);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
