package com.pengkong.boatrace.exp10;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.result.AbstractResultCreator;
import com.pengkong.boatrace.exp10.simulation.AbstractSimulationCreator;
import com.pengkong.boatrace.exp10.simulation.data.SimulationDBRaceDataLoader;
import com.pengkong.boatrace.exp10.simulation.step2.Step2PropertyLoaderFSB;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.StringUtil;

public class MLSimulationGeneratorFSBStep2 extends MLResultGenerator {
	/** simulation結果生成クラス */
	AbstractSimulationCreator simulationCreator;

	/**
	 * 実行前処理 Creatorクラス動的生成処理
	 */
	void preProcess() throws Exception {
		// 시뮬레이션생성모듈 reflection
		// 생성자 아규먼트는 없음.
		String className = staTpl.getStrategy(prop.getString("simul_class_id")).javaClass;
		String clazz = "com.pengkong.boatrace.exp10.simulation." + className;
		Constructor<?> c = Class.forName(clazz).getConstructor();
		simulationCreator = (AbstractSimulationCreator) c.newInstance();
	}

	public MLSimulationGeneratorFSBStep2() {
		super();
	}

	@Override
	protected void setupConfigProperty() {
		if (prop.getString("simulation_step").equals("1")) {
			configProperty = "file_simul_config_step1";
			graphBuilder.dirProperty = "dir_simul_output_step1";
			graphBuilderSplit.dirProperty = "dir_simul_output_step1";
		} else {
			configProperty = "file_simul_config_step2";
			graphBuilder.dirProperty = "dir_simul_output_step2";
			graphBuilderSplit.dirProperty = "dir_simul_output_step2";
		}
	}

	@Override
	public void execute(List<String> exNoList) throws Exception {
		super.execute(exNoList);
	}

	/**
	 * simulation前処理を行ってからMLResultGeneratorを呼び出す。
	 */
	@Override
	void executeExperiment(String exNo) throws Exception {
		List<String> ids = new ArrayList<>();
		
		Step2PropertyLoaderFSB st2 = new Step2PropertyLoaderFSB();
		String[] rankingToken = prop.getString("ranking").split(Delimeter.COMMA.getValue());
		String[] simulBonusTermToken = prop.getString("simul_bonus_term").split(Delimeter.COMMA.getValue());
		// step 2 loop
		for (String ranking : rankingToken) {
			prop.putProperty("ranking", ranking);
			try {
				// 当該rankのFSB-SIM2-1結果をロードしてpropを再設定する
				st2.setStep2CommonProperties();
			} catch (WarningException e) {
				logger.warn("no simulation step2 data. ranking=" + ranking);
				continue;
			}
			
			for (String simulBonusTerm : simulBonusTermToken) {
				prop.putProperty("simul_bonus_term", simulBonusTerm);

				// step 1 loop
				String[] termToken = prop.getString("simul_term").split(Delimeter.COMMA.getValue());
				String[] incrToken = prop.getString("simul_incr").split(Delimeter.COMMA.getValue());
				String[] limitToken = prop.getString("simul_limit").split(Delimeter.COMMA.getValue());
				String[] gradeTypeToken = prop.getString("simul_grade_type").split(Delimeter.COMMA.getValue());
				for (String incr : incrToken) {
					for (String limit : limitToken) {
						for (String gradeType : gradeTypeToken) {
							for (String term : termToken) {
								prop.putProperty("term", term);
								prop.putProperty("incr", incr);
								prop.putProperty("limit", limit);
								prop.putProperty("grade_type", gradeType);

								String custom = prop.getString("simul_custom");
								if (custom.equals("all")) {
									prop.putProperty("custom", "true");
								} else if (custom.equals("wkall")) {
									// prop.putProperty("custom", "sb.patternid like '%wk%'");
									prop.putProperty("custom", "true");
								} else if (custom.equals("wk")) {
									prop.putProperty("custom", "sb.patternid in ('wk1','wk12','wk123','wk1234')");
								}
								String resultEvalId = prop.getString("result_eval_id");
								if (resultEvalId.startsWith("7")) {
									prop.putProperty("modelno", "79100");
								} else {
									prop.putProperty("modelno", "99100");
								}
								
								// 当該step1のbonus_bork,bonus_borを再設定する
								st2.setStep2BonusProperties();

								preProcess();
								String evaluationsId = String.join(Delimeter.UNDERBAR.getValue(),
									// exNo,
									prop.getString("grade_type"), 
									prop.getString("bettype"),
									prop.getString("kumiban"), 
									ranking,
									incr, 
									limit,
									simulBonusTerm,
									//prop.getString("bonus_bork"),
									//prop.getString("bonus_bor"),
									prop.getString("group_sql_id"), 
									prop.getString("factor"), 
									custom, 
									resultEvalId, 
									exNo,
									term 
								);
								prop.putProperty("evaluations_id", evaluationsId);

								try {
									ids.add(evaluationsId);
									System.out.println(evaluationsId);
									
									executeSuper(exNo);
									simulationCreator.getEvLoader().printConsole();

								} catch (WarningException e) {
									logger.warn(evaluationsId + " Warining " + e.getMessage());
								} catch (Exception e) {
									logger.error(evaluationsId + " Exception " + e.getMessage(), e);
									throw e;
								}
								afterProcess();

							}
						}
					}
				}
			}
		}
		
//		Collections.sort(ids);
//		for (String id : ids) {
//			System.out.println(id);
//		}
	
	}

	void executeSuper(String exNo) throws Exception {
		super.executeExperiment(exNo);

	}

	/** 実行後処理 */
	void afterProcess() throws Exception {
		simulationCreator.destroy();
		simulationCreator = null;
	}

	/** DBデータ取得 */
	@Override
	List<DBRecord> loadDB(String fromYmd, String toYmd) throws Exception {
		// return DataServerClinet.getInstance().getSimulationData(fromYmd, toYmd);
		return new SimulationDBRaceDataLoader().load(fromYmd, toYmd);
	}

	/**
	 * result生成処理をAbstractSimulationCreatorに委任する。
	 */
	@Override
	List<MlResult> createResult(DBRecord rec, AbstractResultCreator resultCreator) throws Exception {
		simulationCreator.setResultCreator(super.resultCreator);
		List<MlResult> result = simulationCreator.execute(rec);

		return result;
	}

	public static void main(String[] args) {
//		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
//		String exNoList = "1";
		String propertyFilepath = args[0];
		String exNoList = args[1];

		List<String> listExno = StringUtil.parseNumericOptionsString(exNoList);

		try {
			// PropertyUtilを使っている箇所もあるので互換性確保のために
			PropertyUtil.getInstance().addFile(propertyFilepath);
			MLPropertyUtil.getInstance().addFile(propertyFilepath);

			MLSimulationGeneratorFSBStep2 generator = new MLSimulationGeneratorFSBStep2();
			generator.execute(listExno);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
