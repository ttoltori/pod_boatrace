package com.pengkong.boatrace.exp10.result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.provider.OddsProviderInterface;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.probability.calculator.AbstractProbabilityCalculator;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.MathUtil;
import com.pengkong.common.StringUtil;

/**
 * ベッティング生成抽象クラス.
 * @author ttolt
 *
 */
public abstract class AbstractResultCreator {
	/** 実験プロパティ */
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	/** 直前オッズprovider */
	protected OddsProviderInterface beforeOddsProvider;

	/** 確定オッズprovider */
	protected OddsProviderInterface resultOddsProvider;
	
	/** bettype定義 !!! 追加時はResultStatBuilder#getPredictions()にも追加が必要 */
	TreeMap<BetType , String> mapBetType;
	
	/** 予想的中確率をbettype毎の戦略に沿って組み合わせるためのクラス */
	protected AbstractProbabilityCalculator probabilityCalculator;
	
	public AbstractResultCreator() {
	}
	
	protected abstract void preExecute();
	protected abstract List<MlResult> get1Tresult(String kumiban, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get2Tresult(String kumiban, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Tresult(String kumiban, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get2Fresult(String kumiban, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Fresult(String kumiban, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get2Mresult(String[] predictions, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Mresult(String[] predictions, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Nresult(String[] predictions, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get2Nresult(String[] predictions, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Presult(String[] predictions, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Rresult(String[] predictions, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Uresult(String[] predictions, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Xresult(String[] predictions, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Yresult(String[] predictions, DBRecord rec) throws Exception;
    protected abstract List<MlResult> get3Aresult(String[] predictions, DBRecord rec) throws Exception;
    protected abstract List<MlResult> get2Aresult(String[] predictions, DBRecord rec) throws Exception;

	void initialize() {
		mapBetType = new TreeMap<>();
		mapBetType.put(BetType._1T, "tansyo");  // 1,2
		mapBetType.put(BetType._2T, "nirentan"); // 1*, 2*
		mapBetType.put(BetType._3T, "sanrentan"); // 1*, 2*
		mapBetType.put(BetType._2F, "nirenhuku"); // 1*, 2*
		mapBetType.put(BetType._3F, "sanrenhuku"); // 1*, 2*
		mapBetType.put(BetType._2M, "nirentan"); // formation 12,21
		mapBetType.put(BetType._3M, "sanrentan"); // ３連単1-2-3, 1-3-2, 2-1-3, 3-1-2 4点 (통계단위 3자리)
		mapBetType.put(BetType._3N, "nirentan"); // formation 12,13
		mapBetType.put(BetType._2N, "sanrentan"); // ３連単1-2-3456, 4点 (통계단위 2자리)
		mapBetType.put(BetType._3P, "sanrentan"); // ３連単1-2-3456, 1-3-2456  6点 無視
		mapBetType.put(BetType._3R, "sanrentan"); // ３連単1-2-3456, 1-3-2456  8点 無視
		mapBetType.put(BetType._3U, "sanrentan"); // ３連単1-2-3456, 1-3456-2  8点 無視
		mapBetType.put(BetType._3X, "sanrentan"); // ３連単1-3456-2, 2-3456-1 8点 (통계단위 2자리)
		mapBetType.put(BetType._3Y, "sanrentan"); // ３連単1-2-3456, 1-3456-2  8点 (통계단위 2자리)
        mapBetType.put(BetType._3A, "sanrentan"); // ３連単
        mapBetType.put(BetType._2A, "nirentan"); // ２連単
		
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
		String[] tokenKumiban = kumibans.split(Delimeter.DASH.getValue());
		for (String betTypeStr : tokenBettype) {
			// 予測組番の桁数チェック
			if (!ResultHelper.isValidDigits(betTypeStr, predictions)) {
				continue;
			}
			// 予測組番の重複チェック
			String usedModelNo = prop.getString("used_model_no");
			if (!ResultHelper.isUnique(usedModelNo, betTypeStr, predictions)) {
				continue;
			}
			// 予測組番の出力対象チェック
			if (!ResultHelper.isValidRange(betTypeStr, predictions, tokenKumiban)) {
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
			// 2M 2T formation
			if (BetType._2M.getValue().equals(betTypeStr)) {
				result.addAll(get2Mresult(predictions, dbRec));
			}
			// 3N 2T formation
			if (BetType._3N.getValue().equals(betTypeStr)) {
				result.addAll(get3Nresult(predictions, dbRec));
			}
			// 2N ３連単1-2-3456, 4点
			if (BetType._2N.getValue().equals(betTypeStr)) {
				result.addAll(get2Nresult(predictions, dbRec));
			}
			// 3M ３連単1-2-3, 1-3-2, 2-1-3, 3-1-2 4点
			if (BetType._3M.getValue().equals(betTypeStr)) {
				result.addAll(get3Mresult(predictions, dbRec));
			}
			// 3P ３連単1-2-3, 1-3-2, 2-1-3, 2-3-1, 3-1-2, 3-2-1 6点
			if (BetType._3P.getValue().equals(betTypeStr)) {
				result.addAll(get3Presult(predictions, dbRec));
			}
			// 3R ３連単1-2-3456, 1-3-2456  8点
			if (BetType._3R.getValue().equals(betTypeStr)) {
				result.addAll(get3Rresult(predictions, dbRec));
			}
			// 3U ３連単1-2-3456, 1-3456-2  8点
			if (BetType._3U.getValue().equals(betTypeStr)) {
				result.addAll(get3Uresult(predictions, dbRec));
			}
			if (BetType._3X.getValue().equals(betTypeStr)) {
				result.addAll(get3Xresult(predictions, dbRec));
			}
			if (BetType._3Y.getValue().equals(betTypeStr)) {
				result.addAll(get3Yresult(predictions, dbRec));
			}
            if (BetType._3A.getValue().equals(betTypeStr)) {
                result.addAll(get3Aresult(predictions, dbRec));
            }
            if (BetType._2A.getValue().equals(betTypeStr)) {
                result.addAll(get2Aresult(predictions, dbRec));
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
	protected MlResult createDefault(BetType statBetType, BetType betType, String kumiban, DBRecord rec) throws Exception {
		MlResult result = createDefault(betType, kumiban, rec);
		result.setStatBettype(statBetType.getValue());
		return result;
	}

	protected MlResult createDefault(BetType betType, String kumiban, DBRecord rec) throws Exception {
		// 共通レース情報設定
		MlResult result = createDefaultResult(rec);
		
		// bettype
		result.setBettype(betType.getValue());
		// bet_kumiban
		result.setBetKumiban(kumiban);
		// betamt
		result.setBetamt(getDefaultBetamt(betType.getValue()));
		
		// 予想的中確率を設定する(BetTypeを基に計算する)
		result.setProbability(MathUtil.scale2(probabilityCalculator.calculate(betType.getValue(), rec)));
		
		// 直前オッズ
		result = setBeforeOdds(result);
		
		// 確定オッズ
		result = setResultOdds(result);
		
		// レース結果設定 */
		result = setRaceResult(betType, rec, result);
		
		return result;
	}

	/** 直前オッズ設定 */
	protected MlResult setBeforeOdds(MlResult result) throws Exception {
		// 直前オッズ
		Odds beforeOdds = beforeOddsProvider.get(result.getYmd(), result.getJyocd(),
				String.valueOf(result.getRaceno()), result.getBettype(), result.getBetKumiban());
		if (beforeOdds != null) {
			result.setBetOdds(beforeOdds.value);
			result.setBetOddsrank(beforeOdds.rank);
		}
		
		return result;
	}
	
	/** 確定オッズ設定 */
	protected MlResult setResultOdds(MlResult result) throws Exception {
		Odds resultOdds = resultOddsProvider.get(result.getYmd(), result.getJyocd(),
				String.valueOf(result.getRaceno()), result.getBettype(), result.getBetKumiban());
		if (resultOdds != null) {
			result.setResultOdds(resultOdds.value);
			result.setResultOddsrank(resultOdds.rank);
		} 
		
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
	
	/**
	 * ml_resultのresultno～result_rank123の情報を設定して返却する.
	 * @param rec
	 * @return
	 */
	protected MlResult createDefaultResult(DBRecord rec) {
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
		result.setPredictRank123(rec.getString("prediction1", "") + rec.getString("prediction2", "") + rec.getString("prediction3", ""));
		
		return result;
	}
}
