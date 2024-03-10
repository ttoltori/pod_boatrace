package com.pengkong.boatrace.exp10.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.enums.RangeValidationType;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.FileUtil;
import com.pengkong.common.StringUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * evaluationファイルの構成
 * 
 * COMMENT_BEGIN 説明を記載 COMMENT_END RANGE_SELECTOR:bor BONUS_PR:0~1=1
 * BONUS_BOR:0~1=1 BONUS_BORKBOR:1~1:5~8=1 column title line data line ...
 * 
 * @author ttolt
 *
 */
public class EvaluationHelper {
	
	public static Evaluation createEvaluation(DBRecord rec) {
		Evaluation eval = new Evaluation();
		eval.put("sel", rec.getStringForced("sel"));
		eval.put("grades", rec.getStringForced("grades"));
		eval.put("bettype", rec.getStringForced("bettype"));
		eval.put("kumiban", rec.getStringForced("kumiban"));
//		eval.put("resultno", rec.getStringForced("resultno"));
		eval.put("modelno", rec.getStringForced("modelno"));
		eval.put("patternid", rec.getStringForced("patternid"));
		eval.put("pattern", rec.getStringForced("pattern"));
//		eval.put("incamt", rec.getStringForced("incamt"));
//		eval.put("betcnt", rec.getStringForced("betcnt"));
//		eval.put("incrate", rec.getStringForced("incrate"));
//		eval.put("hitrate", rec.getStringForced("hitrate"));
//		eval.put("bal_pluscnt", rec.getStringForced("bal_pluscnt"));
		eval.put("range_selector", rec.getStringForced("range_selector"));
		eval.put("bonus_pr", rec.getStringForced("bonus_pr"));
		eval.put("bonus_bor", rec.getStringForced("bonus_bor"));
		eval.put("bonus_bork", rec.getStringForced("bonus_bork"));
		eval.put("bonus_borkbor", rec.getStringForced("bonus_borkbor"));
		
		return eval;
	}
	
	
	public static List<Evaluation> readFile(File file) throws Exception {
		return readFile(file.getAbsolutePath());
	}

	public static HashMapList<Evaluation> createMapPatternIdEvaluations(List<Evaluation> listEval, String grade) {
		HashMapList<Evaluation> mapIdEval = new HashMapList<>();
		for (Evaluation eval : listEval) {
			// 対象gradeでない場合スキップ
			if (!eval.get("grades").contains(grade)) {
				continue;
			}
			mapIdEval.addItem(eval.get("patternid"), eval);
		}
		return mapIdEval;
	}
	
	/** 共通情報部なしのtsvファイルからevaluationをロードする。 */
	public static List<Evaluation> readTsvFile(File file) throws Exception{
		List<Evaluation> result = new ArrayList<>();
		
		List<String> listLine = FileUtil.readFileByLineArr(file);
		String[] titles = listLine.get(0).split(Delimeter.TAB.getValue());
		
		for (int i = 1; i < listLine.size(); i++) {
			String[] contents = listLine.get(i).split(Delimeter.TAB.getValue());
			
			result.add(createEvaluation(titles, contents, null, null, null, null, null));
		}
		
		return result;
	}
	
	public static List<Evaluation> readFile(String filepath) throws Exception {
		MLPropertyUtil prop = MLPropertyUtil.getInstance();

		List<Evaluation> result = new ArrayList<>();

		List<String> listLine = FileUtil.readFileByLineArr(filepath);
		// COMMENT SECTION SKIP
		int line;
		for (line = 0; line < listLine.size(); line++) {
			if (listLine.get(line).contains("COMMENT_END")) {
				break;
			}
		}

		line++;

		// 定義値はgroupファイル設定値⇒property設定値の取得を試す。取得できない場合は"x"を設定する
		// 1行目 range_selectorr定義
		String rangeSelector = listLine.get(line).split(Delimeter.COLON.getValue())[1];
		if (rangeSelector.equals(RangeValidationType.NONE.getValue())) {
			rangeSelector = prop.getString("range_selector", RangeValidationType.NONE.getValue());
		}

		line++;
		// 2行目 group bonus_pr定義
		String bonusPr = listLine.get(line).split(Delimeter.COLON.getValue())[1];
		if (bonusPr.equals(RangeValidationType.NONE.getValue())) {
			bonusPr = prop.getString("bonus_pr", RangeValidationType.NONE.getValue());
		}

		line++;
		// 3行目 group bonus_bor定義
		String bonusBor = listLine.get(line).split(Delimeter.COLON.getValue())[1];
		if (bonusBor.equals(RangeValidationType.NONE.getValue())) {
			bonusBor = prop.getString("bonus_bor", RangeValidationType.NONE.getValue());
		}

		line++;
		// 4行目 group bonus_bork定義
		String bonusBork = listLine.get(line).split(Delimeter.COLON.getValue())[1];
		if (bonusBork.equals(RangeValidationType.NONE.getValue())) {
			bonusBork = prop.getString("bonus_bork", RangeValidationType.NONE.getValue());
		}

		line++;
		// 5行目 group bonus_borkbor定義
		String bonusBorkBor = "x";
		if (!listLine.get(line).startsWith("BONUS_BORKBOR")) {
			// BONUS_BORKBORが追加される前のgroup file
			line--;
			// throw new IllegalStateException("group file format missed BONUS_BORKBOR");
		} else {
			bonusBorkBor = listLine.get(line).split(Delimeter.COLON.getValue())[1];
			if (bonusBorkBor.equals(RangeValidationType.NONE.getValue())) {
				bonusBorkBor = prop.getString("bonus_borkbor", RangeValidationType.NONE.getValue());
			}
		}

		// 6行目はカラムタイトル
		line++;
		String[] titles = listLine.get(line).split(Delimeter.TAB.getValue());

		line++;
		// 7行目以降をループ
		for (int i = line; i < listLine.size(); i++) {
			String str = listLine.get(i);
			// 空白行はskip
			if (StringUtil.isEmpty(str)) {
				continue;
			}
			String[] contents = str.split(Delimeter.TAB.getValue());
			result.add(createEvaluation(titles, contents, rangeSelector, bonusPr, bonusBor, bonusBork, bonusBorkBor));
		}

		return result;
	}

	/**
	 * Evaluationを生成する
	 * 
	 * @param titles       行目のカラムタイトル (!!!bettype, kumiban必須)
	 * @param contents     ファイル内容
	 * @param bonusPr      グループに適用するbonus_pr定義値 例）0.7~0.8=1
	 * @param bonusBor     グループに適用するbonus_bor定義値 例）0.23~0.3=1
	 * @param bonusBork    グループに適用するbonus_bork定義値 例）1~2=1
	 * @param bonusBorkBor グループに適用するbonus_borkbor定義値 例）1~2_5.6~10.5=1
	 * @return
	 */
	public static Evaluation createEvaluation(String[] titles, String[] contents, String rangeSelector, String bonusPr,
			String bonusBor, String bonusBork, String bonusBorkBor) {
		Evaluation eval = new Evaluation();
		for (int i = 0; i < contents.length; i++) {
			eval.put(titles[i], contents[i]);
		}
		
        // 20221110 上段部のrange定義を上書きでなく～～2に追加する。
        eval.put("bonus_pr2", bonusPr);
		eval.put("bonus_bor2", bonusBor);
        eval.put("bonus_bork2", bonusBork);
		
		return eval;
		
		// 20221110 上段部のrange定義を上書きでなく～～2に追加する。
		//return postFixRangeSelector(eval, rangeSelector, bonusPr, bonusBor, bonusBork, bonusBorkBor);
	}
	
	public static Evaluation postFixRangeSelector(Evaluation eval, String rangeSelector, String bonusPr,
			String bonusBor, String bonusBork, String bonusBorkBor) {
		Logger logger = LoggerFactory.getLogger(EvaluationHelper.class);		
		try {

			// 個別evaluationにxが設定されていれば設定する
			if (eval.get("range_selector").equals(RangeValidationType.NONE.getValue())) {
				eval.put("range_selector", rangeSelector);
			}
			if (eval.get("bonus_pr").equals(RangeValidationType.NONE.getValue())) {
				eval.put("bonus_pr", bonusPr);
			}

			if (eval.get("bonus_bor").equals(RangeValidationType.NONE.getValue())) {
				eval.put("bonus_bor", bonusBor);
			}

			if (eval.get("bonus_bork").equals(RangeValidationType.NONE.getValue())) {
				eval.put("bonus_bork", bonusBork);
			}

			// borkborが追加される前のgroupファイルはnullになる。　if (eval.get("bonus_borkbor").equals(RangeValidationType.NONE.getValue())) {
			if (eval.get("bonus_borkbor") == null || eval.get("bonus_borkbor").equals(RangeValidationType.NONE.getValue())) {
				eval.put("bonus_borkbor", bonusBorkBor);
			}
		} catch (Exception e) {
			logger.error("evaluation format error: " + eval, e);
		}

		return eval;
	}
	
}
