package com.pengkong.boatrace.exp10;

import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.StringUtil;

public class MLSimulationGeneratorPtnIdAll extends MLSimulationGenerator {
	/**
	 * bettype별 전모델,패턴id에 대해 학습기간 수익률이 1이상인 evaluation을 추출해서 simulation한 후에 베팅수가 일정수 이상인 것을 그래프출력
	 */
	@Override
	void executeExperiment(String exNo) throws Exception {

		String[] betTypeToken = prop.getString("bettype").split(Delimeter.COMMA.getValue());
		String[] incrToken = prop.getString("incr").split(Delimeter.COMMA.getValue());
		for (String betType : betTypeToken) {
			for (String incr : incrToken) {
				prop.putProperty("bettype", betType);
				prop.putProperty("incr", incr);
				
				preProcess();
				String evaluationsId = String.join(Delimeter.UNDERBAR.getValue(),  
						prop.getString("grade_type"),
						betType,
						prop.getString("kumiban"),
						prop.getString("patternid"), 
						prop.getString("modelno"), 
						prop.getString("term"),
						prop.getString("bonus_pr"),
						prop.getString("bonus_bork"),
						prop.getString("bonus_bor"),
						incr,
						exNo
						);
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

			MLSimulationGeneratorPtnIdAll generator = new MLSimulationGeneratorPtnIdAll();
			generator.execute(listExno);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
