package com.pengkong.boatrace.online.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.mybatis.client.RecRacerMapper;
import com.pengkong.boatrace.mybatis.client.RecRacerTrendMapper;
import com.pengkong.boatrace.mybatis.entity.RecRacer;
import com.pengkong.boatrace.mybatis.entity.RecRacerExample;
import com.pengkong.boatrace.mybatis.entity.RecRacerTrend;
import com.pengkong.boatrace.mybatis.entity.RecRacerTrendExample;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.PropertyUtil;

public class RecRacerTrendGenerator {
	Logger logger = LoggerFactory.getLogger(RecRacerTrendGenerator.class);
	PropertyUtil prop = PropertyUtil.getInstance();
	
	RecRacerTrendCreator creator = new RecRacerTrendCreator();
	
	public void execute(String fromYmd, String toYmd, int daysOfTrend) throws Exception {
		
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		
		try {
			RecRacerMapper recRacerMapper = session.getMapper(RecRacerMapper.class);
			RecRacerTrendMapper recRacerTrendMapper = session.getMapper(RecRacerTrendMapper.class);
			CustomMapper customMapper = session.getMapper(CustomMapper.class);
			
			DateTime currDate = BoatUtil.parseYmd(fromYmd); 
			DateTime toDate = BoatUtil.parseYmd(toYmd); 
			
			while (currDate.compareTo(toDate) <= 0) {
				// 基準日
				String currYmd = BoatUtil.formatYmd(currDate);

				// 既存データ削除
				RecRacerTrendExample rrtExample = new RecRacerTrendExample();
				rrtExample.createCriteria().andYmdEqualTo(currYmd);
				recRacerTrendMapper.deleteByExample(rrtExample);
				
				// 基準日の出走選手一覧を取得 
				List<Short> entrys = getEntryList(currYmd, customMapper);
				if (entrys.size() <= 0) {
					logger.info("entry list size from ol_race is zero. skipped. " + currYmd);
					// 1日増加
					currDate = currDate.plusDays(1);
					
					continue;
				}
				
				// 直近データ取得開始、終了日 ex) 60日前～昨日
				String startYmd = BoatUtil.formatYmd(currDate.minusDays(daysOfTrend+1));
				String endYmd = BoatUtil.formatYmd(currDate.minusDays(1));
				
				// 基準日racer直近記録取得
				RecRacerExample rrExample = new RecRacerExample();
				rrExample.createCriteria().andYmdBetween(startYmd, endYmd).andEntryIn(entrys);
				List<RecRacer> records = recRacerMapper.selectByExample(rrExample);
				
				List<RecRacerTrend> results = creator.create(currYmd, daysOfTrend, records);
				
				for (RecRacerTrend rec : results) {
					recRacerTrendMapper.insert(rec);
				}
				
				session.commit();
				logger.info("rec_racer_trend inserted. " + currYmd);
				
				// 1日増加
				currDate = currDate.plusDays(1);
			}
		} finally {
			DatabaseUtil.close(session);
		}
	}
	
	List<Short> getEntryList(String ymd, CustomMapper mapper) {
		List<Short> results = new ArrayList<>();
		
		String sql = "select entry entryno from ol_racer where ymd = '{ymd}' group by entry";
		sql = sql.replace("{ymd}", ymd);
		
		HashMap<String, String> mapParam = new HashMap<>();
		mapParam.put("sql", sql);
		
		List<DBRecord> records = mapper.selectSql(mapParam);
		for (DBRecord rec : records) {
			results.add(rec.getInteger("entryno").shortValue());
		}
		
		return results;
	}
	
	public static void main(String[] args) {
		String fromYmd = args[0];
		String toYmd = args[1];
		int daysOfTrend = Integer.valueOf(args[2]);
		String propFile = args[3];
		
		if (args.length != 4) {
			System.out.println("usage RecRacerTrendGenerator {fromYmd} {toYmd} {daysOfTrend} {property filepath}");
		}
		
//		String fromYmd = "20180415";
//		String toYmd = "20230420";
//		int daysOfTrend = 180;
		
		try {
			PropertyUtil.getInstance().addFile(propFile);			
			new RecRacerTrendGenerator().execute(fromYmd, toYmd, daysOfTrend);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
