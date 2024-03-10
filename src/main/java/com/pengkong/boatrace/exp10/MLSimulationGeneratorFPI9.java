package com.pengkong.boatrace.exp10;

import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.StringUtil;

public class MLSimulationGeneratorFPI9 extends MLSimulationGenerator {
	/**
	 * simulation前処理を行ってからMLResultGeneratorを呼び出す。
	 */
	@Override
	void executeExperiment(String exNo) throws Exception {

		String[] termToken = prop.getString("term").split(Delimeter.COMMA.getValue());
		String[] bonusBorkToken = prop.getString("bonus_bork").split(Delimeter.COMMA.getValue());
		for (String term : termToken) {
			for (String bonusBork : bonusBorkToken) {
				prop.putProperty("term", term);
				prop.putProperty("bonus_bork", bonusBork);

				preProcess();
				String evaluationsId = String.join(Delimeter.UNDERBAR.getValue(), 
						prop.getString("grade_type"), 
						prop.getString("bettype"),
						prop.getString("kumiban"),
						prop.getString("patternid"),
						prop.getString("pattern"),
						prop.getString("modelno"), 
						prop.getString("bonus_bork"),
						prop.getString("bonus_bor"),
						prop.getString("bonus_pr"),
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

			MLSimulationGeneratorFPI9 generator = new MLSimulationGeneratorFPI9();
			generator.execute(listExno);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
