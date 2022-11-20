package com.pengkong.boatrace.online.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.mybatis.client.MlResultMapper;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.mybatis.entity.MlResultExample;

public class MlResultDAO extends AbstractDAO<MlResult> {

	MlResultMapper mapper;

	public MlResultDAO(SqlSession session) {
		super(session);
		mapper = session.getMapper(MlResultMapper.class);
	}

	@Override
	public int insertImplementation(MlResult e) {
		return mapper.insert(e);
	}

	@Override
	public int updateImplementation(MlResult e) {
		MlResultExample example = new MlResultExample();
		example.createCriteria().andYmdEqualTo(e.getYmd()).andJyocdEqualTo(e.getJyocd()).andRacenoEqualTo(e.getRaceno())
			.andBettypeEqualTo(e.getBettype()).andBetKumibanEqualTo(e.getBetKumiban());
		
		return mapper.updateByExampleSelective(e, example);
	}
	
	public List<MlResult> select(String ymd, String jyoCd, Short raceNo) {
		MlResultExample example = new MlResultExample();
		example.createCriteria().andYmdEqualTo(ymd).andJyocdEqualTo(jyoCd).andRacenoEqualTo(raceNo);
		
		return mapper.selectByExample(example);
	}
}
