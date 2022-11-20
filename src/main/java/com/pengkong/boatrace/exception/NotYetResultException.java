package com.pengkong.boatrace.exception;

import lombok.Getter;

@Getter
public class NotYetResultException extends Exception {
	private static final long serialVersionUID = 7239260663337272045L;
	
	public NotYetResultException() {
		super();
	}
}
