package com.pengkong.boatrace.server.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.DBCol;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.server.db.dto.BoatDbRecord;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.PropertyUtil;

@SuppressWarnings("serial")
public class BoatDbProvider extends UnicastRemoteObject implements BoatDbRmiInterface {

	List<BoatDbRecord> listDbRecord = new ArrayList<>();
	Logger logger = LoggerFactory.getLogger(BoatDbProvider.class);

	PropertyUtil prop = PropertyUtil.getInstance();
	
	public BoatDbProvider() throws RemoteException {
		super();
		String defaultFromYmd = prop.getString("BoatDbProvider.defalut.fromYmd"); 
		String defaultToYmd = prop.getString("BoatDbProvider.defalut.toYmd"); 
		loadDB(defaultFromYmd, defaultToYmd);
	}

	@Override
	public void loadDB(String fromYmd, String toYmd) throws RemoteException {
		try {
			loadDBInner(fromYmd, toYmd);
		} catch (Exception e) {
			logger.error("load db failed", e);
		}
	}
	
	@Override
	public List<BoatDbRecord> getBoatDbRecordList(String fromYmd, String toYmd, String betType, String kumiban)
			throws RemoteException {
		int fromYmdInt = Integer.parseInt(fromYmd);
		int toYmdInt = Integer.parseInt(toYmd);

		logger.info("req : fromYmd=" + fromYmd + ", toYmd=" + toYmd 
				+ ", betType=" + ((betType==null) ? "all" : betType)
				+ ", kumiban=" + ((kumiban==null) ? "all" : kumiban));
		List<BoatDbRecord> result = new ArrayList<BoatDbRecord>();

		int ymd;
		for (BoatDbRecord rec : listDbRecord) {
			ymd = Integer.parseInt(rec.getString(DBCol.YMD));
			if (ymd < fromYmdInt) {
				continue;
			} else if (ymd > toYmdInt) {
				break;
			}

			if ((betType != null && !betType.equals(rec.getString(DBCol.BETTYPE)))
					|| (kumiban != null && !kumiban.equals(rec.getString(DBCol.BET_KUMIBAN)))) {
				continue;
			}

			result.add(rec);
		}
		
		logger.info("res : " + result.size() + " records.");
		
		return result;
	}
	
	public void loadDBInner(String fromYmd, String toYmd) throws Exception {
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		try {
			CustomMapper customMapper = session.getMapper(CustomMapper.class);
			
			HashMap<String, String> mapParam = new HashMap<>();
			mapParam.put("fromYmd", fromYmd);
			mapParam.put("toYmd", toYmd);
			listDbRecord = customMapper.selectResultWithPatterns(mapParam);
			
			if (listDbRecord.size() <= 0) {
				throw new Exception("stat_ml_result has no data.");
			}
			
			logger.info("db loaded. " + fromYmd + "~" + toYmd + ": " + listDbRecord.size() + " records.");
		} finally {
			DatabaseUtil.close(session);
		}
	}
}
