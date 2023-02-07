package com.pengkong.boatrace.exp10.util;

import lombok.Getter;

@Getter
public class ResultName {
    private String resultType;
    private String betType;
    private String kumiban;
    private String patternId;
    private String pattern;
    private String modelNo;
    private String resultno;
    
    public ResultName(String resultName) {
        String[] token = resultName.split("_");
        this.resultType = token[0];
        this.betType = token[1];
        this.kumiban = token[2];
        this.patternId = token[3];
        this.pattern = token[4];
        this.modelNo = token[5];
        this.resultno = token[6];
    }
    
    public String getGrades() {
       if (this.resultType.equals("1")) {
           return "ip,G3";
       } else {
           return "SG,G1,G2";
       }
    }
}
