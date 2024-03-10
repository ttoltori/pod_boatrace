package com.pengkong.boatrace.weka.automation.prop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.util.Combinations;

import com.pengkong.common.PropertyUtil;
import com.pengkong.common.StringUtil;

public class KumibanPattern {

	public String betType;
	public String kumiban;
	
	public List<Pattern> listPattern = new ArrayList<>();
	public List<Condition> listCondition = new ArrayList<>();
	public List<FinalizeCondition> listFinalizeCondition = new ArrayList<>();
	private PropertyUtil propUtil = PropertyUtil.getInstance();
	private HashMap<String, String> mapRequired = new HashMap<>();
	public KumibanPattern(String betType, String kumiban) {
		this.betType = betType;
		this.kumiban = kumiban;
		initialize();
	}

	private void initialize() {
		String required = propUtil.getString("PTN_UNIT.REQUIRED");
		if (StringUtil.isEmpty(required)) {
			return;
		}
		
		String[] token = required.split(",");
		for (String str : token) {
			mapRequired.putIfAbsent(str, str);
		}
	}
	

	/**
	 * stat_ml_ptn_final테이블로부터 취득한 패턴명과 프로퍼티로부터 취득한 패턴값을 조합하여 리스트를 반환한다. 
	 * AnalyzeWakePattern에서 사용
	 * @param listFinalizedPtnNames DB취득값리스트 (pattern_name필드 포함)
	 * @return
	 * @throws Exception
	 */
	public List<PatternComposition> getPatternCompositionList(List<HashMap<String, ?>> listFinalizedPtnNames) throws Exception{
		List<PatternComposition> listPtnComp = new ArrayList<>();
		for (HashMap<String, ?> entryPtnName : listFinalizedPtnNames) {
			PatternComposition ptnComp = new PatternComposition();
			ptnComp.betType = this.betType;
			ptnComp.kumiban = this.kumiban;
			ptnComp.name = (String)entryPtnName.get("pattern_name");
			String[] tokenPtnUnit = ptnComp.name.split("_");
			int compCount = 1;
			String ptnValue = "";
			boolean isDeprecatedPtnUnit = false;
			for (String ptnUnit : tokenPtnUnit) {
				String ptnValueItem = propUtil.getString("PTN_UNIT.ALL.ALL." + ptnUnit);
				if (StringUtil.isEmpty(ptnValueItem)) {
					isDeprecatedPtnUnit = true;
					break;
				}
				String[] tokenPtnValue = ptnValueItem.split(",");
				ptnValue += tokenPtnValue[0] + " || '_' || ";
				compCount *= Integer.valueOf(tokenPtnValue[1]);
			}
			
			// 대상외가 된 패턴명 유닛을 포함하고 있다면 해당 패턴명은 스킵하자.
			if (isDeprecatedPtnUnit) {
				continue;
			}
			
			ptnComp.value = ptnValue.substring(0,ptnValue.length() - 11);
			ptnComp.compositionCount = compCount;
			
			listPtnComp.add(ptnComp);
		}
		
		return listPtnComp;
	}
	
	/**
	 * 프로퍼티로부터 취득한 패턴명과 패턴값의 리스트를 반환한다. OutputWakePattern에서 사용 
	 * @param minDepth
	 * @param maxDepth
	 * @return
	 * @throws Exception
	 */
	public List<PatternComposition> getPatternCompositionList(int minDepth, int maxDepth) throws Exception{
		List<PatternComposition> listPtnComp = new ArrayList<>();
		
		if (maxDepth > listPattern.size()) {
			throw new Exception("too large depth");
		}
		
		for (int i = minDepth; i <= maxDepth; i++) {
			Combinations combinations = new Combinations(listPattern.size(), i);
			Iterator<int[]> it = combinations.iterator();
			while(it.hasNext()) {
				int[] arrIdx = it.next();
				PatternComposition ptnComp = createPatternComposition(arrIdx);
				if (isValidPatternComposition(ptnComp)) {
					listPtnComp.add(ptnComp);	
				}
			}
		}
		
		return listPtnComp;
	}
	
	public List<FinalizeCondition> getFinalizeConditions() {
		return listFinalizeCondition;
	}
	
	private boolean isValidPatternComposition(PatternComposition ptnComp){
		if ( hasRequiredPattern(ptnComp)
			) {
			return true;
		}
		
		return false;
	}

	private boolean hasRequiredPattern(PatternComposition ptnComp) {
		// 필수패턴유닛이 미정의라면 본체크`는 스킵한다.
		if (mapRequired.isEmpty()) {
			return true;
		}
		
		String[] token = ptnComp.name.split("_");
		int cnt = 0;
		for (String ptn : token) {
			if (mapRequired.containsKey(ptn)) {
				cnt++;
			}
		}
		
		if ((token.length - cnt) <= 1 ) {
			return true;
		}
		
		return false;
	}
	
	private PatternComposition createPatternComposition(int[] arrIdx) {
		PatternComposition result = new PatternComposition();
		result.betType = this.betType;
		result.kumiban = this.kumiban;
		String name = "";
		String value = "";
		int compCount = 1;
		for (int i : arrIdx) {
			Pattern ptn = listPattern.get(i);
			name += ptn.name + "_";
			value += ptn.value + " || '_' || ";
			compCount *= ptn.count;
		}
		result.name = name.substring(0, name.length() - 1);
		result.value = value.substring(0,value.length() - 11);
		result.compositionCount = compCount;
		return result;
	}
}
