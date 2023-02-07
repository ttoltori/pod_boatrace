package com.pengkong.boatrace.exp10.simulation.evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.collection.HashMapList;

import lombok.Getter;

/**
 * evaluation一件をプロパティから取得する
 * 
 * @author ttolt
 *
 */
public class EvaluationSimulLoaderResultGraphFilename extends AbstractEvaluationLoader {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	EvaluationSimulGroupFileGenerator groupFileGenerator;

	@Override
	void initialize() throws Exception {
		mapListEval = new HashMapList<>();
		groupFileGenerator = new EvaluationSimulGroupFileGenerator();

		loadFilenames();
	}

	
	void loadFilenames() throws Exception {
		String dir = prop.getString("dir_result_selected");
		List<File> files = FileUtil.listFilesByExtensionRecursively(dir, "png");
		
		for (File file : files) {
			String fileName = file.getName();
			if (fileName.startsWith("#")) {
				continue;
			}
			
			// ex) 1_1T_1_jyo+alcnt_09-3_97080_005265_1.2
			ResultGraphFilename rgFile = new ResultGraphFilename(fileName.split("\\.png")[0]);
			
	        Evaluation eval = new Evaluation();

	        eval.put("sel", "~");
	        eval.put("grades", rgFile.getGrades());
	        eval.put("bettype", rgFile.getBettype());
	        eval.put("kumiban", rgFile.getKumiban());
	        eval.put("resultno", rgFile.getResulno());
	        eval.put("modelno", rgFile.getModelno());
	        eval.put("patternid", rgFile.getPatternid());
	        eval.put("pattern", rgFile.getPattern());
	        eval.put("dailybet", String.valueOf(rgFile.getDailybet()));
	        
	        eval.put("range_selector", "x");
	        eval.put("bonus_pr",  prop.getString("bonus_pr"));
	        eval.put("bonus_bor",  prop.getString("bonus_bor"));
	        eval.put("bonus_bork",  prop.getString("bonus_bork"));
	        eval.put("bonus_borkbor",  "x");
	        
			mapListEval.addItem(eval.getBettypeKumibanModelNo(), eval);
			listEval.add(eval);
		}

		// group file 保存
		groupFileGenerator.generateWithEvaluations("no sql", listEval);
	}
	
	@Getter
	private class ResultGraphFilename {
		private String grades;
		private String bettype;
		private String kumiban;
		private String resulno;
		private String modelno;
		private String patternid;
		private String pattern;
		private double dailybet;
		
		public ResultGraphFilename(String fileName) {
			// ex) 1_1T_1_jyo+alcnt_09-3_97080_005265_1.2
			String[] token = fileName.split(Delimeter.UNDERBAR.getValue());
			
			// result_type=1 or 1C
			if (token[0].equals("1") || token[0].equals("1C")) {
				grades = "ip.G3";
			} else {
				grades = "SG,G1,G2";
			}
			
			this.bettype = token[1];
			this.kumiban = token[2];
			this.patternid = token[3];
			this.pattern = token[4];
			this.modelno = token[5];
			this.resulno = Integer.valueOf(token[6]).toString();
			this.dailybet = Double.valueOf(token[7]); 
		}
	}

	public static void main(String[] args) {
		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
		
		try {
			MLPropertyUtil.getInstance().addFile(propertyFilepath);
			MLPropertyUtil prop = MLPropertyUtil.getInstance();
			
			List<Evaluation> evals = new EvaluationSimulLoaderResultGraphFilename().getEvaluations();
			System.out.println(evals.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
