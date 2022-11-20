package com.pengkong.boatrace.online.classifier;

import java.util.List;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.classifier.MultiModelDBClassifierCache;
import com.pengkong.boatrace.exp10.simulation.data.AbstractMultiClassificationDataLoader;
import com.pengkong.boatrace.exp10.simulation.data.DBMultiClassificationDataLoader;
import com.pengkong.boatrace.util.BoatUtil;

public class DailyMultiModelDBClassifierCache extends MultiModelDBClassifierCache {
	public DailyMultiModelDBClassifierCache(List<String> modelNoList) {
		super(modelNoList);
	}
	
	@Override
	protected AbstractMultiClassificationDataLoader createLoader() {
		// return new RmiMultiClassificationDataProvider();
		return new DBMultiClassificationDataLoader(MLPropertyUtil.getInstance().getString("target_db_resource"));
	}
	
	@Override
	protected String[] getFromToYmd() {
		// キャッシュ範囲を当日だけに限定する。
		String ymd = BoatUtil.currentYmd(); 
		return new String[] {ymd, ymd};
	}
}
