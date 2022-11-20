package com.pengkong.boatrace.util.tmp;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.model.Odds;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.scraping.parser.OddsKFPageParser;
import com.pengkong.boatrace.scraping.parser.RaceResultPageParser;
import com.pengkong.boatrace.service.manager.ResultOddsManager;
import com.pengkong.common.FileUtil;

public class DbUtil {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public void aggregateCsv(String csvFileDir, String targetFilepath) throws Exception {
		File dir = new File(csvFileDir);
		File[] files = dir.listFiles();
		String filepath;
		StringBuilder sb = new StringBuilder();
		for (File file : files) {
			if (!file.isFile()) {
				continue;
			}
			String fname = file.getName();
			if (!fname.contains("race")) {
				continue;
			}
			filepath = file.getPath();
			List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
			
			
			for (String line : lines) {
				String[] t = line.split(",");
				
				String strLevelList = "";
				String[] token = t[276].split("-");
				for (int i=0; i < token.length; i++) {
					strLevelList += token[i].substring(0,1);
				}
				if (strLevelList.contains("X")) {
					continue;
				}
				
				sb.append(t[0]); sb.append(",");
				sb.append(t[1]); sb.append(",");
				sb.append(t[2]); sb.append(",");
				sb.append(t[3]); sb.append(",");
				sb.append(t[4]); sb.append(",");
				sb.append(t[5]); sb.append(",");
				sb.append(t[6]); sb.append(",");
				sb.append(t[7]); sb.append(",");
				sb.append(t[8]); sb.append(",");
				sb.append(t[9]); sb.append(",");
				sb.append(t[10]); sb.append(",");
				sb.append(t[11]); sb.append(",");
				sb.append(t[12]); sb.append(",");
//				sb.append(t[13]); sb.append(",");
				sb.append(t[14]); sb.append(",");
				sb.append(t[15]); sb.append(",");
				sb.append(t[16]); sb.append(",");
				sb.append(getSetuNumber(t[17])); sb.append(",");
				sb.append(t[18]); sb.append(",");
//				sb.append(t[19]); sb.append(",");
//				sb.append(t[20]); sb.append(",");
				sb.append(t[21]); sb.append(",");
				sb.append(t[22]); sb.append(",");
				sb.append(t[23]); sb.append(",");
				sb.append(t[24]); sb.append(",");
				sb.append(t[25]); sb.append(",");
				sb.append(t[26]); sb.append(",");
//				sb.append(t[27]); sb.append(",");
//				sb.append(t[28]); sb.append(",");
//				sb.append(t[29]); sb.append(",");
//				sb.append(t[30]); sb.append(",");
//				sb.append(t[31]); sb.append(",");
//				sb.append(t[32]); sb.append(",");
//				sb.append(t[33]); sb.append(",");
//				sb.append(t[34]); sb.append(",");
//				sb.append(t[35]); sb.append(",");
//				sb.append(t[36]); sb.append(",");
//				sb.append(t[37]); sb.append(",");
//				sb.append(t[38]); sb.append(",");
				sb.append(t[39]); sb.append(",");
				sb.append(t[40]); sb.append(",");
				sb.append(t[41]); sb.append(",");
				sb.append(t[42]); sb.append(",");
				sb.append(t[43]); sb.append(",");
				sb.append(t[44]); sb.append(",");
//				sb.append(t[45]); sb.append(",");
//				sb.append(t[46]); sb.append(",");
//				sb.append(t[47]); sb.append(",");
//				sb.append(t[48]); sb.append(",");
//				sb.append(t[49]); sb.append(",");
//				sb.append(t[50]); sb.append(",");
//				sb.append(t[51]); sb.append(",");
//				sb.append(t[52]); sb.append(",");
//				sb.append(t[53]); sb.append(",");
//				sb.append(t[54]); sb.append(",");
//				sb.append(t[55]); sb.append(",");
//				sb.append(t[56]); sb.append(",");
//				sb.append(t[57]); sb.append(",");
//				sb.append(t[58]); sb.append(",");
//				sb.append(t[59]); sb.append(",");
//				sb.append(t[60]); sb.append(",");
//				sb.append(t[61]); sb.append(",");
//				sb.append(t[62]); sb.append(",");
//				sb.append(t[63]); sb.append(",");
//				sb.append(t[64]); sb.append(",");
//				sb.append(t[65]); sb.append(",");
//				sb.append(t[66]); sb.append(",");
//				sb.append(t[67]); sb.append(",");
//				sb.append(t[68]); sb.append(",");
				sb.append(t[69]); sb.append(",");
				sb.append(t[70]); sb.append(",");
				sb.append(t[71]); sb.append(",");
				sb.append(t[72]); sb.append(",");
				sb.append(t[73]); sb.append(",");
				sb.append(t[74]); sb.append(",");
				sb.append(t[75]); sb.append(",");
				sb.append(t[76]); sb.append(",");
				sb.append(t[77]); sb.append(",");
				sb.append(t[78]); sb.append(",");
				sb.append(t[79]); sb.append(",");
				sb.append(t[80]); sb.append(",");
//				sb.append(t[81]); sb.append(",");
//				sb.append(t[82]); sb.append(",");
//				sb.append(t[83]); sb.append(",");
//				sb.append(t[84]); sb.append(",");
//				sb.append(t[85]); sb.append(",");
//				sb.append(t[86]); sb.append(",");
//				sb.append(t[87]); sb.append(",");
//				sb.append(t[88]); sb.append(",");
//				sb.append(t[89]); sb.append(",");
//				sb.append(t[90]); sb.append(",");
//				sb.append(t[91]); sb.append(",");
//				sb.append(t[92]); sb.append(",");
//				sb.append(t[93]); sb.append(",");
//				sb.append(t[94]); sb.append(",");
//				sb.append(t[95]); sb.append(",");
//				sb.append(t[96]); sb.append(",");
//				sb.append(t[97]); sb.append(",");
//				sb.append(t[98]); sb.append(",");
				sb.append(t[99]); sb.append(",");
				sb.append(t[100]); sb.append(",");
				sb.append(t[101]); sb.append(",");
				sb.append(t[102]); sb.append(",");
				sb.append(t[103]); sb.append(",");
				sb.append(t[104]); sb.append(",");
				sb.append(t[105]); sb.append(",");
				sb.append(t[106]); sb.append(",");
				sb.append(t[107]); sb.append(",");
				sb.append(t[108]); sb.append(",");
				sb.append(t[109]); sb.append(",");
				sb.append(t[110]); sb.append(",");
//				sb.append(t[111]); sb.append(",");
//				sb.append(t[112]); sb.append(",");
//				sb.append(t[113]); sb.append(",");
//				sb.append(t[114]); sb.append(",");
//				sb.append(t[115]); sb.append(",");
//				sb.append(t[116]); sb.append(",");
				sb.append(t[117]); sb.append(",");
				sb.append(t[118]); sb.append(",");
				sb.append(t[119]); sb.append(",");
				sb.append(t[120]); sb.append(",");
				sb.append(t[121]); sb.append(",");
				sb.append(t[122]); sb.append(",");
				sb.append(t[123]); sb.append(",");
				sb.append(t[124]); sb.append(",");
				sb.append(t[125]); sb.append(",");
				sb.append(t[126]); sb.append(",");
				sb.append(t[127]); sb.append(",");
				sb.append(t[128]); sb.append(",");
				sb.append(t[129]); sb.append(",");
				sb.append(t[130]); sb.append(",");
				sb.append(t[131]); sb.append(",");
				sb.append(t[132]); sb.append(",");
				sb.append(t[133]); sb.append(",");
				sb.append(t[134]); sb.append(",");
//				sb.append(t[135]); sb.append(",");
//				sb.append(t[136]); sb.append(",");
//				sb.append(t[137]); sb.append(",");
//				sb.append(t[138]); sb.append(",");
//				sb.append(t[139]); sb.append(",");
//				sb.append(t[140]); sb.append(",");
//				sb.append(t[141]); sb.append(",");
//				sb.append(t[142]); sb.append(",");
//				sb.append(t[143]); sb.append(",");
//				sb.append(t[144]); sb.append(",");
//				sb.append(t[145]); sb.append(",");
//				sb.append(t[146]); sb.append(",");
//				sb.append(t[147]); sb.append(",");
//				sb.append(t[148]); sb.append(",");
//				sb.append(t[149]); sb.append(",");
//				sb.append(t[150]); sb.append(",");
//				sb.append(t[151]); sb.append(",");
//				sb.append(t[152]); sb.append(",");
//				sb.append(t[153]); sb.append(",");
//				sb.append(t[154]); sb.append(",");
//				sb.append(t[155]); sb.append(",");
//				sb.append(t[156]); sb.append(",");
//				sb.append(t[157]); sb.append(",");
//				sb.append(t[158]); sb.append(",");
				sb.append(t[159]); sb.append(",");
				sb.append(t[160]); sb.append(",");
				sb.append(t[161]); sb.append(",");
				sb.append(t[162]); sb.append(",");
				sb.append(t[163]); sb.append(",");
				sb.append(t[164]); sb.append(",");
				sb.append(t[165]); sb.append(",");
				sb.append(t[166]); sb.append(",");
				sb.append(t[167]); sb.append(",");
				sb.append(t[168]); sb.append(",");
				sb.append(t[169]); sb.append(",");
				sb.append(t[170]); sb.append(",");
//				sb.append(t[171]); sb.append(",");
//				sb.append(t[172]); sb.append(",");
//				sb.append(t[173]); sb.append(",");
//				sb.append(t[174]); sb.append(",");
//				sb.append(t[175]); sb.append(",");
//				sb.append(t[176]); sb.append(",");
//				sb.append(t[177]); sb.append(",");
//				sb.append(t[178]); sb.append(",");
//				sb.append(t[179]); sb.append(",");
//				sb.append(t[180]); sb.append(",");
//				sb.append(t[181]); sb.append(",");
//				sb.append(t[182]); sb.append(",");
//				sb.append(t[183]); sb.append(",");
//				sb.append(t[184]); sb.append(",");
//				sb.append(t[185]); sb.append(",");
//				sb.append(t[186]); sb.append(",");
//				sb.append(t[187]); sb.append(",");
//				sb.append(t[188]); sb.append(",");
//				sb.append(t[189]); sb.append(",");
//				sb.append(t[190]); sb.append(",");
//				sb.append(t[191]); sb.append(",");
//				sb.append(t[192]); sb.append(",");
//				sb.append(t[193]); sb.append(",");
//				sb.append(t[194]); sb.append(",");
				sb.append(t[195]); sb.append(",");
				sb.append(t[196]); sb.append(",");
				sb.append(t[197]); sb.append(",");
				sb.append(t[198]); sb.append(",");
				sb.append(t[199]); sb.append(",");
				sb.append(t[200]); sb.append(",");
				sb.append(t[201]); sb.append(",");
				sb.append(t[202]); sb.append(",");
				sb.append(t[203]); sb.append(",");
				sb.append(t[204]); sb.append(",");
				sb.append(t[205]); sb.append(",");
				sb.append(t[206]); sb.append(",");
//				sb.append(t[207]); sb.append(",");
//				sb.append(t[208]); sb.append(",");
//				sb.append(t[209]); sb.append(",");
//				sb.append(t[210]); sb.append(",");
//				sb.append(t[211]); sb.append(",");
//				sb.append(t[212]); sb.append(",");
//				sb.append(t[213]); sb.append(",");
//				sb.append(t[214]); sb.append(",");
//				sb.append(t[215]); sb.append(",");
//				sb.append(t[216]); sb.append(",");
//				sb.append(t[217]); sb.append(",");
//				sb.append(t[218]); sb.append(",");
//				sb.append(t[219]); sb.append(",");
//				sb.append(t[220]); sb.append(",");
//				sb.append(t[221]); sb.append(",");
//				sb.append(t[222]); sb.append(",");
//				sb.append(t[223]); sb.append(",");
//				sb.append(t[224]); sb.append(",");
// ｵｯｽﾞ1T
				sb.append(t[225]); sb.append(",");
				sb.append(t[226]); sb.append(",");
				sb.append(t[227]); sb.append(",");
				sb.append(t[228]); sb.append(",");
				sb.append(t[229]); sb.append(",");
				sb.append(t[230]); sb.append(",");
// ｵｯｽﾞ1T
//				sb.append(t[231]); sb.append(",");
//				sb.append(t[232]); sb.append(",");
//				sb.append(t[233]); sb.append(",");
//				sb.append(t[234]); sb.append(",");
//				sb.append(t[235]); sb.append(",");
//				sb.append(t[236]); sb.append(",");
//				sb.append(t[237]); sb.append(",");
//				sb.append(t[238]); sb.append(",");
//				sb.append(t[239]); sb.append(",");
//				sb.append(t[240]); sb.append(",");
//				sb.append(t[241]); sb.append(",");
//				sb.append(t[242]); sb.append(",");
				sb.append(t[243]); sb.append(",");
				sb.append(t[244]); sb.append(",");
				sb.append(t[245]); sb.append(",");
				sb.append(t[246]); sb.append(",");
				sb.append(t[247]); sb.append(",");
				sb.append(t[248]); sb.append(",");
				sb.append(t[249]); sb.append(",");
				sb.append(t[250]); sb.append(",");
				sb.append(t[251]); sb.append(",");
				sb.append(t[252]); sb.append(",");
				sb.append(t[253]); sb.append(",");
				sb.append(t[254]); sb.append(",");
				sb.append(t[255]); sb.append(",");
				sb.append(t[256]); sb.append(",");
				sb.append(t[257]); sb.append(",");
//				sb.append(t[258]); sb.append(",");
//				sb.append(t[259]); sb.append(",");
//				sb.append(t[260]); sb.append(",");
//				sb.append(t[261]); sb.append(",");
//				sb.append(t[262]); sb.append(",");
//				sb.append(t[263]); sb.append(",");
//				sb.append(t[264]); sb.append(",");
//				sb.append(t[265]); sb.append(",");
//				sb.append(t[266]); sb.append(",");
//				sb.append(t[267]); sb.append(",");
//				sb.append(t[268]); sb.append(",");
//				sb.append(t[269]); sb.append(",");
//				sb.append(t[270]); sb.append(",");
//				sb.append(t[271]); sb.append(",");
//				sb.append(t[272]); sb.append(",");
				sb.append(t[273]); sb.append(",");
				sb.append(t[274]); sb.append(",");
				sb.append(t[275]); sb.append(",");
				sb.append(t[276]); sb.append(",");
				sb.append(t[277]); sb.append(",");
				sb.append(t[278]); sb.append(",");
				sb.append(t[279]); sb.append(",");
//				sb.append(t[280]); sb.append(",");
//				sb.append(t[281]); sb.append(",");
//				sb.append(t[282]); sb.append(",");
				sb.append(t[283]); sb.append(",");
				sb.append(t[284]); sb.append(",");
//				sb.append(t[285]); sb.append(",");
//				sb.append(t[286]); sb.append(",");
				sb.append(t[287]); sb.append(",");
				sb.append(t[288]); sb.append(",");
				// 20180606 追加 　進入固定　シード番組　枠順級一覧　A級選手数
				sb.append(t[292]); sb.append(",");
				sb.append(t[293]); sb.append(",");
				sb.append(t[294]); sb.append(",");
				sb.append(t[295]); 

				
//				sb.append(strLevelList);
				sb.append(System.lineSeparator());
			}

			System.out.println(fname);
		}
		
		FileUtil.appendFileByLine(targetFilepath, sb.toString());
	}

	public void oddsTest() throws Exception{
		Gson gson = new Gson();
		JsonObject jo = gson.fromJson(new FileReader("C:/Dev/workspace/Oxygen/pod_boatrace/resources/api/odds_data.txt"), JsonObject.class);
		JsonObject oddsDetailListByKachishiki = jo.getAsJsonObject("oddsDetailListByKachishiki");
		JsonArray arrJo = oddsDetailListByKachishiki.getAsJsonArray("6");
		for (JsonElement je : arrJo) {
			com.pengkong.boatrace.model.json.OddsJson odds = gson.fromJson(je, com.pengkong.boatrace.model.json.OddsJson.class);
			System.out.println(odds);	
		}
	}
	
	private String getSetuNumber(String setu) {
		if (setu.equals("初日")) {
			return "1";
		} else if (setu.equals("２日目")) {
			return "2";
		} else if (setu.equals("３日目")) {
			return "3";
		} else if (setu.equals("４日目")) {
			return "4";
		} else if (setu.equals("５日目")) {
			return "5";
		} else if (setu.equals("最終日")) {
			return "6";
		} else {
			return "0";
		}
	}
	
	public static void main(String[] args) {
		try {
			//BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/race_result_digest.properties");
			new DbUtil().aggregateCsv("C:/Dev/workspace/Oxygen/pod_boatrace/test/csv/", 
					"C:/Dev/workspace/Oxygen/pod_boatrace/resources/race_recent.csv");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
