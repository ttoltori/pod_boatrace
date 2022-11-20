package com.pengkong.boatrace.exp10;

import java.lang.reflect.Constructor;
import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.result.AbstractResultCreator;
import com.pengkong.boatrace.exp10.simulation.AbstractSimulationCreator;
import com.pengkong.boatrace.exp10.simulation.data.SimulationDBRaceDataLoader;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.StringUtil;

public class MLSimulationGenerator extends MLResultGenerator {
	/** simulation結果生成クラス */
	AbstractSimulationCreator simulationCreator;
	
	/** 実行前処理 
	 * Creatorクラス動的生成処理 */
	void preProcess() throws Exception {
		// 시뮬레이션생성모듈 reflection
		// 생성자 아규먼트는 없음.
		String className = staTpl.getStrategy(prop.getString("simul_class_id")).javaClass;
		String clazz = "com.pengkong.boatrace.exp10.simulation." + className; 
		Constructor<?> c = Class.forName(clazz).getConstructor();
		simulationCreator = (AbstractSimulationCreator) c.newInstance();
	}

	public MLSimulationGenerator() {
		super();
	}

	@Override
	protected void setupConfigProperty() {
		if (prop.getString("simulation_step").equals("1")) {
			configProperty = "file_simul_config_step1";
			graphBuilder.dirProperty = "dir_simul_output_step1";
		} else {
			configProperty = "file_simul_config_step2";	
			graphBuilder.dirProperty = "dir_simul_output_step2";
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

		String[] termToken = prop.getString("term").split(Delimeter.COMMA.getValue());
		String[] betTypeToken = prop.getString("bettype").split(Delimeter.COMMA.getValue());
		String[] factorToken = prop.getString("factor").split(Delimeter.COMMA.getValue());
        String[] incrToken = prop.getString("incr").split(Delimeter.COMMA.getValue());
        String[] limitToken = prop.getString("limit").split(Delimeter.COMMA.getValue());
        String[] gradeTypeToken = prop.getString("grade_type").split(Delimeter.COMMA.getValue());
        for (String term : termToken) {
            for (String betType : betTypeToken) {
                for (String factor : factorToken) {
                    for (String incr : incrToken) {
                        for (String limit : limitToken) {
                            for (String gradeType : gradeTypeToken) {
                            	prop.putProperty("term", term);
                                prop.putProperty("bettype", betType);
                                prop.putProperty("factor", factor);
                                prop.putProperty("incr", incr);
                                prop.putProperty("limit", limit);
                                prop.putProperty("grade_type", gradeType);
                                
                                preProcess();
                                String evaluationsId = String.join(Delimeter.UNDERBAR.getValue(), 
                                		exNo, 
                                		betType, 
                                		prop.getString("kumiban"),
                                		factor, 
                                		limit, 
                                		incr, 
                                		prop.getString("group_sql_id", "x"),
                                		term,
                                		prop.getString("result_type"), 
                                		prop.getString("models"), 
                                		gradeType, 
                                		prop.getString("probability_type"),
                                		prop.getString("bonus_pr"), prop.getString("bonus_bor"), prop.getString("bonus_bork")
                                		);
                                prop.putProperty("evaluations_id", evaluationsId);
                                
                                try {
                                    executeSuper(exNo);
                                	
                                    // evalustion内訳を出力
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
		//return DataServerClinet.getInstance().getSimulationData(fromYmd, toYmd);
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

			MLSimulationGenerator generator = new MLSimulationGenerator();
			generator.execute(listExno);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
