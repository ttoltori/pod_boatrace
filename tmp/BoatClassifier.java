package com.pengkong.boatrace.weka.classifiers;

import java.util.HashMap;

public interface BoatClassifier {
	public void initialize(String[] classVal, String modelFilepath) throws Exception;
	public String predictFromHashmap(HashMap<String, ?> hashmap) throws Exception;
}
