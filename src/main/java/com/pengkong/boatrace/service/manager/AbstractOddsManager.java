package com.pengkong.boatrace.service.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.model.OddsFItem;
import com.pengkong.boatrace.model.OddsItemEx;
import com.pengkong.boatrace.model.OddsTItem;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.comparator.OddsFItemValueComparator;
import com.pengkong.boatrace.model.comparator.OddsTItemValueComparator;
import com.pengkong.boatrace.model.json.OddsJson;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.collection.HashMapList;

import lombok.AllArgsConstructor;
import lombok.Data;

@Deprecated
public abstract class AbstractOddsManager {
	Logger logger = LoggerFactory.getLogger(AbstractOddsManager.class);
	PropertyUtil prop = PropertyUtil.getInstance();

	/** 組番別オッズ- 複勝(1F) or 拡連複(KF)以外 */
	private HashMap<String, Float> oddsValueMapT;

	/** 組番別のオッズ - 複勝(1F) or 拡連複(KF) */
	private HashMap<String, Float[]> oddsValueMapF;

	/** オッズ種類別のmaplist - 複勝(1F) or 拡連複(KF)以外 */
	private HashMapList<OddsTItem> oddsMapListT;

	/** オッズ種類別のmaplist - 複勝(1F) or 拡連複(KF) */
	private HashMapList<OddsFItem> oddsMapListF;

	private OddsTItemValueComparator oddsTComparator = new OddsTItemValueComparator();
	
	private OddsFItemValueComparator oddsFComparator = new OddsFItemValueComparator();
	
	private HashMap<String, JyoRace> mapJyoRace = new HashMap<>();
	
	public void initialize() {
		oddsValueMapT = new HashMap<>();
		oddsValueMapF = new HashMap<>();
		oddsMapListT = new HashMapList<>();
		oddsMapListF = new HashMapList<>();
		mapJyoRace = new HashMap<>();
	}

	abstract protected String getOddsFilepath(String ymd);

	/** @return true = 当該日付のオッズファイルが存在する */
	public boolean isOddsFileExist(String ymd) {
		return FileUtil.isExist(getOddsFilepath(ymd));
	}
	
	public void load(String ymd) throws Exception {
		initialize();

		String filepath = getOddsFilepath(ymd);
		if (!new File(filepath).exists()) {
			return ;
		}
		
		List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
		loadLines(lines);
		//logger.info("OddsManagger loading is OK. file = " + filepath);
	}

	public void loadLines(List<String> lines) throws Exception {
		for (String line : lines) {
			loadLine(line);
		}
	}
	
	public void loadOddsJsonList(RaceEx race, List<OddsJson> oddsJsonList, String type) throws Exception {
		// keyPrefix= 場コード + レースNO + type
		// key= 場コード + レースNO + type + 組番
		String keyPrefix = String.join("", race.setu.jyoCd, String.valueOf(race.raceInfo.no), type);
		for (OddsJson oj : oddsJsonList) {
			String kumiban = oj.getKumiban();
			String key = keyPrefix + kumiban;
			if (oj.oddsValue <= 0f) {
				continue;
			}
			oddsValueMapT.put(key, oj.oddsValue);
			oddsMapListT.addItem(keyPrefix, new OddsTItem(kumiban, oj.oddsValue));
		}
		
		if (oddsMapListT.get(keyPrefix) != null) {
			oddsMapListT.get(keyPrefix).sort(oddsTComparator);	
		}
	}
	
	void loadLine(String line) throws Exception {
		String[] tokenCsv = line.split(BoatTemplate.CSV_DELIMITER);
		String type = tokenCsv[3];
		String jyoCd = tokenCsv[1];
		String raceNo =  tokenCsv[2];
		
		if (!mapJyoRace.containsKey(jyoCd + raceNo)) {
			mapJyoRace.put(jyoCd + raceNo, new JyoRace(jyoCd, raceNo));
		}
		
		// keyPrefix= 場コード + レースNO + type
		// key= 場コード + レースNO + type + 組番
		String keyPrefix = String.join("", jyoCd, raceNo, type);
		for (int i = 4; i < tokenCsv.length; i++) {
			// token2 : key=value
			String[] tokenKeyValue = tokenCsv[i].split("=");
			String key = keyPrefix + tokenKeyValue[0];
			if (type.equals("1F") || type.equals("KF")) {
				String[] tokenKeyValue1F = tokenKeyValue[1].split("-");
				Float[] values = new Float[2];
				values[0] = Float.parseFloat(tokenKeyValue1F[0]);
				values[1] = Float.parseFloat(tokenKeyValue1F[1]);
				// odds=0は除外する
				// 20191130 odds가 0이라는 것은 인기가 아예 없다는 것이므로
				// 아주 높은 옺즈를 대입주어야 한다. 0을 주면 인기가 최고로 높은 것으로 통계에 반영되어버린다.
				if (values[0] <= 0f || values[1] <= 0f) {
					// 20191130 continue;
					values[0] = values[1] = 999f;
				}
				oddsValueMapF.put(key, values);
				oddsMapListF.addItem(keyPrefix, new OddsFItem(tokenKeyValue[0], values));
			} else {
				// 複勝・拡連複以外
				Float value = Float.parseFloat(tokenKeyValue[1]);
				// odds=0は除外する
				// 20191130 odds가 0이라는 것은 인기가 아예 없다는 것이므로
				// 아주 높은 옺즈를 대입주어야 한다. 0을 주면 인기가 최고로 높은 것으로 통계에 반영되어버린다.
				if (value <= 0f) {
					// 20191130 continue;
					value = 999f;
				}
				oddsValueMapT.put(key, value);
				oddsMapListT.addItem(keyPrefix, new OddsTItem(tokenKeyValue[0], value));
			}
		}
		
		try {
			// sort 
			if (keyPrefix.contains("1F") || keyPrefix.contains("KF")) {
				if (oddsMapListF.get(keyPrefix) != null) {
					oddsMapListF.get(keyPrefix).sort(oddsFComparator);	
				}
			} else {
				if (oddsMapListT.get(keyPrefix) != null) {
					oddsMapListT.get(keyPrefix).sort(oddsTComparator);	
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public OddsItemEx getOddsItemEx(String jyoCd, String raceNo, String type, String kumiban) {
		ArrayList<OddsTItem> list = oddsMapListT.get(jyoCd + raceNo + type);
		if (list == null) {
			return null;
		}
		
		int rank = 1;
		for (OddsTItem item : list) {
			if (item.kumiban.equals(kumiban)) {
				return new OddsItemEx(kumiban, item.value, rank);
			}
			rank++;
		}
		
		return null;
	}

	public OddsTItem getRankedOddsItem(String jyoCd, String raceNo, String type, int rank) {
			ArrayList<OddsTItem> list = oddsMapListT.get(jyoCd + raceNo + type);
			if (list == null || list.size() < rank) {
				return null;
			}
			return list.get(rank-1); 
	}
	
	public List<OddsTItem> getRankedOddsItemList(String jyoCd, String raceNo, String type, int ...ranks) {
		ArrayList<OddsTItem> list = oddsMapListT.get(jyoCd + raceNo + type);
		if (list == null) {
			return null;
		}
		for (int rank : ranks) {
			if (list.size() < rank) {
				return null;
			}
		}
		
		ArrayList<OddsTItem> result = new ArrayList<>();
		for (int rank : ranks) {
			result.add(list.get(rank-1)); 
		}
		
		return result;
	}
	
	public int getPopular(String jyoCd, String raceNo, String type, String kumiban) {
		ArrayList<OddsTItem> list = oddsMapListT.get(jyoCd + raceNo + type);
		if (list == null) {
			return -1;
		}
		
		int rank = 1;
		for (OddsTItem item : list) {
			if (item.kumiban.equals(kumiban)) {
				return rank;
			}
			rank++;
		}
		
		return -1;
	}
	
	public Float getOddsValue(String jyoCd, String raceNo, String type, String kumiban) {
		return oddsValueMapT.get(jyoCd + raceNo + type + kumiban);
	}
	
	public OddsTItem getOddsItem(String jyoCd, String raceNo, String type, String kumiban) {
		Float value =  oddsValueMapT.get(jyoCd + raceNo + type + kumiban);
		if (value == null) {
			return null;
		}
		
		OddsTItem item = new OddsTItem(kumiban, value);
		
		return item;
	}

	public Float[] getOddsValue1FKF(String jyoCd, String raceNo, String type, String kumiban) {
		return oddsValueMapF.get(jyoCd + raceNo + type + kumiban);
	}

	public List<OddsTItem> getOddsItemList(String jyoCd, String raceNo, String type) {
		return oddsMapListT.get(jyoCd + raceNo + type);
	}
	
	public List<OddsTItem> getOddsItemList(String jyoCd, String raceNo, String type, String[] kumibanList, boolean sortByOdds) {
		List<OddsTItem> listOdds = new ArrayList<>();
		for (int i = 0; i < kumibanList.length; i++) {
			OddsTItem oddsItem = getOddsItem(jyoCd, raceNo, type, kumibanList[i]);
			if (oddsItem != null) {
				listOdds.add(oddsItem);
			}
		}
		
		if (sortByOdds) {
			listOdds.sort(oddsTComparator);
		}
		
		return listOdds;
	}

	public float[] getOddsValueArray(String jyoCd, String raceNo, String type) {
		List<OddsTItem> list = getOddsItemList(jyoCd, raceNo, type);
		float[] arr = new float[list.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = list.get(i).value;
		}
		
		return arr;
	}
	
	public List<OddsFItem> getOddsItemList1FKF(String jyoCd, String raceNo, String type) {
		return oddsMapListF.get(jyoCd + raceNo + type);
	}
	
	public List<JyoRace> getJyoRaceList() {
		return new ArrayList<>(mapJyoRace.values());
	}
	
	@AllArgsConstructor
	@Data
	public class JyoRace {
		String jyoCd;
		String raceNo;
	}
}
