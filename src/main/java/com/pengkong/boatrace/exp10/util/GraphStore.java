package com.pengkong.boatrace.exp10.util;

import java.io.File;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.mybatis.entity.MlEvaluation;
import com.pengkong.boatrace.online.dao.MlEvaluationDAO;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.FileUtil;

/**
 * result_type 2,21の結果物を分析用のディレクトリにファイル名変更して移動させる。
 * @author ttolt
 *
 */
public class GraphStore {
	Logger logger = LoggerFactory.getLogger(GraphStore.class);

	MlEvaluationDAO dao;
	
	void initialize() throws Exception {
		SqlSession session = DatabaseUtil.open("mybatis-config.0.xml", false);
		dao = new MlEvaluationDAO(session);
	}

	public void execute(String srcDir, String targetDir) throws Exception {
		if (dao == null) {
			initialize();
		}
		
		List<File> files = FileUtil.listFilesByExtensionRecursively(srcDir, "png");
		int count = 1;
		for (File file : files) {
			// *!all*だけ対象 */
			if (!file.getName().contains("!all")) {
				continue;
			}
			
			File target = renameAndMove(file, targetDir);
			if (target == null) {
				logger.warn("move failed. " + file.getAbsolutePath());
			}
			logger.info("progress " + count++ + "/" + files.size());
		}
	}
	
	File renameAndMove(File file, String targetDir) throws Exception {
		// 62051_!all_1T_1.png
		String fName = file.getName();
		
		// DTO取得
		MlEvaluation eval = dao.selectOne(fName.split("_")[0]); // 62051
		if (eval == null) {
			return null;
		}
		
		// D:\Dev\experiment\expr10\simulation_store\99080_ip_62051-64750\62051_2-10046-1T-1_count_ptn_x_x_x_x -> 99080
		String modelNo = file.getParentFile().getParentFile().getName().split("_")[0];
		
		// resultno_resulttype_groupno_modelno_patternid_bettype_kumiban.png ex) 62051_21_10046_99080_wk1+prob12_1T_1.png
		String newName = String.join("_", 
				eval.getResultno(), 
				eval.getResultType(),
				eval.getEvaluationsId(),
				modelNo,
				eval.getPatternid(),
				eval.getBettype(),
				eval.getKumiban()
				);
		
		// ディレクトリなければ作成 ex)
		targetDir += "/" + eval.getBettype() + "/" + eval.getKumiban();
		FileUtil.createDirIfNotExist(targetDir);
		
		File targetFile = new File(targetDir + "/" + newName + ".png");
		
		boolean result = file.renameTo(targetFile);
		if (!result) {
			return null;
		}
		
		return targetFile;
	}
	
	
	public static void main(String[] args) {
//		File file = new File("D:/Dev/experiment/expr10/simulation_store/99080_ip_62051-64750/62051_2-10046-1T-1_count_ptn_x_x_x_x/62051_!all_1T_1.png");
//		System.out.println(file.getName()); // 62051_!all_1T_1.png
//		System.out.println(file.getParent()); //D:\Dev\experiment\expr10\simulation_store\99080_ip_62051-64750\62051_2-10046-1T-1_count_ptn_x_x_x_x
//		System.out.println(file.getParentFile().getName());
//		System.out.println(file.getAbsolutePath());
		try {
			new GraphStore().execute("D:/Dev/experiment/expr10/simulation_store", "D:/Dev/experiment/expr10/simulation_work");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
