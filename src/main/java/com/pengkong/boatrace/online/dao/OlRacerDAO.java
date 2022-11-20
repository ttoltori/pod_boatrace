package com.pengkong.boatrace.online.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.mybatis.client.OlRacerMapper;
import com.pengkong.boatrace.mybatis.entity.OlRacer;
import com.pengkong.boatrace.mybatis.entity.OlRacerExample;

public class OlRacerDAO extends AbstractDAO<OlRacer> {

	OlRacerMapper mapper;
	
	public OlRacerDAO(SqlSession session) {
		super(session);
		mapper = session.getMapper(OlRacerMapper.class);
	}

	@Override
	public int insertImplementation(OlRacer e) {
		return mapper.insert(e);
	}

	@Override
	public int updateImplementation(OlRacer e) {
		throw new IllegalAccessError("not allowed.");
	}

	public List<OlRacer> select(String ymd) {
		OlRacerExample example = new OlRacerExample();
		example.createCriteria().andYmdEqualTo(ymd);
		
		return mapper.selectByExample(example);
	}
	
	public int delete(String ymd) {
		OlRacerExample example = new OlRacerExample();
		example.createCriteria().andYmdEqualTo(ymd);

		return mapper.deleteByExample(example);
	}
}
