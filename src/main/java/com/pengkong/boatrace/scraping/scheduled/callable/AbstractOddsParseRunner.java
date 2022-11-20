package com.pengkong.boatrace.scraping.scheduled.callable;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.pengkong.boatrace.exception.FatalException;
import com.pengkong.boatrace.scraping.scheduled.OddsParseResult;

import lombok.Getter;

public abstract class AbstractOddsParseRunner implements Callable<OddsParseResult>{

	/** url for parsing */
	@Getter
	String url;

	public AbstractOddsParseRunner(String url) {
		this.url = url;
	}

	@Override
	public OddsParseResult call() throws IOException, FatalException {
		OddsParseResult result = execute();
		
		return result;
	}
	
	/**
	 * 実行メソッド
	 */
	abstract public OddsParseResult execute() throws IOException, FatalException;

}
