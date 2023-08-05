package com.pengkong.boatrace.exp10.result.stat;

import java.util.List;

public class BorkTermUnit {
	public int betAmt;
	public int hitAmt;
	public BorkTermUnit(int betAmt, int hitAmt) {
		super();
		this.betAmt = betAmt;
		this.hitAmt = hitAmt;
	}
	
	public static boolean isPlus(List<BorkTermUnit> units) {
		int balance = 0;
		for (BorkTermUnit unit : units) {
			balance += (unit.hitAmt - unit.betAmt); 
		}
		
		return (balance > 0);
	}
}
