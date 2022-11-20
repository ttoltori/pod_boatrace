package com.pengkong.boatrace.weka.classifiers.regression;

import java.util.HashMap;

public interface BoatRegression {
	public void initialize(String[] classVal, String modelFilepath) throws Exception;
	public double predictFromHashmap(HashMap<String, ?> hashmap) throws Exception;
}
