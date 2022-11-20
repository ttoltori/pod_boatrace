package com.pengkong.boatrace.weka.automation.regression;

import java.util.Arrays;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.common.CommandExecutor;
import com.pengkong.common.FileUtil;

public class RegressionModelBatchCreator {

	Logger logger = LoggerFactory.getLogger(RegressionModelBatchCreator.class);
	
	public RegressionModelBatchCreator() {
	}

	public void create(String defFileDir, String executeFileName, String defName, String fromYmd, String toYmd1, String toYmd2, int interval) throws Exception{
		String executeFileFullpath = defFileDir + "/" + executeFileName;
		DateTime dateToYmd1 = DateTime.parse(toYmd1, DateTimeFormat.forPattern("yyyyMMdd"));
		DateTime dateToYmd2 = DateTime.parse(toYmd2, DateTimeFormat.forPattern("yyyyMMdd"));
		
		while(dateToYmd1.isBefore(dateToYmd2)) {
			String endYmd = DateTimeFormat.forPattern("yyyyMMdd").print(dateToYmd1);
			
			String modelFileFullpath = defFileDir + "/regression_release/" + defName + "_" + fromYmd + "_" + endYmd + ".model";
			// すでに結果のモデルファイルが存在している場合は、スキップする。
			if (!FileUtil.isExist(modelFileFullpath)) {
				logger.info("executing " + executeFileFullpath + " : " + endYmd  );
				int exitcode = CommandExecutor.execute(executeFileFullpath, Arrays.asList(defName, fromYmd, endYmd), 1000);
				System.out.println(exitcode);
			}
			
			dateToYmd1 = dateToYmd1.plusDays(interval);
		}
	}
	
	public static void main(String[] args) {
//		args = new String[] {
//			"C:/Dev/workspace/Oxygen/pod_boatrace/test/wekamodels",
//			"make_arff_model_bayesnet_multi.bat",
//			"40_1T_1_entry_multi",
//			"20100101",
//			"20161231",
//			"20181231",
//			"364"
//		};
		
    	if (args.length != 7) {
    		System.out.println("Usage: ModelBatchCreator {work directory} {execution file name} {definition name only} {from ymd} {to ymd1} {to ymd2} {interval}");
    		System.exit(1);
    	}
    	
    	
		RegressionModelBatchCreator ac = new RegressionModelBatchCreator();
		try {
			String dir = args[0];
			String executableName = args[1];
			String defName = args[2];
			String fromYmd = args[3];
			String toYmd1 = args[4];
			String toYmd2 = args[5];
			int interval = Integer.parseInt(args[6]);
			
			ac.create(dir, executableName,  defName, fromYmd, toYmd1, toYmd2, interval);
			
		} catch (Exception e) {
			ac.logger.error("failed.", e);
		}
	}
}
