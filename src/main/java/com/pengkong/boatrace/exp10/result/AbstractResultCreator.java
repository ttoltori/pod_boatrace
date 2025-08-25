package com.pengkong.boatrace.exp10.result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.common.enums.Delimeter;
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
public abstract class AbstractResultCreator {
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
	
	public AbstractResultCreator() {
	}
	
	protected abstract void preExecute();
	protected abstract List<MlResult> get1Tresult(String kumiban, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get2Tresult(String kumiban, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Tresult(String kumiban, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get2Fresult(String kumiban, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Fresult(String kumiban, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get2Mresult(String[] predictions, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Nresult(String[] predictions, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get2Nresult(String[] predictions, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Presult(String[] predictions, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Rresult(String[] predictions, DBRecord rec) throws Exception;
	protected abstract List<MlResult> get3Xresult(String[] predictions, DBRecord rec) throws Exception;
    protected abstract List<MlResult> get2Gresult(String[] predictions, DBRecord rec) throws Exception;
    protected abstract List<MlResult> get3Gresult(String[] predictions, DBRecord rec) throws Exception;
    protected abstract List<MlResult> get3Bresult(String[] predictions, DBRecord rec) throws Exception;
    protected abstract List<MlResult> get3Cresult(String[] predictions, DBRecord rec) throws Exception;
    protected abstract List<MlResult> get3Dresult(String[] predictions, DBRecord rec) throws Exception;
    protected abstract List<MlResult> get3Eresult(String[] predictions, DBRecord rec) throws Exception;

	void initialize() {
		mapBetType = new TreeMap<>();
		mapBetType.put(BetType._1T, "tansyo");  // 1,2
		mapBetType.put(BetType._2T, "nirentan"); // 1*, 2*
		mapBetType.put(BetType._3T, "sanrentan"); // 1*, 2*
		mapBetType.put(BetType._2F, "nirenhuku"); // 1*, 2*
		mapBetType.put(BetType._3F, "sanrenhuku"); // 1*, 2*
		mapBetType.put(BetType._2M, "nirentan"); // formation 12,21
		mapBetType.put(BetType._3N, "nirentan"); // formation 12,13
		mapBetType.put(BetType._2N, "sanrentan"); // ３連単1-2-3456, 4点 (통계단위 2자리)
		mapBetType.put(BetType._3P, "sanrentan"); // ３連単1-2-3456, 1-3-2456  6点 無視
		mapBetType.put(BetType._3R, "sanrentan"); // ３連単1-2-3456, 1-3-2456  8点 無視
		mapBetType.put(BetType._3X, "sanrentan"); // ３連単1-3456-2, 2-3456-1 8点 (통계단위 2자리)
        mapBetType.put(BetType._2G, "nirenhuku"); // ２連複 12,13 2点 (통계단위 3자리)
        mapBetType.put(BetType._3G, "sanrenhuku"); // ３連複 1-2-3456 4点 (통계단위 2자리)
        mapBetType.put(BetType._3B, "sanrentan"); // 3連単 123,132 2点 (통계단위 3자리)
        mapBetType.put(BetType._3C, "sanrentan"); // 3連単 123,124 2点 (통계단위 4자리)
        mapBetType.put(BetType._3D, "sanrentan"); // 3連単 123,124,213,214 4点 (통계단위 4자리)
        mapBetType.put(BetType._3E, "sanrentan"); // 3連単 123,124,132,134,142,143 6点 (통계단위 4자리)
		
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
			// 予測組番の桁数チェック
//			if (!ResultHelper.isValidDigits(betTypeStr, predictions)) {
//				continue;
//			}
//			// 予測組番の重複チェック
//			String usedModelNo = prop.getString("used_model_no");
//			//String usedModelNo = prop.getString("modelno");
//			if (!ResultHelper.isUnique(usedModelNo, betTypeStr, predictions)) {
//				continue;
//			}
//			// 予測組番の出力対象チェック
//			if (!ResultHelper.isValidRange(betTypeStr, predictions, tokenKumiban)) {
//				continue;
//			}
			
			if (!ResultHelper.isValidPredictions(betTypeStr, predictions)) {
				continue;
			}
			
//			if (!ResultHelper.isValidPredictionsRange(betTypeStr, predictions, tokenKumiban)) {
//				continue;
//			}
			
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
			// 3P ３連単1-2-3, 1-3-2, 2-1-3, 2-3-1, 3-1-2, 3-2-1 6点
			if (BetType._3P.getValue().equals(betTypeStr)) {
				result.addAll(get3Presult(predictions, dbRec));
			}
			// 3R ３連単1-2-3456, 1-3-2456  8点
			if (BetType._3R.getValue().equals(betTypeStr)) {
				result.addAll(get3Rresult(predictions, dbRec));
			}
			if (BetType._3X.getValue().equals(betTypeStr)) {
				result.addAll(get3Xresult(predictions, dbRec));
			}
            if (BetType._2G.getValue().equals(betTypeStr)) {
                result.addAll(get2Gresult(predictions, dbRec));
            }
            if (BetType._3G.getValue().equals(betTypeStr)) {
                result.addAll(get3Gresult(predictions, dbRec));
            }
            if (BetType._3B.getValue().equals(betTypeStr)) {
                result.addAll(get3Bresult(predictions, dbRec));
            }
            if (BetType._3C.getValue().equals(betTypeStr)) {
                result.addAll(get3Cresult(predictions, dbRec));
            }
            if (BetType._3D.getValue().equals(betTypeStr)) {
                result.addAll(get3Dresult(predictions, dbRec));
            }
            if (BetType._3E.getValue().equals(betTypeStr)) {
                result.addAll(get3Eresult(predictions, dbRec));
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
		MlResult result = createDefaultInner(statBetType, betType, kumiban, rec);
		result.setStatBettype(statBetType.getValue());
		return result;
	}

	protected MlResult createDefaultInner(BetType statBetType, BetType betType, String kumiban, DBRecord rec) throws Exception {
		// 共通レース情報設定
		MlResult result = createDefaultResult(rec);
		
		// bettype
		result.setBettype(betType.getValue());
		// bet_kumiban
		result.setBetKumiban(kumiban);
		// betamt
		result.setBetamt(getDefaultBetamt(betType.getValue()));
		
		// 予想的中確率を設定する(BetTypeを基に計算する)
		//result.setProbability(MathUtil.scale2(probabilityCalculator.calculate(betType.getValue(), rec)));
		result.setProbability(MathUtil.scale2(probabilityCalculator.calculate(statBetType.getValue(), rec)));
		
		// 直前オッズ
		result = setbeforeOdds(result);
		// 確定オッズ
		result = setResultOdds(result);
		
		if (result.getBetOdds() != null) {
			result.setExpectBor(  MathUtil.scale1(probabilityExpCalculator.calculate(betType.getValue(), rec) * result.getBetOdds()) );
		}
		if (result.getBetOddsrank() != null) {
			result.setExpectBork(  MathUtil.scale1(probabilityExpCalculator.calculate(betType.getValue(), rec) * result.getBetOddsrank()) );
		}
		if (result.getResultOdds() != null) {
			result.setExpectRor(  MathUtil.scale1(probabilityExpCalculator.calculate(betType.getValue(), rec) * result.getResultOdds()) );
		}
		if (result.getResultOddsrank() != null) {
			result.setExpectRork(  MathUtil.scale1(probabilityExpCalculator.calculate(betType.getValue(), rec) * result.getResultOddsrank()) );
		}
		
		// レース結果設定 */
		result = setRaceResult(betType, rec, result);
		
		return result;
	}
	
	protected MlResult setbeforeOdds(MlResult result) throws Exception {
		// 直前オッズ
		Odds beforeOdds = beforeOddsProvider.get(result.getYmd(), result.getJyocd(),
				String.valueOf(result.getRaceno()), result.getBettype(), result.getBetKumiban());
		if (beforeOdds != null) {
			result.setBetOdds(beforeOdds.value);
			result.setBetOddsrank(beforeOdds.rank);
		}
		
		return result;
	}
	
	protected MlResult setResultOdds(MlResult result) throws Exception {
		// 確定オッズ
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
		if (rec.getString("prediction4") == null) {
			result.setPredictRank123(rec.getString("prediction1", "") + rec.getString("prediction2", "") + rec.getString("prediction3", ""));
		} else { // ranking알고리즘은 6개의 prediction이 존재한다.
			result.setPredictRank123(rec.getString("prediction1", "") + rec.getString("prediction2", "") + rec.getString("prediction3", "") + rec.getString("prediction4", ""));
		}
		
		return result;
	}
}
