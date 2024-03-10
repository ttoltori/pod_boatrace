package com.pengkong.boatrace.exp10.odds.loader;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.OddsHelper;
import com.pengkong.common.FileUtil;
import com.pengkong.common.collection.HashMapList;

public class OddsResultFileLoader extends AbstractOddsFileLoader {
	Logger logger = LoggerFactory.getLogger(OddsMonitorFileLoader.class);

	@Override
	public HashMapList<Odds> load(String filepath, String...betTypes) throws Exception {
		HashMapList<Odds> mapOddsList = new HashMapList<>();
		
		List<String> lines;
		try {
			lines = FileUtil.readFileByLineArr(filepath);
		} catch (FileNotFoundException e) {
			logger.warn("odds result file does not exist. " + filepath);
			return mapOddsList;
		}
		
		String key;
		for (String line : lines) {
			// ex) 20100128,02,1,3T,123=133.2,124=104.6,125=152.4,...
			String[] token = line.split(Delimeter.COMMA.getValue());

			// 対象bettype以外はskip
			if (!Arrays.asList(betTypes).contains(token[3]))	{
				continue;
			}
			
			// key = ymd_jyoCd_raceNo_betType
			key = String.join("_", token[0], token[1], token[2], token[3]);
			mapOddsList.addItemAll(key, getOddsList(token));
		}

		return OddsHelper.applyRanking(mapOddsList, new OddsValueComparator());
	}
	
	/**
	 * csvトークンからオッズリストを取得する 
	 * @param token ex) 114706,20210602,06,2,1T,1,1=1.0,2=10.5,3=0.0,4=0.0,5=4.2,6=0.0
	 */
	List<Odds> getOddsList(String[] token) {
		List<Odds> list = new ArrayList<>();
		for (int i = 4; i < token.length; i++) {
			String[] oddsToken = token[i].split(Delimeter.EQUAL.getValue()); 
			list.add(new Odds(token[0], token[1], token[2], token[3], oddsToken[0], Double.parseDouble(oddsToken[1])));
		}
		
		return list;
	}

	public static void main(String[] args) {
		try {
			OddsResultFileLoader loader = new OddsResultFileLoader();
			HashMapList<Odds> mapList = loader.load("C:/Dev/workspace/Oxygen/pod_boatrace_test/odds/odds_20210625.csv", "1T");
			System.out.println(mapList.size());
			mapList = loader.load("C:/Dev/workspace/Oxygen/pod_boatrace_test/odds/odds_20210625.csv", "1T", "3T");
			System.out.println(mapList.size());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
