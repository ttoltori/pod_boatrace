package com.pengkong.boatrace.online.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.mybatis.client.MlResultMapper;
import com.pengkong.boatrace.mybatis.client.OlResultMapper;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.mybatis.entity.MlResultExample;
import com.pengkong.boatrace.mybatis.entity.OlResult;
import com.pengkong.boatrace.mybatis.entity.OlResultExample;

public class OlResultDAO extends AbstractDAO<OlResult> {

	OlResultMapper mapper;

	public OlResultDAO(SqlSession session) {
		super(session);
		mapper = session.getMapper(OlResultMapper.class);
	}

	@Override
	public int insertImplementation(OlResult e) {
		return mapper.insert(e);
	}

	@Override
	public int updateImplementation(OlResult e) {
		OlResultExample example = new OlResultExample();
		example.createCriteria().andYmdEqualTo(e.getYmd()).andJyocdEqualTo(e.getJyocd()).andRacenoEqualTo(e.getRaceno())
			.andBettypeEqualTo(e.getBettype()).andBetKumibanEqualTo(e.getBetKumiban());
		
		return mapper.updateByExampleSelective(e, example);
	}
	
	public List<OlResult> select(String ymd, String jyoCd, Integer raceNo) {
		OlResultExample example = new OlResultExample();
		example.createCriteria().andYmdEqualTo(ymd).andJyocdEqualTo(jyoCd).andRacenoEqualTo(raceNo);
		
		return mapper.selectByExample(example);
	}
}
