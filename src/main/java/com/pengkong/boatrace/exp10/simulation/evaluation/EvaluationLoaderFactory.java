package com.pengkong.boatrace.exp10.simulation.evaluation;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

public class EvaluationLoaderFactory {
	public static AbstractEvaluationLoader create() {
		String loaderType = MLPropertyUtil.getInstance().getString("evaluation_loader");
		if (loaderType == null || loaderType.equals("EvaluationFileLoader")) {
			return new EvaluationFileLoader();	
		} else if (loaderType.equals("EvaluationSimul2Loader")) {
			return new EvaluationSimul2Loader();
		} else if (loaderType.equals("EvaluationSimul3Loader")) {
			return new EvaluationSimul3Loader();
		} else {
			return new EvaluationSimulExcelLoader();
		}
	}
}
