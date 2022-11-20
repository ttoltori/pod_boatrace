package com.pengkong.boatrace.exp10.simulation.data;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.mybatis.client.MlClassificationMapper;
import com.pengkong.boatrace.mybatis.entity.MlClassification;
import com.pengkong.boatrace.mybatis.entity.MlClassificationExample;
import com.pengkong.boatrace.util.DatabaseUtil;

public class DBMultiClassificationDataLoader extends AbstractMultiClassificationDataLoader {
	/** MyBatis リソース名 */
	String targetDbResourceName; 
	
	public DBMultiClassificationDataLoader(String targetDbResourceName) {
		this.targetDbResourceName = targetDbResourceName;
	}

	@Override
	public List<MlClassification> load(List<String> modelNoList, String fromYmd, String toYmd) throws Exception {
		List<MlClassification> results;
		
		SqlSession session = DatabaseUtil.open(targetDbResourceName, false);
		MlClassificationMapper mapper = session.getMapper(MlClassificationMapper.class);

		MlClassificationExample example = new MlClassificationExample();
		example.createCriteria().andModelnoIn(modelNoList).andYmdBetween(fromYmd, toYmd);

		results = mapper.selectByExample(example);
		
		DatabaseUtil.close(session);
		return results;
	}
}
