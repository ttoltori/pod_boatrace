package com.pengkong.boatrace.exp10.simulation.evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.util.EvaluationHelper;
import com.pengkong.common.FileUtil;

/**
 * tsvから直接Evaluationをロードする。Heaader部分は意識しない。
 * @author ttolt
 */
public class EvaluationSimulTsvFileLoader extends EvaluationFileLoader {
	
	/** group定義ファイルのevaluationをロードする. */
	void loadFile(File file) throws Exception {
		HashMap<String, String> mapDuplicateCheck = new HashMap<>();
		
		List<Evaluation> listEvaluation = EvaluationHelper.readTsvFile(file);
		for (Evaluation eval : listEvaluation) {
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
	}

	/**
	 * group定義ファイルをスキャンして対象のファイルを取得する。
	 * @param groups group idのリスト。空きの場合は全group file　例) [1,2,3]
	 * @return Fileリスト
	 */
	@Override
	 List<File> findFiles(String dir, String[] groups) throws Exception {
		List<File> listFiles = new ArrayList<>();
		Set<String> groupSet = new HashSet<>(Arrays.asList(groups));
		
		
		List<File> files = Arrays.asList(FileUtil.listFilesByExtension(dir, "tsv"));
		for (File file : files) {
			// 全ファイル対象
			if (groupSet.isEmpty()) {
				listFiles.add(file);
				continue;
			}

			//String namePart = file.getName().split(Delimeter.PERIOD.getValue())[0];
			// ファイル名はfactor_noで始まり'-'で結ぶ。 ex) 392-i02_0.9_555.tsv
			String namePart = file.getName().split(Delimeter.DASH.getValue())[0];
			if (groupSet.contains(namePart)) {
				listFiles.add(file);
			}
		}
		
		return listFiles;
	}
	
	public static void main(String[] args) {
		MLPropertyUtil prop = MLPropertyUtil.getInstance();
		prop.putProperty("simulation_step", "1");
		prop.putProperty("dir_groups_step1", "D:/Dev/experiment/expr10/work/groups_store/");
		prop.putProperty("result_type", "4");
		prop.putProperty("groups", "400,401");
		
		try {
			List<Evaluation> evals = new EvaluationSimulTsvFileLoader().getEvaluations("1T_1_99100");
			System.out.println(evals.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
