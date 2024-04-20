package com.pengkong.boatrace.util;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.client.RecBoddsMapper;
import com.pengkong.boatrace.mybatis.entity.RecBodds;
import com.pengkong.boatrace.mybatis.entity.RecBoddsExample;
import com.pengkong.common.FileUtil;

/**
 * 実行前にRaceFileUploadが終わってないといけない！！ 
 * m 
 * 
 * @author qwerty
 *
 */
public class RecBoddsUploader {
	Logger logger = LoggerFactory.getLogger(RecBoddsUploader.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	String betType;
	String[] arrKumiban;
	OddsProviderInterface boddsProvider = new  BeforeOddsProvider();
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	public RecBoddsUploader(String betType) {
		super();
		this.betType = betType;
		arrKumiban = prop.getString("kumiban_" + betType).split(Delimeter.COMMA.getValue());
	}
	
	public void execute(String fromYmd, String toYmd) throws Exception {
		String resource = "mybatis-config.0.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		
		// open session with auto-transaction mode = false
		try(SqlSession session = sqlSessionFactory.openSession(false)) {
			RecBoddsMapper mapper = session.getMapper(RecBoddsMapper.class);
			RecBoddsExample example = new RecBoddsExample();
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(fromYmd));
			Date currDate = calendar.getTime();
			Date toDate = sdf.parse(toYmd);
			
			SortedMap<String, RecBoddsItem> mapItem;
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
					RecBoddsItem recBoddsItem = mapItem.get(key);
					if (recBoddsItem == null) {
						recBoddsItem = createRecBoodsItem(yyyyMMdd, token);
						mapItem.put(recBoddsItem.getKey(), recBoddsItem);
					}
				}
				
				// 以前データ削除
				example.clear();
				example.createCriteria().andYmdEqualTo(yyyyMMdd).andBettypeEqualTo(this.betType);
				mapper.deleteByExample(example);
				
				for (RecBoddsItem item : mapItem.values()) {
					RecBodds recBodds = convertRecBodds(item);
					if (recBodds == null) {
						logger.warn("skip 직전オッズが未存在. " + yyyyMMdd + "_" + item.jyoCd + "_" + item.raceNo);
						continue;
					}
					mapper.insert(recBodds);
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
	RecBodds convertRecBodds(RecBoddsItem item) throws Exception {
		RecBodds result = new RecBodds();
		result.setYmd(item.ymd);
		result.setJyocd(item.jyoCd);
		result.setRaceno(item.raceNo);
		result.setBettype(item.betType);
		List<Double> listBor = new ArrayList<Double>();
		List<Integer> listBork = new ArrayList<Integer>();
		for (String kumiban : arrKumiban) {
			Odds bOdds =   boddsProvider.get(item.ymd, item.jyoCd, String.valueOf(item.raceNo), item.betType, kumiban);
			if (bOdds == null) {
				logger.warn("convertRecBodds failed. bOdds is null. " + item.ymd + "," + item.jyoCd + "," + item.raceNo);
				return null;
			}
			listBor.add(bOdds.value);
			listBork.add(bOdds.rank);
		}
		
		double[] bors  = listBor.stream().mapToDouble(Double::doubleValue).toArray();
		int [] borks = listBork.stream().mapToInt(Integer::intValue).toArray();
		result.setBor(bors);
		result.setBork(borks);

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
	
	/** csv toke으로부터 옺즈모니터 아이템을 생성한다. */
	RecBoddsItem createRecBoodsItem(String ymd, String[] token) {
		RecBoddsItem item = new RecBoddsItem();
		item.ymd = ymd;
		item.jyoCd = token[2];
		item.raceNo = Short.parseShort(token[3]);
		item.betType = this.betType;
		
		return item;
	}
	
	/** オッズモニラックitem */
	class RecBoddsItem {
		public String ymd;
		public String jyoCd;
		public short raceNo;
		public String betType;
		
		public String getKey() {
			return jyoCd + raceNo;
		}
	}
	
	public static void main(String[] args) {
		String fromYmd;
		String toYmd;
		String betType;
		
//		fromYmd = args[0];
//		toYmd = args[1];
//		betType = args[2];
		fromYmd = "20210603";
		toYmd = "20231031";
		betType = "3T";
		
		try {
			MLPropertyUtil.getInstance().addFile("C:/Dev/github/pod_boatrace/properties/expr10/expr10.properties");
			new RecBoddsUploader(betType).execute(fromYmd, toYmd);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
