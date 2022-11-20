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
public class EvaluationSimul3GroupFileGenerator {
	Logger logger = LoggerFactory.getLogger(EvaluationSimul3GroupFileGenerator.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	public void execute(String sql, List<DBRecord> results) throws Exception {
		StringBuilder sb = new StringBuilder();
		
		// MLSimulation3Generatorでprop設定する
		String evaluationsId = prop.getString("evaluations_id");
		
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
		
		String titles = String.join("\t", 
				"sel", "grades", "bettype", "kumiban", "resultno", "modelno", "patternid", "pattern",
				"incamt", "betcnt", "hitrate", "bal_pluscnt",
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
			
			sb.append(rec.getDouble("factor")); sb.append("\t");
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
		
		String filepath = prop.getString("dir_groups_output_step1") + evaluationsId + ".tsv";
		FileUtil.writeFile(filepath, sb.toString());
	}
	
}
