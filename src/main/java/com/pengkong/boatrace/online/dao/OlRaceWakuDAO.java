package com.pengkong.boatrace.online.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.mybatis.client.OlRaceWakuMapper;
import com.pengkong.boatrace.mybatis.entity.OlRaceWaku;
import com.pengkong.boatrace.mybatis.entity.OlRaceWakuExample;

public class OlRaceWakuDAO extends AbstractDAO<OlRaceWaku> {

	OlRaceWakuMapper mapper;
	
	public OlRaceWakuDAO(SqlSession session) {
		super(session);
		mapper = session.getMapper(OlRaceWakuMapper.class);
	}

	@Override
	public int insertImplementation(OlRaceWaku e) {
		return mapper.insert(e);
	}

	@Override
	public int updateImplementation(OlRaceWaku e) {
		throw new IllegalAccessError("not allowed.");
	}

	public List<OlRaceWaku> select(String ymd) {
		OlRaceWakuExample example = new OlRaceWakuExample();
		example.createCriteria().andYmdEqualTo(ymd);
		
		return mapper.selectByExample(example);
	}
	
	public int delete(String ymd) {
		OlRaceWakuExample example = new OlRaceWakuExample();
		example.createCriteria().andYmdEqualTo(ymd);

		return mapper.deleteByExample(example);
	}
}
