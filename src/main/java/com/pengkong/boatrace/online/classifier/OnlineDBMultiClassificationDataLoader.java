package com.pengkong.boatrace.online.classifier;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.exp10.simulation.data.AbstractMultiClassificationDataLoader;
import com.pengkong.boatrace.mybatis.client.OlClassificationMapper;
import com.pengkong.boatrace.mybatis.entity.MlClassification;
import com.pengkong.boatrace.mybatis.entity.OlClassification;
import com.pengkong.boatrace.mybatis.entity.OlClassificationExample;
import com.pengkong.boatrace.online.helper.DtoHelper;
import com.pengkong.boatrace.util.DatabaseUtil;

public class OnlineDBMultiClassificationDataLoader extends AbstractMultiClassificationDataLoader {
	/** MyBatis リソース名 */
	String targetDbResourceName; 
	
	public OnlineDBMultiClassificationDataLoader(String targetDbResourceName) {
		this.targetDbResourceName = targetDbResourceName;
	}

	@Override
	public List<MlClassification> load(List<String> modelNoList, String fromYmd, String toYmd) throws Exception {
		List<MlClassification> results = new ArrayList<>();
		List<OlClassification> resultsOl;
		
		SqlSession session = DatabaseUtil.open(targetDbResourceName, false);
		try {
			OlClassificationMapper mapper = session.getMapper(OlClassificationMapper.class);

			OlClassificationExample example = new OlClassificationExample();
			example.createCriteria().andModelnoIn(modelNoList).andYmdBetween(fromYmd, toYmd);
			resultsOl = mapper.selectByExample(example);
			
			for (OlClassification ol : resultsOl) {
				results.add(DtoHelper.copyOlClassification2MlClassification(ol));
			}
			
			return results;
			
		} finally {
			DatabaseUtil.close(session);
		}
	}
}
