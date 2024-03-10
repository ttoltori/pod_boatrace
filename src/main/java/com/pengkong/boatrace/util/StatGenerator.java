package com.pengkong.boatrace.util;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.converter.RaceMotorStat2RaceStatDataSet;
import com.pengkong.boatrace.converter.RaceStatDataSet2RecWakuRecent;
import com.pengkong.boatrace.converter.RaceStatDataSet2StatWakuRecent;
import com.pengkong.boatrace.converter.RaceStatDataSet2StatWakuWin;
import com.pengkong.boatrace.converter.RecentList2StatRace;
import com.pengkong.boatrace.mybatis.client.RecRaceWakuMapper;
import com.pengkong.boatrace.mybatis.client.RecRacerExMapper;
import com.pengkong.boatrace.mybatis.client.RecWakuRecentMapper;
import com.pengkong.boatrace.mybatis.client.StatRaceMapper;
import com.pengkong.boatrace.mybatis.client.StatWakuRecentMapper;
import com.pengkong.boatrace.mybatis.client.StatWakuWinMapper;
import com.pengkong.boatrace.mybatis.entity.RecRaceWaku;
import com.pengkong.boatrace.mybatis.entity.RecRaceWakuExample;
import com.pengkong.boatrace.mybatis.entity.RecWakuRecent;
import com.pengkong.boatrace.mybatis.entity.RecWakuRecentExample;
import com.pengkong.boatrace.mybatis.entity.StatRace;
import com.pengkong.boatrace.mybatis.entity.StatRaceExample;
import com.pengkong.boatrace.mybatis.entity.StatWakuRecent;
import com.pengkong.boatrace.mybatis.entity.StatWakuRecentExample;
import com.pengkong.boatrace.mybatis.entity.StatWakuWin;
import com.pengkong.boatrace.mybatis.entity.StatWakuWinExample;
import com.pengkong.boatrace.mybatis.entity.ex.RecRacerEx;
import com.pengkong.boatrace.service.stat.MotorStat;
import com.pengkong.boatrace.service.stat.RaceStatDataSet;
import com.pengkong.boatrace.service.stat.RacerStat;
import com.pengkong.boatrace.util.tmp.StatGeneratorOld;
import com.pengkong.common.PropertyUtil;

public class StatGenerator {
	private Logger logger = LoggerFactory.getLogger(StatGenerator.class);
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	/** 選手統計マネージャ key=entryno */
	HashMap<String, RacerStat> mapRacerStat = new HashMap<>();
	
	/** モータ統計マネージャ key=jyocd + "_" + motorno */
	HashMap<String, MotorStat> mapMotorStat = new HashMap<>(); 
	
	/** スレッドマネージャ */
//	ExecutorService executor = null;
	
	public StatGenerator() {
	}
	
	public void execute(String startYmd, String fromYmd, String toYmd) throws Exception {
		String resource = "mybatis-config.0.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		
		// open session with auto-transaction mode = false
		try(SqlSession session = sqlSessionFactory.openSession(false)) {
			// toYmdの前日までの以下のデータをロードする
			// RacerStat, MotorStat
			initializeStat(session, startYmd, toYmd);
			
			RecWakuRecentMapper recWakuRecentMapper = session.getMapper(RecWakuRecentMapper.class);
			StatWakuRecentMapper statWakuRecentMapper = session.getMapper(StatWakuRecentMapper.class);
			StatWakuWinMapper statWakuWinMapper = session.getMapper(StatWakuWinMapper.class);
			StatRaceMapper statRaceMapper = session.getMapper(StatRaceMapper.class);
			RecRaceWakuMapper recRaceWakuMapper = session.getMapper(RecRaceWakuMapper.class);
			
			// TODO 単体テスト完了後スレッド処理化すること
//			ExecutorService executor = Executors.newCachedThreadPool();
			
			// fromYmd~toYmdのrec_race_wakuの一覧を取得する
			List<RecRaceWaku> listRecRaceWaku = selectRecRaceWaku(fromYmd, toYmd, recRaceWakuMapper);
			List<RaceStatDataSet> listRaceStatDataSet = null;
			
			logger.info("集計開始  RecRaceWaku:" + listRecRaceWaku.size() + "件");
			int i = 0;
			for (RecRaceWaku recRaceWaku : listRecRaceWaku) {
				// 6人分の統計計算結果データを取得する(1~6枠順）
				listRaceStatDataSet = calculateRaceStatDataSet(recRaceWaku);
				
				// delete stat records
				deleteStatRecords(recRaceWaku, recWakuRecentMapper, statWakuRecentMapper, statWakuWinMapper, statRaceMapper);
				
				// insert rec_waku_recent
//				RecWakuRecent recWakuRecent = RaceStatDataSet2RecWakuRecent.convert(listRaceStatDataSet);
//				recWakuRecentMapper.insert(recWakuRecent);
				
				// insert stat_waku_recent
//				StatWakuRecent statWakuRecent = RaceStatDataSet2StatWakuRecent.convert(listRaceStatDataSet);
//				statWakuRecentMapper.insert(statWakuRecent);
				
				// insert stat_waku_win
				StatWakuWin statWakuWin = RaceStatDataSet2StatWakuWin.convert(listRaceStatDataSet);
				statWakuWinMapper.insert(statWakuWin);
				
				// insert stat_race
//				StatRace statRace = RecentList2StatRace.convert(recWakuRecent, statWakuRecent, statWakuWin);
				StatRace statRace = RecentList2StatRace.convert(null, null, statWakuWin);
				statRaceMapper.insert(statRace);
				i++;
				if ( (i % 500) == 0) {
					logger.info( i + " / " + listRecRaceWaku.size());
				}
			}
			
			// スレッド処理完了
//			executor.shutdown();

			session.commit();
		}
	}

	/**
	 * fromYmd~toYmdのrec_race_wakuの一覧を取得する
	 * @param fromYmd
	 * @param toYmd
	 * @param recRaceWakuMapper
	 * @return
	 */
	private List<RecRaceWaku> selectRecRaceWaku(String fromYmd, String toYmd, RecRaceWakuMapper recRaceWakuMapper) {
		RecRaceWakuExample recRaceWakuExample = new RecRaceWakuExample();
		recRaceWakuExample.createCriteria().andYmdBetween(fromYmd, toYmd);
		recRaceWakuExample.setOrderByClause("ymd, jyocd, raceno");
		List<RecRaceWaku> listRecRaceWaku = recRaceWakuMapper.selectByExample(recRaceWakuExample);
		
		return listRecRaceWaku;
	}
	
	/**
	 * 特定レース(ymd+jyoCd+raceNo)の統計テーブルレコードを削除する.
	 * @param recRaceWaku
	 * @param recWakuRecentMapper
	 * @param statWakuRecentMapper
	 * @param statWakuWinMapper
	 */
	private void deleteStatRecords(RecRaceWaku recRaceWaku,
			RecWakuRecentMapper recWakuRecentMapper,
			StatWakuRecentMapper statWakuRecentMapper,
			StatWakuWinMapper statWakuWinMapper,
			StatRaceMapper statRaceMapper
			) {
		String ymd = recRaceWaku.getYmd();
		String jyoCd = recRaceWaku.getJyocd();
		Short raceNo = recRaceWaku.getRaceno();
		
//		RecWakuRecentExample example1 = new RecWakuRecentExample();
//		example1.createCriteria().andYmdEqualTo(ymd).andJyocdEqualTo(jyoCd).andRacenoEqualTo(raceNo);
//		recWakuRecentMapper.deleteByExample(example1);
//		
//		StatWakuRecentExample example2 = new StatWakuRecentExample();
//		example2.createCriteria().andYmdEqualTo(ymd).andJyocdEqualTo(jyoCd).andRacenoEqualTo(raceNo);
//		statWakuRecentMapper.deleteByExample(example2);
		
		StatWakuWinExample example3 = new StatWakuWinExample();
		example3.createCriteria().andYmdEqualTo(ymd).andJyocdEqualTo(jyoCd).andRacenoEqualTo(raceNo);
		statWakuWinMapper.deleteByExample(example3);
		
		StatRaceExample example4 = new StatRaceExample();
		example4.createCriteria().andYmdEqualTo(ymd).andJyocdEqualTo(jyoCd).andRacenoEqualTo(raceNo);
		statRaceMapper.deleteByExample(example4);
	}
	
	/**
	 * 6人分の統計結果データ一覧を取得する
	 * @param recRaceWaku rec_race_wakuのレコード
	 * @return
	 */
	private List<RaceStatDataSet> calculateRaceStatDataSet(RecRaceWaku recRaceWaku) {
		
		RaceMotorStat2RaceStatDataSet converter = new RaceMotorStat2RaceStatDataSet();
		List<RaceStatDataSet> listRaceStatDataSet = new ArrayList<>();
		
//		CallableRaceMotorStat2RaceStatDataSet converter = null;
//		Future<RaceStatDataSet> future = null;
//		List<Future<RaceStatDataSet>> listFutureRaceStatDataSet = new ArrayList<>();

//		converter = new CallableRaceMotorStat2RaceStatDataSet(racerStat, motorStat, ymd, jyoCd, (short)1);
//		future = executor.submit(converter);
//		listFuture.add(future);
		
		String ymd = recRaceWaku.getYmd();
		String jyoCd = recRaceWaku.getJyocd();
		Short raceNo = recRaceWaku.getRaceno();
		RacerStat racerStat = null;
		MotorStat motorStat = null;
		RaceStatDataSet raceStatDataSet = null;
		
		// 枠1
		racerStat = getRacerStat(recRaceWaku.getEntry1());
		motorStat = getMotorStat(jyoCd, recRaceWaku.getMotorno1());
		raceStatDataSet = converter.convert(racerStat, motorStat, ymd, jyoCd, raceNo, (short)1);
		listRaceStatDataSet.add(raceStatDataSet);
		
		// 枠2
		racerStat = getRacerStat(recRaceWaku.getEntry2());
		motorStat = getMotorStat(jyoCd, recRaceWaku.getMotorno2());
		raceStatDataSet = converter.convert(racerStat, motorStat, ymd, jyoCd, raceNo, (short)2);
		listRaceStatDataSet.add(raceStatDataSet);
		
		// 枠3
		racerStat = getRacerStat(recRaceWaku.getEntry3());
		motorStat = getMotorStat(jyoCd, recRaceWaku.getMotorno3());
		raceStatDataSet = converter.convert(racerStat, motorStat, ymd, jyoCd, raceNo, (short)3);
		listRaceStatDataSet.add(raceStatDataSet);
		
		// 枠4
		racerStat = getRacerStat(recRaceWaku.getEntry4());
		motorStat = getMotorStat(jyoCd, recRaceWaku.getMotorno4());
		raceStatDataSet = converter.convert(racerStat, motorStat, ymd, jyoCd, raceNo, (short)4);
		listRaceStatDataSet.add(raceStatDataSet);

		// 枠5
		racerStat = getRacerStat(recRaceWaku.getEntry5());
		motorStat = getMotorStat(jyoCd, recRaceWaku.getMotorno5());
		raceStatDataSet = converter.convert(racerStat, motorStat, ymd, jyoCd, raceNo, (short)5);
		listRaceStatDataSet.add(raceStatDataSet);

		// 枠6
		racerStat = getRacerStat(recRaceWaku.getEntry6());
		motorStat = getMotorStat(jyoCd, recRaceWaku.getMotorno6());
		raceStatDataSet = converter.convert(racerStat, motorStat, ymd, jyoCd, raceNo, (short)6);
		listRaceStatDataSet.add(raceStatDataSet);

//		for (Future<RaceStatDataSet> future : listFutureRaceStatDataSet) {
//			listRaceStatDataSet.add(future.get());
//		}
		
		// TODO listRaceStatDataSetをソートすること（スレッドにより順番が変わった可能性があるため )
		
		return listRaceStatDataSet;
	}
	
	/**
	 * toYmdの前日までの以下のデータをロードする
	 * RacerStat, MotorStat
	 * @param session
	 * @param toYmd
	 */
	private void initializeStat (SqlSession session, String fromYmd, String toYmd) {
		/** 日付変換用フォーマッタ */
		DateTimeFormatter formatterYmd = DateTimeFormat.forPattern("yyyyMMdd");
		
		RecRacerExMapper recRacerExMapper = session.getMapper(RecRacerExMapper.class);
		
		DateTime toDateTime = DateTime.parse(toYmd, formatterYmd);
		DateTime fromDateTime = DateTime.parse(fromYmd, formatterYmd);
		DateTime startDateTime = fromDateTime;
		DateTime endDateTime;
		List<RecRacerEx> listRecRacerEx;
		int loadingTermDays = 10;
		boolean flgBreak = false;;
		for (int i = loadingTermDays; i < 100000; i+=loadingTermDays) {
			// 100日分単位で読み込む
			endDateTime = startDateTime.plusDays(loadingTermDays);
			if (toDateTime.compareTo(endDateTime) < 0) {
				endDateTime = toDateTime;
				flgBreak = true;
			}
			
			HashMap<String, String> param = new HashMap<>();
			param.put("fromYmd", formatterYmd.print(startDateTime));
			param.put("toYmd", formatterYmd.print(endDateTime));
			logger.info("rec_racer data loading..." + formatterYmd.print(startDateTime) + "~" + formatterYmd.print(endDateTime));
			
			listRecRacerEx = recRacerExMapper.selectAll(param);
			for (RecRacerEx recRacerEx : listRecRacerEx) {
				RacerStat racerStat = getRacerStat(recRacerEx.getEntry());
				racerStat.addRecRacerEx(recRacerEx);
				
				MotorStat motorStat = getMotorStat(recRacerEx.getJyocd(), recRacerEx.getMotorno());
				motorStat.addRecRacer(recRacerEx);
			}
			listRecRacerEx.clear();
			listRecRacerEx= null;
			if (flgBreak) {
				break;
			}
			
			startDateTime = endDateTime.plusDays(1);
		}
	}
	
	/**
	 * 選手統計マネージャを取得する
	 * @param entry
	 * @return
	 */
	private RacerStat getRacerStat(Short entry) {
		String key = String.valueOf(entry);
		RacerStat racerStat = mapRacerStat.get(key);
		if (racerStat == null) {
			racerStat = new RacerStat();
			mapRacerStat.put(key, racerStat);
		}
		
		return racerStat; 
	}
	
	/**
	 * モータ統計マネージャを取得する
	 * @param jyoCd
	 * @param motorNo
	 * @return
	 */
	private MotorStat getMotorStat(String jyoCd, Short motorNo) {
		String key = jyoCd + "_" + String.valueOf(motorNo);
		MotorStat motorStat = mapMotorStat.get(key);
		if (motorStat == null) {
			motorStat = new MotorStat();
			mapMotorStat.put(key, motorStat);
		}
		
		return motorStat; 
	}
	
	public static void main(String[] args) {
		try {
//			BoatProperty.init(args[2]);
//			new StatGenerator().execute(args[0], args[1]);
			PropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/race_result_rebuild.properties");
			//new StatGeneratorOnetime().execute("20160101", "20181007", "20181007");
			new StatGenerator().execute(args[0], args[1], args[2]);
			
			//BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/race_result_rebuild.properties");
			//new RaceFileUploader().execute("20100101", "20100102");
			//new RaceFileUploader().execute("20090730", "20121231");
			// 20090729 エラー発生
			//new RaceFileUploader().execute("20090101", "20170424");
			//new RaceFileUploader().execute("20090101", "20180725");
			// new RaceFileUploader().execute("20180726", "20181007");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
