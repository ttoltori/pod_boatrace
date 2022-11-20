package com.pengkong.boatrace.exp02.result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.pengkong.boatrace.exception.NullValueException;
import com.pengkong.boatrace.model.OddsItemEx;
import com.pengkong.boatrace.mybatis.entity.RankResult;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.service.manager.BeforeOddsManager;
import com.pengkong.boatrace.util.BoatUtil;

public class RankResultCreatorDefault implements RankResultCreator {
	/** ex key=2T value=nirentan */
	private TreeMap<String , String> mapBetType = new TreeMap<>();
	private String desc;
	
	public int betCount = 0;
	public RankResultCreatorDefault() {
		mapBetType.put("1T", "tansyo");  // 1,2
		mapBetType.put("2T", "nirentan"); // 1*, 2*
		mapBetType.put("3T", "sanrentan"); // 1*, 2*
		mapBetType.put("2F", "nirenhuku"); // 1*, 2*
		mapBetType.put("3F", "sanrenhuku"); // 1*, 2*
	}

	@Override
	public String getStat() {
		StringBuilder sb = new StringBuilder();
		sb.append("total=" + betCount);
		return sb.toString();
	}
	
	@Override
	public List<RankResult> create(String desc, String predictRank123, DBRecord dbRec) throws NullValueException {
		this.desc = desc;
		List<RankResult> results = new ArrayList<RankResult>();
		// 1,2로 시작되지 않거나 x를 포함한다면 스킵한다.
		//if ((!predictRank123.startsWith("1") && !predictRank123.startsWith("2")) || predictRank123.contains("x")) {
		if (predictRank123.contains("x")) {
			return results;
		}
		
		// `랭크 123 중복체크 및 설정
		String duplicate = getDuplicate(predictRank123);
		dbRec.put("duplicate", duplicate);
		
		// 베팅타입별로 루프
		for (String betType : mapBetType.keySet()) {
			// ex) "2T"에서 2을 추출한다.
			int betTypeIdx = Integer.parseInt(betType.substring(0,1));
			// ex) "123"에서 "12"를 추출한다.
			String kumiban = predictRank123.substring(0, betTypeIdx);
			if (betTypeIdx == 1) {
				results.add(createDefault("1T", kumiban, predictRank123, dbRec));
				continue;
			}
			
			// 랭크123이 모두 중복
			if (duplicate.equals("123")) {
				continue;
			}
			
			if (betTypeIdx == 2) {
				// 랭크 12가 중복
				if (duplicate.equals("12")) {
					continue;
				}
				
				if (betType.equals("2T")) {
					results.add(createDefault("2T", kumiban, predictRank123, dbRec));
				} else {
					kumiban = BoatUtil.sortKumiban(kumiban);
					results.add(createDefault("2F", kumiban, predictRank123, dbRec));
				}
				
				continue;
			}
			
			// 랭크1,2,3중 하나라도 중복
			if (!duplicate.equals("")) {
				continue;
			}
			
			if (betTypeIdx == 3) {
				if (betType.equals("3T")) {
					results.add(createDefault("3T", kumiban, predictRank123, dbRec));	
				} else {
					kumiban = BoatUtil.sortKumiban(kumiban);
					results.add(createDefault("3F", kumiban, predictRank123, dbRec));
				}
			}
		}
		
		betCount += results.size();
		return results;
	}
	
	private RankResult createDefault(String betType, String kumiban, String predictRank123, DBRecord dbRec) throws NullValueException {
		RankResult rec = new RankResult();
		rec.setYmd(dbRec.getString("ymd"));
		rec.setJyocd(dbRec.getString("jyocd"));
		rec.setRaceno((short)dbRec.getInt("raceno"));
		rec.setSime(dbRec.getString("sime"));
		rec.setDescription(this.desc);
		rec.setPattern(dbRec.getString("pattern"));
		rec.setBettype(betType);
		rec.setPredictRank123(predictRank123);
		rec.setResultRank123(dbRec.getString("sanrentanno"));
		
		// 베팅
		rec.setBetKumiban(kumiban);
		rec.setBetamt(100);
		OddsItemEx beforeOddsEx = BeforeOddsManager.getInstance().getOddsItemEx(rec.getJyocd(), String.valueOf(rec.getRaceno()), betType, kumiban);
		// 직전옺즈가 존재하면
		if (beforeOddsEx != null) {
			rec.setBetOdds(BigDecimal.valueOf(beforeOddsEx.value).setScale(2, RoundingMode.FLOOR));
			rec.setBetOddsrank(beforeOddsEx.rank);
		} else {
			rec.setBetOdds(new BigDecimal(0));
			rec.setBetOddsrank(0);
		}
		
		// 결과
		String betTypePrefix = mapBetType.get(betType);
		int prize = dbRec.getInt( betTypePrefix + "prize");
		BigDecimal resultOdds = new BigDecimal((float)prize / 100f).setScale(2, RoundingMode.FLOOR);
		rec.setResultKumiban(dbRec.getString(betTypePrefix + "no"));
		rec.setResultAmt(prize);
		rec.setResultOdds(resultOdds);
		// 존재하지 않을시(1T)의경우 0이 설정된다.
		rec.setResultOddsrank(dbRec.getInt(betTypePrefix + "popular"));
		
		// 적중여부
		if (rec.getBetKumiban().equals(rec.getResultKumiban())) {
			rec.setHity(1);
			rec.setHitn(0);
			rec.setHitamt(prize);
		} else {
			rec.setHity(0);
			rec.setHitn(1);
			rec.setHitamt(0);
		}
		// 중복예측값
		rec.setDuplicate(dbRec.getString("duplicate"));
		
		return rec;
		
	}

	private String getDuplicate(String predictRank123) {
		String rank1 = predictRank123.substring(0,1);
		String rank2 = predictRank123.substring(1,2);
		String rank3 = predictRank123.substring(2,3);
		if (rank1.equals(rank2) && rank2.equals(rank3)) {
			return "123";
		}
		
		if (rank1.equals(rank2)) {
			return "12";
		}
		
		if (rank1.equals(rank3)) {
			return "13";
		}

		if (rank2.equals(rank3)) {
			return "23";
		}
		
		return "";
	}
	
	public static void main(String[] args) {
		System.out.println("12".substring(0,1));
		System.out.println("12".substring(1,2));
	}
}
