package com.pengkong.boatrace.server.stat;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.server.db.dto.BoatDbRecord;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.server.util.BoatMath;

public class BoatStatObject extends StatObject {
	public int betcnt = 0;
	public int betamt = 0;
	public int hitcnt = 0;
	public int hitamt = 0;
	public int incomeamt = 0;
	public int minusBalanceCount = 0;
	public int plusBalanceCount = 0;
	public int minusBalanceChangedCount = 0; 
	public int plusBalanceChangedCount = 0; 
	
	private int lastIncomeAmt = 0;
	
	public List<BoatDbRecord> listDbRecord = new ArrayList<>();
	
	public BoatStatObject(StatObject parent, String key) {
		super(parent, key);
	}

	public BoatStatObject(String key) {
		super(key);
	}
	
	public double getBetCntRatePerParent() {
		return (double)this.betcnt / (double)((BoatStatObject)parent).betcnt;
	}
	
	public double getHitAmtRatePerParent() {
		return (double)this.hitamt / (double)((BoatStatObject)parent).hitamt;
	}

	public double getHitRate() {
		return (double)this.hitcnt / (double)this.betcnt;
	}
	
	public double getIncomeRate() {
		return (double)this.hitamt / (double)this.betamt;
	}
	
	public double getMinusBalanceCntRate() {
		return (double)this.minusBalanceCount / (double)this.betcnt;
	}
	
	/**
	 * 평균값을 반환한다. 
	 * @param column 대상 컬럼명
	 * @return
	 */
	public double getAverage(String column) {
		return BoatMath.getAverage(listDbRecord, column);
	}
	
	/**
	 * 평균편차를 반환한다.
	 * @param column 대상 컬럼명
	 * @return
	 */
	public double getDeviation(String column) {
		return BoatMath.getDeviation(listDbRecord, column);
	}
	
	@Override
	public void addRecord(Object rec) throws Exception {
		listDbRecord.add((BoatDbRecord)rec);
		super.addRecord(rec);
	}
	
	@Override
	public void addRecord(Object rec, StatObject child) throws Exception {
		if (child != null) {
			listDbRecord.add((BoatDbRecord)rec); 
		}
		super.addRecord(rec, child);
	}
	
	@Override
	void calculate(Object record) throws Exception {
		DBRecord rec = (DBRecord) record;  
		betcnt++;
		int recBetAmt = rec.getInt("betamt");
		int recHitAmt = rec.getInt("hitamt");
		
		betamt += recBetAmt;
		int hity = rec.getInt("hity");
		if (hity == 1) {
			hitcnt++;
			hitamt += recHitAmt;
			incomeamt += (recHitAmt - recBetAmt);
		} else {
			incomeamt -= recBetAmt;
		}
		
		if (incomeamt < 0) {
			minusBalanceCount++;
			if (lastIncomeAmt >= 0) {
				minusBalanceChangedCount++;
			}
		} else {
			plusBalanceCount++;
			if (lastIncomeAmt < 0) {
				plusBalanceChangedCount++;
			}
		}
		lastIncomeAmt = incomeamt;
	}
	
	public static void main(String[] args) {
		BoatStatObject o1 = new BoatStatObject("o1");
		BoatStatObject o2 = new BoatStatObject("o2");
		BoatStatObject o3 = new BoatStatObject("o3");
		BoatStatObject o4 = new BoatStatObject("o4");
		
		o1.addChild("o2", o2);
		o2.addChild("o3", o3);
		o3.addChild("o4", o4);

		StatObject child = o1.getChild(o1, 3, new String[] {"o2","o3","o4"}); 
		System.out.println( child.key);
		child = o1.getChild(o1, 2, new String[] {"o2", "o3"}); 
		System.out.println( child.key);
		child = o1.getChild(o1, 1, new String[] {"o2"}); 
		System.out.println( child.key);
	}
}
