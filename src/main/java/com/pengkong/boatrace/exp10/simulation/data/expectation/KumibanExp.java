package com.pengkong.boatrace.exp10.simulation.data.expectation;

import com.pengkong.boatrace.common.enums.BetType;

public class KumibanExp {
    BetType bettyType; // 1T,2T,2F,3T,3F
    String kumiban; // 組番 (例; "123" or "12" or "1")
    Double bor; // 直前オッズ
    Double probability; // 予想確率
    Double expectaion; // 期待値
}
