package com.pengkong.boatrace.util;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.provider.BeforeOddsProvider;
import com.pengkong.boatrace.exp10.odds.provider.OddsProviderInterface;
import com.pengkong.boatrace.exp10.odds.provider.ResultOddsProvider;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.client.OddsMonitorMapper;
import com.pengkong.boatrace.mybatis.entity.OddsMonitor;
import com.pengkong.boatrace.mybatis.entity.OddsMonitorExample;
import com.pengkong.common.FileUtil;

/**
 * 実行前にRaceFileUploadが終わってないといけない！！ 
 * m 
 * 
 * @author qwerty
 *
 */
public class OddsMonitorFileUploader {
	Logger logger = LoggerFactory.getLogger(OddsMonitorFileUploader.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	String betType;
	String kumiban; 
	
	/** 確定オッズ管理者 */
	// ResultOddsManager oddsMgr = ResultOddsManager.getInstance();
	OddsProviderInterface boddsProvider = new  BeforeOddsProvider();
	OddsProviderInterface roddsProvider = new  ResultOddsProvider();
	
	public OddsMonitorFileUploader(String betType, String kumiban) {
		super();
		this.betType = betType;
		this.kumiban = kumiban;
	}

	public void execute(String fromYmd, String toYmd) throws Exception {
		String resource = "mybatis-config.0.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		
		// open session with auto-transaction mode = false
		try(SqlSession session = sqlSessionFactory.openSession(false)) {
			OddsMonitorMapper mapper = session.getMapper(OddsMonitorMapper.class);
			OddsMonitorExample example = new OddsMonitorExample();
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(fromYmd));
			Date currDate = calendar.getTime();
			Date toDate = sdf.parse(toYmd);
			
			SortedMap<String, OddsMonitorItem> mapItem;
			while (currDate.compareTo(toDate) <= 0) {
				mapItem = new TreeMap<>();
				String yyyyMMdd = sdf.format(currDate);
				
				String filepath = MLPropertyUtil.getInstance().getString("DIRECTORY_ODDSMONITOR") + "odds_" + yyyyMMdd + ".csv";
				//String filepath = BoatProperty.DIRECTORY_ODDS + "odds_" + yyyyMMdd + ".csv";
				File file = new File(filepath);
				if (!file.exists()) {
					// 1日増加
					calendar.add(Calendar.DATE, 1);
					currDate = calendar.getTime();
					continue;
				}
				
				String fname = file.getName();
				filepath = file.getPath();
				List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
				for (String line : lines) {
					// ex) 114706,20210602,06,2,1T,1,1=1.0,2=10.5,3=0.0,4=0.0,5=4.2,6=0.0
					String[] token = line.split(Delimeter.COMMA.getValue());
					if (!isValidToken(token))
						continue;
					
					// オッズモニタ取得
					String key = token[2] + token[3]; // jyoCd + raceNo
					OddsMonitorItem monitorItem = mapItem.get(key);
					if (monitorItem == null) {
						monitorItem = createOddsMonitorItem(yyyyMMdd, token);
						mapItem.put(monitorItem.getKey(), monitorItem);
					}
					
//					OddsListItem oddsListItem = getOddsListItem(token);
//					if (oddsListItem == null) { // 対象組番が未存在ならスキップ
//						logger.warn("skip 対象組番が未存在. " + yyyyMMdd + "_" + token[2] + "_" + token[3]);
//						continue;
//					}
//					
//					monitorItem.oddsList.add(oddsListItem);
				}
				
				// 以前データ削除
				example.clear();
				example.createCriteria().andYmdEqualTo(yyyyMMdd).andBettypeEqualTo(this.betType).andKumibanEqualTo(this.kumiban);
				mapper.deleteByExample(example);
				
				for (OddsMonitorItem item : mapItem.values()) {
					OddsMonitor oddsMonitor = convertOddsMonitor(item);
					// 確定オッズがない場合はskipしないで-1を設定する。
//					if (oddsMonitor == null) {
//						logger.warn("skip 確定オッズが未存在. " + yyyyMMdd + "_" + item.jyoCd + "_" + item.raceNo);
//						continue;
//					}
					mapper.insert(oddsMonitor);
				} 
				
				System.out.println(fname);

				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
			}
			
			session.commit();
		}
	}
	
	/**
	 * DB用DTOを返却する
	 * @param item
	 * @return
	 */
	OddsMonitor convertOddsMonitor(OddsMonitorItem item) throws Exception {
		OddsMonitor result = new OddsMonitor();
		result.setYmd(item.ymd);
		result.setJyocd(item.jyoCd);
		result.setRaceno(item.raceNo);
		result.setBettype(item.betType);
		result.setKumiban(item.kumiban);
		// 20221114 no need to upload. 
		// result.setOddslist(item.getOddsArray());
		// result.setLastindex((short)item.oddsList.size());
		
		Odds bOdds = boddsProvider.get(item.ymd, item.jyoCd, String.valueOf(item.raceNo), item.betType, item.kumiban);
		result.setBork(bOdds.rank);
		result.setBor(bOdds.value);
		
		Odds rOdds = roddsProvider.get(item.ymd, item.jyoCd, String.valueOf(item.raceNo), item.betType, item.kumiban);
		if (rOdds == null) {
			// 確定オッズがない場合はskipしないで-1を設定する。
			//return null;
			result.setRork(-1);
			result.setRor(-1.0);
		} else {
			result.setRork(rOdds.rank);
			result.setRor(rOdds.value);
		}

		return result;
	}
	
	/**
	 * oddsmonitorのCSVがアップロード対象かを判定する
	 * @param token ex) 114706,20210602,06,2,1T,1,1=1.0,2=10.5,3=0.0,4=0.0,5=4.2,6=0.0
	 * @return true=対象 false=対象外
	 */
	boolean isValidToken(String[] token) {
		return (token[4].equals(betType)); 
		
	}
	
	/**
	 * csvトークンからオッズを取得する 
	 * @param token ex) 114706,20210602,06,2,1T,1,1=1.0,2=10.5,3=0.0,4=0.0,5=4.2,6=0.0
	 * @return null=指定組番が存在しない
	 */
	OddsListItem getOddsListItem(String[] token) {
		OddsListItem item = new OddsListItem();
		item.no = Short.parseShort(token[5]);
		
		for (int i = 6; i < token.length; i++) {
			// ex) 2=1.7
			String[] oddsToken = token[i].split(Delimeter.EQUAL.getValue());
			if (oddsToken[0].equals(kumiban)) {
				item.odds =  Double.parseDouble(oddsToken[1]);
				return item;
			}
		}

		// couldnt find kumiban
		return null;
	}
	
	/** csv toke으로부터 옺즈모니터 아이템을 생성한다. */
	OddsMonitorItem createOddsMonitorItem(String ymd, String[] token) {
		OddsMonitorItem item = new OddsMonitorItem();
		item.ymd = ymd;
		item.betType = this.betType;
		item.kumiban = this.kumiban;
		item.jyoCd = token[2];
		item.raceNo = Short.parseShort(token[3]);
		
		return item;
	}
	
	/** オッズモニラックitem */
	class OddsMonitorItem {
		public String ymd;
		public String jyoCd;
		public short raceNo;
		public String betType;
		public String kumiban;
//		public List<OddsListItem> oddsList = new ArrayList<>();
		
		public String getKey() {
			return jyoCd + raceNo;
		}
		
//		public double[] getOddsArray() {
//			double[] odds = new double[oddsList.size()];
//			
//			oddsList.sort(new OddsListItemComparator());
//			for (int i = 0; i < oddsList.size(); i++) {
//				odds[i] = oddsList.get(i).odds;
//			}
//			
//			return odds;
//		}
	}
	
	class OddsListItem {
		public short no;
		public double odds;
	}
	
	class OddsListItemComparator implements Comparator<OddsListItem> {

		@Override
		public int compare(OddsListItem o1, OddsListItem o2) {
			return Short.compare(o1.no, o2.no);
		}
	}
	
	public static void main(String[] args) {
		String fromYmd;
		String toYmd;
		String betType;
		String kumiban;
		
		fromYmd = args[0];
		toYmd = args[1];
		betType = args[2];
		kumiban = args[3];
		
		try {
			MLPropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties");
			new OddsMonitorFileUploader(betType, kumiban).execute(fromYmd, toYmd);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
