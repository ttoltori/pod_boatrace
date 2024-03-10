package com.pengkong.boatrace.model;

import com.pengkong.boatrace.exception.NotEnoughBalanceException;

import lombok.Getter;

@Getter
public class Wallet {
	private volatile int balance = 0;
	
	/** 入金 */
	public void deposit(int amount) {
		balance += amount;
	}
	
	public int withdrawl(int amount) throws NotEnoughBalanceException{
		if (amount > balance) {
			throw new NotEnoughBalanceException(amount, amount - balance);
		}
		balance -= amount;
		
		return amount;
	}
}
