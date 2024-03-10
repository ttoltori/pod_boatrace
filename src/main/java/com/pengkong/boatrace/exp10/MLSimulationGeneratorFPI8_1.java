package com.pengkong.boatrace.exp10;

import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.StringUtil;

public class MLSimulationGeneratorFPI8_1 extends MLSimulationGenerator {
	/**
	 * simulation前処理を行ってからMLResultGeneratorを呼び出す。
	 */
	@Override
	void executeExperiment(String exNo) throws Exception {
		
		String[] termToken = prop.getString("term").split(Delimeter.COMMA.getValue());
		String[] incrToken = prop.getString("incr").split(Delimeter.COMMA.getValue());
		String[] gradeTypeToken = prop.getString("grade_type").split(Delimeter.COMMA.getValue());
		String[] modelnoToken = prop.getString("models").split(Delimeter.COMMA.getValue());
		String[] patternIdToken = prop.getString("patternids").split(Delimeter.COMMA.getValue());
		for (String gradeType : gradeTypeToken) {
			for (String patternId : patternIdToken) {
				for (String modelno : modelnoToken) {
					for (String incr : incrToken) {
						for (String term : termToken) {
							prop.putProperty("grade_type", gradeType);
							prop.putProperty("patternid", patternId);
							prop.putProperty("modelno", modelno);
							prop.putProperty("incr", incr);
							prop.putProperty("term", term);

							preProcess();
							String evaluationsId = String.join(Delimeter.UNDERBAR.getValue(), 
									gradeType, 
									prop.getString("bettype"),
									prop.getString("kumiban"),
									prop.getString("patternid"),
									prop.getString("modelno"), 
									incr, 
									prop.getString("bonus_pr"),
									prop.getString("bonus_bor"),
									prop.getString("bonus_bork"),
									term, 
									prop.getString("group_sql_id"), exNo);
							prop.putProperty("evaluations_id", evaluationsId);

							try {
								executeSuper(exNo);

								// evalustion内訳を出力
								simulationCreator.getEvLoader().printConsole();

							} catch (WarningException e) {
								logger.warn(evaluationsId + " Warining " + e.getMessage());
							} catch (Exception e) {
								logger.error(evaluationsId + " Exception " + e.getMessage(), e);
								throw e;
							}
							afterProcess();
							
						}
					}
				}
			}
		}

	}

	public static void main(String[] args) {
//		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
//		String exNoList = "1";
		String propertyFilepath = args[0];
		String exNoList = args[1];

		List<String> listExno = StringUtil.parseNumericOptionsString(exNoList);

		try {
			// PropertyUtilを使っている箇所もあるので互換性確保のために
			PropertyUtil.getInstance().addFile(propertyFilepath);
			MLPropertyUtil.getInstance().addFile(propertyFilepath);

			MLSimulationGeneratorFPI8_1 generator = new MLSimulationGeneratorFPI8_1();
			generator.execute(listExno);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
