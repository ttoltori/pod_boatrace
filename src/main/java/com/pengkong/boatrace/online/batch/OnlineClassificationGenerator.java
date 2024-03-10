package com.pengkong.boatrace.online.batch;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.exp10.MLClassificationGenerator;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.remote.client.Classification;
import com.pengkong.boatrace.mybatis.client.OlClassificationMapper;
import com.pengkong.boatrace.mybatis.entity.OlClassification;
import com.pengkong.boatrace.mybatis.entity.OlClassificationExample;
import com.pengkong.boatrace.online.dao.OlClassificationDAO;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.PropertyUtil;

/**
 * OlClassificationGeneratorを継承して当日のonline classificationを生成する。
 * features_online.tsv, featureset.tsv, sqltemplate.tsv, model_config.tsvが必要
 * @author ttolt
 *
 */
public class OnlineClassificationGenerator extends MLClassificationGenerator {
	String yyyyMMdd;
	
	public OnlineClassificationGenerator(String yyyyMMdd) {
		super();
		this.yyyyMMdd = yyyyMMdd;
	}
	
	@Override
	protected String[] getModelPeriod() {
		return new String[] {yyyyMMdd, yyyyMMdd};
	}
	
	/** 既存データ削除 */
	@Override
	protected void deleteDB(SqlSession session, String modelNo, String ymd) throws Exception {
		OlClassificationMapper mapper = session.getMapper(OlClassificationMapper.class);
		OlClassificationExample exam = new OlClassificationExample();
		exam.createCriteria().andModelnoEqualTo(modelNo).andYmdEqualTo(ymd);
		mapper.deleteByExample(exam);
	}
	
	@Override
	protected void insertDB(String exNo, SqlSession session, DBRecord dbRec, List<Classification> listClassification)
			throws Exception {
		OlClassificationDAO dao = new OlClassificationDAO(session);
		dao.insert(createInsertDto(exNo, dbRec, listClassification));
	}
	
	/** DB insert用DTOへ変換する*/
	protected OlClassification createInsertDto(String exNo, DBRecord dbRec, List<Classification> listClassification) throws Exception {
		OlClassification rec = new OlClassification();
		rec.setModelno(exNo);
		rec.setYmd(dbRec.getString("ymd"));
		rec.setJyocd(dbRec.getString("jyocd"));
		rec.setRaceno(dbRec.getInt("raceno"));
		rec.setSime(dbRec.getString("sime"));

		rec.setPrediction1(listClassification.get(0).prediction);
		rec.setProbability1(listClassification.get(0).probability);
		
		if (listClassification.size() > 1) {
			rec.setPrediction2(listClassification.get(1).prediction);
			rec.setProbability2(listClassification.get(1).probability);
		}

		if (listClassification.size() > 2) {
			rec.setPrediction3(listClassification.get(2).prediction);
			rec.setProbability3(listClassification.get(2).probability);
		}

		return rec;
	}
	
	public static void main(String[] args) {
//		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
//		String exNoList = "1";
		String ymd = args[0];
		String propertyFilepath = args[1];
		try {
			// PropertyUtilを使っている箇所もあるので互換性確保のために
			PropertyUtil.getInstance().addFile(propertyFilepath);
			MLPropertyUtil.getInstance().addFile(propertyFilepath);

			OnlineClassificationGenerator generator = new OnlineClassificationGenerator(ymd);
			generator.execute(MLPropertyUtil.getInstance().getString("models"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
