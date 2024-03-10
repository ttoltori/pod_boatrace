package com.pengkong.boatrace.exp10.simulation.evaluation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.FileUtil;

/**
 * EvaluationSimul2LoaderのDB取得データをonline_simulation用ファイルフォーマットに変換して保存する。
 * @author ttolt
 *
 */
public class EvaluationSimulGroupFileGenerator {
	Logger logger = LoggerFactory.getLogger(EvaluationSimulGroupFileGenerator.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	public void generateWithDBResults(String sql, List<DBRecord> results) throws Exception {
		StringBuilder sb = new StringBuilder();
		
		String evaluationsId = prop.getString("evaluations_id");
		
		// ヘッダ部
		createHeader(sql, evaluationsId, sb);
		
		String factor = prop.getString("factor");
		String titles = String.join("\t", 
				"sel", "grades", "bettype", "kumiban", "resultno", "modelno", "patternid", "pattern",
				factor, "incamt", "betcnt", "hitrate", "bal_pluscnt",
				"range_selector", "bonus_pr", "bonus_bor", "bonus_bork", "bonus_borkbor" 
				);		
		sb.append(titles); sb.append("\n");
		
		// data section
		for (DBRecord rec : results) {
			sb.append(rec.getString("sel")); sb.append("\t");
			sb.append(rec.getString("grades")); sb.append("\t");
			sb.append(rec.getString("bettype")); sb.append("\t");
			sb.append(rec.getString("kumiban")); sb.append("\t");
			sb.append(rec.getString("resultno")); sb.append("\t");
			sb.append(rec.getString("modelno")); sb.append("\t");
			sb.append(rec.getString("patternid")); sb.append("\t");
			sb.append(rec.getString("pattern")); sb.append("\t");
			if (factor.equals("x")) { // factorを使わないsimulation (simulation3)
				sb.append("x"); sb.append("\t");
			} else {
				sb.append(rec.getDouble("factor")); sb.append("\t");
			}
			sb.append(rec.getInteger("incamt")); sb.append("\t");
			sb.append(rec.getInteger("betcnt")); sb.append("\t");
			sb.append(rec.getDouble("hitrate")); sb.append("\t");
			sb.append(rec.getInteger("bal_pluscnt")); sb.append("\t");
			
			sb.append(rec.getString("range_selector")); sb.append("\t");
			sb.append(rec.getString("bonus_pr")); sb.append("\t");
			sb.append(rec.getString("bonus_bor")); sb.append("\t");
			sb.append(rec.getString("bonus_bork")); sb.append("\t");
			sb.append(rec.getString("bonus_borkbor")); sb.append("\n");
		}
		
		String dir = prop.getString("dir_groups_output_step1");
		FileUtil.createDirIfNotExist(dir);
		String filepath = dir + evaluationsId + ".tsv";

		filepath = filepath.replaceAll("\\*", "@");
		FileUtil.writeFile(filepath, sb.toString());
	}

	/**
	 * Evaluationのリストでgroupファイルを作成する。
	 * @param sql 
	 * @param evals
	 * @throws Exception
	 */
	public void generateWithEvaluations(String sql, List<Evaluation> evals) throws Exception {
		StringBuilder sb = new StringBuilder();
		
		String evaluationsId = prop.getString("evaluations_id");
		
		// ヘッダ部
		createHeader(sql, evaluationsId, sb);
		
		String titles = String.join("\t", 
				"sel", "grades", "bettype", "kumiban", "resultno", "modelno", "patternid", "pattern",
				"factor", "incamt", "betcnt", "hitrate", "bal_pluscnt",
				"range_selector", "bonus_pr", "bonus_bor", "bonus_bork", "bonus_borkbor" 
				);		
		sb.append(titles); sb.append("\n");
		
		// data section
		for (Evaluation rec : evals) {
			sb.append(rec.getString("sel")); sb.append("\t");
			sb.append(rec.getString("grades")); sb.append("\t");
			sb.append(rec.getString("bettype")); sb.append("\t");
			sb.append(rec.getString("kumiban")); sb.append("\t");
			sb.append(rec.getString("resultno")); sb.append("\t");
			sb.append(rec.getString("modelno")); sb.append("\t");
			sb.append(rec.getString("patternid")); sb.append("\t");
			sb.append(rec.getString("pattern")); sb.append("\t");
			// factorを使わないsimulation (simulation3)
			sb.append("x"); sb.append("\t");
			sb.append(rec.getString("incamt")); sb.append("\t");
			sb.append(rec.getString("betcnt")); sb.append("\t");
			sb.append(rec.getString("hitrate")); sb.append("\t");
			sb.append(rec.getString("bal_pluscnt")); sb.append("\t");
			
			sb.append(rec.getString("range_selector")); sb.append("\t");
			sb.append(rec.getString("bonus_pr")); sb.append("\t");
			sb.append(rec.getString("bonus_bor")); sb.append("\t");
			sb.append(rec.getString("bonus_bork")); sb.append("\t");
			sb.append(rec.getString("bonus_borkbor")); sb.append("\n");
		}

		String dir;
		if (prop.getString("simulation_step").equals("1")) {
			dir = prop.getString("dir_groups_output_step1");	
		} else {
			dir = prop.getString("dir_groups_output_step2");	
		}
		
		FileUtil.createDirIfNotExist(dir);
		String filepath = dir + evaluationsId + ".tsv";

		filepath = filepath.replaceAll("\\*", "@");
		FileUtil.writeFile(filepath, sb.toString());
	}

	void createHeader(String sql, String evaluationsId, StringBuilder sb) {
		// comment section
		sb.append("COMMENT_BEGIN " + evaluationsId); sb.append("\n");
		sb.append("generated by EvaluationSimul2Save."); sb.append("\n");
		sb.append(sql); sb.append("\n\n");
		sb.append("COMMENT_END"); sb.append("\n");
		
		// group bonus 定義 
		sb.append("RANGE_SELECTOR:x"); sb.append("\n");
		sb.append("BONUS_PR:" + prop.getString("bonus_pr")); sb.append("\n");
		sb.append("BONUS_BOR:" + prop.getString("bonus_bor")); sb.append("\n");
		sb.append("BONUS_BORK:" + prop.getString("bonus_bork")); sb.append("\n");
		sb.append("BONUS_BORKBOR:x"); sb.append("\n");
	}
}
