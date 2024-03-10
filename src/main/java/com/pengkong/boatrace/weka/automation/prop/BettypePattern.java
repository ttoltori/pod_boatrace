package com.pengkong.boatrace.weka.automation.prop;

import java.util.ArrayList;
import java.util.List;

public class BettypePattern {

	public String bettype;
	
	@Deprecated // 20190908 사용안함.
	public int betcountLimit; 
	
	public List<KumibanPattern> listKumibanPattern = new ArrayList<>();
	
	public BettypePattern() {
	}
	
	public KumibanPattern getKumibanPattern(String kumiban) {
		for (KumibanPattern ptn : listKumibanPattern) {
			if (ptn.kumiban.equals(kumiban)) {
				return ptn;
			}
		}
		
		return null;
	}
}
