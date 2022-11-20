package com.pengkong.boatrace.exp10;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.remote.client.Classification;
import com.pengkong.boatrace.mybatis.client.MlPorkClassificationMapper;
import com.pengkong.boatrace.mybatis.client.OddsPredictMapper;
import com.pengkong.boatrace.mybatis.entity.MlPorkClassification;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.PropertyUtil;

public class MLPorkClassificationGenerator extends MLClassificationGenerator {
	/** classification結果をDBにinsertする */
	@Override
	protected void insertDB(String exNo, SqlSession session, DBRecord dbRec, List<Classification> listClassification) throws Exception {
		OddsPredictMapper mapperRegr = session.getMapper(OddsPredictMapper.class); // odds regression 
		MlPorkClassificationMapper mapper = session.getMapper(MlPorkClassificationMapper.class);
		
		// db insert
		if (isRegression()) {
			mapperRegr.insert(createInsertOddsPredictDto(exNo, dbRec, listClassification));
		} else {
			MlPorkClassification rec = new MlPorkClassification();
			rec.setModelno(exNo);
			rec.setYmd(dbRec.getString("ymd"));
			rec.setJyocd(dbRec.getString("jyocd"));
			rec.setRaceno((short) dbRec.getInt("raceno"));
			rec.setSime(dbRec.getString("sime"));
			rec.setBettype(dbRec.getString("bettype"));
			rec.setBetKumiban(dbRec.getStringForced("bkumiban"));
			rec.setBetOddsrank(dbRec.getInteger("bork"));
			rec.setBetOdds(dbRec.getDouble("bodds"));
			rec.setPattern(dbRec.getString("pattern"));

			rec.setPrediction1(listClassification.get(0).prediction);
			rec.setProbability1(listClassification.get(0).probability);
			rec.setProbabilities1(listClassification.get(0).probabilities);
			rec.setSkewness1(listClassification.get(0).skewness);
			rec.setKurtosis1(listClassification.get(0).kurtosis);
			
			if (listClassification.size() > 1) {
				rec.setPrediction2(listClassification.get(1).prediction);
				rec.setProbability2(listClassification.get(1).probability);
				rec.setProbabilities2(listClassification.get(1).probabilities);
				rec.setSkewness2(listClassification.get(1).skewness);
				rec.setKurtosis2(listClassification.get(1).kurtosis);
			}

			if (listClassification.size() > 2) {
				rec.setPrediction3(listClassification.get(2).prediction);
				rec.setProbability3(listClassification.get(2).probability);
				rec.setProbabilities3(listClassification.get(2).probabilities);
				rec.setSkewness3(listClassification.get(2).skewness);
				rec.setKurtosis3(listClassification.get(2).kurtosis);
			}
			mapper.insert(rec);
		}
	}

	public static void main(String[] args) {
//		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
//		String exNoList = "1";
		String propertyFilepath = args[0];
		String exNoList = args[1];
		try {
			// PropertyUtilを使っている箇所もあるので互換性確保のために
			PropertyUtil.getInstance().addFile(propertyFilepath);
			
			MLPropertyUtil.getInstance().addFile(propertyFilepath);

			MLPorkClassificationGenerator generator = new MLPorkClassificationGenerator();
			generator.execute(exNoList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
