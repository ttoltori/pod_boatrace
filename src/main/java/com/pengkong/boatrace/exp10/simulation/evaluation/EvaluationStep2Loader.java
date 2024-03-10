package com.pengkong.boatrace.exp10.simulation.evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.util.EvaluationHelper;
import com.pengkong.common.FileUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * evaluationリストファイルをロードして保持する.
 * @author ttolt
 *
 */
public class EvaluationStep2Loader extends AbstractEvaluationLoader {
	/** group定義ファイルロード */
	@Override
	void initialize() throws Exception {
		mapListEval = new HashMapList<>();

		String dir = prop.getString("dir_groups_step2") + prop.getString("group_no");

		List<File> files = findFiles(dir);
		for (File file : files) {
			loadFile(file);
		}
	}

	/** group定義ファイルのevaluationをロードする. */
	void loadFile(File file) throws Exception {
		HashMap<String, String> mapDuplicateCheck = new HashMap<>();
		
		List<Evaluation> listEvaluation = EvaluationHelper.readFile(file);
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
		
		// group file 保存
		if (prop.getString("save_group").equals("yes")) {
			new EvaluationSimulGroupFileGenerator().generateWithEvaluations("no sql", listEval);
		}
		
	}

	/**
	 * group定義ファイルをスキャンして対象のファイルを取得する。
	 * @param groups group idのリスト。空きの場合は全group file　例) [1,2,3]
	 * @return Fileリスト
	 */
	List<File> findFiles(String dir) throws Exception {
		List<File> listFiles = new ArrayList<>();
		
		
		List<File> files = Arrays.asList(FileUtil.listFilesByExtension(dir, "tsv"));
		for (File file : files) {
			listFiles.add(file);
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
			List<Evaluation> evals = new EvaluationStep2Loader().getEvaluations("1T_1_99100");
			System.out.println(evals.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
