package com.pengkong.boatrace.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.mybatis.entity.StatWakuWin;
import com.pengkong.boatrace.service.stat.WakuValue;

public class StatWakuWin2WakuValueList {

	public StatWakuWin2WakuValueList() {
	}

	/** 枠別の当該枠出走回数一覧を取得する */
	public static List<WakuValue> convertRacerRunCount(StatWakuWin statWakuWin) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", new BigDecimal(statWakuWin.getRacerruncount1())));
		results.add(new WakuValue("2", new BigDecimal(statWakuWin.getRacerruncount2())));
		results.add(new WakuValue("3", new BigDecimal(statWakuWin.getRacerruncount3())));
		results.add(new WakuValue("4", new BigDecimal(statWakuWin.getRacerruncount4())));
		results.add(new WakuValue("5", new BigDecimal(statWakuWin.getRacerruncount5())));
		results.add(new WakuValue("6", new BigDecimal(statWakuWin.getRacerruncount6())));
		
		return results;
	}
	
	/** 枠別の当該枠の選手勝率一覧を取得する */
	public static List<WakuValue> convertRacerWiningRate(StatWakuWin statWakuWin) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuWin.getRacerwiningrate1()));
		results.add(new WakuValue("2", statWakuWin.getRacerwiningrate2()));
		results.add(new WakuValue("3", statWakuWin.getRacerwiningrate3()));
		results.add(new WakuValue("4", statWakuWin.getRacerwiningrate4()));
		results.add(new WakuValue("5", statWakuWin.getRacerwiningrate5()));
		results.add(new WakuValue("6", statWakuWin.getRacerwiningrate6()));
		
		return results;
	}
	
	/** 枠別の当該枠の選手２連帯率一覧を取得する */
	public static List<WakuValue> convertRacer2WiningRate(StatWakuWin statWakuWin) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuWin.getRacer2winingrate1()));
		results.add(new WakuValue("2", statWakuWin.getRacer2winingrate2()));
		results.add(new WakuValue("3", statWakuWin.getRacer2winingrate3()));
		results.add(new WakuValue("4", statWakuWin.getRacer2winingrate4()));
		results.add(new WakuValue("5", statWakuWin.getRacer2winingrate5()));
		results.add(new WakuValue("6", statWakuWin.getRacer2winingrate6()));
		
		return results;
	}
	
	/** 枠別の当該枠の選手3連帯率一覧を取得する */
	public static List<WakuValue> convertRacer3WiningRate(StatWakuWin statWakuWin) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuWin.getRacer3winingrate1()));
		results.add(new WakuValue("2", statWakuWin.getRacer3winingrate2()));
		results.add(new WakuValue("3", statWakuWin.getRacer3winingrate3()));
		results.add(new WakuValue("4", statWakuWin.getRacer3winingrate4()));
		results.add(new WakuValue("5", statWakuWin.getRacer3winingrate5()));
		results.add(new WakuValue("6", statWakuWin.getRacer3winingrate6()));
		
		return results;
	}

	/** 枠別の当該枠モータ出走回数一覧を取得する */
	public static List<WakuValue> convertMotorRunCount(StatWakuWin statWakuWin) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", new BigDecimal(statWakuWin.getMotorruncount1())));
		results.add(new WakuValue("2", new BigDecimal(statWakuWin.getMotorruncount2())));
		results.add(new WakuValue("3", new BigDecimal(statWakuWin.getMotorruncount3())));
		results.add(new WakuValue("4", new BigDecimal(statWakuWin.getMotorruncount4())));
		results.add(new WakuValue("5", new BigDecimal(statWakuWin.getMotorruncount5())));
		results.add(new WakuValue("6", new BigDecimal(statWakuWin.getMotorruncount6())));
		
		return results;
	}
	
	/** 枠別の当該枠のモータ勝率一覧を取得する */
	public static List<WakuValue> convertMotorWiningRate(StatWakuWin statWakuWin) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuWin.getMotorwiningrate1()));
		results.add(new WakuValue("2", statWakuWin.getMotorwiningrate2()));
		results.add(new WakuValue("3", statWakuWin.getMotorwiningrate3()));
		results.add(new WakuValue("4", statWakuWin.getMotorwiningrate4()));
		results.add(new WakuValue("5", statWakuWin.getMotorwiningrate5()));
		results.add(new WakuValue("6", statWakuWin.getMotorwiningrate6()));
		
		return results;
	}
	
	/** 枠別の当該枠のモータ２連帯率一覧を取得する */
	public static List<WakuValue> convertMotor2WiningRate(StatWakuWin statWakuWin) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuWin.getMotor2winingrate1()));
		results.add(new WakuValue("2", statWakuWin.getMotor2winingrate2()));
		results.add(new WakuValue("3", statWakuWin.getMotor2winingrate3()));
		results.add(new WakuValue("4", statWakuWin.getMotor2winingrate4()));
		results.add(new WakuValue("5", statWakuWin.getMotor2winingrate5()));
		results.add(new WakuValue("6", statWakuWin.getMotor2winingrate6()));
		
		return results;
	}
	
	/** 枠別の当該枠のモータ3連帯率一覧を取得する */
	public static List<WakuValue> convertMotor3WiningRate(StatWakuWin statWakuWin) {
		List<WakuValue> results = new ArrayList<>();
		results.add(new WakuValue("1", statWakuWin.getMotor3winingrate1()));
		results.add(new WakuValue("2", statWakuWin.getMotor3winingrate2()));
		results.add(new WakuValue("3", statWakuWin.getMotor3winingrate3()));
		results.add(new WakuValue("4", statWakuWin.getMotor3winingrate4()));
		results.add(new WakuValue("5", statWakuWin.getMotor3winingrate5()));
		results.add(new WakuValue("6", statWakuWin.getMotor3winingrate6()));
		
		return results;
	}
}
