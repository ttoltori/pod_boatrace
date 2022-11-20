package com.pengkong.boatrace.exp10.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.StringUtil;

@Deprecated
public class SimulationSelectTool {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	Logger logger = LoggerFactory.getLogger(SimulationSelectTool.class);

	Map<String, File> mapGraph;
	Map<String, File> mapGroup;

	String simulFileDir;
	String groupFileDir;
	String simulSelectedDir;
	
	GroupFileUpdater grpUpdater = new GroupFileUpdater();
	
	String configProperty;
	
	void initialize() throws Exception {
		configProperty = "file_selected_config";
		
		simulFileDir = prop.getString("dir_simulation_store");
		groupFileDir = prop.getString("dir_groups_output_step1");
		simulSelectedDir = prop.getString("dir_simulation_selected");
		
		// 全graph file 取得
		mapGraph = new HashMap<>();
		List<File> files =  FileUtil.listFilesByExtensionRecursively(simulFileDir, "png");
		for (File file : files) {
			// key = file name without extension ex) 50014_2_14_99100_wk1+race_1T_2.png -> 50014_2_14_99100_wk1+race_1T_2
			mapGraph.put(file.getName().split(Delimeter.PERIOD.getValue())[0], file);
		}

		// 全group file 取得
		mapGroup = new HashMap<>();
		files =  FileUtil.listFilesByExtensionRecursively(groupFileDir, "tsv");
		for (File file : files) {
			// key = group no ex) 00008_1T-1.tsv -> 00008
			mapGroup.put(file.getName().split(Delimeter.UNDERBAR.getValue())[0], file);
		}
	}

	public void execute(List<String> exNoList) throws Exception {
		if (configProperty == null) {
			initialize();
		}
		
		// 実験番号mapを生成する
		HashMap<String, String> mapExNo = new HashMap<>();
		for (String exNo : exNoList) {
			mapExNo.put(exNo, exNo);
		}
		
		prop.reset(configProperty);
		while(prop.hasNext()) {
			prop.next();
			if (!mapExNo.containsKey(prop.getString("selected_no"))) 
				continue;
			// 実験実行
			executeExperiment(prop.getString("selected_no"));
		}
	}

	void executeExperiment(String exNo) throws Exception {
		String graphFilename = prop.getString("filename");
		File target;
		target = mapGraph.get(graphFilename);
		if (target == null) {
			logger.warn("graph file does't exist. " + exNo + ", " + graphFilename);
		} else {
			// graph file move
			moveFile(target, "graph");
		}
		
		// ex) 60003_2_7998_99102_wk12+jyo_1T_1 -> 7998
		String groupNo = StringUtil.leftPad(graphFilename.split(Delimeter.UNDERBAR.getValue())[2], 5, "0");
		target = mapGroup.get(groupNo);
		if (target == null) {
			logger.warn("group file does't exist. " + exNo + ", " + groupNo);
			return;
		}
		
		// group file move
		target = moveFile(target, "group");
		
		// group file upate
		updateGroupFile(target);
	}

	
	File moveFile(File file, String fileType) throws Exception {
		String targetPath;
		String[] fileNamePart = file.getName().split("\\.")[0].split("_");
		
		if (fileType.equals("graph")) { // graph
			// filenamepart=50001_4_1_99100_prob1+wk1_1T_1  -> ~/1T/1/
			targetPath = simulSelectedDir + fileType + "/" + fileNamePart[5] + "/" + fileNamePart[6]; 
		} else { // group
			// filenamepart=00045_1T-5.tsv ~/1T/5/
			String[] betTypeKumibanToken = fileNamePart[1].split("-");
			targetPath = simulSelectedDir + fileType + "/" + betTypeKumibanToken[0] + "/" + betTypeKumibanToken[1]; 
		}
		FileUtil.createDirIfNotExist(targetPath);
		
		String targetFilepath = targetPath + "/" + file.getName();
		File target = new File(targetFilepath);

		// 既存ファイルがあればbakにrenameする
		if (target.exists()) {
			//target.renameTo(new File(targetFilepath + ".bak"));
			//logger.warn("pre-existed file renamed to " + targetFilepath + ".bak");
			FileUtil.deleteFile(targetFilepath);
			logger.warn("pre-existed file deleted" + targetFilepath );
		}
		
		if (fileType.equals("graph")) {
			file.renameTo(target);
			logger.info(fileType + " file moved to " + targetFilepath);
		} else {
			FileUtils.copyFile(file, target);
			logger.info(fileType + " file copied to " + targetFilepath);
		}
		
		return target;
	}

	void updateGroupFile(File file) throws Exception {
		String borMin, borkMin;
		String borUpdate;
		String borkUpdate;
		
		// BONUS_BOR
		borMin = prop.getString("bor_min");
		if (!borMin.equals("x")) {
			borUpdate = borMin + "~" + prop.getString("bor_max") + "=1";
		} else {
			borUpdate = "x";
		}
		
		// BONUS_BORK
		borkMin = prop.getString("bork_min");
		if (!borkMin.equals("x")) {
			borkUpdate = borkMin + "~" + prop.getString("bork_max") + "=1";
		} else {
			borkUpdate = "x";
		}
		
		grpUpdater.updateBonus(file, null, borUpdate, borkUpdate);
		logger.info("group file updated BONUS_BOR:" + borUpdate + ", BONUS_BORK:" + borkUpdate);
	}
	
	public static void main(String[] args) {
		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
		String exNoList = "141";
		
		List<String> listExno = StringUtil.parseNumericOptionsString(exNoList);
		try {
			MLPropertyUtil.getInstance().addFile(propertyFilepath);

			SimulationSelectTool tool = new SimulationSelectTool();
			tool.execute(listExno);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
