package com.pengkong.boatrace.exp02;

import com.pengkong.boatrace.common.DBCol;
import com.pengkong.boatrace.common.RankType;
import com.pengkong.boatrace.server.db.dto.RankDbRecord;

public class WakuPatternManager {

	private static class InstanceHolder {
		private static final WakuPatternManager INSTANCE = new WakuPatternManager();
	}
	
	public static WakuPatternManager getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	public WakuPatternManager() {
	}

	/**
	 * 랭크123예측치와 필터레코드를 체크하여 해당되는 필터레코드를 반환한다.
	 * @param rank123 랭크123예측치
	 * @param recFilter 필터레크드
	 * @return
	 */
	public boolean isValid(String rank123, RankDbRecord recFilter) {
		String rankType = recFilter.getString(DBCol.RANKTYPE);
		String betKumiban = recFilter.getString(DBCol.BET_KUMIBAN);
		String waku = recFilter.getString(DBCol.WAKU);
		if (rankType.equals(RankType.NORM_1T_23)) {
			return checkNorm1t_23(rank123, betKumiban, waku);
		} else if (rankType.equals(RankType.NORM_2T_3)) {
			return checkNorm2t_3(rank123, betKumiban, waku);
		} else if (rankType.equals(RankType.NORM_2T_34)) {
			return checkNorm2t_34(rank123, betKumiban, waku);
		} else if (rankType.equals(RankType.NORM_2F_12)) {
			return checkNorm2F_12(rank123, waku);
		} else if (rankType.equals(RankType.NORM_2F_123)) {
			return checkNorm2F_123(rank123, waku);
		} else if (rankType.equals(RankType.NORM_3T_0)) {
			return checkNorm3T_0(rank123, betKumiban);
		} else if (rankType.equals(RankType.NORM_3T_4)) {
			return checkNorm3T_4(rank123, betKumiban, waku);
		} else if (rankType.equals(RankType.NORM_3F_123)) {
			return checkNorm3F_123(rank123, waku);
		} else if (rankType.equals(RankType.NORM_3F_1234)) {
			return checkNorm3F_1234(rank123, waku);
		} else if (rankType.equals(RankType.FORM_2T2_23)) {
			return checkForm2T2_23(rank123, betKumiban, waku);
		} else if (rankType.equals(RankType.FORM_2T2_23)) {
			return checkForm2T2_23(rank123, betKumiban, waku);
		} else if (rankType.equals(RankType.FORM_3T4_3)) {
			return checkForm3T4_3(rank123, betKumiban);
		} else if (rankType.equals(RankType.FORM_3T8_23)) {
			return checkForm3T8_23(rank123, betKumiban, waku);
		}
		
		return false;
	}

	private boolean checkNorm1t_23(String rank123, String betKumiban, String waku) {
		return (rank123.substring(0,1).equals(betKumiban) && rank123.substring(1,3).equals(waku) );
	}
	
	private boolean checkNorm2t_3(String rank123, String betKumiban, String waku) {
		return (rank123.substring(0,2).equals(betKumiban) && rank123.substring(2,3).equals(waku)); 
	}

	private boolean checkNorm2t_34(String rank123, String betKumiban, String waku) {
		return (rank123.substring(0,2).equals(betKumiban) && rank123.substring(2,4).equals(waku)); 
	}
	
	private boolean checkNorm2F_12(String rank123, String waku) {
		return (rank123.substring(0,2).equals(waku));
	}
	
	private boolean checkNorm2F_123(String rank123, String waku) {
		return (rank123.substring(0,3).equals(waku));
	}
	
	private boolean checkNorm3F_123(String rank123, String waku) {
		return (rank123.substring(0,3).equals(waku));
	}

	private boolean checkNorm3F_1234(String rank123, String waku) {
		return (rank123.substring(0,4).equals(waku));
	}
	
	private boolean checkNorm3T_0(String rank123, String betKumiban) {
		return (rank123.substring(0,3).equals(betKumiban));
	}
	
	private boolean checkNorm3T_4(String rank123, String betKumiban, String waku) {
		return ((rank123.substring(0,3).equals(betKumiban)) && rank123.substring(3,4).equals(waku));
	}
	
	private boolean checkForm2T2_23(String rank123, String betKumiban, String waku) {
		return (rank123.substring(0, 1).equals(betKumiban.substring(0, 1))
				&& (     rank123.substring(1, 2).equals(waku.substring(0, 1))
					  || rank123.substring(1, 2).equals(waku.substring(1, 2))
				   )
			   );
	}
	
	private boolean checkForm3T4_3(String rank123, String betKumiban) {
		return (rank123.substring(0, 2).equals(betKumiban.substring(0, 2))
			   );
	}
	
	private boolean checkForm3T8_23(String rank123, String betKumiban, String waku) {
		return (rank123.substring(0, 1).equals(betKumiban.substring(0, 1)) &&
				 ( rank123.substring(1, 2).equals(waku.substring(0, 1)) || 
				   rank123.substring(1, 2).equals(waku.substring(1, 2))	 
				 )
			   );
	}
	
	public static void main(String[] args) {
		
		System.out.println("123".substring(2,3));
	}
}
