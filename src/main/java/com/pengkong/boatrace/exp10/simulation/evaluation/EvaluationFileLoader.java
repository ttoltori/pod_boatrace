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
import com.pengkong.common.StringUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * evaluationリストファイルをロードして保持する.
 * @author ttolt
 *
 */
public class EvaluationFileLoader extends AbstractEvaluationLoader {
	/** group定義ファイルロード */
	@Override
	void initialize() throws Exception {
		mapListEval = new HashMapList<>();

		String groups = prop.getString("groups");
		String[] evaluations = {};
		// *の場合、全てのgroup fileが対象となる
		if (!groups.equals("*")) {
			evaluations = prop.getString("groups").split(Delimeter.COMMA.getValue());	
		}

		String dir;
		if (prop.getString("simulation_step").equals("1")) {
			dir = prop.getString("dir_groups_step1");	
		} else if (prop.getString("simulation_step").equals("2")) {
			dir = prop.getString("dir_groups_step2");
		} else {
			dir = prop.getString("dir_groups_step3");
		}

		List<File> files = findFiles(dir, evaluations);
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
	}

	/**
	 * group定義ファイルをスキャンして対象のファイルを取得する。
	 * @param groups group idのリスト。空きの場合は全group file　例) [1,2,3]
	 * @return Fileリスト
	 */
	List<File> findFiles(String dir, String[] groups) throws Exception {
		List<File> listFiles = new ArrayList<>();
		Set<String> groupSet = new HashSet<>(Arrays.asList(groups));
		
		
		List<File> files = FileUtil.listFilesByExtensionRecursively(dir, "tsv");
		for (File file : files) {
			// 全ファイル対象
			if (groupSet.isEmpty()) {
				listFiles.add(file);
				continue;
			}

			// ex) 00001_1T-1.tsv -> [00001, 1T-1.tsv]
			String[] token = file.getName().split(Delimeter.UNDERBAR.getValue());
			if (groupSet.contains(StringUtil.unpadZero(token[0]))) {
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
			List<Evaluation> evals = new EvaluationFileLoader().getEvaluations("1T_1_99100");
			System.out.println(evals.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
