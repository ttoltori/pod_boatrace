package com.pengkong.boatrace.online.helper;

import com.pengkong.boatrace.mybatis.entity.MlClassification;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.mybatis.entity.OlClassification;
import com.pengkong.boatrace.mybatis.entity.OlResult;

public class DtoHelper {

	public static MlClassification copyOlClassification2MlClassification(OlClassification ol) {
		MlClassification ml = new MlClassification();
		ml.setModelno(ol.getModelno());
		ml.setYmd(ol.getYmd());
		ml.setJyocd(ol.getJyocd());
		ml.setRaceno(ol.getRaceno().shortValue());
		ml.setSime(ol.getSime());
		ml.setPrediction1(ol.getPrediction1());
		ml.setProbability1(ol.getProbability1());
		ml.setPrediction2(ol.getPrediction2());
		ml.setProbability2(ol.getProbability2());
		ml.setPrediction3(ol.getPrediction3());
		ml.setProbability3(ol.getProbability3());
		
		return ml;
	}
	
	public static OlResult copyMlResult2OlResult(MlResult ml) {
		OlResult ol = new OlResult();
		ol.setYmd(ml.getYmd());
		ol.setJyocd(ml.getJyocd());
		ol.setRaceno(ml.getRaceno().intValue());
		ol.setSime(ml.getSime());
		ol.setPredictRank123(ml.getPredictRank123());
		ol.setResultRank123(ml.getResultRank123());
		ol.setBettype(ml.getBettype());
		ol.setBetKumiban(ml.getBetKumiban());
		ol.setBetOdds(ml.getBetOdds());
		ol.setBetOddsrank(ml.getBetOddsrank());
		ol.setResultKumiban(ml.getResultKumiban());
		ol.setResultOdds(ml.getResultOdds());
		ol.setResultOddsrank(ml.getResultOddsrank());
		ol.setResultAmt(ml.getResultAmt());
		ol.setHity(ml.getHity());
		ol.setHitn(ml.getHitn());
		ol.setBetamt(ml.getBetamt());
		ol.setHitamt(ml.getHitamt());
		ol.setProbability(ml.getProbability());
		ol.setCustom(ml.getCustom());
		
		return ol;
	}
}
