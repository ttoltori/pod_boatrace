package com.pengkong.boatrace.exp10;

import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.StringUtil;

public class MLSimulationGeneratorFPI8 extends MLSimulationGenerator {
	/**
	 * simulation前処理を行ってからMLResultGeneratorを呼び出す。
	 */
	@Override
	void executeExperiment(String exNo) throws Exception {

		String[] betTypeToken = prop.getString("bettype").split(Delimeter.COMMA.getValue());
		String[] termToken = prop.getString("term").split(Delimeter.COMMA.getValue());
		String[] incrToken = prop.getString("incr").split(Delimeter.COMMA.getValue());
		String[] gradeTypeToken = prop.getString("grade_type").split(Delimeter.COMMA.getValue());
		for (String term : termToken) {
			for (String betType : betTypeToken) {
				for (String incr : incrToken) {
					for (String gradeType : gradeTypeToken) {
						prop.putProperty("term", term);
						prop.putProperty("bettype", betType);
						prop.putProperty("incr", incr);
						prop.putProperty("grade_type", gradeType);

						preProcess();
						String evaluationsId = String.join(Delimeter.UNDERBAR.getValue(), gradeType, betType, prop.getString("kumiban"),
								prop.getString("patternids"),
								prop.getString("models"), 
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

			MLSimulationGeneratorFPI8 generator = new MLSimulationGeneratorFPI8();
			generator.execute(listExno);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
