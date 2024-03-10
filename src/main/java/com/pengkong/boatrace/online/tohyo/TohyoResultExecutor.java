package com.pengkong.boatrace.online.tohyo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.result.ResultHelper;
import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.RaceResult;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.mybatis.entity.OlResult;
import com.pengkong.boatrace.online.dao.OlResultDAO;
import com.pengkong.boatrace.scraping.parser.RaceResultPageParser;
import com.pengkong.boatrace.util.DatabaseUtil;

/**
 * 投票結果htmlをparsingして、 parsingした結果を投票結果に反映する。
 * 
 * @author ttolt
 *
 */
public class TohyoResultExecutor {
	MLPropertyUtil prop =  MLPropertyUtil.getInstance();

	public TohyoResultExecutor() {
	}

	/**
	 * 投票結果htmlをparsingして、MlResultそれぞれの投票結果に反映する。
	 * ml_resultを更新する。
	 * @param results 対象のMlResult
	 * @return 処理した件数
	 * @throws Exception
	 */
	public int execute(List<OlResult> results) throws Exception {
		String yyyyMMdd = results.get(0).getYmd();
		String jyoCd = results.get(0).getJyocd();
		String raceNo = results.get(0).getRaceno().toString();

		String url = BoatTemplate.URL_RACE_RESULT.replace("{raceNo}", raceNo).replace("{jyoCd}", jyoCd)
				.replace("{yyyyMMdd}", yyyyMMdd);
		
		// 結果情報取得
		Race race = new Race();
		race.wakuList = Arrays.asList(new Waku(),new Waku(),new Waku(),new Waku(),new Waku(),new Waku());
		RaceResultPageParser parser = new RaceResultPageParser();
		RaceResult rr = parser.parseAndSet(race.raceResult, race.wakuList, url);
		
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		try {
			OlResultDAO dao = new OlResultDAO(session);
			
			for (OlResult result : results) {
				result.setResultRank123(rr.sanrentanNo);
				result.setResultKumiban(getResultKumiban(rr, result.getBettype()));
				
				int prize = getResultPrize(rr, result.getBettype());
				result.setResultAmt(prize);

				// レースオッズ、
				result.setRaceOdds(new BigDecimal((float)prize / 100f).setScale(2, RoundingMode.FLOOR).doubleValue());
				result.setRaceOddsrank(getResultRank(rr, result.getBettype()));
				
				// レース結果
				result = ResultHelper.calculateIncomeOl(result);

				dao.update(result);
			}
			session.commit();

			return results.size();
		} finally {
			DatabaseUtil.close(session);
		}
	}
	
	private String getResultKumiban(RaceResult raceResult, String type) throws Exception {
		String ret = null;
		if (type.equals("3T")) {
			ret = raceResult.sanrentanNo;
		} else if (type.equals("3F")) {
			ret = raceResult.sanrenhukuNo;
		} else if (type.equals("2T")) {
			ret = raceResult.nirentanNo;
		} else if (type.equals("2F")) {
			ret = raceResult.nirenhukuNo;
		} else if (type.equals("1T")) {
			ret = raceResult.tansyoNo;
		} 

		return ret;
	}

	private int getResultPrize(RaceResult raceResult, String type) throws Exception {
		int ret = -1;
		if (type.equals("3T")) {
			ret = raceResult.sanrentanPrize;
		} else if (type.equals("3F")) {
			ret = raceResult.sanrenhukuPrize;
		} else if (type.equals("2T")) {
			ret = raceResult.nirentanPrize;
		} else if (type.equals("2F")) {
			ret = raceResult.nirenhukuPrize;
		} else if (type.equals("1T")) {
			ret = raceResult.tansyoPrize;
		} 

		return ret;
	}
	
	private int getResultRank(RaceResult raceResult, String type) throws Exception {
		int ret = -1;
		if (type.equals("3T")) {
			ret = raceResult.sanrentanPopular;
		} else if (type.equals("3F")) {
			ret = raceResult.sanrenhukuPopular;
		} else if (type.equals("2T")) {
			ret = raceResult.nirentanPopular;
		} else if (type.equals("2F")) {
			ret = raceResult.nirenhukuPopular;
		} else if (type.equals("1T")) {
			ret = 0;
		} 

		return ret;
	}
}
