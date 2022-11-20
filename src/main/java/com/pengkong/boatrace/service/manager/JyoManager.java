package com.pengkong.boatrace.service.manager;

import java.util.HashMap;

public class JyoManager {
	private static HashMap<String, String> jyoMapByName;
	private static HashMap<String, String> jyoMapByCd;
	private static HashMap<String, String> jyoMapByCd2;
	private static HashMap<String, String> branchMapByJyoCd;
	private static HashMap<String, String> jyoMapByBranch;
	static {
		initialize();
	}

	private static void initialize() {
		jyoMapByName = new HashMap<>();
		jyoMapByName.put("桐生", "01");
		jyoMapByName.put("戸田", "02");
		jyoMapByName.put("江戸川", "03");
		jyoMapByName.put("平和島", "04");
		jyoMapByName.put("多摩川", "05");
		jyoMapByName.put("浜名湖", "06");
		jyoMapByName.put("蒲郡", "07");
		jyoMapByName.put("常滑", "08");
		jyoMapByName.put("津", "09");
		jyoMapByName.put("三国", "10");
		jyoMapByName.put("びわこ", "11");
		jyoMapByName.put("住之江", "12");
		jyoMapByName.put("尼崎", "13");
		jyoMapByName.put("鳴門", "14");
		jyoMapByName.put("丸亀", "15");
		jyoMapByName.put("児島", "16");
		jyoMapByName.put("宮島", "17");
		jyoMapByName.put("徳山", "18");
		jyoMapByName.put("下関", "19");
		jyoMapByName.put("若松", "20");
		jyoMapByName.put("芦屋", "21");
		jyoMapByName.put("福岡", "22");
		jyoMapByName.put("唐津", "23");
		jyoMapByName.put("大村", "24");

		jyoMapByCd = new HashMap<>();
		jyoMapByCd.put("01", "桐生");
		jyoMapByCd.put("02", "戸田");
		jyoMapByCd.put("03", "江戸川");
		jyoMapByCd.put("04", "平和島");
		jyoMapByCd.put("05", "多摩川");
		jyoMapByCd.put("06", "浜名湖");
		jyoMapByCd.put("07", "蒲郡");
		jyoMapByCd.put("08", "常滑");
		jyoMapByCd.put("09", "津");
		jyoMapByCd.put("10", "三国");
		jyoMapByCd.put("11", "びわこ");
		jyoMapByCd.put("12", "住之江");
		jyoMapByCd.put("13", "尼崎");
		jyoMapByCd.put("14", "鳴門");
		jyoMapByCd.put("15", "丸亀");
		jyoMapByCd.put("16", "児島");
		jyoMapByCd.put("17", "宮島");
		jyoMapByCd.put("18", "徳山");
		jyoMapByCd.put("19", "下関");
		jyoMapByCd.put("20", "若松");
		jyoMapByCd.put("21", "芦屋");
		jyoMapByCd.put("22", "福岡");
		jyoMapByCd.put("23", "唐津");
		jyoMapByCd.put("24", "大村");

		jyoMapByCd2 = new HashMap<>();
		jyoMapByCd2.put("01", "KIRYU");
		jyoMapByCd2.put("02", "TODA");
		jyoMapByCd2.put("03", "EDOKAWA");
		jyoMapByCd2.put("04", "HEIWAJIMA");
		jyoMapByCd2.put("05", "TAMAGAWA");
		jyoMapByCd2.put("06", "HAMANAKO");
		jyoMapByCd2.put("07", "GAMAGORI");
		jyoMapByCd2.put("08", "TOKONAME");
		jyoMapByCd2.put("09", "MINATO");
		jyoMapByCd2.put("10", "SANKOKU");
		jyoMapByCd2.put("11", "BIWAKO");
		jyoMapByCd2.put("12", "SUMINOE");
		jyoMapByCd2.put("13", "AMAGASAKI");
		jyoMapByCd2.put("14", "NARUTO");
		jyoMapByCd2.put("15", "MARUGAME");
		jyoMapByCd2.put("16", "KAGOSIMA");
		jyoMapByCd2.put("17", "MIYAJIMA");
		jyoMapByCd2.put("18", "TOKUYAMA");
		jyoMapByCd2.put("19", "SHIMONOSEKI");
		jyoMapByCd2.put("20", "WAKAMATU");
		jyoMapByCd2.put("21", "ASHIYA");
		jyoMapByCd2.put("22", "HUKUOKA");
		jyoMapByCd2.put("23", "KARATU");
		jyoMapByCd2.put("24", "OMURA");
		
		branchMapByJyoCd = new HashMap<>();
		branchMapByJyoCd.put("01", "群馬");
		branchMapByJyoCd.put("02", "埼玉");
		branchMapByJyoCd.put("03", "東京");
		branchMapByJyoCd.put("04", "東京");
		branchMapByJyoCd.put("05", "東京");
		branchMapByJyoCd.put("06", "静岡");
		branchMapByJyoCd.put("07", "愛知");
		branchMapByJyoCd.put("08", "愛知");
		branchMapByJyoCd.put("09", "三重");
		branchMapByJyoCd.put("10", "福井");
		branchMapByJyoCd.put("11", "京滋");
		branchMapByJyoCd.put("12", "大阪");
		branchMapByJyoCd.put("13", "兵庫");
		branchMapByJyoCd.put("14", "徳島");
		branchMapByJyoCd.put("15", "香川");
		branchMapByJyoCd.put("16", "岡山");
		branchMapByJyoCd.put("17", "広島");
		branchMapByJyoCd.put("18", "山口");
		branchMapByJyoCd.put("19", "山口");
		branchMapByJyoCd.put("20", "福岡");
		branchMapByJyoCd.put("21", "福岡");
		branchMapByJyoCd.put("22", "福岡");
		branchMapByJyoCd.put("23", "佐賀");
		branchMapByJyoCd.put("24", "長崎");
		
		jyoMapByBranch = new HashMap<>();
		jyoMapByBranch.put("群馬", "01");
		jyoMapByBranch.put("埼玉", "02");
		jyoMapByBranch.put("東京", "03");
		jyoMapByBranch.put("東京", "04");
		jyoMapByBranch.put("東京", "05");
		jyoMapByBranch.put("静岡", "06");
		jyoMapByBranch.put("愛知", "07");
		jyoMapByBranch.put("愛知", "08");
		jyoMapByBranch.put("三重", "09");
		jyoMapByBranch.put("福井", "10");
		jyoMapByBranch.put("京滋", "11");
		jyoMapByBranch.put("大阪", "12");
		jyoMapByBranch.put("兵庫", "13");
		jyoMapByBranch.put("徳島", "14");
		jyoMapByBranch.put("香川", "15");
		jyoMapByBranch.put("岡山", "16");
		jyoMapByBranch.put("広島", "17");
		jyoMapByBranch.put("山口", "18");
		jyoMapByBranch.put("山口", "19");
		jyoMapByBranch.put("福岡", "20");
		jyoMapByBranch.put("福岡", "21");
		jyoMapByBranch.put("福岡", "22");
		jyoMapByBranch.put("佐賀", "23");
		jyoMapByBranch.put("長崎", "24");
		
	}

	public static String getJyoCd(String jyo) {
		return jyoMapByName.get(jyo);
	}

	public static String getJyocdByBranch(String branch) {
		return jyoMapByBranch.get(branch);
	}
	
	public static String getJyoName(String jyoCd) {
		return jyoMapByCd.get(jyoCd);
	}

	public static String getJyoName2(String jyoCd) {
		return jyoMapByCd2.get(jyoCd);
	}
	
	public static String getBranchByJyoCd(String jyoCd) {
		return branchMapByJyoCd.get(jyoCd);
	}

}
