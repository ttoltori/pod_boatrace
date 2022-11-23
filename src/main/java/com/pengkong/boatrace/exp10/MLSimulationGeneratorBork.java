package com.pengkong.boatrace.exp10;

import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.StringUtil;

public class MLSimulationGeneratorBork extends MLSimulationGenerator {
	/**
	 * simulation前処理を行ってからMLResultGeneratorを呼び出す。
	 */
	@Override
	void executeExperiment(String exNo) throws Exception {

		String[] termToken = prop.getString("term").split(Delimeter.COMMA.getValue());
		String[] betTypeToken = prop.getString("bettype").split(Delimeter.COMMA.getValue());
		String[] incrToken = prop.getString("incr").split(Delimeter.COMMA.getValue());
		String[] borkTrimToken = prop.getString("bork_trim").split(Delimeter.COMMA.getValue());
		String[] borkRangeToken = prop.getString("bork_range").split(Delimeter.COMMA.getValue());
		String[] borkConvToken = prop.getString("bork_bor_convert").split(Delimeter.COMMA.getValue());
		String[] modelToken = prop.getString("model").split(Delimeter.COMMA.getValue());
		String[] gradeTypeToken = prop.getString("grade_type").split(Delimeter.COMMA.getValue());
		String[] customToken = prop.getString("custom").split(Delimeter.COMMA.getValue());
		for (String term : termToken) {
			for (String betType : betTypeToken) {
				for (String incr : incrToken) {
					for (String borkTrim : borkTrimToken) {
						for (String borkRange : borkRangeToken) {
							for (String borkConv : borkConvToken) {
								for (String model : modelToken) {
									for (String gradeType : gradeTypeToken) {
										for (String custom : customToken) {
											prop.putProperty("term", term);
											prop.putProperty("bettype", betType);
											prop.putProperty("incr", incr);
											prop.putProperty("bork_trim", borkTrim);
											prop.putProperty("bork_range", borkRange);
											prop.putProperty("bork_bor_convert", borkConv);
											prop.putProperty("model", model);
											prop.putProperty("grade_type", gradeType);
											prop.putProperty("custom", custom);
											preProcess();
											String evaluationsId = String.join(Delimeter.UNDERBAR.getValue(), 
													exNo, 
													betType,
													prop.getString("kumiban"), 
													borkTrim,
													borkRange,
													prop.getString("group_sql_id", "x"), 
													term,
													prop.getString("result_type"), 
													gradeType,
													model, 
													custom,
													borkConv,
													incr 
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
							}
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

			MLSimulationGeneratorBork generator = new MLSimulationGeneratorBork();
			generator.execute(listExno);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}