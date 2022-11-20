package com.pengkong.boatrace.scraping;

import java.io.File;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jsoup.UncheckedIOException;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.converter.Csv2RaceEx;
import com.pengkong.boatrace.exception.OddsBankException;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.scraping.parser.OddsBankPageParser;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public class OddsBankDownloader {
	/** 電投締切２分前 */
	private final String MODE_BEFORE = "1";
	/** 本場締切後 */
	private final String MODE_END = "99";
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	OddsBankPageParser parser = new OddsBankPageParser();
	PropertyUtil prop = PropertyUtil.getInstance();

	public OddsBankDownloader() {
	}
	
	public void execute(String yyyyMMdd, String mode, int interval, int errorWait) throws Exception {
		execute(yyyyMMdd, yyyyMMdd, mode, interval, errorWait);
	}

	public void execute(String fromYmd, String toYmd, String mode, int interval, int errorWait) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);
		
		while (currDate.compareTo(toDate) <= 0) {
			String yyyyMMdd = sdf.format(currDate);

			String filepathCsv = prop.getString("DIRECTORY_CSV") + "race_" + yyyyMMdd + ".csv";
			File file = new File(filepathCsv);
			if (!file.exists()) {
				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
				continue;
			}
			
			// read csv file
			String fname = file.getName();
			filepathCsv = file.getPath();
			List<String> lines = FileUtil.readFileByLineArr(filepathCsv, "UTF8");
			List<String> result = new ArrayList<>();
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				
				RaceEx race = Csv2RaceEx.convertOnlyRaceInfo(line);
				String ymd = race.raceInfo.ymd;
				String jyoCd = race.getJyoCd();
				String raceNo = race.getRaceNo();
				
				// parse odds
				try {
					result.addAll(parser.parse(ymd, jyoCd, raceNo, mode));
				} catch (OddsBankException e) {
					e.printStackTrace();
					i = (i > 0) ? i - 1: i;
					Thread.sleep(1000 * (errorWait + 1));
				} catch (UncheckedIOException e) {
					e.printStackTrace();
					i = (i > 0) ? i - 1: i;
					Thread.sleep(1000 * (errorWait + 1));
				} catch (SocketException e) {
					e.printStackTrace();
					i = (i > 0) ? i - 1: i;
					Thread.sleep(1000 * (errorWait + 1));
				} catch (Exception e) {
					e.printStackTrace();
				}
				Thread.sleep(1000*interval);
			}
			
			StringBuilder sb = new StringBuilder();
			for (String lineOdds : result) {
				sb.append(lineOdds);
				sb.append(System.lineSeparator());
			}
			
//			for (int i = 0; i < result.size(); i++) {
//				sb.append(result.get(i));
//				if (i < result.size() - 1) {
//					sb.append(System.lineSeparator());
//				}
//			}

			String filepathOdds;
			if (mode.equals(MODE_BEFORE)) {
				filepathOdds = prop.getString("DIRECTORY_ODDSBEFORE") + "odds_" + yyyyMMdd + ".before.csv";
			} else  {
				filepathOdds = prop.getString("DIRECTORY_ODDS") + "odds_" + yyyyMMdd + ".csv";	
			}
			
			FileUtil.writeFile(filepathOdds, sb.toString().substring(0, sb.length()-2));

			System.out.println(fname);

			// 1日増加
			calendar.add(Calendar.DATE, 1);
			currDate = calendar.getTime();
			
			//Thread.sleep(1000 * 30);

		}
			
	}
	
	public static void main(String[] args) {
		try {
			System.setProperty("http.proxyHost", "127.0.0.1");
			System.setProperty("http.proxyPort", "9668");
			
			//BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/race_result_rebuild.properties");
			// new OddsBankDownloader().execute("20170309", "20170309", "1");
			
			PropertyUtil.getInstance().addFile(args[5]);
			// fromYmd, toYmd, mode(1 or 99), request interval (sec), error wait (sec)
			new OddsBankDownloader().execute(args[0], args[1], args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
