package com.pengkong.boatrace.exp10.util;

import java.util.Comparator;
import java.util.List;

import com.pengkong.boatrace.converter.MlExpectedRec;

public class ResultExpHelper {
    /** 리스트에 담긴 MlExpectedRec들을  bexp값이 큰 순으로 소트하고 
     * 그 랭킹을 bexprank에 설정한다.
    */
    public static void setBexpRank(List<MlExpectedRec> list) {
        // bexp 값이 큰 순으로 정렬 (null 값은 마지막으로)
        list.sort(Comparator.comparing(
            (MlExpectedRec rec) -> rec.bexp != null ? rec.bexp : Double.NEGATIVE_INFINITY,
            Comparator.reverseOrder()
        ));
        
        // 랭킹 설정
        for (int i = 0; i < list.size(); i++) {
            list.get(i).bexprank = i + 1;
        }
    }
}
