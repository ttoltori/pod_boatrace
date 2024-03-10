package com.pengkong.boatrace.exp10.simulation.data.rmi.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.property.SqlTemplate;
import com.pengkong.boatrace.mybatis.client.MlClassificationMapper;
import com.pengkong.boatrace.mybatis.entity.MlClassification;
import com.pengkong.boatrace.mybatis.entity.MlClassificationExample;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.collection.HashMapList;

public class MultiClassificationDataServer {
	Logger logger = LoggerFactory.getLogger(MultiClassificationDataServer.class);
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/** sql template */
	SqlTemplate sqlTpl = SqlTemplate.getInstance();
	
	/** key=modelno, value=list of MlClassification */
	HashMapList<MlClassification> mapList;
	
	public List<MlClassification> getClassificationData(List<String> modelNoList) throws Exception {
		List<MlClassification> result = new ArrayList<>();

		for (String modelNo : modelNoList) {
			List<MlClassification> list = mapList.get(modelNo);
			if (list == null) {
				logger.warn("classification data does not exist. " + modelNo);
				continue;
			}
			
			result.addAll(list);
		}
		
		return result;
	}
	
	void load() throws Exception {
		mapList = new HashMapList<>();
		
		String[] period = prop.getString("dataserver_simulation_period").split(Delimeter.WAVE.getValue());
		List<String> modelNoList = Arrays
				.asList(prop.getString("dataserver_classification_models").split(Delimeter.COMMA.getValue()));
		String resource = prop.getString("target_db_resource");
		
		List<MlClassification> listData = loadDB(modelNoList, period[0], period[1], resource);
		
		// 결과범위내 데이터를 날짜별로 분류
		for (MlClassification rec : listData) {
			mapList.addItem(rec.getModelno(), rec);
		}
		
		logger.info("classification data loaded from db. " + period[0] + "~" + period[1]);
	}
	
	List<MlClassification> loadDB(List<String> modelNoList, String fromYmd, String toYmd, String resource) throws Exception {
		List<MlClassification> result = new ArrayList<>();
		
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		try {
			MlClassificationMapper mapper = session.getMapper(MlClassificationMapper.class);

			MlClassificationExample example = new MlClassificationExample();
			example.createCriteria().andModelnoIn(modelNoList).andYmdBetween(fromYmd, toYmd);
			
			result.addAll(mapper.selectByExample(example));
			
			return result;
		} finally {
			DatabaseUtil.close(session);
		}
	}
	
	public static void main(String[] args) {
		try {
			MLPropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties");
			MultiClassificationDataServer server = new MultiClassificationDataServer();
			server.load();
			
			List<MlClassification> result = server.getClassificationData(Arrays.asList("99063"));
			System.out.println(result.size());			
			
			result = server.getClassificationData(Arrays.asList("99063", "99094"));
			System.out.println(result.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
