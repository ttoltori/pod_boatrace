package com.pengkong.boatrace.exp10.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatConst;
import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;
import com.pengkong.common.FileUtil;
import com.pengkong.common.StringUtil;

public class GraphHarvestor {
	Logger logger = LoggerFactory.getLogger(GraphHarvestor.class);
	
	void execute(String evalDir, String evalId, String inDir, String outDir) throws Exception {
		// 出力ディレクトリにevaluation idを追加する
		outDir = outDir + "/" + evalId;
		
		List<Evaluation> listEval = readEvalfile(evalDir, evalId);
		if (listEval == null) {
			logger.error("no eval file. " + evalDir + "," + evalId);
			return;
		}

		HashMap<String, File> mapFiles = loadFileInfo(inDir);
		
		// 出力ディレクトリ作成
		if (!FileUtil.isExist(outDir)) {
			FileUtil.createDirIfNotExist(outDir);
		}
		
		for (Evaluation eval : listEval) {
			String fileName = getGraphFileName(eval, "");
			File srcFile = mapFiles.get(fileName);
			if (srcFile == null) {
				logger.warn("graph file for evaluation does not exist. " + eval);
			} else {
				FileUtils.copyFile(srcFile, new File(outDir + "/" + fileName));
				logger.debug("file copied. " + srcFile.getName());
			}
			
			fileName = getGraphFileName(eval, "_bork");
			srcFile = mapFiles.get(fileName);
			if (srcFile == null) {
				logger.warn("graph file (bork) for evaluation does not exist. " + eval);
			} else {
				FileUtils.copyFile(srcFile, new File(outDir + "/" + fileName));
				logger.debug("file copied. " + srcFile.getName());
			}
		}
	}

	List<Evaluation> readEvalfile(String evalDir, String evalId) throws Exception {
		File[] files = FileUtil.listFilesByExtension(evalDir, "tsv");
		for (File file : files) {
			if (Integer.parseInt(file.getName().split("_")[0]) == Integer.parseInt(evalId) ) {
				return EvaluationHelper.readFile(file);
			}
		}
		
		return null;
	}
	
	/** graph파일의 파일명을 취득한다. */
	String getGraphFileName(Evaluation eval, String postfix) {
		return String.join("_",
				StringUtil.leftPad(eval.get("resultno"),BoatConst.LEFT_PAD6, "0"),
				eval.get("result_type"),
				eval.get("modelno"),
				eval.get("patternid"),
				eval.get("pattern"), 
				eval.get("bettype"), 
				eval.get("kumiban"))
				+ postfix + ".png";
	}

	/** graph파일의 copy타겟파일명을 취득한다. */
	@Deprecated
	String getTargetGraphFilename(Evaluation eval, String groupNo) {
		return String.join("_", eval.get("modelno"), eval.get("patternid"), 
				eval.get("pattern"), StringUtil.leftPad(eval.get("resultno"),BoatConst.LEFT_PAD, "0"),
				groupNo, eval.get("bettype"), eval.get("kumiban")) + ".png"; 

	}

	/**
	 * 대상디렉토리 이하의 모든 그래프파일정보를 취득하여 맵형태로 반환한다.
	 * @param inDir 대상 디렉토리
	 * @return hashmap  key=파일명, value=File
	 * @throws Exception
	 */
	HashMap<String, File> loadFileInfo(String inDir) throws Exception {
		List<File> listFile = FileUtil.listFilesByExtensionRecursively(inDir, "png");
		HashMap<String, File> mapFiles = new HashMap<>();
		for (File file : listFile) {
			String fileName = file.getName();
			if (mapFiles.containsKey(fileName)) {
				File file2 = mapFiles.get(fileName);
				String errMsg = "file name is duplicated. file1="
						+ file.getAbsolutePath() + "/" + file.getName() + ", file2="
						+ file2.getAbsolutePath() + "/" + file2.getName();
				throw new IllegalStateException(errMsg);
			}
			mapFiles.put(file.getName(), file);
		}
		
		return mapFiles;
	}
	
	public static void main(String[] args) {
		String evalDir= args[0];
		String evalId= args[1];
		String inDir = args[2];
		String outDir = args[3];
		
		GraphHarvestor gh = new GraphHarvestor();
		try {
			gh.execute(evalDir, evalId, inDir, outDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
