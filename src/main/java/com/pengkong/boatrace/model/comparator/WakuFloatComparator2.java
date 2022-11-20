package com.pengkong.boatrace.model.comparator;

import java.util.Comparator;

import com.pengkong.boatrace.model.Waku;

public class WakuFloatComparator2 implements Comparator<Waku> {

	protected String varName;

	public WakuFloatComparator2(String varName) {
		this.varName = varName;
	}

	@Override
	public int compare(Waku o1, Waku o2) {
		return Float.compare(getFloatValue(o1, varName), getFloatValue(o2, varName));
	}

	protected float getFloatValue(Waku waku, String varName) {
		if (varName.equals("nationWiningRate")) {
			
			return from1Digit(waku.nationWiningRate);
		} else if (varName.equals("nation2WiningRate")) {
			
			return from2Digit(waku.nation2WiningRate);
		} else if (varName.equals("nation3WiningRate")) {
			
			return from2Digit(waku.nation3WiningRate);
		} else if (varName.equals("localWiningRate")) {
			
			return from1Digit(waku.localWiningRate);
		} else if (varName.equals("local2WiningRate")) {
			
			return from2Digit(waku.local2WiningRate);
		} else if (varName.equals("local3WiningRate")) {
			
			return from2Digit(waku.local3WiningRate);
		} else if (varName.equals("motor2WiningRate")) {
			
			return from2Digit(waku.motor2WiningRate);
		} else if (varName.equals("motor3WiningRate")) {
			
			return from2Digit(waku.motor3WiningRate);
		} else if (varName.equals("boat2WiningRate")) {
			
			return from2Digit(waku.boat2WiningRate);
		} else if (varName.equals("boat3WiningRate")) {
			
			return from2Digit(waku.boat3WiningRate);
		} else if (varName.equals("startExhibit")) {
			
			return waku.startExhibit;
		} else if (varName.equals("exhibitTime")) {
			
			return waku.exhibitTime;
		} else if (varName.equals("basePoint")) {
			
			return waku.basePoint;
		} else {
			
			return 0;
		}
	}
	
	protected float from1Digit(float value) {
		return (int) (value*10);
	}
	protected float from2Digit(float value) {
		return (int)(value);
	}
	
	public static void main(String[] args) {
		float a = 3.78f;
		System.out.println( (int)(a * 10) );
	}
}
