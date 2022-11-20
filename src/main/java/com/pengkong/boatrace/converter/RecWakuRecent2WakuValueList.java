package com.pengkong.boatrace.converter;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.mybatis.entity.RecWakuRecent;
import com.pengkong.boatrace.service.stat.WakuValue;

public class RecWakuRecent2WakuValueList {

	public RecWakuRecent2WakuValueList() {
	}

	/** 枠別の全国勝率一覧を取得する */
	public static List<WakuValue> convertNationWiningRate(RecWakuRecent recWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", recWakuRecent.getRecentnationwiningrate1()));
		results.add(new WakuValue("2", recWakuRecent.getRecentnationwiningrate2()));
		results.add(new WakuValue("3", recWakuRecent.getRecentnationwiningrate3()));
		results.add(new WakuValue("4", recWakuRecent.getRecentnationwiningrate4()));
		results.add(new WakuValue("5", recWakuRecent.getRecentnationwiningrate5()));
		results.add(new WakuValue("6", recWakuRecent.getRecentnationwiningrate6()));
		
		return results;
	}
	
	/** 枠別の全国２連帯率一覧を取得する */
	public static List<WakuValue> convertNation2WiningRate(RecWakuRecent recWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", recWakuRecent.getRecentnation2winingrate1()));
		results.add(new WakuValue("2", recWakuRecent.getRecentnation2winingrate2()));
		results.add(new WakuValue("3", recWakuRecent.getRecentnation2winingrate3()));
		results.add(new WakuValue("4", recWakuRecent.getRecentnation2winingrate4()));
		results.add(new WakuValue("5", recWakuRecent.getRecentnation2winingrate5()));
		results.add(new WakuValue("6", recWakuRecent.getRecentnation2winingrate6()));
		
		return results;
	}

	/** 枠別の全国３連帯率一覧を取得する */
	public static List<WakuValue> convertNation3WiningRate(RecWakuRecent recWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", recWakuRecent.getRecentnation3winingrate1()));
		results.add(new WakuValue("2", recWakuRecent.getRecentnation3winingrate2()));
		results.add(new WakuValue("3", recWakuRecent.getRecentnation3winingrate3()));
		results.add(new WakuValue("4", recWakuRecent.getRecentnation3winingrate4()));
		results.add(new WakuValue("5", recWakuRecent.getRecentnation3winingrate5()));
		results.add(new WakuValue("6", recWakuRecent.getRecentnation3winingrate6()));
		
		return results;
	}
	
	/** 枠別の当地勝率一覧を取得する */
	public static List<WakuValue> convertLocalWiningRate(RecWakuRecent recWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", recWakuRecent.getRecentlocalwiningrate1()));
		results.add(new WakuValue("2", recWakuRecent.getRecentlocalwiningrate2()));
		results.add(new WakuValue("3", recWakuRecent.getRecentlocalwiningrate3()));
		results.add(new WakuValue("4", recWakuRecent.getRecentlocalwiningrate4()));
		results.add(new WakuValue("5", recWakuRecent.getRecentlocalwiningrate5()));
		results.add(new WakuValue("6", recWakuRecent.getRecentlocalwiningrate6()));
		
		return results;
	}
	
	/** 枠別の当地２連帯率一覧を取得する */
	public static List<WakuValue> convertLocal2WiningRate(RecWakuRecent recWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", recWakuRecent.getRecentlocal2winingrate1()));
		results.add(new WakuValue("2", recWakuRecent.getRecentlocal2winingrate2()));
		results.add(new WakuValue("3", recWakuRecent.getRecentlocal2winingrate3()));
		results.add(new WakuValue("4", recWakuRecent.getRecentlocal2winingrate4()));
		results.add(new WakuValue("5", recWakuRecent.getRecentlocal2winingrate5()));
		results.add(new WakuValue("6", recWakuRecent.getRecentlocal2winingrate6()));
		
		return results;
	}

	/** 枠別の当地３連帯率一覧を取得する */
	public static List<WakuValue> convertLocal3WiningRate(RecWakuRecent recWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", recWakuRecent.getRecentlocal3winingrate1()));
		results.add(new WakuValue("2", recWakuRecent.getRecentlocal3winingrate2()));
		results.add(new WakuValue("3", recWakuRecent.getRecentlocal3winingrate3()));
		results.add(new WakuValue("4", recWakuRecent.getRecentlocal3winingrate4()));
		results.add(new WakuValue("5", recWakuRecent.getRecentlocal3winingrate5()));
		results.add(new WakuValue("6", recWakuRecent.getRecentlocal3winingrate6()));
		
		return results;
	}

	/** 枠別の平均ST一覧を取得する */
	public static List<WakuValue> convertAvgSt(RecWakuRecent recWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", recWakuRecent.getRecentavgst1()));
		results.add(new WakuValue("2", recWakuRecent.getRecentavgst2()));
		results.add(new WakuValue("3", recWakuRecent.getRecentavgst3()));
		results.add(new WakuValue("4", recWakuRecent.getRecentavgst4()));
		results.add(new WakuValue("5", recWakuRecent.getRecentavgst5()));
		results.add(new WakuValue("6", recWakuRecent.getRecentavgst6()));
		
		return results;
	}
	
	/** 枠別の平均タイム一覧を取得する */
	public static List<WakuValue> convertAvgTime(RecWakuRecent recWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", recWakuRecent.getRecentavgtime1()));
		results.add(new WakuValue("2", recWakuRecent.getRecentavgtime2()));
		results.add(new WakuValue("3", recWakuRecent.getRecentavgtime3()));
		results.add(new WakuValue("4", recWakuRecent.getRecentavgtime4()));
		results.add(new WakuValue("5", recWakuRecent.getRecentavgtime5()));
		results.add(new WakuValue("6", recWakuRecent.getRecentavgtime6()));
		
		return results;
	}
	
	/** 枠別のモータ２連帯率一覧を取得する */
	public static List<WakuValue> convertMotor2WiningRate(RecWakuRecent recWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", recWakuRecent.getRecentmotor2winingrate1()));
		results.add(new WakuValue("2", recWakuRecent.getRecentmotor2winingrate2()));
		results.add(new WakuValue("3", recWakuRecent.getRecentmotor2winingrate3()));
		results.add(new WakuValue("4", recWakuRecent.getRecentmotor2winingrate4()));
		results.add(new WakuValue("5", recWakuRecent.getRecentmotor2winingrate5()));
		results.add(new WakuValue("6", recWakuRecent.getRecentmotor2winingrate6()));
		
		return results;
	}

	/** 枠別のモータ３連帯率一覧を取得する */
	public static List<WakuValue> convertMotor3WiningRate(RecWakuRecent recWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", recWakuRecent.getRecentmotor3winingrate1()));
		results.add(new WakuValue("2", recWakuRecent.getRecentmotor3winingrate2()));
		results.add(new WakuValue("3", recWakuRecent.getRecentmotor3winingrate3()));
		results.add(new WakuValue("4", recWakuRecent.getRecentmotor3winingrate4()));
		results.add(new WakuValue("5", recWakuRecent.getRecentmotor3winingrate5()));
		results.add(new WakuValue("6", recWakuRecent.getRecentmotor3winingrate6()));
		
		return results;
	}
	
}
