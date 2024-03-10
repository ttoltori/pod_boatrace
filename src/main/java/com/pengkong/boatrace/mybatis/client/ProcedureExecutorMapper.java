package com.pengkong.boatrace.mybatis.client;

import java.util.HashMap;

public interface ProcedureExecutorMapper {
	void executeOutputWekaPattern(HashMap<String, ?> hashmap);

	void executeFinalizeWekaPattern(HashMap<String, ?> hashmap);
	
	void executeFinalizeManualPattern(HashMap<String, ?> hashmap);
	
	void executeAnalyzeWekaPattern(HashMap<String, ?> hashmap);
	
	void executeAnalyzeWekaKumiban(HashMap<String, ?> hashmap);
	
	void executeSql(String paramSql);
	
	void executeOutputMultiWeka(HashMap<String, ?> hashmap);
	
	void executeOutputWeka(HashMap<String, ?> hashmap);
	
	void executeGenerateDataset49(HashMap<String, String> hashmap);
}