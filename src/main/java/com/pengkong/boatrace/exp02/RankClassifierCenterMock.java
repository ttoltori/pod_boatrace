package com.pengkong.boatrace.exp02;

import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.common.DBCol;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.server.db.dto.RankDbRecord;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.PropertyUtil;

public class RankClassifierCenterMock extends RankClassifierCenter {

	PropertyUtil prop = PropertyUtil.getInstance();
	
	// key = description+pattern+ymd+jyocd+raceno
	HashMap<String, String> mapPredict = new HashMap<>();
	private final String DELIM = "+"; 
	public RankClassifierCenterMock() {
	}
	
	@Override
	public void initialize(List<RankDbRecord> listRankFilter) throws Exception {
		loadDB();
	}
	
	@Override
	public String predict(RankDbRecord rec) throws Exception {
		String key = createKey(rec);
		String value = mapPredict.get(key);
		
		return value;
	}
	
	private void loadDB() {
		CustomMapper mapper = DatabaseUtil.getSession().getMapper(CustomMapper.class);
		HashMap<String, String> param = new HashMap<>();
		param.put("fromYmd", prop.getString("simulationFromYmd"));
		param.put("toYmd", prop.getString("simulationToYmd"));
		List<DBRecord> result = mapper.selectRankClassifierCenter(param);
		for (DBRecord rec : result) {
			String key = createKey(rec);
			String value = rec.getString(DBCol.PREDICT_RANK123);
			mapPredict.put(key, value);
		}
	}
	
	private String createKey(DBRecord rec) {
		return rec.getString(DBCol.DESCRIPTION) + DELIM + 
				rec.getString(DBCol.PATTERN) + DELIM + 
				rec.getString(DBCol.YMD) + DELIM + 
				rec.getString(DBCol.JYOCD) + DELIM + 
				rec.getString(DBCol.RACENO);
	}
}
