package com.pengkong.boatrace.service.manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.entity.OddsMonitor;
import com.pengkong.common.FileUtil;

@Deprecated
public class OddsMonitorFileLoader {
	Logger logger = LoggerFactory.getLogger(OddsMonitorFileLoader.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	public Map<String, OddsMonitor> load(String fileDir, String ymd, String betTypes) throws Exception {
		Map<String, OddsMonitor> mapOddsMonitor = new HashMap<>();

		String filepath = fileDir + "odds_" + ymd + ".csv";
		if (!FileUtil.isExist(filepath)) {
			logger.warn("odds monitor file does not exist. " + ymd);
			return mapOddsMonitor;
		}

		List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
		// 例）1T,3T,2T
		betTypes = betTypes.replace("3M", "3T");
		betTypes = betTypes.replace("2M", "2T");
		betTypes = betTypes.replace("3N", "2T");
		betTypes = betTypes.replace("2N", "3T");
		
		
		mapOddsMonitor = new HashMap<>();
		// key = jyoCd + raceNo + betType + kumiban
		SortedMap<String, OddsMonitorItem> mapItem = new TreeMap<>();
		
		for (String line : lines) {
			// ex) 114706,20210602,06,2,1T,1,1=1.0,2=10.5,3=0.0,4=0.0,5=4.2,6=0.0
			String[] token = line.split(Delimeter.COMMA.getValue());
			
			// 対象bettype以外はskip
			if (!betTypes.contains(token[4])) {
				continue;
			}
			
			List<OddsListItem> oddsListItem = getOddsListItem(token);

			for (OddsListItem item : oddsListItem) {
				// オッズモニタ取得
				// key = jyoCd + raceNo + betType + kumiban
				String key = token[2] + token[3] + token[4] +  item.kumiban; 
				OddsMonitorItem monitorItem = mapItem.get(key);
				if (monitorItem == null) {
					monitorItem = createOddsMonitorItem(ymd, token, item.kumiban);
					mapItem.put(key, monitorItem);
				}
				
				monitorItem.oddsList.add(item);
			}
		}
		
		for (OddsMonitorItem item : mapItem.values()) {
			OddsMonitor oddsMonitor = convertOddsMonitor(item);
			
			// key = ymd + jyoCd + raceNo + betType + kumiban
			String key = String.join("_", item.ymd, item.jyoCd, item.raceNo, item.betType, item.kumiban);
			mapOddsMonitor.put(key, oddsMonitor);
		}
		
		
		
		return mapOddsMonitor;
	}

	/**
	 * DB用DTOを返却する
	 * @param item
	 * @return
	 */
	OddsMonitor convertOddsMonitor(OddsMonitorItem item) {
		OddsMonitor result = new OddsMonitor();
		result.setYmd(item.ymd);
		result.setJyocd(item.jyoCd);
		result.setRaceno(Short.valueOf(item.raceNo));
		result.setBettype(item.betType);
		result.setKumiban(item.kumiban);
		result.setOddslist(null);
		//result.setOddslist(item.getOddsArray());
		result.setLastindex((short)item.oddsList.size());
		result.setBeforeodds(item.oddsList.get(item.oddsList.size()-1).odds);
		
		return result;
	}
	
	/**
	 * csvトークンからオッズを取得する 
	 * @param token ex) 114706,20210602,06,2,1T,1,1=1.0,2=10.5,3=0.0,4=0.0,5=4.2,6=0.0
	 * @return null=指定組番が存在しない
	 */
	List<OddsListItem> getOddsListItem(String[] token) {
		List<OddsListItem> list = new ArrayList<>();
		OddsListItem item;
		for (int i = 6; i < token.length; i++) {
			item = new OddsListItem();
			item.no = Short.parseShort(token[5]);
			
			// ex) 2=1.7
			String[] oddsToken = token[i].split(Delimeter.EQUAL.getValue());
			item.kumiban = oddsToken[0];
			item.odds =  Double.parseDouble(oddsToken[1]);
			
			list.add(item);
		}
		
		return list;
	}

	/** csv toke으로부터 옺즈모니터 아이템을 생성한다. */
	OddsMonitorItem createOddsMonitorItem(String ymd, String[] token, String kumiban) {
		OddsMonitorItem item = new OddsMonitorItem();
		item.ymd = ymd;
		item.jyoCd = token[2];
		item.raceNo = token[3];
		item.betType = token[4];
		item.kumiban = kumiban;
		
		return item;
	}

	/** オッズモニラックitem */
	class OddsMonitorItem {
		public String ymd;
		public String jyoCd;
		public String raceNo;
		public String betType;
		public String kumiban;
		public List<OddsListItem> oddsList = new ArrayList<>();
		
		public String getKey() {
			return jyoCd + raceNo;
		}
		
		public double[] getOddsArray() {
			double[] odds = new double[oddsList.size()];
			
			oddsList.sort(new OddsListItemComparator());
			for (int i = 0; i < oddsList.size(); i++) {
				odds[i] = oddsList.get(i).odds;
			}
			
			return odds;
		}
	}
	
	class OddsListItem {
		public String kumiban;
		public short no;
		public double odds;
	}
	
	class OddsListItemComparator implements Comparator<OddsListItem> {

		@Override
		public int compare(OddsListItem o1, OddsListItem o2) {
			return Short.compare(o1.no, o2.no);
		}
	}	
}
