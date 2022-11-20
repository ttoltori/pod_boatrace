package com.pengkong.boatrace.exp10.result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.StrategyTemplate;
import com.pengkong.boatrace.exp10.result.stat.ResultStatBuilder;

/**
 * 投票結果を生成するdefaultクラス
 * 
 * @author ttolt
 *
 */
public class RCManjiOdds extends RCDefault {
	
	Logger logger = LoggerFactory.getLogger(RCManjiOdds.class);

	ResultStatBuilder rsb = ResultStatBuilder.getInstance();

	StrategyTemplate tpl =StrategyTemplate.getInstance();

	double manjiFacotr = -1;
	double manjiOdds = -1;
	
	/** 卍方式。確定オッズ利用 */
	public RCManjiOdds() {
		super();
	}

//	@Override
//	protected int getDefaultBetamt(String betType) {
//			if (manjiFacotr < 0) {
//				Strategy strategy = tpl.getStrategy("ManjiOdds");
//				manjiFacotr = strategy.manjiFactor;
//				manjiOdds = strategy.manjiOdds;
//				if (manjiOdds < 0) {
//					throw new IllegalStateException("invalid manji odds. " + manjiOdds);
//				}
//			}
//
//			double balance = rsb.getCurrentBalance(result.getStatBettype(), result.getBetKumiban(), result.getPattern());
//			double odds = result.getResultOdds(); //確定オッズ
//			if (odds <= 0) {
//				odds = manjiOdds;
//			}
//			//卍公式
//			int betAmt = (int) (balance * manjiFacotr / odds);
//			// 100単位に変換
//			result.setBetamt(BoatUtil.convBetUnit(betAmt));
//	}
}
