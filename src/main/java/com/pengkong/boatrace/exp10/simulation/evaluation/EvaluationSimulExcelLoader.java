package com.pengkong.boatrace.exp10.simulation.evaluation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.util.EvaluationHelper;
import com.pengkong.common.ExcelFileUtil;
import com.pengkong.common.StringUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * excel파일로부터 evaluation을 생성한다.
 * 
 * @author ttolt
 *
 */
public class EvaluationSimulExcelLoader extends AbstractEvaluationLoader {
	/** group定義DBロード */
	@Override
	void initialize() throws Exception {
		mapListEval = new HashMapList<>();
		
		loadExcel();
	}

	void loadExcel() throws Exception {
		// 対象group no
		List<String> listGroupNo = StringUtil.parseNumericOptionsString(prop.getString("groups"));
		
		// ex) file_group_config_step1=C:/Dev/workspace/Oxygen/pod_boatrace/document/expr10/simul1_group.xlsx#groups
		String filepath = prop.getString("file_group_config_step1");
		String[] token = filepath.split("#");
		List<String> lines = ExcelFileUtil.read(token[0], token[1], Delimeter.TAB.getValue());
		
		// タイトル行取得
		String[] titles = lines.remove(0).split(Delimeter.TAB.getValue());
		
		Map<String, String> mapLine;
		HashMap<String, String> mapDuplicateCheck = new HashMap<>();
		// 行loop
		for (int i = 0; i < lines.size(); i++) {
			String[] content = lines.get(i).split(Delimeter.TAB.getValue());
			// 列loop and evaluation生成用map作成
			mapLine = new HashMap<>();
			for (int j = 0; j < titles.length; j++) {
				mapLine.put(titles[j], content[j]);
			}
			
			// コメント行 or group_noが対象外
			if (mapLine.get("sel").equals("#") || 
					!listGroupNo.contains(mapLine.get("group_no"))) {
				continue;
			}

			// Evaluation生成
			Evaluation eval = createEvauation(mapLine);
				
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
	
	Evaluation createEvauation(Map<String, String> rec) {
		Evaluation eval = new Evaluation();
		rec.keySet().forEach(key -> eval.put(key, rec.get(key)));

		return EvaluationHelper.postFixRangeSelector(eval, prop.getString("range_selector"), prop.getString("bonus_pr"),
				prop.getString("bonus_bor"), prop.getString("bonus_bork"), prop.getString("bonus_borkbor"));
	}

	public static void main(String[] args) {
		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
		
		try {
			MLPropertyUtil.getInstance().addFile(propertyFilepath);
			MLPropertyUtil prop = MLPropertyUtil.getInstance();
			prop.putProperty("result_no", "99999");
			prop.putProperty("select", "x");
			prop.putProperty("result_type", "5");
			prop.putProperty("bettype", "3R");
			prop.putProperty("kumiban", "123");
			prop.putProperty("term", "842_1");
			prop.putProperty("probability_type", "digit1");
			prop.putProperty("grade_type", "ip");
			prop.putProperty("groups", "1");
			
			List<Evaluation> evals = new EvaluationSimulExcelLoader().getEvaluations();
			System.out.println(evals.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
