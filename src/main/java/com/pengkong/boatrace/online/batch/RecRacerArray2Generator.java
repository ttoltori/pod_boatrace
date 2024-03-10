package com.pengkong.boatrace.online.batch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.racer.race.RaceWakuTrendProvider;
import com.pengkong.boatrace.exp10.racer.race.RacerWakuTrend;
import com.pengkong.boatrace.mybatis.client.OlRaceWakuMapper;
import com.pengkong.boatrace.mybatis.client.RecRacerArr2Mapper;
import com.pengkong.boatrace.mybatis.client.RecRacerTrendMapper;
import com.pengkong.boatrace.mybatis.entity.OlRaceWaku;
import com.pengkong.boatrace.mybatis.entity.OlRaceWakuExample;
import com.pengkong.boatrace.mybatis.entity.RecRacerArr2;
import com.pengkong.boatrace.mybatis.entity.RecRacerArr2Example;
import com.pengkong.boatrace.mybatis.entity.RecRacerTrend;
import com.pengkong.boatrace.mybatis.entity.RecRacerTrendExample;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.PropertyUtil;

public class RecRacerArray2Generator {
	Logger logger = LoggerFactory.getLogger(RecRacerArray2Generator.class);
	PropertyUtil prop = PropertyUtil.getInstance();

	public void execute(String fromYmd, String toYmd, int daysOfTrend) throws Exception {
		
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		
		// open session with auto-transaction mode = false
		try {
			OlRaceWakuMapper OlRaceWakuMapper = session.getMapper(OlRaceWakuMapper.class); 
			RecRacerArr2Mapper recRacerArr2Mapper = session.getMapper(RecRacerArr2Mapper.class);
			RecRacerTrendMapper recRacerTrendMapper = session.getMapper(RecRacerTrendMapper.class);
			
			DateTime currDate = BoatUtil.parseYmd(fromYmd); 
			DateTime toDate = BoatUtil.parseYmd(toYmd); 
			
			while (currDate.compareTo(toDate) <= 0) {
				// 基準日
				String currYmd = BoatUtil.formatYmd(currDate);

				// 既存データ削除
				RecRacerArr2Example rraExample = new RecRacerArr2Example();
				rraExample.createCriteria().andYmdEqualTo(currYmd);
				recRacerArr2Mapper.deleteByExample(rraExample);

				// レース取得（entry含む）
				List<OlRaceWaku> wakuRecords = loadRaceWaku(currYmd, OlRaceWakuMapper);
				if (wakuRecords.size() <= 0) {
					logger.error("no races. day skipped. ");

					// 1日増加
					currDate = currDate.plusDays(1);
					continue;
				}
				
				// 選手一覧抽出
				List<Short> entrys = getEntryList(wakuRecords);
				
				
				// 直近選手指数データ取得
				String startYmd = BoatUtil.formatYmd(currDate.minusDays(daysOfTrend));
				String endYmd = BoatUtil.formatYmd(currDate);
				List<RecRacerTrend> racerRecords = loadRacerTrends(startYmd, endYmd, entrys, recRacerTrendMapper);
				
				// 枠別選手能力指数プロバイダ
				RaceWakuTrendProvider wakuTrendProvider = new RaceWakuTrendProvider(racerRecords);
				
				for (OlRaceWaku recWaku : wakuRecords) {
					try {
						List<RacerWakuTrend> trends = new ArrayList<>();
						trends.add(wakuTrendProvider.getRacerWakuTrend(recWaku.getEntry1().shortValue(), 0));
						trends.add(wakuTrendProvider.getRacerWakuTrend(recWaku.getEntry2().shortValue(), 1));
						trends.add(wakuTrendProvider.getRacerWakuTrend(recWaku.getEntry3().shortValue(), 2));
						trends.add(wakuTrendProvider.getRacerWakuTrend(recWaku.getEntry4().shortValue(), 3));
						trends.add(wakuTrendProvider.getRacerWakuTrend(recWaku.getEntry5().shortValue(), 4));
						trends.add(wakuTrendProvider.getRacerWakuTrend(recWaku.getEntry6().shortValue(), 5));
						
						RecRacerArr2 dto = initializeDto();
						dto.setYmd(currYmd);
						dto.setJyocd(recWaku.getJyocd());
						dto.setRaceno(recWaku.getRaceno().shortValue());
						
						for (int i = 0; i < 6; i++) {
							((int[])dto.getRuncnt())[i] = trends.get(i).runcount;
							((double[])dto.getRuncntSlope())[i] = trends.get(i).runCountSlope;
							((double[])dto.getCond())[i] = trends.get(i).cond;
							((double[])dto.getCondSlope())[i] = trends.get(i).condSlope;
							((double[])dto.getN1point())[i] = trends.get(i).n1point;
							((double[])dto.getN1pointSlope())[i] = trends.get(i).n1pointSlope;
							((double[])dto.getN2point())[i] = trends.get(i).n2point;
							((double[])dto.getN2pointSlope())[i] = trends.get(i).n2pointSlope;
							((double[])dto.getN3point())[i] = trends.get(i).n3point;
							((double[])dto.getN3pointSlope())[i] = trends.get(i).n3pointSlope;
							((double[])dto.getN1pointWaku())[i] = trends.get(i).n1PointWaku;
							((double[])dto.getN1pointWakuSlope())[i] = trends.get(i).n1PointWakuSlope;
							((double[])dto.getN2pointWaku())[i] = trends.get(i).n2PointWaku;
							((double[])dto.getN2pointWakuSlope())[i] = trends.get(i).n2PointWakuSlope;
							((double[])dto.getN3pointWaku())[i] = trends.get(i).n3PointWaku;
							((double[])dto.getN3pointWakuSlope())[i] = trends.get(i).n3PointWakuSlope;
							((double[])dto.getAvgstartWaku())[i] = trends.get(i).avgstartWaku;
							((double[])dto.getAvgstartWakuSlope())[i] = trends.get(i).avgstartWakuSlope;
						}
						
						recRacerArr2Mapper.insert(dto);
					} catch (Exception e) {
						logger.error("error skipped. " + String.join(",", recWaku.getYmd(), recWaku.getJyocd(), recWaku.getRaceno().toString()) );
						continue;
					}
				}
				
				session.commit();
				logger.info("rec_racer_arr2 inserted. " + currYmd);
				
				// 1日増加
				currDate = currDate.plusDays(1);
			}
		} finally {
			DatabaseUtil.close(session);
		}
	}

	RecRacerArr2 initializeDto() {
		RecRacerArr2 dto = new RecRacerArr2();
		dto.setRuncnt(new int[6]);
		dto.setRuncntSlope(new double[6]);
		dto.setCond(new double[6]);
		dto.setCondSlope(new double[6]);
		dto.setN1point(new double[6]);
		dto.setN1pointSlope(new double[6]);
		dto.setN2point(new double[6]);
		dto.setN2pointSlope(new double[6]);
		dto.setN3point(new double[6]);
		dto.setN3pointSlope(new double[6]);
		dto.setN1pointWaku(new double[6]);
		dto.setN1pointWakuSlope(new double[6]);
		dto.setN2pointWaku(new double[6]);
		dto.setN2pointWakuSlope(new double[6]);
		dto.setN3pointWaku(new double[6]);
		dto.setN3pointWakuSlope(new double[6]);
		dto.setAvgstartWaku(new double[6]);
		dto.setAvgstartWakuSlope(new double[6]);
		
		return dto;
	}
	
	List<OlRaceWaku> loadRaceWaku(String ymd, OlRaceWakuMapper mapper) {
		OlRaceWakuExample exam = new OlRaceWakuExample();
		exam.createCriteria().andYmdEqualTo(ymd);
		exam.setOrderByClause("ymd");

		return mapper.selectByExample(exam);
	}

	List<RecRacerTrend> loadRacerTrends(String fromYmd, String toYmd,  List<Short> entrys, RecRacerTrendMapper mapper) {
		RecRacerTrendExample exam = new RecRacerTrendExample();
		exam.createCriteria().andYmdBetween(fromYmd, toYmd).andEntryIn(entrys);
		exam.setOrderByClause("entry,ymd");
		
		return mapper.selectByExample(exam);
	}
	
	List<Short> getEntryList(List<OlRaceWaku> races) {
		Set<Short> entrys = new HashSet<>();
		for (OlRaceWaku race : races) {
			entrys.add(race.getEntry1().shortValue());
			entrys.add(race.getEntry2().shortValue());
			entrys.add(race.getEntry3().shortValue());
			entrys.add(race.getEntry4().shortValue());
			entrys.add(race.getEntry5().shortValue());
			entrys.add(race.getEntry6().shortValue());
		}
		
		return new ArrayList<Short>(entrys);
	}
	
	public static void main(String[] args) {
		String fromYmd = args[0];
		String toYmd = args[1];
		int daysOfTrend = Integer.valueOf(args[2]);
		String propFile = args[3];
		
		if (args.length != 4) {
			System.out.println("usage RecRacerArray2Generator {fromYmd} {toYmd} {daysOfTrend} {property filepath}");
		}
		
//		String fromYmd = "20120110";
//		String toYmd = "20120110";
//		int daysOfTrend = 180;
		
		try {
			PropertyUtil.getInstance().addFile(propFile);
			new RecRacerArray2Generator().execute(fromYmd, toYmd, daysOfTrend);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}