package com.pengkong.boatrace.converter;

import java.util.List;

import com.pengkong.boatrace.mybatis.entity.RecOdds;
import com.pengkong.boatrace.mybatis.entity.RecOddsRank10;

public class RecOddsList2RecOddsRankTop10 {
	/**
	 * ！注意 
	 *   1.recOddsListはoddsでソートされたことを前提とする。
	 *   2.recOddsList.size()が10未満の場合は項目数だけを持つ(1T)
	 * 
	 * @param recOddsList
	 * @return
	 */
	public static RecOddsRank10 convert(List<RecOdds> recOddsList) {
		RecOddsRank10 recOddsRank10 = new RecOddsRank10();
		
		// 共通項目
		RecOdds recOdds = recOddsList.get(0);
		recOddsRank10.setYmd(recOdds.getYmd());
		recOddsRank10.setJyocd(recOdds.getJyocd());
		recOddsRank10.setRaceno(recOdds.getRaceno());
		recOddsRank10.setBettype(recOdds.getBettype());
		
		int max = 6;
		if (recOddsList.size() < max) {
			max = recOddsList.size();
		}
		
		for (int i = 0; i < max; i++) {
			if (i == 0) {
				recOddsRank10.setRank1kumiban((recOddsList.get(i).getKumiban()));
				recOddsRank10.setRank1odds((recOddsList.get(i).getOdds()));
			} else if (i == 1) {
				recOddsRank10.setRank2kumiban((recOddsList.get(i).getKumiban()));
				recOddsRank10.setRank2odds((recOddsList.get(i).getOdds()));
			} else if (i == 2) {
				recOddsRank10.setRank3kumiban((recOddsList.get(i).getKumiban()));
				recOddsRank10.setRank3odds((recOddsList.get(i).getOdds()));
			} else if (i == 3) {
				recOddsRank10.setRank4kumiban((recOddsList.get(i).getKumiban()));
				recOddsRank10.setRank4odds((recOddsList.get(i).getOdds()));
			} else if (i == 4) {
				recOddsRank10.setRank5kumiban((recOddsList.get(i).getKumiban()));
				recOddsRank10.setRank5odds((recOddsList.get(i).getOdds()));
			} else if (i == 5) {
				recOddsRank10.setRank6kumiban((recOddsList.get(i).getKumiban()));
				recOddsRank10.setRank6odds((recOddsList.get(i).getOdds()));
			} else if (i == 6) {
				recOddsRank10.setRank7kumiban((recOddsList.get(i).getKumiban()));
				recOddsRank10.setRank7odds((recOddsList.get(i).getOdds()));
			} else if (i == 7) {
				recOddsRank10.setRank8kumiban((recOddsList.get(i).getKumiban()));
				recOddsRank10.setRank8odds((recOddsList.get(i).getOdds()));
			} else if (i == 8) {
				recOddsRank10.setRank9kumiban((recOddsList.get(i).getKumiban()));
				recOddsRank10.setRank9odds((recOddsList.get(i).getOdds()));
			} else if (i == 9) {
				recOddsRank10.setRank10kumiban((recOddsList.get(i).getKumiban()));
				recOddsRank10.setRank10odds((recOddsList.get(i).getOdds()));
			}
		}
		
		return recOddsRank10;
	}
}
