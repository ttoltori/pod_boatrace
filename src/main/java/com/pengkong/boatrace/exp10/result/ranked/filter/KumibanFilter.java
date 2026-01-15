package com.pengkong.boatrace.exp10.result.ranked.filter;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.common.StringUtil;

public class KumibanFilter {
    private final MLPropertyUtil prop = MLPropertyUtil.getInstance();

    public boolean isValid(String betType, String kumiban) {
        String[] kumibanToken = kumiban.split("");
        // 組番をfilterする文字列. *-*-* 
        String[] fileterToken= prop.getString("kumiban").split(Delimeter.DASH.getValue());
        if (StringUtil.contains(betType, "1T")) {
            if (!fileterToken[0].equals("*") && !fileterToken[0].equals(kumibanToken[0])) {
                return false;
            }
        } else if (StringUtil.contains(betType, "2T", "2F")) {
            if (!fileterToken[0].equals("*") && !fileterToken[0].equals(kumibanToken[0])) {
                return false;
            }
            if (!fileterToken[1].equals("*") && !fileterToken[1].equals(kumibanToken[1])) {
                return false;
            }
        } else if (StringUtil.contains(betType, "3T", "3F")) {
            if (!fileterToken[0].equals("*") && !fileterToken[0].equals(kumibanToken[0])) {
                return false;
            }
            if (!fileterToken[1].equals("*") && !fileterToken[1].equals(kumibanToken[1])) {
                return false;
            }
            if (!fileterToken[2].equals("*") && !fileterToken[2].equals(kumibanToken[2])) {
                return false;
            }
        }

        return true;
    }
}
