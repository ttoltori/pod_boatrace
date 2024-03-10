package com.pengkong.boatrace.exp10.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.common.FileUtil;
import com.pengkong.common.collection.HashMapList;

public class EvaluationsIdToolJSJ {
	Logger logger = LoggerFactory.getLogger(EvaluationsIdToolJSJ.class);
	
	Map<String, EvaluationsId> mapIds = new HashMap<>();
	HashMapList<GraphFile> mapFiles = new HashMapList<>();
	
	public void harvestGraphFromTsv(String tsvFilepath, String dirGraphsIn, String dirGraphsOut) throws Exception {
		
		// load graph files
		List<File> files = FileUtil.listFilesByExtensionRecursively(dirGraphsIn, "png");
		for (File file : files) {
			GraphFile gFile = new GraphFile(file);
			mapFiles.addItem(gFile.id.getKeyIgnoreTerm(), gFile);
		}
		
		// load evaluationsid list file
		List<String> lines = FileUtil.readFileByLineArr(tsvFilepath);
		for (String line : lines) {
			String[] lineToken = line.split(Delimeter.TAB.getValue());
			String rank = lineToken[0];
			String id = lineToken[1];
			EvaluationsId eid = new EvaluationsId(id);
			
			List<GraphFile> gFiles = mapFiles.get(eid.getKeyIgnoreTerm());
			if (gFiles == null || gFiles.size() != 2) {
				logger.error("evaluationsid = " + id);
				continue;
			}
			
			for (GraphFile gFile : gFiles) {
				String targetDir = String.join("/", dirGraphsOut, eid.grade, eid.kumiban);
				FileUtil.createDirIfNotExist(targetDir);
				
				String newFIlename;
//				if (eid.factor.equals("i09-100") || eid.factor.equals("incomerate")) {
//					// incamt_dailybet -> daily_incamt_daily_hitrate_dailybetでファイル名変更するために
//					newFIlename = createNewGraphfileName(eid, gFile.id);
//				} else {
					newFIlename = rank + "_" + gFile.file.getName();
//				}
				File targetFile = new File(targetDir + "/" + newFIlename);
				FileUtils.copyFile(gFile.file, targetFile);
			}
		}
	}
	
	
	String createNewGraphfileName(EvaluationsId eid, EvaluationsId fid) {
		return String.join(Delimeter.UNDERBAR.getValue(), fid.grade, fid.bettype, fid.kumiban, fid.patternid, fid.factor, fid.factorMin,
				fid.modelno, fid.sqlid, fid.termtype, fid.term, fid.resultno, 
				eid.detail1, eid.detail2, fid.detail2, fid.detail3) + ".png";
	}

	private class GraphFile {
		public File file;
		public EvaluationsId id;
		
		public GraphFile (File file) {
			this.file = file;
			id = new EvaluationsId(file.getName().replace(".png", ""));
		}
		
	}
	/**
	 * ex) SG_1T_*-*-*_wk12+jyo+race_i09-100_0_79100_JSJ-S2_77_12_10150
	 * ex) ip_1T_1_wk12+jyo+race_incrate_1.0_11609_JSJ-S2_77_12_10077_186_5.7_8.0
	 * @author ttolt
	 *
	 */
	private class EvaluationsId {
		public String grade;
		public String bettype;
		public String kumiban;	
		public String patternid;
		public String factor;
		public String factorMin;
		public String modelno;
		public String sqlid;	
		public String termtype;
		public String term;
		public String resultno;
		
		public String detail1;
		public String detail2;
		public String detail3;
		
		public EvaluationsId(String evalId) {
			parse(evalId);
		}
		
		private void parse(String evalId) {
			String[] token = evalId.split(Delimeter.UNDERBAR.getValue());
			grade = token[0];
			bettype = token[1];
			kumiban = token[2];
			patternid = token[3];
			factor = token[4];
			factorMin = token[5].replace("-", "~");
			modelno = token[6];
			sqlid = token[7];
			termtype = token[8];
			term = token[9];
			resultno = token[10];
			detail1 = token[11];
			detail2 = token[12];
			detail3 = token[13];
		}

		//SG_1T_*-*-*_wk12+jyo+race_i09-100_0_79100_JSJ-S2_77_12_10150_detail1_detail2
		public String getKeyIgnoreTerm() {
			return String.join(Delimeter.UNDERBAR.getValue(), grade, bettype, kumiban, patternid, factor, factorMin,
					modelno, sqlid, termtype);
		}
	}
	public static void main(String[] args) {
		String tsvFilepath = "D:/Dev/experiment/expr10/simulation_step1/simul_JSN_ranked/i0204/top20_i0204_1T.tsv";
		String dirGraphsIn = "D:/Dev/experiment/expr10/simulation_step1/simul_JSN";
		String dirGraphsOut = "D:/Dev/experiment/expr10/simulation_step1/simul_JSN_ranked/i0204/top20_i0204_1T";;
		try {
			new EvaluationsIdToolJSJ().harvestGraphFromTsv(tsvFilepath, dirGraphsIn, dirGraphsOut);
			System.out.println("EvaluationsIdToolJSJ.harvestGraphFromTsv() ended!!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
