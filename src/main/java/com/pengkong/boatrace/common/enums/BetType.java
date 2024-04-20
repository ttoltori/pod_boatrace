package com.pengkong.boatrace.common.enums;

import com.pengkong.boatrace.exp10.odds.provider.AbstractOddsProvider;
import com.pengkong.boatrace.exp10.result.AbstractResultCreator;
import com.pengkong.boatrace.exp10.result.RCDefault;
import com.pengkong.boatrace.exp10.result.ResultHelper;
import com.pengkong.boatrace.exp10.simulation.AbstractSimulationCreator;
import com.pengkong.boatrace.exp10.simulation.SCDefault;

/**
 * !!! bettypec추가시 아래 클래스에도 정의를 추가해주어야한다. !!!
 * {@link AbstractOddsProvider}.createBettypes()
 * {@link ResultHelper}.getPredictions(), isValidDigits(), isValidRange()
 * {@link AbstractResultCreator}
 * {@link RCDefault}
 * {@link AbstractSimulationCreator}.getPredictions()
 * {@link SCDefault}
 * {@link AbstractProbabilityCalculator}
 * 
 * !!! 첫칸 숫자는 통계단위자릿수를 나타내어야한다. 
 *   예) 3N -> 통계단위 3자리 (unique체크에서 중복디지트 걸러내기 위해필요)
 */
public enum BetType {
	_1F("1F"),  // deprecated
	_KF("KF"),  // deprecated
	_3M("3M"),  // deprecated ３連単1-2-3, 1-3-2, 2-1-3, 3-1-2 4点 (통계단위 3자리)
	_3U("3U"),  // deprecated ３連単1-2-3456, 1-3456-2  8点 (통계단위 3자리)
	_3Y("3Y"),  // deprecated ３連単1-3456-2, 2-3456-1 8点 (통계단위 2자리)
    _3A("3A"),  // deprecated ３連単 1-23456-3456
    _2A("2A"),  // deprecated ２連単  1-23456
	_1T("1T"),
	_2F("2F"),
	_2T("2T"),
	_3F("3F"),
	_3T("3T"),
	_2M("2M"),  // ２連単 // formation 12,21
	_3N("3N"),  // ２連単 // formation 12,13
	_2N("2N"),  // ３連単1-2-3456, 4点 (통계단위 2자리)
	_3P("3P"),  // ３連単1-2-3, 1-3-2, 2-1-3, 2-3-1, 3-1-2, 3-2-1 6点 (통계단위 3자리)
	_3R("3R"),  // ３連単1-2-3456, 1-3-2456  8点 (통계단위 3자리)
	_3X("3X"),  // ３連単1-2-3456, 1-3456-2  8点 (통계단위 2자리)
    _2G("2G"),  // ２連複 12,13 2点 (통계단위 3자리)
    _3G("3G"),  // ３連複 1-2-3456 4点 (통계단위 2자리)
	_3B("3B"),  // 3連単 123,132 2点 (통계단위 3자리)
	_3C("3C"),  // 3連単 123,124 2点 (통계단위 4자리)
	_3D("3D"),  // 3連単 123,124,213,214 4点 (통계단위 4자리)
	_3E("3E");  // 3連単 123,124,132,134,142,143 6点 (통계단위 4자리)

	private final String bettype;
	
	private BetType(String bettype) {
		this.bettype = bettype;
	}
	
	public String getValue() {
		return this.bettype;
	}
}
