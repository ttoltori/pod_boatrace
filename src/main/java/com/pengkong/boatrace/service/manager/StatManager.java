package com.pengkong.boatrace.service.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.model.StatResult;
import com.pengkong.boatrace.model.comparator.StatResultFloatComparator;
import com.pengkong.boatrace.simulation.model.BettingRule;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.collection.HashMapList;

public class StatManager {
	private Logger logger = LoggerFactory.getLogger(StatManager.class);

	private HashMapList<StatResult> mapStatResult = new HashMapList<>();

	private static StatManager instance = new StatManager();

	public StatManager() {
	}

	public static StatManager getInstance() {
		return instance;
	}

	
	
	public void loadDailyFile(String ymd) throws Exception {
		mapStatResult.clear();
		
		String filepath;
		List<String> lines;
		
		File dir = new File(PropertyUtil.getInstance().getString("DIRECTORY_STASTICS"));
		File[] files = dir.listFiles(new StatFilenameFilter(ymd));
		for (File file : files) {
			if (!file.isFile()) {
				continue;
			}
			
			filepath = file.getPath();
			lines = FileUtil.readFileByLineArr(filepath, "UTF8");
			for (String line : lines) {
				String[] token = line.split(",");
				StatResult rec = StatResult.fromCsv(token);
				mapStatResult.addItem(rec.createKey(), rec);
			}
		}
		
//		logger.info("StatManager load OK. " + ymd);
	}
	
	
	public List<StatResult> getMostHitRate(List<String> arrBettype) {
		List<StatResult> result = new ArrayList<>();
		
		List<StatResult> resultTmp = getAll(arrBettype);
		if (resultTmp.size() <= 0) {
			return null;
		}
		if (resultTmp.size() > 1) {
			logger.info("resultTmp.size()=" + resultTmp.size());
		}
		
		resultTmp.sort(new StatResultFloatComparator("hitRate"));
		result.add(resultTmp.get(resultTmp.size() - 1));
		
		return result;
	}

	public List<StatResult> getMostIncomeRate(List<String> arrBettype) {
		List<StatResult> result = new ArrayList<>();
		
		List<StatResult> resultTmp = getAll(arrBettype);
		if (resultTmp.size() <= 0) {
			return null;
		}
		if (resultTmp.size() > 1) {
			logger.info("resultTmp.size()=" + resultTmp.size());
		}
		
		resultTmp.sort(new StatResultFloatComparator("incomeRate"));
		result.add(resultTmp.get(resultTmp.size() - 1));
		
		return result;
	}
	
	public List<StatResult> getMostAverageIncomeRate(List<String> arrBettype) {
		List<StatResult> result = new ArrayList<>();
		
		List<StatResult> resultTmp = getAll(arrBettype);
		if (resultTmp.size() <= 0) {
			return null;
		}
		
		if (resultTmp.size() > 1) {
			logger.info("resultTmp.size()=" + resultTmp.size());
		}
		resultTmp.sort(new StatResultFloatComparator("avgincomeRate"));
		result.add(resultTmp.get(resultTmp.size() - 1));
		
		return result;
	}
	

	public List<StatResult> getMostPrize(List<String> arrBettype) {
		List<StatResult> result = new ArrayList<>();
		
		List<StatResult> resultTmp = getAll(arrBettype);
		if (resultTmp.size() <= 0) {
			return null;
		}
		if (resultTmp.size() > 1) {
			logger.info("resultTmp.size()=" + resultTmp.size());
		}
		
		resultTmp.sort(new StatResultFloatComparator("prize"));
		result.add(resultTmp.get(resultTmp.size() - 1));
		
		return result;
	}
	
	public List<StatResult> getAll(List<String> arrBettype) {
		List<StatResult> result = new ArrayList<>();
		for (String bettype : arrBettype) {
			List<StatResult> list = mapStatResult.get(bettype);
			if (list == null) {
				continue;
			}
			if (list.size() > 1) {
				logger.info("list.size()=" + list.size());
			}
			result.addAll(list);
		}
		
		return result;
	}
	
	public class StatFilenameFilter implements FilenameFilter {

		private String ymd;
		
		public StatFilenameFilter(String ymd) {
			this.ymd = ymd;
		}
		
		@Override
		public boolean accept(File dir, String name) {
			if (name.indexOf(ymd + "_" + "stat_") == -1) {
				return false;	
			} else {
				return true;
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			PropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/race_result_rebuild.properties");
			StatManager.getInstance().loadDailyFile("20170504");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
