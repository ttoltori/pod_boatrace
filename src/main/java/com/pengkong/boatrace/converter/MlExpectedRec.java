package com.pengkong.boatrace.converter;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.server.db.dto.DBRecord;

/** MlExpectedPlus에서 배열로 담긴 값들을 한개의 레코드 단위로 변환한다. */
public class MlExpectedRec {
	public String modelno;
	public String ymd;
	public String jyocd;
	public short raceno;
	public String bkumiban; // 予測期待値で黒字となった３連単組番
	public Double bprob; // 予測確率
	public Double bor; // 予測オッズ
	public Integer bork; // 予測ランキング
	public Double bexp; // 予測期待値
	public Integer bcnt; // 予測期待値が黒字となった組番の数
	public String rkumiban; // 確定期待値で黒字となった３連単組番
	public Double rprob; // 確定確率
	public Double ror; // 確定オッズ
	public Integer rork; // 確定ランキング
	public Double rexp; // 確定期待値
	public Integer rcnt; // 確定期待値が黒字となった組番の数

    public static MlExpectedRec create(DBRecord dbRec, int idx) {
        MlExpectedRec rec = new MlExpectedRec();
        rec.modelno = dbRec.getString("modelno");
        rec.ymd = dbRec.getString("ymd");
        rec.jyocd = dbRec.getString("jyocd");
        rec.raceno = dbRec.getShort("raceno");
        rec.bkumiban = ((String[])dbRec.get("bkumiban"))[idx];
        rec.bprob = ((Double[])dbRec.get("bprob"))[idx];
        rec.bor = ((Double[])dbRec.get("bor"))[idx];
        rec.bork = ((Integer[])dbRec.get("bork"))[idx];
        rec.bexp = ((Double[])dbRec.get("bexp"))[idx];
        rec.bcnt = ((Integer[])dbRec.get("bcnt"))[idx];
        rec.rkumiban = ((String[])dbRec.get("rkumiban"))[idx];
        rec.rprob = ((Double[])dbRec.get("rprob"))[idx];
        rec.ror = ((Double[])dbRec.get("ror"))[idx];
        rec.rork = ((Integer[])dbRec.get("rork"))[idx];
        rec.rexp = ((Double[])dbRec.get("rexp"))[idx];
        rec.rcnt = ((Integer[])dbRec.get("rcnt"))[idx];
        return rec;
    }

    public static List<MlExpectedRec> create(DBRecord mlExpectedPlus) {
        List<MlExpectedRec> list = new ArrayList<>();
        String[] bkumibans = mlExpectedPlus.getStringArray("bkumiban");
        for (int i = 0; i < bkumibans.length; i++) {
            list.add(create(mlExpectedPlus, i));
        }
        return list;
    }
}
