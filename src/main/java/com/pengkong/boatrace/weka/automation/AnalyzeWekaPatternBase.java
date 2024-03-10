package com.pengkong.boatrace.weka.automation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.weka.automation.prop.BettypePattern;
import com.pengkong.boatrace.weka.automation.prop.Condition;
import com.pengkong.boatrace.weka.automation.prop.FinalizeCondition;
import com.pengkong.boatrace.weka.automation.prop.KumibanPattern;
import com.pengkong.boatrace.weka.automation.prop.Pattern;
import com.pengkong.common.Prop;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.StringUtil;

public class AnalyzeWekaPatternBase {

	PropertyUtil propUtil = PropertyUtil.getInstance();
	List<BettypePattern> listBettypePattern = new ArrayList<>();
	Logger logger = LoggerFactory.getLogger(AnalyzeWekaPatternBase.class);

	public AnalyzeWekaPatternBase(String propertyFilepath) throws Exception {
		propUtil.addFile(propertyFilepath);
		initialize();
	}

	/**
	 * 프로퍼티 파일에 정의된 모든 내용을 로드한다.
	 */
	private void initialize() {
		List<Prop> listPropKumiban = propUtil.getProps("KUMIBAN.");
		// loop for bettype
		for (Prop prop : listPropKumiban) {
			BettypePattern betTypePattern = new BettypePattern();
			String betType = prop.key; // ex) 2T
			betTypePattern.bettype = betType;
			String[] tokenKumiban = prop.value.split(",");
			// loop for kumiban
			for (String kumiban : tokenKumiban) {
				KumibanPattern kumibanPattern = new KumibanPattern(betType, kumiban);
				kumibanPattern.kumiban = kumiban;
				betTypePattern.listKumibanPattern.add(kumibanPattern);
				
				addAllFinalizeConditions(kumibanPattern);
				addKumibanFinalizeConditions(kumibanPattern);
				
				addAllConditions(kumibanPattern);
				addBetTypeAllConditions(kumibanPattern);
				addKumibanConditions(kumibanPattern);
				
				addAllPatterns(kumibanPattern);
				addKumibanAllPatterns(kumibanPattern);
				addKumibanPatterns(kumibanPattern);
			}

			listBettypePattern.add(betTypePattern);
		}
	}

	/**
	 * FIN.COND.ALL.ALL.*のFinalizeConditionを追加する.
	 * 
	 * @param kumibanPtn
	 * @return
	 */
	private KumibanPattern addAllFinalizeConditions(KumibanPattern kumibanPtn) {
		List<FinalizeCondition> result = kumibanPtn.listFinalizeCondition;
		String[] token1 = propUtil.getString("FIN.COND.ALL.ALL.paramPercentIncomeAmtStr","").split(",");
		String[] token2 = propUtil.getString("FIN.COND.ALL.ALL.paramPercentHitRateStr","").split(",");
		String[] token3 = propUtil.getString("FIN.COND.ALL.ALL.paramPercentIncomeRateStr","").split(",");
		String[] token4 = propUtil.getString("FIN.COND.ALL.ALL.paramPercentMinusDaysRateStr","").split(",");
		String[] token5 = propUtil.getString("FIN.COND.ALL.ALL.paramPercentIncomeAmtSlopeStr","").split(",");
		String[] token6 = propUtil.getString("FIN.COND.ALL.ALL.paramPercentRecoverPlusRateStr","").split(",");
		for (String paramPercentBetCntStr : token1) {
			for (String paramPercentHitRateStr : token2) {
				for (String paramPercentIncomeRateStr : token3) {
					for (String paramPercentMinusDaysRateStr : token4) {
						for (String paramPercentIncomeAmtSlopeStr : token5) {
							for (String paramPercentRecoverPlusRateStr : token6) {
								if (!StringUtil.isEmpty(paramPercentBetCntStr) && !StringUtil.isEmpty(paramPercentHitRateStr)
										&& !StringUtil.isEmpty(paramPercentIncomeRateStr) && !StringUtil.isEmpty(paramPercentMinusDaysRateStr)
										&& !StringUtil.isEmpty(paramPercentIncomeAmtSlopeStr) && !StringUtil.isEmpty(paramPercentRecoverPlusRateStr)) {
									FinalizeCondition cond = new FinalizeCondition(paramPercentBetCntStr, paramPercentHitRateStr,
											paramPercentIncomeRateStr, paramPercentMinusDaysRateStr, paramPercentIncomeAmtSlopeStr, paramPercentRecoverPlusRateStr);
									result.add(cond);
								}
							}
						}
					}
				}
			}
		}
		return kumibanPtn;
	}

	/**
	 * FIN.COND.*.*.*のFinalizeConditionを追加する.
	 * 
	 * @param kumibanPtn
	 * @return
	 */
	private KumibanPattern addKumibanFinalizeConditions(KumibanPattern kumibanPtn) {
		List<FinalizeCondition> result = kumibanPtn.listFinalizeCondition;
		String betType = kumibanPtn.betType;
		String kumiban = kumibanPtn.kumiban;
		String paramPercentIncomeAmtStr = propUtil.getString("FIN.COND." + betType + "." + kumiban + ".paramPercentIncomeAmtStr" );
		String paramPercentHitRateStr = propUtil.getString("FIN.COND." + betType + "." + kumiban + ".paramPercentHitRateStr" );
		String paramPercentIncomeRateStr = propUtil.getString("FIN.COND." + betType + "." + kumiban + ".paramPercentIncomeRateStr" );
		String paramPercentMinusDaysRateStr = propUtil.getString("FIN.COND." + betType + "." + kumiban + ".paramPercentMinusDaysRateStr" );
		String paramPercentIncomeAmtSlopeStr = propUtil.getString("FIN.COND." + betType + "." + kumiban + ".paramPercentIncomeAmtSlopeStr" );
		String paramPercentRecoverPlusRateStr = propUtil.getString("FIN.COND." + betType + "." + kumiban + ".paramPercentRecoverPlusRateStr" );
		FinalizeCondition cond = new FinalizeCondition(paramPercentIncomeAmtStr, paramPercentHitRateStr,
				paramPercentIncomeRateStr, paramPercentMinusDaysRateStr, paramPercentIncomeAmtSlopeStr, paramPercentRecoverPlusRateStr);
		if (!cond.isEmpty()) {
			result.add(cond);
		}
		
		return kumibanPtn;
	}
	
	/**
	 * KumibanPatternに対してANZ.COND.*.*.*のPatternを追加する.
	 * 
	 * @param ptn
	 */
	private KumibanPattern addKumibanConditions(KumibanPattern kumibanPtn) {
		List<Condition> result = kumibanPtn.listCondition;
		String betType = kumibanPtn.betType;
		String kumiban = kumibanPtn.kumiban;
		result.addAll(getConditionList("ANZ.COND." + betType + "." + kumiban + "."));

		return kumibanPtn;
	}
	
	/**
	 * KumibanPatternに対してANZ.COND.*.ALL.*のPatternを追加する.
	 * 
	 * @param ptn
	 */
	private KumibanPattern addBetTypeAllConditions(KumibanPattern kumibanPtn) {
		List<Condition> result = kumibanPtn.listCondition;
		String betType = kumibanPtn.betType;
		result.addAll(getConditionList("ANZ.COND." + betType + ".ALL."));

		return kumibanPtn;
	}

	private KumibanPattern addAllConditions(KumibanPattern kumibanPtn) {
		List<Condition> result = kumibanPtn.listCondition;
		result.addAll(getConditionList("ANZ.COND.ALL.ALL."));

		return kumibanPtn;
	}

	/**
	 * KumibanPatternに対してPTN_UNIT.ALL.ALL.*のPatternを追加する.
	 * 
	 * @param ptn
	 */
	private KumibanPattern addAllPatterns(KumibanPattern kumibanPtn) {
		List<Pattern> result = kumibanPtn.listPattern;
		result.addAll(getPatternList("PTN_UNIT.ALL.ALL."));

		return kumibanPtn;
	}

	/**
	 * KumibanPatternに対してPTN_UNIT.*.ALL.*のPatternを追加する.
	 * 
	 * @param ptn
	 */
	private KumibanPattern addKumibanAllPatterns(KumibanPattern kumibanPtn) {
		List<Pattern> result = kumibanPtn.listPattern;
		String betType = kumibanPtn.betType;
		result.addAll(getPatternList("PTN_UNIT." + betType + ".ALL."));

		return kumibanPtn;
	}

	/**
	 * KumibanPatternに対してPTN_UNIT.*.*.*のPatternを追加する.
	 * 
	 * @param ptn
	 */
	private KumibanPattern addKumibanPatterns(KumibanPattern kumibanPtn) {
		List<Pattern> result = kumibanPtn.listPattern;
		String betType = kumibanPtn.betType;
		String kumiban = kumibanPtn.kumiban;
		result.addAll(getPatternList("PTN_UNIT." + betType + "." + kumiban + "."));

		return kumibanPtn;
	}

	private List<Pattern> getPatternList(String propsKey) {
		List<Pattern> result = new ArrayList<>();
		List<Prop> props = propUtil.getProps(propsKey);
		// loop for patterns
		for (Prop prop : props) {
			String ptnName = prop.key; // ex) raceno
			String[] token = prop.value.split(",");
			Pattern ptn = new Pattern();
			ptn.name = ptnName; // ex) race
			ptn.value = token[0]; // ex) race.raceno
			ptn.count = Integer.valueOf(token[1]); // ex) 12
			result.add(ptn);
		}

		return result;
	}

	private List<Condition> getConditionList(String propsKey) {
		List<Condition> result = new ArrayList<>();
		List<Prop> props = propUtil.getProps(propsKey);
		// loop for patterns
		for (Prop prop : props) {
			String name = prop.key; // ex) raceno
			String[] token = prop.value.split(",");
			for (String val : token) {
				Condition cond = new Condition();
				cond.name = name; // ex) paramArrOddsRankStr
				cond.value = val; // ex) 1~100
				result.add(cond);
			}
		}

		return result;
	}
}
