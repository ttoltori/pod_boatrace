package com.pengkong.boatrace.exp10.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.enums.RangeValidationType;
import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.provider.AbstractOddsProvider;
import com.pengkong.boatrace.exp10.odds.provider.BeforeOddsProvider;
import com.pengkong.boatrace.exp10.odds.provider.OddsProviderInterface;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.result.AbstractResultCreator;
import com.pengkong.boatrace.exp10.simulation.adjustment.BonusProvider;
import com.pengkong.boatrace.exp10.simulation.adjustment.SimulationPatternProvider;
import com.pengkong.boatrace.exp10.simulation.classifier.MultiModelDBClassifierCache;
import com.pengkong.boatrace.exp10.simulation.evaluation.AbstractEvaluationLoader;
import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;
import com.pengkong.boatrace.exp10.simulation.evaluation.EvaluationLoaderFactory;
import com.pengkong.boatrace.exp10.simulation.evaluation.EvaluationSet;
import com.pengkong.boatrace.exp10.simulation.pattern.IEvaluationPatternMatcher;
import com.pengkong.boatrace.exp10.simulation.pattern.PatternMatcherFactory;
import com.pengkong.boatrace.exp10.simulation.probability.calculator.AbstractProbabilityCalculator;
import com.pengkong.boatrace.exp10.simulation.probability.calculator.ProbabilityCalculatorFactory;
import com.pengkong.boatrace.exp10.simulation.range.validation.RangeValidationProvider;
import com.pengkong.boatrace.mybatis.entity.MlClassification;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.StringUtil;
import com.pengkong.common.collection.HashMapList;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractSimulationCreator {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/** 対象evaluationリストからパタンmatchingを行う */
	protected AbstractEvaluationLoader evLoader;
	
	protected IEvaluationPatternMatcher evMatcher;

	/** DBから複数モデルのMLClassificationを取得する */
	protected MultiModelDBClassifierCache mmdClassifier;

	/*----- 継承クラスへの受け渡しデータ start---------------*/
	/** key = modelno, value=MlClassification */
	HashMap<String, MlClassification> mapClassification;

	/** key = predictions, value=[Evaluation...] */
	HashMapList<Evaluation> mapPredictionEvaluations = null;

	List<String> predictionList = null;

	@Setter
	AbstractResultCreator resultCreator;
	
	String rangeSelector;
	String patternSelector;
	String predictionSelector;
	String modelSelector;

	/** ML予測値から当該betypeに対する予想的中確率を計算 */
	AbstractProbabilityCalculator probCalculator;
	
	/** 直前オッズ、予想確率の最適範囲チェック */
	RangeValidationProvider rangeValidator;
	
	/** 予想確率、直前オッズ、直前オッズランクでbonusを適用する
	 * 適用値の定義はevaluationファイル⇒propertyの優先順で適用される */
	BonusProvider bonusProvider;
	
	/** 2回目のbonus provider 20221110 group fileの上段部定義を反映する 
	 * */
	BonusProvider secondBonusProvider;
	
	/** 直前オッズprovider */
	AbstractOddsProvider beforeOddsProvider;
	
	/** simulation専用パタン適用 */
	SimulationPatternProvider simulationPatternProvider;

	/** 初期処理フラグ */
	private boolean isInitialized = false;

	public AbstractSimulationCreator() {
		super();
	}

	protected void initialize() throws Exception {
		if (!isInitialized) {
			rangeSelector = prop.getString("range_selector");
			patternSelector = prop.getString("pattern_selector");
			predictionSelector = prop.getString("prediction_selector");
			modelSelector = prop.getString("model_selector");

			// Bonus適用
			bonusProvider = new BonusProvider("bonus_pr", "bonus_bor", "bonus_bork");
            secondBonusProvider = new BonusProvider("bonus_pr2", "bonus_bor2", "bonus_bork2");

			// Evaluationチェック
			evLoader = EvaluationLoaderFactory.create();
			evMatcher = PatternMatcherFactory.create(prop.getString("pattern_matcher", "x"), evLoader);
			
			// 最適範囲チェック
			beforeOddsProvider = new BeforeOddsProvider();
			probCalculator = ProbabilityCalculatorFactory.create();
			rangeValidator = new RangeValidationProvider();
			

			// DBClassifierを生成しておく
			mmdClassifier = new MultiModelDBClassifierCache(
					new EvaluationSet(evLoader.getEvaluations()).getUniqueList("modelno"));

			// simulationパタン
			simulationPatternProvider = new SimulationPatternProvider();

			isInitialized = true;
		}
	}

	public void destroy() throws Exception {
	}

	public AbstractEvaluationLoader getEvLoader() throws Exception {
		if (!isInitialized) {
			initialize();
		}
		
		return evLoader;
	}
	
	/**
	 * DBRecordのレースに対して、 
	 * ①.複数のMlClassificationに対して
	 * ②-1.各MlClassificationに対して対してパタンマッチングしたい複数のEvaluationを取得する。
	 * ②-2.複数のEvaluationから戦略(pattern_selector)によって一つだけEvaluationをselectする（MlClassification:Evaluationは1:1関係）
	 * ③.②の結果の複数のEvaluationを組番毎に分類する（組番：Evaluationは1:N関係）
	 * ④.③で得られて複数組番から戦略(kumiban_selector)によって複数の組番をselectする
	 * ⑤-1.④で得られた複数組番のそれぞれに対して戦略(model_selector)によって代表Evaluationを一つだけselectする
	 * ⑤-2.④で得られた複数組番のそれぞれに対して⑤-1で得られたEvaluationのMlClassificationを付加してResultCreatorを実行する
	 * 
	 * @param rec           DBRecord
	 * @param resultCreator ResultCreatorインスタンス
	 * @return [MlResult...]
	 * @throws Exception
	 */
	public List<MlResult> execute(DBRecord dbRec) throws Exception {
		if (!isInitialized) {
			initialize();
		}

		List<MlResult> result = new ArrayList<>();

		// 複数モデルからclassificationを取得
		List<MlClassification> listClf = mmdClassifier.classify(dbRec.getString("ymd"), dbRec.getString("jyocd"),
				dbRec.getInteger("raceno").toString());

		// 20230630 31601.31602はml_classificationがない場合がある。
		if (listClf == null) {
			return result;
		}
		
		// working変数初期化
		// key = modelno, value=MlClassification
		mapClassification = new HashMap<>();
		listClf.stream().forEach((clf) -> {
			mapClassification.put(clf.getModelno(), clf);
		});
		mapPredictionEvaluations = null;
		predictionList = null;

		String[] tokenBettype = prop.getString("bettype").split(Delimeter.COMMA.getValue());
		for (String betType : tokenBettype) {
			// 1T
			if (BetType._1T.getValue().equals(betType)) {
				result.addAll(get1Tresult(dbRec));
			}
			// 2T
			if (BetType._2T.getValue().equals(betType)) {
				result.addAll(get2Tresult(dbRec));
			}
			// 2F
			if (BetType._2F.getValue().equals(betType)) {
				result.addAll(get2Fresult(dbRec));
			}
			// 3T
			if (BetType._3T.getValue().equals(betType)) {
				result.addAll(get3Tresult(dbRec));
			}
			// 3F
			if (BetType._3F.getValue().equals(betType)) {
				result.addAll(get3Fresult(dbRec));
			}
			// 2M 2T formation
			if (BetType._2M.getValue().equals(betType)) {
				result.addAll(get2Mresult(dbRec));
			}
			// 3M 3T formation
			if (BetType._3M.getValue().equals(betType)) {
				result.addAll(get3Mresult(dbRec));
			}
			// 3N 2T formation
			if (BetType._3N.getValue().equals(betType)) {
				result.addAll(get3Nresult(dbRec));
			}
			// 2N 3T formation
			if (BetType._2N.getValue().equals(betType)) {
				result.addAll(get2Nresult(dbRec));
			}
            // 3P ３連単1-2-3, 1-3-2, 2-1-3, 2-3-1, 3-1-2, 3-2-1 6点
            if (BetType._3P.getValue().equals(betType)) {
                result.addAll(get3Presult(dbRec));
            }
            // 3R ３連単1-2-3456, 1-3-2456  8点
            if (BetType._3R.getValue().equals(betType)) {
                result.addAll(get3Rresult(dbRec));
            }
            // 3U ３連単1-2-3456, 1-3456-2  8点
            if (BetType._3U.getValue().equals(betType)) {
                result.addAll(get3Uresult(dbRec));
            }
            if (BetType._3X.getValue().equals(betType)) {
                result.addAll(get3Xresult(dbRec));
            }
            if (BetType._3Y.getValue().equals(betType)) {
                result.addAll(get3Yresult(dbRec));
            }
            if (BetType._3A.getValue().equals(betType)) {
                result.addAll(get3Aresult(dbRec));
            }
            if (BetType._2A.getValue().equals(betType)) {
                result.addAll(get2Aresult(dbRec));
            }
            if (BetType._2G.getValue().equals(betType)) {
                result.addAll(get2Gresult(dbRec));
            }
            if (BetType._3G.getValue().equals(betType)) {
                result.addAll(get3Gresult(dbRec));
            }
		}

		return result;
	}

	abstract List<MlResult> get1Tresult(DBRecord rec) throws Exception;
	abstract List<MlResult> get2Tresult(DBRecord rec) throws Exception;
	abstract List<MlResult> get3Tresult(DBRecord rec) throws Exception;
	abstract List<MlResult> get2Fresult(DBRecord rec) throws Exception;
	abstract List<MlResult> get3Fresult(DBRecord rec) throws Exception;
	abstract List<MlResult> get2Mresult(DBRecord rec) throws Exception;
	abstract List<MlResult> get3Mresult(DBRecord rec) throws Exception;
	abstract List<MlResult> get3Nresult(DBRecord rec) throws Exception;
	abstract List<MlResult> get2Nresult(DBRecord rec) throws Exception;
    abstract List<MlResult> get3Presult(DBRecord rec) throws Exception;
    abstract List<MlResult> get3Rresult(DBRecord rec) throws Exception;
    abstract List<MlResult> get3Uresult(DBRecord rec) throws Exception;
    abstract List<MlResult> get3Xresult(DBRecord rec) throws Exception;
    abstract List<MlResult> get3Yresult(DBRecord rec) throws Exception;
    abstract List<MlResult> get3Aresult(DBRecord rec) throws Exception;
    abstract List<MlResult> get2Aresult(DBRecord rec) throws Exception;
    abstract List<MlResult> get2Gresult(DBRecord rec) throws Exception;
    abstract List<MlResult> get3Gresult(DBRecord rec) throws Exception;

	/**
	 * ①.複数のMlClassificationに対して
	 * ②-1.各MlClassificationに対して対してパタンマッチングした複数のEvaluationを取得する。
	 * ②-2.複数のEvaluationから戦略(pattern_selector)によって一つだけEvaluationをselectする（MlClassification:Evaluationは1:1関係）
	 * ③.②の結果の複数のEvaluationを組番毎に分類する（組番：Evaluationは1:N関係）
	 * 
	 * @param listClf 複数のMlClassification
	 * @param rec     パタンマッチング対象DBRecord
	 * @param betType パタンマッチング対象bettype
	 * @throws Exception
	 */
	HashMapList<Evaluation> createMapPredictionEvaluations(String betType, String patternSelector, DBRecord rec)
			throws Exception {
		
		// model別pattern matching 抽出
		// 同一bettype, kumibanに対して異なる複数Evaluationを保持する
		HashMapList<Evaluation> mapPredictionEvaluations = new HashMapList<>();

		// 複数MlClassificationをループする
		for (MlClassification clf : mapClassification.values()) {
			String prediction = getPredictions(betType, clf);
			rec = putClassification(rec, clf);

			// パタンにマッチする複数Evaluationを取得する
			List<Evaluation> evaluations = evMatcher.match(betType, prediction, clf.getModelno(), rec);

			if (evaluations.size() <= 0) {
				continue;
			}
			
			if (evaluations.size() == 1) {
				mapPredictionEvaluations.addItem(prediction, evaluations.get(0));
				continue;
			}
			
			if (patternSelector.equals("x")) {
				mapPredictionEvaluations.addItemAll(prediction, evaluations);
				continue;
			}
			
			// 複数Evaluationから特定値を比較して最も高い一つだけselectする
			Evaluation eval = new EvaluationSet(evaluations).selectMostHigh(patternSelector);
			mapPredictionEvaluations.addItem(prediction, eval);
		}
		
		return mapPredictionEvaluations;
	}

	/** 統計単位用のMLで予測した組番を取得する。(ex) 2Fの場合、betKumiban = 12, predictions = 21） 
	 * 3X,3Y追加 2022/9/2
	 * */
	String getPredictions(String betType, MlClassification clf) {
		if (BetType._1T.getValue().equals(betType) || BetType._1F.getValue().equals(betType)
		        || BetType._3A.getValue().equals(betType) || BetType._2A.getValue().equals(betType) ) {
			return getPredictRank123(clf).substring(0, 1);
		} else if (BetType._2T.getValue().equals(betType) || BetType._2F.getValue().equals(betType)
				|| BetType._2M.getValue().equals(betType) || BetType._2N.getValue().equals(betType)
				|| BetType._3X.getValue().equals(betType) || BetType._3Y.getValue().equals(betType)
				) {
			return getPredictRank123(clf).substring(0, 2);
		} else {
			return getPredictRank123(clf).substring(0, 3);
		}
	}

	String getPredictRank123(MlClassification clf) {
		return clf.getPrediction1() + clf.getPrediction2() + clf.getPrediction3();
	}

	/** 
	 * clafficationから取得したpredictionからkumiban変換して直前オッズを取得する。
	 * @param rec DBRecord
	 * @param betType BetType ex) 2F
	 * @param prediction ex) 12  or 21
	 * @return Odds
	 * @throws Exception
	 */
	Odds getBeforeOdds(DBRecord rec, BetType betType, String prediction) throws Exception {
		String kumiban;
		if (betType.equals(BetType._2F) || betType.equals(BetType._3F)) {
			kumiban = StringUtil.sortString(prediction);
		} else {
			kumiban = prediction;
		}
		return beforeOddsProvider.get(rec.getString("ymd"), rec.getString("jyocd"),
				String.valueOf(rec.getInteger("raceno")), betType.getValue(), kumiban);
	}
	
	/** DBRecordにMlClassificationの情報を設定する */
	DBRecord putClassification(DBRecord rec, MlClassification clf) {
		rec.put("prediction1", clf.getPrediction1());
		rec.put("prediction2", clf.getPrediction2());
		rec.put("prediction3", clf.getPrediction3());
		rec.put("probability1", clf.getProbability1());
		rec.put("probability2", clf.getProbability2());
		rec.put("probability3", clf.getProbability3());
		rec.put("probabilities1", clf.getProbabilities1());
		rec.put("probabilities2", clf.getProbabilities2());
		rec.put("probabilities3", clf.getProbabilities3());

		return rec;
	}
	
	/**
	 * bonus factor range stringを取得する
	 * @param key bonus適用対象のキー ex) bonus_pr or bonus_bor
	 * @param eval Evaluation (EvaluationLoaderdで各Evaluationに値を設定する
	 * @return ex) 1.0~4.3=1
	 */
	String getBonusFactorString(String key, Evaluation eval) {
		String factor;
		
		factor = eval.get(key);
		// evaluationの設定値を優先する
		if (!factor.equals(RangeValidationType.NONE.getValue())) {
			return factor;
		}
		
		// プロパティから取得する
		return prop.getString(key);
	}
	
	/**
	 * 複数組番がそれぞれ保持している複数Evaluationから最も高い値を持っている組番を取得するクラス.
	 */
	class EvaluationPredictionSelector {
		/** key = kumiban */
		HashMapList<Evaluation> mapListKumiban;

		public EvaluationPredictionSelector(HashMapList<Evaluation> mapListKumiban) {
			this.mapListKumiban = mapListKumiban;
		}

		/**
		 * 複数組番がそれぞれ保持している複数Evaluationから最も高い値を持っている組番を取得する
		 * 
		 * @param selectField all=全て, skip=選択なし, その他=値を比較するEvaluationのfield名
		 * @return 組番リスト. データ未存在の場合は空リスト
		 */
		public List<String> select(String selectField) {
			int predictionCount = mapListKumiban.keySet().size();
			if (predictionCount < 2 || selectField.equals("x")) {
				return new ArrayList<>(mapListKumiban.keySet());
			}
			
			if (selectField.equals("skip")) {
				// 空リスト
				return new ArrayList<>();
			}

			// 組番に関係なく全てのEvaluationを取得
			List<Evaluation> totalEvals = mapListKumiban.getAllItems();
			
			// 全組番に対して最も高い値を持つEvaluationを一つだけselectする.
			Evaluation selected = new EvaluationSet(totalEvals).selectMostHigh(selectField);
			return new ArrayList<>(Arrays.asList(selected.get("kumiban")));
		}
	}
}
