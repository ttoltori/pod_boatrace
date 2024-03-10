package com.pengkong.boatrace.online.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.mybatis.client.OlRacerArrMapper;
import com.pengkong.boatrace.mybatis.entity.OlRacerArr;
import com.pengkong.boatrace.mybatis.entity.OlRacerArrExample;

public class OlRacerArrDAO extends AbstractDAO<OlRacerArr> {

	OlRacerArrMapper mapper;
	
	public OlRacerArrDAO(SqlSession session) {
		super(session);
		mapper = session.getMapper(OlRacerArrMapper.class);
	}

	@Override
	public int insertImplementation(OlRacerArr e) {
		return mapper.insert(e);
	}

	@Override
	public int updateImplementation(OlRacerArr e) {
		throw new IllegalAccessError("not allowed.");
	}

	public List<OlRacerArr> select(String ymd) {
		OlRacerArrExample example = new OlRacerArrExample();
		example.createCriteria().andYmdEqualTo(ymd);
		
		return mapper.selectByExample(example);
	}
	
	public int delete(String ymd) {
		OlRacerArrExample example = new OlRacerArrExample();
		example.createCriteria().andYmdEqualTo(ymd);

		return mapper.deleteByExample(example);
	}
}
