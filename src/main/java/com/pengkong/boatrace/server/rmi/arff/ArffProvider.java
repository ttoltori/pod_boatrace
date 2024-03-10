package com.pengkong.boatrace.server.rmi.arff;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.boatrace.weka.automation.NominalManager;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.StringUtil;

@SuppressWarnings("serial")
public class ArffProvider extends UnicastRemoteObject implements ArffRmiInterface {
	Logger logger = LoggerFactory.getLogger(ArffProvider.class);

	private Map<String, ArffFileInfo> mapDefine = new HashMap<String, ArffFileInfo>();
	
	NominalManager nominalManager = new NominalManager();
	
	PropertyUtil prop = PropertyUtil.getInstance();
	public ArffProvider() throws RemoteException {
		super();
	}

	public void create(String defFileName, String fromYmd, String toYmd) throws RemoteException {
		try {
			createInner(defFileName, fromYmd, toYmd);
		} catch (Exception e) {
			logger.error("create failed", e);
			throw new RemoteException("creation is failed. refer to the rmi server log.");
		}
	}
	
	private void createInner(String defFileName, String fromYmd, String toYmd) throws Exception {
		String arffFilepath = prop.getString("ArffProvider.dir.arff") + defFileName.split("\\.")[0] + ".arff";
		
		ArffFileInfo afi = mapDefine.get(defFileName);
		// 기존 arff파일 정보가 없다면 
		if (afi == null) {
			afi = createArffFileInfo(defFileName);
			mapDefine.put(defFileName, afi);
			
			// 일단 헤더부분만으로 파일 생성
			FileUtil.writeFile(arffFilepath, createArffHeader(afi));
			
			// DB 데이터 로딩해서 afi에 설정한다.
			loadAndSetDB(afi);
		}
		
		StringBuilder sb = new StringBuilder();
		int idx;
		int startIdx = afi.nextStartIndex;
		String endYmd = BoatUtil.daysAfterYmd(toYmd, 1);
		for (idx = startIdx; idx < afi.listDbRecord.size(); idx++) {
			DBRecord rec = afi.listDbRecord.get(idx);
			if (rec.getString("ymd").equals(endYmd)) {
				afi.nextStartIndex = idx;
				break;
			}
			sb.append(DBRecord.toCsv(rec));
			sb.append(System.lineSeparator());
		}
		
		// 디비의 추가데이터를 file에 apeend한다.
		FileUtil.appendFileByLine(arffFilepath, sb.toString());
		logger.debug("arff(" + arffFilepath + ") has been appended of " + (afi.nextStartIndex - startIdx) + " lines.");
	}
	
	/**
	 * 디비로부터 데이터를 로딩하여 afi에 설정한다.
	 * @param afi
	 * @throws Exception
	 */
	private void loadAndSetDB(ArffFileInfo afi) throws Exception{
		String sql = afi.getValue("sql");
		String startYmd = prop.getString("ArffProvider.startYmd");
		String endYmd = prop.getString("ArffProvider.endYmd");
		if (StringUtil.isEmpty(endYmd)) {
			endYmd = BoatUtil.yesterdayYmd();
		}
		sql = sql.replace("{fromYmd}", startYmd);
		sql = sql.replace("{toYmd}", endYmd);
		
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		try {
			CustomMapper customMapper = session.getMapper(CustomMapper.class);
			
			HashMap<String, String> mapParam = new HashMap<>();
			mapParam.put("sql", sql);
			afi.listDbRecord = customMapper.selectSql(mapParam);
			if (afi.listDbRecord.size() <= 0) {
				throw new Exception("db has no data. sql=" + sql);
			}
			
			logger.info("db loaded. " + startYmd + "~" + endYmd + ": " + afi.listDbRecord.size() + " records.");
		} finally {
			DatabaseUtil.close(session);
		}
	}

	/**
	 * Arff 파일정보를 반환한다.
	 * @param defFileName define파일명
	 * @return
	 * @throws Exception
	 */
	private ArffFileInfo createArffFileInfo(String defFileName) throws Exception {
		ArffFileInfo afi = new ArffFileInfo();
		afi.parseFile(prop.getString("ArffProvider.dir.def") + defFileName);

		return afi;
	}
	
	/**
	 * Arff헤더부분을 생성하여 반환한다.
	 * @param fileIfo
	 * @return
	 * @throws Exception
	 */
	private String createArffHeader(ArffFileInfo fileIfo) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("% " + fileIfo.getValue("name")); sb.append(System.lineSeparator());
		sb.append("@RELATION boatrace"); sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		
		String[] tokenAttrs = fileIfo.getValue("attributes").split(",");
		for (String attr : tokenAttrs) {
			sb.append(convertAttribute(attr));
			sb.append(System.lineSeparator());
		}
		
		String strClassValues = fileIfo.getValue("class_values");
		String classValues = null;
		if (strClassValues.startsWith("range")) {
			// ex) range:100:500:10  10単位増分の100~500(prize)
			String[] token = strClassValues.split(":");
			classValues = nominalManager.getRange(
					Integer.parseInt(token[1]), Integer.parseInt(token[2]), Integer.parseInt(token[3]));
		} else {
			classValues = strClassValues;
		}
		 
		sb.append("@ATTRIBUTE class {" + classValues + "}");sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		
		sb.append("@DATA");
		
		return sb.toString();
	}
	
	private String convertAttribute(String attribute) {
		String[] tokenAttr = attribute.split(":");
		String attrName = tokenAttr[0].trim();
		String attrType = tokenAttr[1].trim();
		
		// nominal attribute
		if (attrType.equals("nominal")) {
			return "@ATTRIBUTE " + attrName + " " + nominalManager.getNominalAttr(attrName);
		} 
		
		// numeric attribute
		return "@ATTRIBUTE " + attrName + " NUMERIC"; 
	}
}
