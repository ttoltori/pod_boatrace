package com.pengkong.boatrace.exp10.result.ranked.filter;

import com.pengkong.boatrace.converter.MlExpectedRec;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.StringUtil;

public class ConditionFilter {
    public boolean isValid(DBRecord rec, MlExpectedRec expectedRec)  {
        try {
            int wind = rec.getInt("wind");
            int[] bOddsranks = {1, 2, 3, 4, 5, 6, 7, 12, 13, 16, 26, 27, 28};
            double bexp = expectedRec.bexp;
            if (wind > 2) {
                return false;
            }
            if ( !StringUtil.contains(expectedRec.bork, bOddsranks) ) {
                return false;
            }
            if (bexp < 1.0) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
