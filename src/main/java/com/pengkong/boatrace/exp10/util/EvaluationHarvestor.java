package com.pengkong.boatrace.exp10.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatConst;
import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;
import com.pengkong.common.FileUtil;
import com.pengkong.common.StringUtil;

public class EvaluationHarvestor {
	Logger logger = LoggerFactory.getLogger(EvaluationHarvestor.class);


	@Deprecated
	void execute(String graphDirIn, String evalDirIn, String evalDirOut, String evalId) throws Exception {
		String evalFilepath = evalDirOut + "/" + evalId + ".tsv";
		// 出力ディレクトリ作成
		if (!FileUtil.isExist(evalDirOut)) {
			FileUtil.createDirIfNotExist(evalDirOut);
		}
		
		// 指定ディレクトリ以下の全て(recursively)のグラフファイルに対してevaluation ketのリストを取得する(重複除去）
		List<String> listEvaluationKey = loadEvaluationKeyFromGraphFiles(graphDirIn);
		
		// 指定ディレクトリ以下の全てevaluationファイルをロードする(重複除去）
		Map<String, Evaluation> mapEval = loadEvaluation(evalDirIn);
		
		for (String evalKey : listEvaluationKey) {
			Evaluation eval = mapEval.get(evalKey);
			if (eval == null) {
				logger.error("Evaluation does not exist for graph. " + evalKey);
				continue;
			}
// ERROR			FileUtil.appendFileByLine(evalFilepath, EvaluationHelper.toTsv(eval));
		}
	}

	/** graph파일의 파일명을 취득한다. */
	String getGraphFilename(Evaluation eval) {
		return String.join("_", StringUtil.leftPad(eval.get("resultno"), 
				BoatConst.LEFT_PAD6, "0"), eval.get("pattern"), eval.get("bettype"), eval.get("kumiban")) + ".png"; 

	}

	/**
	 * 대상디렉토리 이하의 모든 그래프파일정보를 취득하여 evaluatio키의 리스트를 소트하여 취득한다.
	 * @param inDir 대상 그래프 파일 디렉토리
	 * @return evaluation 키 리스트
	 * @throws Exception
	 */
	List<String> loadEvaluationKeyFromGraphFiles(String inDir) throws Exception {
		Map<String, String> mapKeys = new TreeMap<>();
		List<File> listFile = FileUtil.listFilesByExtensionRecursively(inDir, "png");
		for (File file : listFile) {
			String key = createEvaluationKey(file.getName());
			if (mapKeys.containsKey(key)) {
				throw new IllegalStateException("Duplicate evaluation exists. " + key);
			}

			mapKeys.put(key, key);
		}
		
		return new ArrayList<>(mapKeys.values());
	}
	
	String createEvaluationKey(String fileName) {
		String namePart = fileName.replace(".png", "");
		
		// token=resultno(zero padded), pattern, bettype, kumiban
		// -> key =  bettype_kumiban_resultno_pattern
		String token[] = namePart.split(Delimeter.UNDERBAR.getValue());
		String modelNo = token[0];
		String patternId = token[1];
		String pattern = token[2];
		String bettype = token[5];
		String kumiban = token[6];
		
		String result = null;
		try {
			result = String.join("_", bettype, kumiban, StringUtil.unpadZero(modelNo), patternId, pattern);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/** simulation定義ファイルを全てロード */
	Map<String, Evaluation> loadEvaluation(String evalDir) throws Exception {
		// key = evaluationのunique key
		Map<String, Evaluation> map = new TreeMap<>();
		File[] files = FileUtil.listFilesByExtension(evalDir, "tsv");
		for (File file : files) {
			// templateはskipする
			if (file.getName().contains("template")) {
				continue;
			}
			List<Evaluation> listEval = EvaluationHelper.readFile(file);
			for (Evaluation eval : listEval) {
				map.put(eval.getUniqueKey(), eval);
			}
		}

		return map;
	}

	public static void main(String[] args) {
		String graphDirIn= args[0];
		String evalDirIn= args[1];
		String evalDirOut= args[2];
		String evalId= args[3];
		
		EvaluationHarvestor eh = new EvaluationHarvestor();
		try {
			eh.execute(graphDirIn, evalDirIn, evalDirOut, evalId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
