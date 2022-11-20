package com.pengkong.boatrace.exception;

public class FatalException extends Exception {
	private static final long serialVersionUID = 1L;

	public FatalException(String message) {
		super(message);
	}
	public FatalException(String message, Throwable e) {
		super(message, e);
	}
}
