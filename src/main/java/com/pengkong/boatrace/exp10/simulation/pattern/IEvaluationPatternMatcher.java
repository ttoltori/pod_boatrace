package com.pengkong.boatrace.exp10.simulation.pattern;

import java.util.List;

import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;
import com.pengkong.boatrace.server.db.dto.DBRecord;

public interface IEvaluationPatternMatcher {
	public List<Evaluation> match(String betType, String kumiban, String modelNo, DBRecord rec) throws Exception;
}
