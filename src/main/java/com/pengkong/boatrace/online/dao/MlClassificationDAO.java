package com.pengkong.boatrace.online.dao;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.mybatis.client.MlClassificationMapper;
import com.pengkong.boatrace.mybatis.entity.MlClassification;

public class MlClassificationDAO extends AbstractDAO<MlClassification> {

	MlClassificationMapper mapper;

	public MlClassificationDAO(SqlSession session) {
		super(session);
		mapper = session.getMapper(MlClassificationMapper.class);
	}

	@Override
	public int insertImplementation(MlClassification e) {
		return mapper.insert(e);
	}

	@Override
	public int updateImplementation(MlClassification e) {
		throw new IllegalAccessError("not allowed.");
	}
}
