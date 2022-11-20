package com.pengkong.boatrace.exp10.simulation.classifier;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.data.AbstractMultiClassificationDataLoader;
import com.pengkong.boatrace.exp10.simulation.data.rmi.client.RmiMultiClassificationDataProvider;
import com.pengkong.boatrace.mybatis.entity.MlClassification;
import com.pengkong.common.collection.HashMapList;

public class MultiModelDBClassifierCache {
	Logger logger = LoggerFactory.getLogger(MultiModelDBClassifierCache.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/** key=ymd_jyocd_raceno value=MlClassification */
	HashMapList<MlClassification> mapClassification;
	
	/** ロード対象のモデル番号リスト */
	List<String> modelNoList;
	
	/** classification data loader */
	AbstractMultiClassificationDataLoader loader;
	
	public MultiModelDBClassifierCache(List<String> modelNoList) {
		super();
		this.modelNoList = modelNoList;
		loader =  createLoader();
	}

	protected AbstractMultiClassificationDataLoader createLoader() {
		//return new DBMultiClassificationDataLoader(MLPropertyUtil.getInstance().getString("target_db_resource"));
		return new RmiMultiClassificationDataProvider();
	}
	
	/** キャッシュする期間を取得する */
	protected String[] getFromToYmd() {
		return new String[] {
			prop.getString("result_start_ymd"), prop.getString("result_end_ymd") 
		};
	}
	
	/**
	 * classificationをDBのml_classificationから取得する。 */
	public List<MlClassification> classify(String ymd, String jyoCd, String raceNo) throws Exception {
		// 初期化処理 
		if (mapClassification == null) {
			String[] fromTo = getFromToYmd();
			loadDB(fromTo[0], fromTo[1]);
		}

		return mapClassification.get(createKey(ymd, jyoCd, raceNo));
	}

	/**
	 * ml_classificationを全て取得する。条件：ymd, modelno_list
	 * @param ymd
	 */
	void loadDB(String fromYmd, String toYmd) throws Exception {
		mapClassification = new HashMapList<>();
		
		List<MlClassification> listRec = loader.load(modelNoList, fromYmd, toYmd);
		
		// mapに保持
		for (MlClassification rec : listRec) {
			mapClassification.addItem(createKey(rec.getYmd(), rec.getJyocd(), rec.getRaceno().toString()), rec);
		}
	}
	
	String createKey(String ymd, String jyoCd, String raceNo) {
		return String.join("_", ymd, jyoCd, raceNo);
	}
}
