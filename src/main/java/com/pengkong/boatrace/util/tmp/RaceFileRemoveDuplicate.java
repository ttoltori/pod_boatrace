package com.pengkong.boatrace.util.tmp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public class RaceFileRemoveDuplicate {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public RaceFileRemoveDuplicate() {
	}

	public void execute(String yyyyMMdd) throws Exception {
		execute(yyyyMMdd, yyyyMMdd);
	}

	public void execute(String fromYmd, String toYmd) throws Exception {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);

		while (currDate.compareTo(toDate) <= 0) {
			String yyyyMMdd = sdf.format(currDate);

			// String filepath = BoatProperty.DIRECTORY_CSV + "race_" + yyyyMMdd + ".csv";
			String filepath = PropertyUtil.getInstance().getString("DIRECTORY_CSV") + "/tmp/race_" + yyyyMMdd + ".csv";
			File file = new File(filepath);
			if (!file.exists()) {
				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
				continue;
			}

			String fname = file.getName();
			filepath = file.getPath();
			List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
			List<String> linesOut = new ArrayList<String>();
			HashMap<String, String> mapTmp = new HashMap<>();
			for (String line : lines) {
				String[] token = line.split(",");
				String key = token[3] + token[5] + token[18];
				if (mapTmp.containsKey(key)) {
					break;
				}
				mapTmp.put(key, key);
				linesOut.add(line);
			}
			System.out.println(fname);

			String filepathOut = PropertyUtil.getInstance().getString("DIRECTORY_CSV") + "race_" + yyyyMMdd + ".csv";

			for (String line : linesOut) {
				FileUtil.appendFileByLine(filepathOut, line);
			}

			// 1日増加
			calendar.add(Calendar.DATE, 1);
			currDate = calendar.getTime();
		}
	}
	
	public static void main(String[] args) {
		try {
			PropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/race_result_rebuild.properties");
			new RaceFileRemoveDuplicate().execute("20130101", "20141231");
			// BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/race_result_rebuild.properties");
			// new RaceFileUploader().execute("20100101", "20100102");
			// new RaceFileUploader().execute("20090730", "20121231");
			// 20090729 エラー発生
			// new RaceFileUploader().execute("20090101", "20170424");
			// new RaceFileUploader().execute("20090101", "20180725");
			// new RaceFileUploader().execute("20180726", "20181007");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
