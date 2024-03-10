package com.pengkong.boatrace.server.stat;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.server.db.dto.BoatDbRecord;
import com.pengkong.boatrace.server.db.dto.DBRecord;

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
	
	/**
	 * obj에 대해 depth에서 지정한 하부 단계만큼 아래의 자식객체를 반환한다.
	 * 자식객체가 없다면 신규 생성한다.
	 * @param obj 대상 객체
	 * @param depth 지정 하부 단계. keys의 길이와 일치해야한다.
	 * @param keys 자식객체를 취득할 하부 단계별 키값 리스트
	 * @return
	 */
	public StatObject getChildWithCreation(StatObject obj, int depth, String ... keys) {
		if (depth <= 0) {
			return obj;
		}

		StatObject child = obj.getChild(keys[keys.length - depth]);
		if (child == null) {
			child = new BoatStatObject(obj, keys[keys.length - depth]);
		}
		
		//return getChildWithCreation(obj.getChild(keys[keys.length - depth]), depth -1, keys); 
		return getChildWithCreation(child, depth -1, keys);
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
	
	public double[] getLinearIncomeAmt() throws Exception {
		double[] result = new double[listDbRecord.size()];
		for (int i = 0; i < result.length; i++) {
			DBRecord rec = listDbRecord.get(i);
			
			int recHitAmt = rec.getInt("hitamt");
			if (recHitAmt > 0) {
				incomeamt += recHitAmt - rec.getInt("betamt");
			} else {
				incomeamt -= rec.getInt("betamt");
			}
			result[i] = (double)incomeamt;
		}
		
		return result;
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
