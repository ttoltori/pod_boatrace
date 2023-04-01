package com.pengkong.boatrace.exp10.simulation.evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.property.SqlTemplate;
import com.pengkong.boatrace.exp10.util.EvaluationHelper;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.collection.HashMapList;

import lombok.Getter;

/**
 * evaluation一件をプロパティから取得する
 * 
 * @author ttolt
 *
 */
public class EvaluationSimulLoaderFPI7GraphFilename extends AbstractEvaluationLoader {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/** sql template */
	SqlTemplate sqlTpl = SqlTemplate.getInstance();
	
	EvaluationSimulGroupFileGenerator groupFileGenerator;

	@Override
	void initialize() throws Exception {
		mapListEval = new HashMapList<>();
		groupFileGenerator = new EvaluationSimulGroupFileGenerator();

		List<SimulGraphFilename> fileNames = loadFilenames();
		
		loadDB(fileNames);
	}

	/**
	 * group
	 * 
	 * @throws Exception
	 */
	void loadDB(List<SimulGraphFilename> fileNames) throws Exception {
		session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		try {
			CustomMapper customMapper = session.getMapper(CustomMapper.class);

			List<DBRecord> results = new ArrayList<>();
			
			for (SimulGraphFilename sgFilename : fileNames) {
				String sql = sqlTpl.get(prop.getString("group_sql_id"));

				sql = sql.replace("{bettype}", sgFilename.getBettype());
				sql = sql.replace("{kumiban}", sgFilename.getKumiban());
				sql = sql.replace("{modelno}", sgFilename.getModelno());
				sql = sql.replace("{patternid}", sgFilename.getPatternid());
				String incr = sgFilename.getIncr();
				if (!incr.contains(Delimeter.WAVE.getValue())) {
					throw new IllegalStateException("incrは「~」で分けて表現してください。");
				}
				String[] incrToken = incr.split(Delimeter.WAVE.getValue());
				sql = sql.replace("{incrmin}", incrToken[0]);
				if (incrToken[1].equals("x")) {
					sql = sql.replace("{incrmax}", "999");
				} else {
					sql = sql.replace("{incrmax}", incrToken[1]);
				}

				if (prop.getString("grade_type").equals("ip")) {
					sql = sql.replace("{result_type}", "1");
				} else if (prop.getString("grade_type").equals("SG")) {
					sql = sql.replace("{result_type}", "11");	
				} else {
					throw new IllegalStateException("undefined grade type.");
				}
				
				HashMap<String, String> mapParam = new HashMap<>();
				mapParam.put("sql", sql);

				// 디비 데이터 일람 취득
				results.addAll(customMapper.selectSql(mapParam));
			}
			
			if (results.size() <= 0) {
				throw new WarningException("db has no evaluation data.");
			}
			
			// Evaluation生成
			HashMap<String, String> mapDuplicateCheck = new HashMap<>();
			for (DBRecord rec : results) {
				// Evaluationファイル１行生成
				Evaluation eval = EvaluationHelper.createEvaluation(rec);
				
				eval =  EvaluationHelper.postFixRangeSelector(eval, prop.getString("range_selector"), prop.getString("bonus_pr"),
						prop.getString("bonus_bor"), prop.getString("bonus_bork"), prop.getString("bonus_borkbor"));

				String unkqueKey = eval.getUniqueKey();
				// 重複あり
				if (mapDuplicateCheck.containsKey(unkqueKey)) {
					logger.warn("Duplicate evaluation exists. " + eval);
					continue;
				}
				mapDuplicateCheck.put(unkqueKey, unkqueKey);
				
				mapListEval.addItem(eval.getBettypeKumibanModelNo(), eval);
				listEval.add(eval);
			}
			
			// group file 保存
			groupFileGenerator.generateWithEvaluations("multiple sqls are executed.", listEval);			
		} finally {
			DatabaseUtil.close(session);
		}
	}

	List<SimulGraphFilename> loadFilenames() throws Exception {
		List<SimulGraphFilename> results = new ArrayList<>();
		
		String dir = prop.getString("dir_simul_selected");
		File[] files = FileUtil.listFilesByExtension(dir, "png");
		
		for (File file : files) {
			String fileName = file.getName();
			if (fileName.startsWith("#")) {
				continue;
			}
			
			results.add(new SimulGraphFilename(fileName.replace(".png", "")));
		}
		
		return results;
	}
	
	@Getter
	private class SimulGraphFilename {
		private String bettype;
		private String kumiban;
		private String patternid;
		private String modelno;
		private String incr;
		
		public SimulGraphFilename(String fileName) {
			// ex) ip_1T_1_jyo+rtype_1442_1_x_x_x_97080_1.01~99_5.4_272486   FPI-5
			String[] token = fileName.split(Delimeter.UNDERBAR.getValue());
			
			this.bettype = token[1];
			this.kumiban = token[2];
			this.patternid = token[3];
			this.modelno = token[4];
			this.incr = token[10];
		}
	}

	public static void main(String[] args) {
		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
		
		try {
			MLPropertyUtil.getInstance().addFile(propertyFilepath);
			MLPropertyUtil prop = MLPropertyUtil.getInstance();
			
			List<Evaluation> evals = new EvaluationSimulLoaderFPI7GraphFilename().getEvaluations();
			System.out.println(evals.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
