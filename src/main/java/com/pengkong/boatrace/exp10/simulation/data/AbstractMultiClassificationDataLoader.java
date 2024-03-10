package com.pengkong.boatrace.exp10.simulation.data;

import java.util.List;

import com.pengkong.boatrace.mybatis.entity.MlClassification;

public abstract class AbstractMultiClassificationDataLoader {

	public abstract List<MlClassification> load(List<String> modelNoList, String fromYmd, String toYmd) throws Exception;
}
