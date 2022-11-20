package com.pengkong.boatrace.online.tohyo.report;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.property.SqlTemplate;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.service.manager.JyoManager;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.StringUtil;

public class Reporter {
	Logger logger = LoggerFactory.getLogger(Reporter.class);
	
	MLPropertyUtil prop =  MLPropertyUtil.getInstance();

	SqlTemplate sqlTpl = SqlTemplate.getInstance();

	public void execute() {
		List<DBRecord> results;
		try {
			results = loadDB();

			StringBuilder sb = new StringBuilder();
			sb.append(createTitle());
			sb.append("\n");
			sb.append("-----------------------------------------------------------------------------------------------\n");

			int totalBetamt = 0;
			int totalHitamt = 0;
			int totalBetcnt = 0;
			for (DBRecord rec : results) {
				Integer hitamt = rec.getInteger("hitamt");
				if (hitamt != null) {
					Integer betamt = rec.getInteger("betamt");
					totalBetamt += betamt;
					totalHitamt += hitamt;
					totalBetcnt++;
				}
				sb.append(createLine(rec));
				sb.append("\n");
			}
			
			sb.append("-----------------------------------------------------------------------------------------------\n");
			sb.append("betcnt = " + totalBetcnt + ", balance = " + (totalHitamt - totalBetamt));

			// ファイル出力
			String filepath = prop.getString("file_tohyo_report");
			filepath = filepath.replace("{ymd}", BoatUtil.currentYmd());
			FileUtil.writeFile(filepath, sb.toString());
			
			// console出力
			System.out.println(sb.toString());
		} catch (Exception e) {
			logger.error("reporter failed. ", e);
		}
		
	}
	
	List<DBRecord> loadDB() throws Exception {
		List<DBRecord> results;
		
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
			CustomMapper customMapper = session.getMapper(CustomMapper.class);
			
			String sql = sqlTpl.get("s-report");
			sql = sql.replace("{ymd}", BoatUtil.currentYmd());
			
			HashMap<String, String> mapParam = new HashMap<>();
			mapParam.put("sql", sql);
			
			// 디비 데이터 일람 취득
			results = customMapper.selectSql(mapParam);
			
			DatabaseUtil.close(session);
			return results;
	}
	
	String createLine(DBRecord rec) {
		StringBuilder sb = new StringBuilder();
		sb.append( StringUtil.leftPad(rec.getString("ymd"), 9, " ") );
		sb.append( StringUtil.leftPad(JyoManager.getJyoName2(rec.getString("jyocd")), 12, " ") );
		sb.append( StringUtil.leftPad(rec.getString("jyocd"), 6, " ") );
		sb.append( StringUtil.leftPad(rec.getString("raceno"), 6, " ") );
		sb.append( StringUtil.leftPad(rec.getString("sime"), 5, " ") );
		sb.append( StringUtil.leftPad(rec.getString("grade"), 6, " ") );
		sb.append( StringUtil.leftPad(convertStatus(rec.getInteger("status")), 8, " ") );
		sb.append( StringUtil.leftPad(rec.getString("bettype", ""), 8, " ") );
		sb.append( StringUtil.leftPad(rec.getString("bet_kumiban", ""), 8, " ") );
		sb.append( StringUtil.leftPad(rec.getString("result_kumiban", ""), 8, " ") );
		sb.append( StringUtil.leftPad(rec.getIntegerStr("betamt", ""), 8, " ") );
		sb.append( StringUtil.leftPad(rec.getIntegerStr("hitamt", ""), 8, " ") );

		return sb.toString();
	}
	
	String convertStatus(Integer status) {
		if (status == 0) {
			return "BEFORE";
		} else if (status == 2) {
			return "TOHYO";
		} else if (status == 3) {
			return "RESULT";
		} else {
			return status.toString();
		}
	}
	
	String createTitle() {
		StringBuilder sb = new StringBuilder();
		sb.append( StringUtil.leftPad("ymd", 9, " ") );
		sb.append( StringUtil.leftPad("jyo", 12, " ") );
		sb.append( StringUtil.leftPad("jyoCd", 6, " ") );
		sb.append( StringUtil.leftPad("race", 6, " ") );
		sb.append( StringUtil.leftPad("sime", 5, " ") );
		sb.append( StringUtil.leftPad("grade", 6, " ") );
		sb.append( StringUtil.leftPad("status", 8, " ") );
		sb.append( StringUtil.leftPad("bettype", 8, " ") );
		sb.append( StringUtil.leftPad("kumiban", 8, " ") );
		sb.append( StringUtil.leftPad("result", 8, " ") );
		sb.append( StringUtil.leftPad("betamt", 8, " ") );
		sb.append( StringUtil.leftPad("hitamt", 8, " ") );

		return sb.toString();

	}
	
	public static void main(String[] args) {
		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace_online/properties/online.properties";
		try {
			MLPropertyUtil.getInstance().addFile(propertyFilepath);
			new Reporter().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
