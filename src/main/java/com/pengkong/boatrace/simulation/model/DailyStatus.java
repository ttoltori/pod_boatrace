package com.pengkong.boatrace.simulation.model;

public class DailyStatus {
	public static final int STATUS_CLOSED = 0;
	public static final int STATUS_OPEN = 1;
	public static final int STATUS_BANKRUPT = 2;
	public static final int STATUS_MARTIN_RESET_RESTRICT = 3;
	public static final int STATUS_MARTIN_AMOUNT_RESTRICT = 4;
	public int status = STATUS_CLOSED;
}
