package com.pengkong.boatrace.exception;

import lombok.Getter;

@Getter
public class NotEnoughBalanceException extends Exception {
	private static final long serialVersionUID = -1062783857043490463L;
	
	private int requested;
	private int lack;
	
	public NotEnoughBalanceException(int requested, int lack) {
		super();
		this.requested = requested;
		this.lack = lack;
	}
}
