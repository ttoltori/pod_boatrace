package com.pengkong.boatrace.weka.automation.regression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.server.rmi.regression.RegressionDbRmiClient;
import com.pengkong.boatrace.weka.automation.DefineFileParser;
import com.pengkong.boatrace.weka.automation.NominalManager;
import com.pengkong.common.FileUtil;

public class RegressionArffCreator {

	Logger logger = LoggerFactory.getLogger(RegressionArffCreator.class);
	
	NominalManager nominalManager;
	
	DefineFileParser defParser;
	
	RegressionDbRmiClient rmiClient;
	
	// RMI 서버 url
	private final String RMI_URL = "rmi://localhost:50762/regrdb";
	
	public RegressionArffCreator() {
		 nominalManager = new NominalManager();
		 defParser = new DefineFileParser();
	}

	public void create(String dir, String defFileName, String fromYmd, String toYmd) throws Exception {
		 rmiClient = new RegressionDbRmiClient(RMI_URL);
		
		// parse def file
		defParser.parseFile(dir + "/" + defFileName);
		String arffName = defFileName.split("\\.")[0];
		// write file (ex: {dir}/2F_01-01.arff.header)
		String headerFilepath = dir + "/regression_arff/" + arffName + ".arff.header";
		
		logger.info("creating temporary arff header file...");
		createArffHeader(headerFilepath);

		String bodyFilepath = dir + "/regression_arff/" + arffName + ".arff.body";
		String betType = arffName.split("_")[1];
		logger.info("creating temporary arff body file...");
		rmiClient.createCsv(fromYmd, toYmd, bodyFilepath, betType);
		
		String arffFilepath = dir + "/regression_arff/" + arffName + ".arff";
		ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "copy", 
				headerFilepath.replaceAll("/", "\\\\") + "+" + bodyFilepath.replaceAll("/", "\\\\"), 
				arffFilepath.replaceAll("/", "\\\\"));
		Process process = pb.start();
		int ret = process.waitFor();
		if (ret != 0) {
			throw new Exception("Process execution failed.");
		}
		logger.info("arff file saved. " + arffFilepath);
		
		FileUtil.deleteFile(headerFilepath);
		FileUtil.deleteFile(bodyFilepath);
		logger.info("temporary files are deleted.");
	}
	
	private void createArffHeader(String headerFilepath) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("% " + defParser.getValue("name")); sb.append(System.lineSeparator());
		sb.append("@RELATION boatrace"); sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		
		String[] tokenAttrs = defParser.getValue("attributes").split(",");
		for (String attr : tokenAttrs) {
			sb.append(convertAttribute(attr));
			sb.append(System.lineSeparator());
		}
		
		sb.append("@DATA");
		
		FileUtil.writeFile(headerFilepath, sb.toString()); // 最後にnewline追加される
	}
	
	private String convertAttribute(String attribute) {
		String[] tokenAttr = attribute.split(":");
		String attrName = tokenAttr[0].trim();
		String attrType = tokenAttr[1].trim();
		
		// nominal attribute
		if (attrType.equals("nominal")) {
			return "@ATTRIBUTE " + attrName + " " + nominalManager.getNominalAttr(attrName);
		} else if (attrType.equals("numeric")) {
			// numeric attribute
			return "@ATTRIBUTE " + attrName + " NUMERIC";	
		}
		
		// 직접지정
		return "@ATTRIBUTE " + attrName + " " + attrType.replaceAll("_", ",");
		 
	}
	
	public static void main(String[] args) {
//		args = new String[] {
//			"C:/Dev/workspace/Oxygen/pod_boatrace_test/wekamodels",
//			"208_3T_123-321_regr.def",
//			"20100101",
//			"20141231"
//		};
		   		
    	if (args.length != 4) {
    		System.out.println("Usage: ArffCreator {work directory} {definition file name} {from ymd} {to ymd} ");
    		System.exit(1);
    	}
    	
		RegressionArffCreator ac = new RegressionArffCreator();
		try {
			String dir = args[0];
			String defFileName = args[1];
			String fromYmd = args[2];
			String toYmd = args[3];
			
			ac.create(dir, defFileName, fromYmd, toYmd);
			
		} catch (Exception e) {
			ac.logger.error("failed.", e);
		}
	}
}
