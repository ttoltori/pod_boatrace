package com.pengkong.boatrace.exp10.odds.loader;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.OddsHelper;
import com.pengkong.common.FileUtil;
import com.pengkong.common.collection.HashMapList;

public class OddsMonitorFileLoader extends AbstractOddsFileLoader{
	Logger logger = LoggerFactory.getLogger(OddsMonitorFileLoader.class);

	@Override
	public HashMapList<Odds> load(String filepath, String...betTypes) throws Exception {
		HashMapList<Odds> mapOddsList = new HashMapList<>();
		
		List<String> lines;
		try {
			lines = FileUtil.readFileByLineArr(filepath);
		} catch (FileNotFoundException e) {
			logger.warn("odds monitor file does not exist. " + filepath);
			return mapOddsList;
		}
		
		// key = ymd + jyoCd + raceNo + betType + kumiban
		SortedMap<String, OddsMonitorItem> mapItem = new TreeMap<>();
		for (String line : lines) {
			// ex) 114706,20210602,06,2,1T,1,1=1.0,2=10.5,3=0.0,4=0.0,5=4.2,6=0.0
			String[] token = line.split(Delimeter.COMMA.getValue());
			
			// 対象bettype以外はskip
			if (!Arrays.asList(betTypes).contains(token[4]))	{
				continue;
			}

			List<OddsListItem> oddsListItem = getOddsListItem(token);

			for (OddsListItem item : oddsListItem) {
				// オッズモニタ取得
				// key = ymd + jyoCd + raceNo + betType + kumiban
				String key = token[1] + token[2] + token[3] + token[4] +  item.kumiban; 
				OddsMonitorItem monitorItem = mapItem.get(key);
				if (monitorItem == null) {
					monitorItem = createOddsMonitorItem(token, item.kumiban);
					mapItem.put(key, monitorItem);
				}
				
				monitorItem.oddsList.add(item);
			}
		}

		String key;
		for (OddsMonitorItem item : mapItem.values()) {
			key = String.join("_", item.ymd, item.jyoCd, item.raceNo, item.betType);
			mapOddsList.addItem(key, new Odds(item.ymd, item.jyoCd, item.raceNo, item.betType, item.kumiban,
					item.oddsList.get(item.oddsList.size() - 1).odds));
		}		

		return OddsHelper.applyRanking(mapOddsList, new OddsValueComparator());
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
			// 20230330 たまに発覚されているbeforeoddsファイルのデータ誤りによるプログラム停止を回避
			if (oddsToken.length != 2 || !NumberUtils.isCreatable(oddsToken[1])) {
				continue;
			}
			
			item.kumiban = oddsToken[0];
			item.odds =  Double.parseDouble(oddsToken[1]);
			
			list.add(item);
		}
		
		return list;
	}
	
	/** csv toke으로부터 옺즈모니터 아이템을 생성한다. */
	OddsMonitorItem createOddsMonitorItem(String[] token, String kumiban) {
		OddsMonitorItem item = new OddsMonitorItem();
		item.ymd = token[1];
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
	}
	
	class OddsListItem {
		public String kumiban;
		public short no;
		public double odds;
	}

	public static void main(String[] args) {
		try {
			OddsMonitorFileLoader loader = new OddsMonitorFileLoader();
			HashMapList<Odds> mapList = loader.load("C:/Dev/workspace/Oxygen/pod_boatrace_test/oddsmonitor/http/odds_20210625.csv", "1T");
			System.out.println(mapList.size());
			mapList = loader.load("C:/Dev/workspace/Oxygen/pod_boatrace_test/oddsmonitor/http/odds_20210625.csv", "1T", "3T");
			System.out.println(mapList.size());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
