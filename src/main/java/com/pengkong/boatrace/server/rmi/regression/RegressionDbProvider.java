package com.pengkong.boatrace.server.rmi.regression;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.collection.HashMapList;

@SuppressWarnings("serial")
public class RegressionDbProvider extends UnicastRemoteObject implements RegressionDbRmiInterface {

	List<DBRecord> listDbRecord = new ArrayList<>();
	Logger logger = LoggerFactory.getLogger(RegressionDbProvider.class);

	HashMapList<DBRecord> mapBetType = new HashMapList<>();
	
	PropertyUtil prop = PropertyUtil.getInstance();
	
	public RegressionDbProvider() throws RemoteException {
		super();
		String defaultFromYmd = prop.getString("RegressionDbProvider.defalut.fromYmd"); 
		String defaultToYmd = prop.getString("RegressionDbProvider.defalut.toYmd"); 
		loadDB(defaultFromYmd, defaultToYmd);
	}

	public void loadDB(String fromYmd, String toYmd) throws RemoteException {
		try {
			loadDBInnter(fromYmd, toYmd);
		} catch (Exception e) {
			logger.error("load db failed", e);
		}
	}
	
	@Override
	public void createCsv(String fromYmd, String toYmd, String filePath, String betType) throws RemoteException {
		logger.info("req : fromYmd=" + fromYmd + ", toYmd=" + toYmd 
				+ ", betType=" + ((betType==null) ? "all" : betType));
		
		List<DBRecord> listDb;
		int fromYmdInt = Integer.parseInt(fromYmd);
		int toYmdInt = Integer.parseInt(toYmd);
		
		listDb = mapBetType.get(betType);
		if (listDb.size() <= 0) {
			throw new RemoteException("no data found. :" + betType);
		}
		
		String prefix = getFieldPrefix(betType);
		StringBuilder sb = new StringBuilder();
		for (DBRecord rec : listDb) {
			int recYmd = rec.getInt("ymd", -1);
			if (recYmd < fromYmdInt) {
				continue;
			}
			
			if (recYmd > toYmdInt) {
				break;
			}
			
			sb.append(rec.getString(prefix + "no"));sb.append(",");
			sb.append(rec.getString("jyocd"));sb.append(",");
			sb.append(rec.getString("raceno"));sb.append(",");
			sb.append(rec.getString("turn"));sb.append(",");
			sb.append(rec.getString("nationwiningrate1"));sb.append(",");
			sb.append(rec.getString("nationwiningrate2"));sb.append(",");
			sb.append(rec.getString("nationwiningrate3"));sb.append(",");
			sb.append(rec.getString("nationwiningrate4"));sb.append(",");
			sb.append(rec.getString("nationwiningrate5"));sb.append(",");
			sb.append(rec.getString("nationwiningrate6"));sb.append(",");
			sb.append(rec.getString("racerwiningrate1"));sb.append(",");
			sb.append(rec.getString("racerwiningrate2"));sb.append(",");
			sb.append(rec.getString("racerwiningrate3"));sb.append(",");
			sb.append(rec.getString("racerwiningrate4"));sb.append(",");
			sb.append(rec.getString("racerwiningrate5"));sb.append(",");
			sb.append(rec.getString("racerwiningrate6"));sb.append(",");
			sb.append(rec.getString(prefix + "prize"));
			sb.append("\n");
		}
		try {
			FileUtil.writeFile(filePath, sb.toString());
		} catch (Exception e) {
			throw new RemoteException("file write error. :" + filePath );
		}
	}
	
	private String getFieldPrefix (String betType) throws RemoteException{
		if (betType.equals("1T")) {
			return "tansyo";
		} else if (betType.equals("2T")) {
			return "nirentan";
		} else if (betType.equals("3T")) {
			return "sanrentan";
		} else if (betType.equals("2F")) {
			return "nirenhuku";
		} else if (betType.equals("3F")) {
			return "sanrenhuku";
		} else {
			throw new RemoteException("invalid bettype : " + betType);
		}
	}

	public void loadDBInnter(String fromYmd, String toYmd) throws Exception {
		SqlSession session = DatabaseUtil.open("mybatis-config.0.xml", false);
		
		CustomMapper customMapper = session.getMapper(CustomMapper.class);
		HashMap<String, String> mapParam = new HashMap<>();
		mapParam.put("fromYmd", fromYmd);
		mapParam.put("toYmd", toYmd);
		listDbRecord = customMapper.selectRegressionDbList(mapParam);
		
		DatabaseUtil.close(session);
		if (listDbRecord.size() <= 0) {
			throw new Exception("db has no data.");
		}
		
		for (DBRecord rec : listDbRecord) {
			String kumiban;
			kumiban = rec.getString("tansyono");
			if (kumiban.equals("1") || kumiban.equals("2") || kumiban.equals("3")) {
				mapBetType.addItem("1T", rec);
			}
			
			kumiban = rec.getString("nirentanno");
			if (kumiban.equals("12") || kumiban.equals("13") || kumiban.equals("21") || kumiban.equals("31")) {
				mapBetType.addItem("2T", rec);
			}
			
			kumiban = rec.getString("sanrentanno");
			if (kumiban.equals("123") || kumiban.equals("132") || kumiban.equals("213") || kumiban.equals("231") || kumiban.equals("312") || kumiban.equals("321")) {
				mapBetType.addItem("3T", rec);
			}
			
			kumiban = rec.getString("nirenhukuno");
			if (kumiban.equals("12") || kumiban.equals("13")) {
				mapBetType.addItem("2F", rec);
			}
			
			kumiban = rec.getString("sanrenhukuno");
			if (kumiban.equals("123")) {
				mapBetType.addItem("3F", rec);
			}
		}
		logger.info("db loaded. " + fromYmd + "~" + toYmd + ": " + listDbRecord.size() + " records.");
	}
}
