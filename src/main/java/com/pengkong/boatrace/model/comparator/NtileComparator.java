package com.pengkong.boatrace.model.comparator;

import java.util.Comparator;

import com.pengkong.boatrace.model.Ntile;

public class NtileComparator implements Comparator<Ntile> {

	private String varName;

	public NtileComparator(String varName) {
		this.varName = varName;
	}

	@Override
	public int compare(Ntile o1, Ntile o2) {
		if (varName.equals("betCnt")) {
			return o2.betCnt - o1.betCnt;
		} else if (varName.equals("hitCnt")) {
			return o2.hitCnt - o1.hitCnt;
		} else if (varName.equals("hitRate")) {
			return Float.compare(o2.hitRate, o1.hitRate);
		} else if (varName.equals("incomeRate")) {
			return Float.compare(o2.incomeRate, o1.incomeRate);
		} else if (varName.equals("balance")) {
			return o2.balance - o1.balance;
		} else {
			return 0;
		}
	}
}
