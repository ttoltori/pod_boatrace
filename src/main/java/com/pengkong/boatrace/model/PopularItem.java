package com.pengkong.boatrace.model;

public class PopularItem {
	public static final int SUPER_POPULAR = 5;
	public static final int POPULAR = 3;
	public static final int NO_POPULAR = 0;
	public int popularity = NO_POPULAR;
	public String type;
	public int rank;
//	public int percent;
	public float percent;
	public PopularItem(String type, int rank, float percent) {
		super();
		this.type = type;
		this.rank = rank;
		this.percent = percent;
	}
}
