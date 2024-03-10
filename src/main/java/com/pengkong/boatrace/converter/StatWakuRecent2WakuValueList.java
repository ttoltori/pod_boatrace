package com.pengkong.boatrace.converter;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.mybatis.entity.StatWakuRecent;
import com.pengkong.boatrace.service.stat.WakuValue;

public class StatWakuRecent2WakuValueList {

	public StatWakuRecent2WakuValueList() {
	}

	/** 枠別の全国勝率一覧を取得する */
	public static List<WakuValue> convertNationWiningRate(StatWakuRecent statWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuRecent.getStatnationwiningrate1()));
		results.add(new WakuValue("2", statWakuRecent.getStatnationwiningrate2()));
		results.add(new WakuValue("3", statWakuRecent.getStatnationwiningrate3()));
		results.add(new WakuValue("4", statWakuRecent.getStatnationwiningrate4()));
		results.add(new WakuValue("5", statWakuRecent.getStatnationwiningrate5()));
		results.add(new WakuValue("6", statWakuRecent.getStatnationwiningrate6()));
		
		return results;
	}
	
	/** 枠別の全国２連帯率一覧を取得する */
	public static List<WakuValue> convertNation2WiningRate(StatWakuRecent statWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuRecent.getStatnation2winingrate1()));
		results.add(new WakuValue("2", statWakuRecent.getStatnation2winingrate2()));
		results.add(new WakuValue("3", statWakuRecent.getStatnation2winingrate3()));
		results.add(new WakuValue("4", statWakuRecent.getStatnation2winingrate4()));
		results.add(new WakuValue("5", statWakuRecent.getStatnation2winingrate5()));
		results.add(new WakuValue("6", statWakuRecent.getStatnation2winingrate6()));
		
		return results;
	}

	/** 枠別の全国３連帯率一覧を取得する */
	public static List<WakuValue> convertNation3WiningRate(StatWakuRecent statWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuRecent.getStatnation3winingrate1()));
		results.add(new WakuValue("2", statWakuRecent.getStatnation3winingrate2()));
		results.add(new WakuValue("3", statWakuRecent.getStatnation3winingrate3()));
		results.add(new WakuValue("4", statWakuRecent.getStatnation3winingrate4()));
		results.add(new WakuValue("5", statWakuRecent.getStatnation3winingrate5()));
		results.add(new WakuValue("6", statWakuRecent.getStatnation3winingrate6()));
		
		return results;
	}
	
	/** 枠別の当地勝率一覧を取得する */
	public static List<WakuValue> convertLocalWiningRate(StatWakuRecent statWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuRecent.getStatlocalwiningrate1()));
		results.add(new WakuValue("2", statWakuRecent.getStatlocalwiningrate2()));
		results.add(new WakuValue("3", statWakuRecent.getStatlocalwiningrate3()));
		results.add(new WakuValue("4", statWakuRecent.getStatlocalwiningrate4()));
		results.add(new WakuValue("5", statWakuRecent.getStatlocalwiningrate5()));
		results.add(new WakuValue("6", statWakuRecent.getStatlocalwiningrate6()));
		
		return results;
	}
	
	/** 枠別の当地２連帯率一覧を取得する */
	public static List<WakuValue> convertLocal2WiningRate(StatWakuRecent statWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuRecent.getStatlocal2winingrate1()));
		results.add(new WakuValue("2", statWakuRecent.getStatlocal2winingrate2()));
		results.add(new WakuValue("3", statWakuRecent.getStatlocal2winingrate3()));
		results.add(new WakuValue("4", statWakuRecent.getStatlocal2winingrate4()));
		results.add(new WakuValue("5", statWakuRecent.getStatlocal2winingrate5()));
		results.add(new WakuValue("6", statWakuRecent.getStatlocal2winingrate6()));
		
		return results;
	}

	/** 枠別の当地３連帯率一覧を取得する */
	public static List<WakuValue> convertLocal3WiningRate(StatWakuRecent statWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuRecent.getStatlocal3winingrate1()));
		results.add(new WakuValue("2", statWakuRecent.getStatlocal3winingrate2()));
		results.add(new WakuValue("3", statWakuRecent.getStatlocal3winingrate3()));
		results.add(new WakuValue("4", statWakuRecent.getStatlocal3winingrate4()));
		results.add(new WakuValue("5", statWakuRecent.getStatlocal3winingrate5()));
		results.add(new WakuValue("6", statWakuRecent.getStatlocal3winingrate6()));
		
		return results;
	}

	/** 枠別のST一覧を取得する */
	public static List<WakuValue> convertStart(StatWakuRecent statWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuRecent.getStatstart1()));
		results.add(new WakuValue("2", statWakuRecent.getStatstart2()));
		results.add(new WakuValue("3", statWakuRecent.getStatstart3()));
		results.add(new WakuValue("4", statWakuRecent.getStatstart4()));
		results.add(new WakuValue("5", statWakuRecent.getStatstart5()));
		results.add(new WakuValue("6", statWakuRecent.getStatstart6()));
		
		return results;
	}
	
	/** 枠別のモータ勝率一覧を取得する */
	public static List<WakuValue> convertMotorWiningRate(StatWakuRecent statWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuRecent.getStatmotorwiningrate1()));
		results.add(new WakuValue("2", statWakuRecent.getStatmotorwiningrate2()));
		results.add(new WakuValue("3", statWakuRecent.getStatmotorwiningrate3()));
		results.add(new WakuValue("4", statWakuRecent.getStatmotorwiningrate4()));
		results.add(new WakuValue("5", statWakuRecent.getStatmotorwiningrate5()));
		results.add(new WakuValue("6", statWakuRecent.getStatmotorwiningrate6()));
		
		return results;
	}
	
	/** 枠別のモータ２連帯率一覧を取得する */
	public static List<WakuValue> convertMotor2WiningRate(StatWakuRecent statWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuRecent.getStatmotor2winingrate1()));
		results.add(new WakuValue("2", statWakuRecent.getStatmotor2winingrate2()));
		results.add(new WakuValue("3", statWakuRecent.getStatmotor2winingrate3()));
		results.add(new WakuValue("4", statWakuRecent.getStatmotor2winingrate4()));
		results.add(new WakuValue("5", statWakuRecent.getStatmotor2winingrate5()));
		results.add(new WakuValue("6", statWakuRecent.getStatmotor2winingrate6()));
		
		return results;
	}

	/** 枠別のモータ３連帯率一覧を取得する */
	public static List<WakuValue> convertMotor3WiningRate(StatWakuRecent statWakuRecent) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuRecent.getStatmotor3winingrate1()));
		results.add(new WakuValue("2", statWakuRecent.getStatmotor3winingrate2()));
		results.add(new WakuValue("3", statWakuRecent.getStatmotor3winingrate3()));
		results.add(new WakuValue("4", statWakuRecent.getStatmotor3winingrate4()));
		results.add(new WakuValue("5", statWakuRecent.getStatmotor3winingrate5()));
		results.add(new WakuValue("6", statWakuRecent.getStatmotor3winingrate6()));
		
		return results;
	}
}
