package com.pengkong.boatrace.online.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.mybatis.client.OlClassificationMapper;
import com.pengkong.boatrace.mybatis.entity.OlClassification;
import com.pengkong.boatrace.mybatis.entity.OlClassificationExample;

public class OlClassificationDAO extends AbstractDAO<OlClassification> {

	OlClassificationMapper mapper;
	
	public OlClassificationDAO(SqlSession session) {
		super(session);
		mapper = session.getMapper(OlClassificationMapper.class);
	}

	@Override
	public int insertImplementation(OlClassification e) {
		return mapper.insert(e);
	}

	@Override
	public int updateImplementation(OlClassification e) {
		throw new IllegalAccessError("not allowed.");
	}

	public List<OlClassification> select(OlClassificationExample example) {
		return mapper.selectByExample(example);
	}
	
	public int delete(String ymd) {
		OlClassificationExample example = new OlClassificationExample();
		example.createCriteria().andYmdEqualTo(ymd);

		return mapper.deleteByExample(example);
	}
}
