package com.pengkong.boatrace.online.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.mybatis.client.MlEvaluationMapper;
import com.pengkong.boatrace.mybatis.entity.MlEvaluation;
import com.pengkong.boatrace.mybatis.entity.MlEvaluationExample;

public class MlEvaluationDAO extends AbstractDAO<MlEvaluation> {

	MlEvaluationMapper mapper;

	public MlEvaluationDAO(SqlSession session) {
		super(session);
		mapper = session.getMapper(MlEvaluationMapper.class);
	}

	@Override
	public int insertImplementation(MlEvaluation e) {
		return mapper.insert(e);
	}

	@Override
	public int updateImplementation(MlEvaluation e) {
		MlEvaluationExample example = new MlEvaluationExample();
		example.createCriteria().andResultnoEqualTo(e.getResultno());
		
		return mapper.updateByExampleSelective(e, example);
	}
	
	public List<MlEvaluation> select(String resultNo) {
		MlEvaluationExample example = new MlEvaluationExample();
		example.createCriteria().andResultnoEqualTo(resultNo);
		
		return mapper.selectByExample(example);
	}

	public MlEvaluation selectOne(String resultNo) {
		MlEvaluationExample example = new MlEvaluationExample();
		example.createCriteria().andResultnoEqualTo(resultNo);
		
		List<MlEvaluation> results = mapper.selectByExample(example);
		if (results.size() > 0) {
			return results.get(0);
		}
		
		return null;
	}
}
