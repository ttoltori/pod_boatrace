package com.pengkong.boatrace.online.exception;

/**
 * 投票API呼び出しエラー
 * @author ttolt
 *
 */
@SuppressWarnings("serial")
public class ApiException extends Exception {
	
	public ApiException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public ApiException(Throwable e) {
		super(e);
	}
}
