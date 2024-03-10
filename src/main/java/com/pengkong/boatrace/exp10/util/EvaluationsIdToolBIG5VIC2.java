package com.pengkong.boatrace.exp10.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.common.FileUtil;
import com.pengkong.common.collection.HashMapList;

public class EvaluationsIdToolBIG5VIC2 {
	Logger logger = LoggerFactory.getLogger(EvaluationsIdToolBIG5VIC2.class);
	
	Map<String, EvaluationsId> mapIds = new HashMap<>();
	HashMapList<GraphFile> mapFiles = new HashMapList<>();
	
	public void saveFilelist() throws Exception {
		// load graph files
		List<File> files = FileUtil.listFilesByExtensionRecursively("F:/simul_step1/VIC2", "png");
		for (File file : files) {
			GraphFile gFile = new GraphFile(file);
			FileUtil.appendFileByLine("F:/simul_step1/VIC2/files.txt", gFile.fullPath);
		}
	}
	
	public void harvestGraphFromTsv(String simType, String simCond) throws Exception {
//		String dirGraphsIn = "F:/simul_step1/" + simType;
//		// load graph files
//		List<File> files = FileUtil.listFilesByExtensionRecursively(dirGraphsIn, "png");
//		for (File file : files) {
//			GraphFile gFile = new GraphFile(file);
//			mapFiles.addItem(gFile.id.getKeyIgnoreTerm(), gFile);
//		}
		String fileGraphsIn = "F:/simul_step1/" + simType + "/files.txt";
		List<String> files = FileUtil.readFileByLineArr(fileGraphsIn);
		for (String file : files) {
			GraphFile gFile = new GraphFile(new File(file));
			mapFiles.addItem(gFile.id.getKeyIgnoreTerm(), gFile);
		}
		
		String tsvFilepath = "D:/simul_step1/candidates_tsv/" + simType + "/" + simCond + ".tsv";
		
		// load evaluationsid list file
		List<String> lines = FileUtil.readFileByLineArr(tsvFilepath);
		System.out.println("total=" + lines.size() );
		int total = lines.size(); 
		int cnt = 0;
		for (String line : lines) {
			cnt += 1;
			if (cnt % 100 == 0) {
				System.out.println(cnt + " / " + total );
			}
			EvaluationsId eid = new EvaluationsId(line);
			
			List<GraphFile> gFiles = mapFiles.get(eid.getKeyIgnoreTerm());
			if (gFiles == null || gFiles.size() != 3) {
				logger.error("evaluationsid = " + eid.getKeyIgnoreTerm());
				continue;
			}
			
			for (GraphFile gFile : gFiles) {
				String targetDir = String.join("/", "D:/simul_step1/candidates", eid.grade, eid.bettype, eid.kumiban, simType + "_" + simCond);
				FileUtil.createDirIfNotExist(targetDir);
				
				File targetFile = new File(targetDir + "/" + gFile.file.getName());
				FileUtils.copyFile(gFile.file, targetFile);
			}
		}
	}
	
	private class GraphFile {
		public File file;
		public String fullPath;
		public EvaluationsId id;
		
		public GraphFile (File file) {
			this.file = file;
			this.fullPath = file.getAbsolutePath();
			id = new EvaluationsId(file.getName().replace(".png", ""));
		}
	}
	
	
	/**
	 * ex) ip_1T_1_i02_hitrate_1.0_30_1_70161_BIG5-77_5-3_r1_80_5.73_12.2.png
	 * @author ttolt
	 *
	 */
	private class EvaluationsId {
		public String grade;
		public String bettype;
		public String kumiban;	
		public String factor;
		public String factor2;
		public String factorMin;
		public String limit;
		public String modelno;
		public String resultno;
		public String sqlid;	
		public String termtype;
//		public String term;
		
//		public String detail1;
//		public String detail2;
//		public String detail3;
		
		public EvaluationsId(String evalId) {
			parse(evalId);
		}
		
		private void parse(String evalId) {
			String[] token = evalId.split(Delimeter.UNDERBAR.getValue());
			grade = token[0];
			bettype = token[1];
			kumiban = token[2];
			factor = token[3];
			factor2 = token[4];
			factorMin = token[5];
			limit = token[6];
			modelno = token[7];
			resultno = token[8];
			sqlid = token[9];
			termtype = token[10];
//			term = token[11];
			
//			detail1 = token[12];
//			detail2 = token[13];
//			detail3 = token[14];
		}

		//SG_1T_*-*-*_wk12+jyo+race_i09-100_0_79100_JSJ-S2_77_12_10150_detail1_detail2
		public String getKeyIgnoreTerm() {
			return String.join(Delimeter.UNDERBAR.getValue(), grade, bettype, kumiban, factor, factor2, factorMin, limit, 
					modelno, resultno, sqlid, termtype);
		}
	}
	public static void main(String[] args) {
		String tsvFilepath;
		String dirGraphsIn;
		String dirGraphsOut;
		String simType = args[0];
		String simCond = args[1];
		if (args[0].startsWith("BIG")) {
			tsvFilepath = "D:/simul_step1/BIG5_candidates/" + args[1] + ".tsv";
			dirGraphsIn = "F:/simul_step1/BIG5";
			dirGraphsOut = "D:/simul_step1/BIG5_candidates/" +  args[1];
		} else {
			tsvFilepath = "D:/simul_step1/VIC2_candidates/" + args[1] + ".tsv";
			dirGraphsIn = "D:/simul_step1/VIC2";
			dirGraphsOut = "D:/simul_step1/VIC2_candidates/" +  args[1];
		}
		try {
			// ex) simType=BIG5 or VIC2   simCond=i02+0 
			new EvaluationsIdToolBIG5VIC2().harvestGraphFromTsv(simType, simCond);
			//new EvaluationsIdToolBIG5VIC2().saveFilelist();
			System.out.println("EvaluationsIdToolBIG5.harvestGraphFromTsv() ended!!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
