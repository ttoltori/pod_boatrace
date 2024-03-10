package com.pengkong.boatrace.expr06;

import com.pengkong.boatrace.weka.automation.DefineFileParser;

public class RankArffCreator extends com.pengkong.boatrace.exp02.RankArffCreator {

	public RankArffCreator(DefineFileParser def) {
		super(def);
		// rank1,2,3,4
		rankCount = 4;
	}
}
