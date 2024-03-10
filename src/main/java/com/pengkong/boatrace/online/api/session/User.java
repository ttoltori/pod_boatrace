package com.pengkong.boatrace.online.api.session;

import lombok.Getter;

@Getter
public class User {
	private String memberNo;
	private String pin;
	private String authPassword;
	private String betPassword;
	
	public User(String memberNo, String pin, String authPassword, String betPassword) {
		super();
		this.memberNo = memberNo;
		this.pin = pin;
		this.authPassword = authPassword;
		this.betPassword = betPassword;
	}
}
