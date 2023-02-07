package com.pengkong.boatrace.exp10.simulation.evaluation;

import java.util.List;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * evaluation一件をプロパティから取得する
 * 
 * @author ttolt
 *
 */
public class EvaluationSimulLoaderProperty extends AbstractEvaluationLoader {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	@Override
	void initialize() throws Exception {
		mapListEval = new HashMapList<>();
		loadProperty();
	}

	/**
	 * group
	 * 
	 * @throws Exception
	 */
	void loadProperty() throws Exception {
        Evaluation eval = new Evaluation();

        eval.put("sel", "~");
        eval.put("grades", prop.getString("grade_condition"));
        eval.put("bettype", prop.getString("bettype"));
        eval.put("kumiban", prop.getString("kumiban"));
        eval.put("resultno", prop.getString("x"));
        eval.put("modelno", prop.getString("modelno"));
        eval.put("patternid", prop.getString("patternid"));
        eval.put("pattern", prop.getString("pattern"));
        eval.put("range_selector", "x");
        eval.put("bonus_pr",  prop.getString("bonus_pr"));
        eval.put("bonus_bor",  prop.getString("bonus_bor"));
        eval.put("bonus_bork",  prop.getString("bonus_bork"));
        eval.put("bonus_borkbor",  "x");
	        
        mapListEval.addItem(eval.getBettypeKumibanModelNo(), eval);
        listEval.add(eval);
	}

	public static void main(String[] args) {
		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
		
		try {
			MLPropertyUtil.getInstance().addFile(propertyFilepath);
			MLPropertyUtil prop = MLPropertyUtil.getInstance();
			
			List<Evaluation> evals = new EvaluationSimulLoaderProperty().getEvaluations();
			System.out.println(evals.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
