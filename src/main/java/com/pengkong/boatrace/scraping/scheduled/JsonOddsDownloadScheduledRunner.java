package com.pengkong.boatrace.scraping.scheduled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.common.enums.BetTypeJson;
import com.pengkong.boatrace.exception.FatalException;
import com.pengkong.boatrace.model.Odds;
import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.json.OddsJson;
import com.pengkong.boatrace.service2.manager.TohyoManager;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

import lombok.Getter;
/**
 * レース毎のJSONオッズを取得してファイルに保存する。 
 * 
 * プロパティ：DIRECTORY_ODDS
 * @author ttolt
 */
public class JsonOddsDownloadScheduledRunner extends AbstractScheduledRunner {

	@Getter
	private Race race;

	/** ダウンロード回数 */
	private int cntDownload = 1;

	private final Logger logger = LoggerFactory.getLogger(JsonOddsDownloadScheduledRunner.class);

	/** オッズが配列の勝式 */
	private static final List<BetTypeJson> listMultiOddsBettypeJson = new ArrayList<>(Arrays.asList(BetTypeJson._1F, BetTypeJson._KF));
	
	public JsonOddsDownloadScheduledRunner(Race race) {
		this.race = race;
		initialize();
	}

	private void initialize() {
	}

	/**
	 * 勝式毎のオッズ一覧をcsvへ変換する.
	 * 
	 * @param betTypeJson 勝式
	 * @param arrJo オッズ一覧
	 * 
	 * @return csv(改行含み)
	 */
	private StringBuilder createCsvLine(BetTypeJson betTypeJson, JsonArray arrJo) {
		StringBuilder sb = new StringBuilder();
		Gson gson = new Gson();
		String line = "";
		
		setPrefix(sb, betTypeJson);
		// 1F, KF
		if (listMultiOddsBettypeJson.contains(betTypeJson)) {
			Map<String, Float[]> mapOddsArray = new TreeMap<>();
			for (JsonElement je : arrJo) {
				OddsJson odds = gson.fromJson(je, OddsJson.class);
				String kumiban = odds.kumiban.replace("-","").replace("=", "");
				Float[] arrFloat = new Float[2];
				arrFloat[0] = odds.minOddsValue;
				arrFloat[1] = odds.maxOddsValue;
				mapOddsArray.put(kumiban, arrFloat);
				
				line = Odds.toCsvFloats(mapOddsArray);
			}
			
		} else {
			Map<String, Float> mapOdds = new TreeMap<>();		
			for (JsonElement je : arrJo) {
				OddsJson odds = gson.fromJson(je, OddsJson.class);
				String kumiban = odds.kumiban.replace("-","").replace("=", "");
				mapOdds.put(kumiban, Float.valueOf(odds.oddsValue));
				
				line = Odds.toCsv(mapOdds);
			}
		}
		
		sb.append(line);
		sb.append(System.lineSeparator());
		
		return sb;
	}
	
	/**
	 * オッズjsonから対象勝式のオッズ一覧をcsvへ変換する.
	 * 
	 * @param json 全オッズのJSON
	 * @param arrBetTypeJson 変換対象の勝式一覧
	 * 
	 * @return 対象勝式のオッズCSV
	 */
	private String createCsv(JsonObject json, BetTypeJson[] arrBetTypeJson) {
		StringBuilder sb = new StringBuilder();

		for (BetTypeJson betTypeJson : arrBetTypeJson) {
			JsonArray arrJo = json.getAsJsonObject("oddsDetailListByKachishiki").getAsJsonArray(betTypeJson.getValue());
			sb.append(createCsvLine(betTypeJson, arrJo));
		}
		String csvOdds = sb.substring(0, sb.length()-2);
		
		return csvOdds;
	}
	
	@Override
	public void execute() throws FatalException {
		String yyyyMMdd = race.raceInfo.ymd;
		String jyoCd = race.setu.jyoCd;
		String raceNo = String.valueOf(race.raceInfo.no);
		String description = yyyyMMdd + "," + jyoCd + "," + raceNo + "," + cntDownload;
		
		logger.debug("start. " + description);

		// get odds json object
		JsonObject json = null;
		try {
			json = TohyoManager.getInstance().getJsonOdds(race);
		} catch (IOException e1) {
			throw new FatalException("json odds retrieval failed. race=" + Race.getDescription(race), e1);
		}

		// convert json to csv string
		BetTypeJson[] arrBetType = new BetTypeJson[] { BetTypeJson._1T, BetTypeJson._1F, BetTypeJson._2T,
				BetTypeJson._2F, BetTypeJson._3T, BetTypeJson._3F, BetTypeJson._KF };
		String csvOdds = createCsv(json, arrBetType); 
		
		// save odds to file
		try {
			saveCsv(race.raceInfo.ymd, csvOdds);
		} catch (Exception e) {
			logger.warn("json odds save failed. race=" + Race.getDescription(race), e);
			return;
		}
		
		logger.info("odds saved. " + description);
		// ダウンロードカウンタを増加
		cntDownload++;

		logger.debug("end. " + description);
	}
	
	/**
	 * オッズをファイルにappendする
	 * @param yyyyMMdd 日付
	 * @param csv オッズのcsv
	 * @throws Exception
	 */
	private synchronized void saveCsv(String yyyyMMdd, String csv) throws Exception {
		String filepath =PropertyUtil.getInstance().getString("DIRECTORY_ODDS") + "odds_" + yyyyMMdd + ".csv";
		FileUtil.appendFileByLine(filepath, csv);
	}

	/**
	 * バッファにレース情報を設定する
	 * @param sb バッファ
	 * @param betType 勝式
	 * 
	 * @result sb
	 */
	private void setPrefix(StringBuilder sb, BetTypeJson betType) {
		
		String yyyyMMdd = race.raceInfo.ymd;
		String jyoCd = race.setu.jyoCd;
		String raceNo = String.valueOf(race.raceInfo.no);
		
		sb.append(BoatUtil.currentHms());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(yyyyMMdd);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(jyoCd);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(raceNo);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(betType);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(cntDownload);
		sb.append(BoatTemplate.CSV_DELIMITER);
	}

	@Override
	protected void notifyScheduleComplete() {
		// nothing todo
	}
}
