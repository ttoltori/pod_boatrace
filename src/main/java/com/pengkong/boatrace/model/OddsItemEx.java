package com.pengkong.boatrace.model;

public class OddsItemEx extends OddsTItem {

	public int rank;
	public OddsItemEx(String kumiban, float value) {
		super(kumiban, value);
	}
	public OddsItemEx(String kumiban, float value, int rank) {
		super(kumiban, value);
		this.rank = rank;
	}
}
