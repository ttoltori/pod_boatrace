package com.pengkong.boatrace.exp10.result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.converter.MlExpectedRec;
import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.provider.AbstractOddsProvider;
import com.pengkong.boatrace.exp10.odds.provider.OddsProviderInterface;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.result.stat.BorkPatternProvider;
import com.pengkong.boatrace.exp10.simulation.calculator.expectation.AbstractProbabilityExpCalculator;
import com.pengkong.boatrace.exp10.simulation.calculator.probability.AbstractProbabilityCalculator;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.MathUtil;
import com.pengkong.common.StringUtil;

/**
 * ベッティング生成抽象クラス.
 * @author ttolt
 *
 */
public abstract class AbstractResultCreatorExp {
	/** 実験プロパティ */
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	/** 直前オッズprovider */
	protected OddsProviderInterface beforeOddsProvider;

	/** 確定オッズprovider */
	protected AbstractOddsProvider resultOddsProvider;
	
	/** bettype定義 !!! 追加時はResultStatBuilder#getPredictions()にも追加が必要 */
	TreeMap<BetType , String> mapBetType;
	
	/** 予想的中確率をbettype毎の戦略に沿って組み合わせるためのクラス */
	protected AbstractProbabilityCalculator probabilityCalculator;
	
	/** 기대치(확률*옺즈)를 계산하기 위한 확률을 취득하는 클래스 */
	protected AbstractProbabilityExpCalculator probabilityExpCalculator;
	
	protected BorkPatternProvider borkPatternProvider = new BorkPatternProvider();
	
	public AbstractResultCreatorExp() {
	}
	
	protected abstract void preExecute();
	protected abstract List<MlResult> get1Tresult(String kumiban, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get2Tresult(String kumiban, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Tresult(String kumiban, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get2Fresult(String kumiban, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Fresult(String kumiban, DBRecord rec) throws Exception;

	void initialize() {
		mapBetType = new TreeMap<>();
		mapBetType.put(BetType._1T, "tansyo");  // 1,2
		mapBetType.put(BetType._2T, "nirentan"); // 1*, 2*
		mapBetType.put(BetType._3T, "sanrentan"); // 1*, 2*
		mapBetType.put(BetType._2F, "nirenhuku"); // 1*, 2*
		mapBetType.put(BetType._3F, "sanrenhuku"); // 1*, 2*
		
		preExecute();
	}

	void ensureInitialized() {
		if (mapBetType == null) {
			initialize();
		}
	}
	
	/**
	 * DB取得したML予測結果からベッティング一覧を生成する。
	 * @param dbRec DB取得したML予測結果
	 * @return List<MlResultEx>
	 * @throws Exception
	 */
	public List<MlResult> execute(DBRecord dbRec, String betTypes, String kumibans) throws Exception {
		ensureInitialized();
		
		List<MlResult> result = new ArrayList<>();
		
		// 予測の組番を取得
		String[] predictions = ResultHelper.getPredictions(dbRec);
		
		// ターゲットのBetTypeリストを巡回
		String[] tokenBettype = betTypes.split(Delimeter.COMMA.getValue());
		String[] tokenKumiban = ResultHelper.parseKumiban(kumibans);
		for (String betTypeStr : tokenBettype) {
			if (!ResultHelper.isValidPredictions(betTypeStr, predictions)) {
				continue;
			}
			
			// 1T
			if (BetType._1T.getValue().equals(betTypeStr)) {
				result.addAll(get1Tresult(predictions[0], dbRec));
			}
			// 2T
			if (BetType._2T.getValue().equals(betTypeStr)) {
				result.addAll(get2Tresult(String.join("", predictions[0], predictions[1]), dbRec));
			}
			// 2F
			if (BetType._2F.getValue().equals(betTypeStr)) {
				String[] sorted = StringUtil.copyAndSort(predictions[0], predictions[1]);
				result.addAll(get2Fresult(String.join("", sorted[0], sorted[1]), dbRec));
			}
			// 3T
			if (BetType._3T.getValue().equals(betTypeStr)) {
				result.addAll(get3Tresult(String.join("", predictions[0], predictions[1], predictions[2]), dbRec));
			}
			// 3F
			if (BetType._3F.getValue().equals(betTypeStr)) {
				String[] sorted = StringUtil.copyAndSort(predictions[0], predictions[1], predictions[2]);
				result.addAll(get3Fresult(String.join("", sorted[0], sorted[1], sorted[2]), dbRec));
			}
		}
		
		return result;
	}

	/**
	 * フォーメーション投票の統計対応の結果を取得する
	 * @param statBetType 統計用bettype ex) 2M
	 * @param betType bettype ex) 2T
	 * @param kumiban ex) 123
	 * @param rec
	 * @return
	 * @throws Exception
	 */
	protected MlResult createDefault(BetType statBetType, BetType betType, String kumiban, DBRecord rec, MlExpectedRec expRec) throws Exception {
		MlResult result = createDefaultInner(statBetType, betType, kumiban, rec, expRec);
		result.setStatBettype(statBetType.getValue());
		return result;
	}

	protected MlResult createDefaultInner(BetType statBetType, BetType betType, String kumiban, DBRecord rec, MlExpectedRec expRec) throws Exception {
		// 共通レース情報設定
		MlResult result = createDefaultResult(rec, expRec);
		
		// bettype
		result.setBettype(betType.getValue());
		// bet_kumiban
		result.setBetKumiban(kumiban);
		// betamt
		result.setBetamt(getDefaultBetamt(betType.getValue()));
		
		// 予想的中確率を設定する(BetTypeを基に計算する)
		//result.setProbability(MathUtil.scale2(probabilityCalculator.calculate(betType.getValue(), rec)));
		result.setProbability(expRec.bprob);
		
		// 直前オッズ
		result = setbeforeOdds(result, expRec);
		// 確定オッズ
		result = setResultOdds(result, expRec);
		result.setExpectBor(  expRec.bexp);
		result.setExpectBork(  MathUtil.scale2(expRec.bork * expRec.bprob) );
		result.setExpectRor(  expRec.rexp);
		result.setExpectRork(  expRec.rork * expRec.ror);
		
		// レース結果設定 */
		result = setRaceResult(betType, rec, result);
		
		return result;
	}
	
	protected MlResult setbeforeOdds(MlResult result, MlExpectedRec expRec) throws Exception {
		result.setBetOdds(expRec.bor);
		result.setBetOddsrank(expRec.bork);
		return result;
	}
	
	protected MlResult setResultOdds(MlResult result, MlExpectedRec expRec) throws Exception {
		result.setResultOdds(expRec.ror);
		result.setResultOddsrank(expRec.rork);
		return result;
	}
	
	/** レース結果設定 */
	protected MlResult setRaceResult(BetType betType, DBRecord rec, MlResult result) throws Exception {
		String betTypePrefix = mapBetType.get(betType);

		result.setResultRank123(rec.getString("sanrentanno"));
		// resul_kumiban
		result.setResultKumiban(rec.getString(betTypePrefix + "no"));
		int prize = rec.getInt(betTypePrefix + "prize");
		// result_amt
		result.setResultAmt(prize);
		
		// レースオッズ、
		result.setRaceOdds(new BigDecimal((float)prize / 100f).setScale(2, RoundingMode.FLOOR).doubleValue());
		
		// レースオッズランク
		Odds raceOdds = resultOddsProvider.get(result.getYmd(), result.getJyocd(),
				String.valueOf(result.getRaceno()), result.getBettype(), result.getResultKumiban());
		if (raceOdds != null) {
			result.setRaceOddsrank(raceOdds.rank);
		} 

		// 最小ベッティング金額を設定して結果を計算する
		result = ResultHelper.calculateIncome(result);
		
		return result;
	}
	
	protected int getDefaultBetamt(String betType) {
		return prop.getInteger("BET_" + betType);
	}

	protected MlResult createDefaultResult(DBRecord rec, MlExpectedRec expRec) {
		MlResult result = new MlResult();
		// 実験番号はpropertyeから取得
		result.setResultno(prop.getString("result_no"));
		result.setResultType(prop.getString("result_type"));
		result.setModelno(rec.getString("modelno"));
		result.setYmd(rec.getString("ymd"));
		result.setJyocd(rec.getString("jyocd"));
		result.setRaceno((short)rec.getInt("raceno", -1));
		result.setSime(rec.getString("sime"));
		result.setPatternid(rec.getString("patternid"));
		result.setPattern(rec.getString("pattern"));
		result.setPredictRank123(expRec.bkumiban);
		
		return result;
	}
}
