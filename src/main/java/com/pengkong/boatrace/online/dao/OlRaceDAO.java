package com.pengkong.boatrace.online.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.mybatis.client.OlRaceMapper;
import com.pengkong.boatrace.mybatis.entity.OlRace;
import com.pengkong.boatrace.mybatis.entity.OlRaceExample;

public class OlRaceDAO extends AbstractDAO<OlRace> {

	OlRaceMapper mapper;
	
	public OlRaceDAO(SqlSession session) {
		super(session);
		mapper = session.getMapper(OlRaceMapper.class);
	}

	@Override
	public int insertImplementation(OlRace e) {
		return mapper.insert(e);
	}

	@Override
	public int updateImplementation(OlRace e) {
		OlRaceExample example = new OlRaceExample();
		example.createCriteria().andYmdEqualTo(e.getYmd()).andJyocdEqualTo(e.getJyocd()).andRacenoEqualTo(e.getRaceno());

		return mapper.updateByExampleSelective(e, example);
	}

	public List<OlRace> select(OlRaceExample example) {
		return mapper.selectByExample(example);
	}
	
	public int delete(String ymd) {
		OlRaceExample example = new OlRaceExample();
		example.createCriteria().andYmdEqualTo(ymd);

		return mapper.deleteByExample(example);
	}
}
