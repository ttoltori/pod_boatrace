package com.pengkong.boatrace.weka.automation.prop;

import java.util.HashMap;
import java.util.Map.Entry;

import com.pengkong.common.PropertyUtil;

/**
 * 같은 승식,구미방내에서 의미상 중복되는 패턴을 체크해준다.
 * 반드시 승식,구미방별로 초기화되어야 한다. 
 * @author qwerty
 *
 */
// 패턴유닛에서부터 중복을 고려하므로 이제는 필요없는 클래스이다.
@Deprecated
public class DuplicatePatternChecker {

	private PropertyUtil propUtil;
	private HashMap<String, DuplicatePattern> mapDuplicate;
	private HashMap<String, String> mapAllPattern;
	private boolean isInitialized = false;
	public DuplicatePatternChecker(PropertyUtil propUtil) {
		this.propUtil = propUtil;
		initialize();
	}
	
	public void initialize() {
		mapDuplicate = new HashMap<>();
		mapAllPattern = new HashMap<>();
		String strDupl = propUtil.getString("DUPLICATE");
		String[] tokenPtnPair = strDupl.split(",");
		for (String ptnPair : tokenPtnPair) {
			DuplicatePattern ptn;
			String[] tokenPtn = ptnPair.split("::");
			if (!mapDuplicate.containsKey(tokenPtn[0])) {
				ptn = new DuplicatePattern();
				ptn.listPattern.add(tokenPtn[0]);
				mapDuplicate.put(tokenPtn[0], ptn);
			}
			mapDuplicate.get(tokenPtn[0]).listPattern.add(tokenPtn[1]);
			mapAllPattern.putIfAbsent(tokenPtn[0], tokenPtn[0]);
			mapAllPattern.putIfAbsent(tokenPtn[1], tokenPtn[1]);
		}
		isInitialized = true;
	}
	
	public boolean isDuplicate(String patternName) {
		if (!isInitialized) {
			initialize();
		}
		
		if (mapAllPattern.containsKey(patternName)) {
			for (Entry<String, DuplicatePattern> entry : mapDuplicate.entrySet()) {
				DuplicatePattern ptn = entry.getValue();
				for (String strPtn : ptn.listPattern) {
					if (strPtn.equals(patternName)) {
						if (ptn.isExist) {
							return true;
						} else {
							ptn.isExist = true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		String propFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/weka_pattern.properties";
		PropertyUtil propUtil= new PropertyUtil();
		try {
			propUtil.addFile(propFilepath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DuplicatePatternChecker checker = new DuplicatePatternChecker(propUtil);
		checker.initialize();
		System.out.println(checker.isDuplicate("level12"));
		System.out.println(checker.isDuplicate("level123_level3"));
		System.out.println(checker.isDuplicate("level12_level2"));
		System.out.println(checker.isDuplicate("level123"));
	}
}
