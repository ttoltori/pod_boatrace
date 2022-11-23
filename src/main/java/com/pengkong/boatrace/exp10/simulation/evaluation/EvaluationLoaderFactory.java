package com.pengkong.boatrace.exp10.simulation.evaluation;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

public class EvaluationLoaderFactory {
	public static AbstractEvaluationLoader create() {
		String loaderType = MLPropertyUtil.getInstance().getString("evaluation_loader");
		if (loaderType == null || loaderType.equals("EvaluationFileLoader")) {
			return new EvaluationFileLoader();	
		} else if (loaderType.equals("EvaluationSimul2Loader")) {
			return new EvaluationSimul2Loader();
		} else if (loaderType.equals("EvaluationSimulLoaderBork")) {
			return new EvaluationSimulLoaderBork();
		} else if (loaderType.equals("EvaluationSimulLoaderPr")) {
			return new EvaluationSimulLoaderPr();
		} else if (loaderType.equals("EvaluationSimulLoaderPtnId")) {
			return new EvaluationSimulLoaderPtnId();
		} else {
			return new EvaluationSimulExcelLoader();
		}
	}
}
