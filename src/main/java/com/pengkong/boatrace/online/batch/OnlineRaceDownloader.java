package com.pengkong.boatrace.online.batch;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.converter.Race2OlRace;
import com.pengkong.boatrace.converter.Race2OlRaceWaku;
import com.pengkong.boatrace.converter.Race2OlRacerArr;
import com.pengkong.boatrace.converter.Race2OlRacerList;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.Setu;
import com.pengkong.boatrace.mybatis.entity.OlRace;
import com.pengkong.boatrace.mybatis.entity.OlRaceWaku;
import com.pengkong.boatrace.mybatis.entity.OlRacer;
import com.pengkong.boatrace.mybatis.entity.OlRacerArr;
import com.pengkong.boatrace.online.dao.OlRaceDAO;
import com.pengkong.boatrace.online.dao.OlRaceWakuDAO;
import com.pengkong.boatrace.online.dao.OlRacerArrDAO;
import com.pengkong.boatrace.online.dao.OlRacerDAO;
import com.pengkong.boatrace.scraping.parser.ComputerPageParser;
import com.pengkong.boatrace.scraping.parser.RaceListPageParser;
import com.pengkong.boatrace.scraping.parser.SetuListPageParser;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.PropertyUtil;

/**
 * online race data downloader.
 * @author ttolt
 *
 */
public class OnlineRaceDownloader {
	String yyyyMMdd;
	
	private Logger logger = LoggerFactory.getLogger(OnlineRaceDownloader.class);

	PropertyUtil prop =  PropertyUtil.getInstance();
	
	SetuListPageParser setuListPageParser =  new SetuListPageParser();
	RaceListPageParser raceListPageParser = new RaceListPageParser();
	ComputerPageParser computerPageParser = new ComputerPageParser();
	
	public OnlineRaceDownloader(String yyyyMMdd) {
		super();
		this.yyyyMMdd = yyyyMMdd;
	}

	public void execute() throws Exception {
		// 節リスト取得
		List<Setu> setuList = parseSetuListHtml();
		logger.debug("setu list size = " + setuList.size());
		
		// open session with auto-transaction mode = false
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		try {
			OlRaceDAO raceDao = new OlRaceDAO(session);
			OlRacerDAO racerDao = new OlRacerDAO(session);
			OlRaceWakuDAO raceWakuDao = new OlRaceWakuDAO(session);
			OlRacerArrDAO racerArrDao = new OlRacerArrDAO(session);
			OlRace raceDto;
			OlRaceWaku wakuDto;
			OlRacerArr racerArrDto;
			
			// 既存当日データ削除
			raceDao.delete(yyyyMMdd);
			racerDao.delete(yyyyMMdd);
			raceWakuDao.delete(yyyyMMdd);
			racerArrDao.delete(yyyyMMdd);
			
			session.commit();
			
			logger.debug("ol_race, ol_racer, ol_race_waku, ol_racer_arr deleted. " + yyyyMMdd);
			
			for (Setu setu : setuList) {
				List<Race> races = parseRaceListHtml(setu);
				for (Race race : races) {
					race.setu = setu;
					race.raceInfo.ymd = yyyyMMdd;
					race = parseRaceHtml(race);
					
					// insert ol_race
					raceDto = Race2OlRace.convert(race);
					raceDao.insert(raceDto);
					
					// insert ol_race_waku
					wakuDto = Race2OlRaceWaku.convert(race);
					raceWakuDao.insert(wakuDto);
					
					// insert ol_racer
					List<OlRacer> records = Race2OlRacerList.convert(race);
					for (OlRacer rec : records) {
						racerDao.insert(rec);
					}

					// insert ol_racer_arr
					racerArrDto = Race2OlRacerArr.convert(race);
					racerArrDao.insert(racerArrDto);
					
					logger.debug("ol_race, ol_racer, ol_race_waku, ol_racer_arr inserted. " + String.join(",", raceDto.getYmd(), raceDto.getJyocd(), raceDto.getRaceno().toString()));
					
					session.commit();
				}
			}
			
		} finally {
			DatabaseUtil.close(session);
		}
	}

	/** 出走表（コンピュータ予想ページ）取得 */
	Race parseRaceHtml(Race race) throws Exception {
		String url = BoatTemplate.URL_COMPUTER_PREDICT.replace("{raceNo}", String.valueOf(race.raceInfo.no))
				.replace("{jyoCd}", race.setu.jyoCd).replace("{yyyyMMdd}", yyyyMMdd);
		return computerPageParser.parseAndSetInfo(race, url);
	}
	
	/**節一覧取得 */
	List<Setu> parseSetuListHtml() throws Exception {
		String urlDailySetuList = BoatTemplate.URL_DAILY_SETU_LIST.replace("{yyyyMMdd}", yyyyMMdd);
		return setuListPageParser.parse(urlDailySetuList, yyyyMMdd, true);
	}
	
	/** レース一覧取得 */
	List<Race> parseRaceListHtml(Setu setu) throws Exception {
		// レース一覧取得
		String urlSetuRaceList = BoatTemplate.URL_SETU_RACELIST.replace("{jyoCd}", setu.jyoCd).replace("{yyyyMMdd}", yyyyMMdd);
		return raceListPageParser.parse(urlSetuRaceList);
	}
	
	public static void main(String[] args) {
		String propertyFilepath = args[0];
		String ymd = args[1];
		
		//String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace_online/properties/online.properties";
		try {
			//MLPropertyUtil.getInstance().addFile(propertyFilepath);
			PropertyUtil.getInstance().addFile(propertyFilepath);
			
			new OnlineRaceDownloader(ymd).execute();
			//new DailyRaceDownloader("20220417").execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
