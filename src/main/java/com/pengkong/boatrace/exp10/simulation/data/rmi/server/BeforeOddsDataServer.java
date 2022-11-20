package com.pengkong.boatrace.exp10.simulation.data.rmi.server;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.entity.OddsMonitor;
import com.pengkong.boatrace.service.manager.OddsMonitorFileLoader;
import com.pengkong.boatrace.util.BoatUtil;

@Deprecated
public class BeforeOddsDataServer {
	Logger logger = LoggerFactory.getLogger(BeforeOddsDataServer.class);
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	/** 日付単位のオッズモニタマップを保持する */
	/** key=ymd, value={ key=ymd_jyocd_raceno_bettype_kumiban  value=OddsMonitor } */
	Map<String, Map<String, OddsMonitor>> mapOddsMonitor; 
	
	OddsMonitorFileLoader fileLoader = new OddsMonitorFileLoader();
	
	void load() throws Exception {
		mapOddsMonitor = new HashMap<String, Map<String,OddsMonitor>>();
		
		String fileDir = prop.getString("DIRECTORY_ODDSMONITOR");
		String betTypes = prop.getString("dataserver_odds_bettypes");
		String[] period = prop.getString("dataserver_odds_period").split(Delimeter.WAVE.getValue());
		
		String ymd = period[0];
		int days = BoatUtil.daysBetween(period[0], period[1]) + 1;
		for (int i = 0; i < days; i++) {
			Map<String, OddsMonitor> daily = fileLoader.load(fileDir, ymd, betTypes);
			if (daily.size() > 0) {
				mapOddsMonitor.put(ymd, fileLoader.load(fileDir, ymd, betTypes));
				logger.debug("odds monitor loaded. " + ymd);
			} else {
				logger.warn("odds monitor data does not exist. " + ymd);
			}

			ymd = BoatUtil.daysAfterYmd(ymd, 1);
		}

		logger.info("odds monitor data loaded from files. " + period[0] + "~" + period[1] + ", " + betTypes);
	}
	
	Map<String, OddsMonitor> getDaily(String ymd) {
		return mapOddsMonitor.get(ymd);
	}
	
	public static void main(String[] args) {
		try {
			MLPropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties");
			BeforeOddsDataServer server = new BeforeOddsDataServer();
			server.load();
			Map<String, OddsMonitor> result = server.getDaily("20210602");
			
			System.out.println(result.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
