package com.pengkong.boatrace.exp10.property;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.common.ExcelFileUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.StringUtil;

public class MLPropertyUtilStep2 extends PropertyUtil {
	//MLReportUtil report = MLReportUtil.getInstance();
	Logger logger = LoggerFactory.getLogger(MLPropertyUtilStep2.class);
	
	int configIndex = 0;
	String[] configTitles;
	List<String> listConfigString;

	private static class MLPropertyUtilInstanceHolder {
		private static final MLPropertyUtilStep2 INSTANCE = new MLPropertyUtilStep2();
	}
	
	public static MLPropertyUtilStep2 getInstance() {
		return MLPropertyUtilInstanceHolder.INSTANCE;
	}
	
	public MLPropertyUtilStep2() {
		super();
	}
	
	@Override
	public String getString(String key, String defaultValue) {
		String str = getString(key);
		if (StringUtil.isEmpty(str)) {
			return defaultValue;
		}
		
		return str;
	}
	
	@Override
	public String getString(String key) {
		// 20220807 simul1_2簡略化対応
		if (key.equals("result_start_ymd") || key.equals("result_end_ymd")) {
			return getTerm(key);
		}
		
		// 20220807 simul1_2簡略化対応
		if (key.equals("grade_condition")) {
			return getGrade(key);
		}
	
		return super.getString(key);
	}
	
	String getTerm(String key) {
		String term = getString("term");
		if (term == null) {
			return super.getString(key);
		}
		
		String[] termToken = super.getString("term_" + term).split(Delimeter.WAVE.getValue()); 
		
		if (key.equals("result_start_ymd")) {
			return termToken[0];
		}
		
		return termToken[1];
	}
	
	String getGrade(String key) {
		String gradeType = getString("grade_type");
		if (gradeType == null) {
			return super.getString(key);
		}
		
		if (gradeType.equals("ip")) {
			return "'ip','G3'";
		} else if (gradeType.equals("SG")) {
			return "'SG','G1','G2'";			
		} else {
			return "'ip','G3','SG','G1','G2'";
		}
	}
	
	/**
	 * reload config file and initialize config index.
	 * @param configName 繰り返し実行するプロパティ一覧ファイルの項目キー 例）file_mode_config, file_result_config
	 * @throws Exception
	 */
	public void reset(String configName) throws Exception {
		// initialize config
		String filepath = super.getString(configName);
		// excelファイルの場合
		if (filepath.contains(".xlsx")) {
			// ex) C:/Dev/workspace/Oxygen/pod_boatrace/document/expr10/result_config.xlsx#simul
			String[] token = filepath.split("#");
			listConfigString = ExcelFileUtil.read(token[0], token[1], Delimeter.TAB.getValue());
		} else {
			listConfigString = FileUtil.readFileByLineArr(filepath,"UTF-8", false, "#");
		}
		
		// title row
		String titleLine = listConfigString.remove(0);
		configTitles = titleLine.split(Delimeter.TAB.getValue());
		
		configIndex = 0;
	}

	/**
	 * reset properties with new line.
	 */
	public void next() {
		String[] token=null;
		try {
			token = listConfigString.get(configIndex).split(Delimeter.TAB.getValue());
			for (int i=0; i < configTitles.length; i++) {
				putProperty(configTitles[i], token[i]);
			}
		} catch (Exception e) {
			logger.error("MLPropertyUtil:next() error: " + listConfigString.get(configIndex));
			throw e;
		}
		configIndex++;
	}
	
	public boolean hasNext() {
		return (configIndex < listConfigString.size());
	}
	
	public static void main(String[] args) {
		MLPropertyUtilStep2 prop = MLPropertyUtilStep2.getInstance();
		try {
			// PropertyUtilを使っている箇所もあるので互換性確保のために
			PropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties");
			prop.addFile("C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties");
			prop.reset("file_model_config");
			prop.next();
			System.out.println(prop.hasNext());
			System.out.println(prop.getString("rank1_features"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
