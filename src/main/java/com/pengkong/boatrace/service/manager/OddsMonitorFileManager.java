package com.pengkong.boatrace.service.manager;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.pengkong.boatrace.mybatis.entity.OddsMonitor;
import com.pengkong.common.MathUtil;

@Deprecated
public class OddsMonitorFileManager extends AbstractOddsMonitorManager {
	/** odds monitor file loader */
	OddsMonitorFileLoader oddsMonitorFileLoader = new OddsMonitorFileLoader();
	
	private static OddsMonitorFileManager ISNTANCE = new OddsMonitorFileManager();
	public static OddsMonitorFileManager getInstance() {
		return ISNTANCE;
	}
	
	/** odds monitorのlinear regressionを取得する
	 * memory節約のためにoddslistを捨てるため、このメソッドは使えない。 
	 * */
	@Deprecated
	public Double getPredictedOdds(String ymd, String jyoCd, String raceNo, String betType, String kumiban) throws Exception {
		if (!isInitializeOK(ymd)) {
			return null;
		}
		
		String key = String.join("_", ymd, jyoCd, raceNo, betType, kumiban);
		OddsMonitor om = mapOddsMonitor.get(key);
		if (om == null) {
			return null;
		}
		Double[] darray = ArrayUtils.toObject((double[])om.getOddslist());
		double[] regResult = MathUtil.getRegression( Arrays.asList(darray));
		
		// y = ax + b 
		return (regResult[0] * (om.getLastindex() + 1)) + regResult[1];
	}

	@Override
	Map<String, OddsMonitor> load(String ymd) throws Exception {
		String fileDir = prop.getString("DIRECTORY_ODDSMONITOR");
		String betTypes = prop.getString("bettype");
		
		return oddsMonitorFileLoader.load(fileDir, ymd, betTypes);
	}
}
