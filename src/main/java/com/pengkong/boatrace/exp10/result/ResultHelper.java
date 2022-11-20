package com.pengkong.boatrace.exp10.result;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.MathUtil;
import com.pengkong.common.StringUtil;

public class ResultHelper {
	
	/** 予測値の桁数チェックする */
	public static boolean isValidDigits(String betTypeStr, String[] predictions) {
		if (betTypeStr.equals("3X") || betTypeStr.equals("3Y")
		        || betTypeStr.equals("3A") || betTypeStr.equals("2A")) {
			return true;
		}
		
		if (betTypeStr.startsWith("1")) {
			return (predictions.length > 0 && !predictions[0].equals("x") );
		}

		if (betTypeStr.startsWith("2")) {
			return (predictions.length > 1 && !String.join("", predictions[0], predictions[1]).contains("x"));
		}

		if (betTypeStr.startsWith("3")) {
			return (predictions.length > 2 && !String.join("", predictions[0], predictions[1], predictions[2]).contains("x"));
		}

		return true;
	}

	/** 予測値の各桁が出力対象か判定する */
	public static boolean isValidRange(String betTypeStr, String[] predictions, String[] tokenKumiban) {
		if (betTypeStr.equals("3X") || betTypeStr.equals("3Y")
		        || betTypeStr.equals("3A") || betTypeStr.equals("2A")) {
			return true;
		}
		
		// simulation 70249에러발생. result_type이 simulation step 2 이면  kumiban 123 -> kumiban 1-2-3으로 자동변환하게 소스수정했다.
		// 예) tokenKumiban=[123]  -> "1-2-3" -> tokenKumiban=[1,2,3] 
		String resultType = MLPropertyUtil.getInstance().getString("result_type");
		
		// 20220827 simulation_step_2でエラー if (!resultType.startsWith("1") && !resultType.contains(Delimeter.DASH.getValue())) {
		if (!resultType.startsWith("1") && tokenKumiban.length == 1) {
			tokenKumiban = StringUtil.addDelimeter(tokenKumiban[0], Delimeter.DASH.getValue()).split(Delimeter.DASH.getValue());
		}
		
		if (betTypeStr.startsWith("1")) {
			return (tokenKumiban[0].equals("*") || tokenKumiban[0].contains(predictions[0]));
		}

		if (betTypeStr.startsWith("2")) {
			return ((tokenKumiban[0].equals("*") || tokenKumiban[0].contains(predictions[0]))
					&& (tokenKumiban[1].equals("*") || tokenKumiban[1].contains(predictions[1]))
					);
		}

		if (betTypeStr.startsWith("3")) {
			return ((tokenKumiban[0].equals("*") || tokenKumiban[0].contains(predictions[0]))
					&& (tokenKumiban[1].equals("*") || tokenKumiban[1].contains(predictions[1]))
					&& (tokenKumiban[2].equals("*") || tokenKumiban[2].contains(predictions[2]))
					);
		}

		return true;
	}
	
	/** 予測組番に重複が存在するかチェックする */
	public static boolean isUnique(String modelno, String betTypeStr, String[] predictions) {
		// prediction桁数が１桁だけのmodel、simulationの場合はスキップする。 2022/9/3
		if (modelno.equals("x") || modelno.endsWith("102") || modelno.endsWith("103")) {
			return true;
		}
			
			
		if (betTypeStr.startsWith("2")) {
			return (!predictions[0].equals(predictions[1]));
		}

		if (betTypeStr.startsWith("3")) {
			return (!predictions[0].equals(predictions[1]) && !predictions[1].equals(predictions[2]) && !predictions[2].equals(predictions[0]));
		}

		return true;
	}

	/**
	 * DB取得値から予測値をリストで返却する.
	 * DBRecordにprediction1, prediction2, prediction3のいずれが設定されていれば前からconcatして取得する。
	 * 各predictionにそれぞれ２桁以上の数値が入った場合にも対応している
	 * @param rec
	 */
	public static String[] getPredictions(DBRecord dbRec) throws IllegalStateException {
		List<String> listPrediction = new ArrayList<>();
		for (int i = 1; i <=3 ; i++) {
			String prediction = dbRec.getString("prediction" + i);
			if (prediction == null) {
				continue;
			}
			String[] predictionToken = prediction.split("");
			for (int j = 0; j < predictionToken.length; j++) {
				listPrediction.add(predictionToken[j]);
			}
		}
		
		if (listPrediction.size() <= 0) {
			throw new IllegalStateException("invalid prediction is retrieved from DB." + dbRec);
		}

		return listPrediction.toArray(new String[0]);
	}
	
	/** 統計単位用のMLで予測した組番を取得する。(ex) 2Fの場合、betKumiban = 12, predictions = 21）
	 * 3X,3Y追加 2022/9/2
	 * */
	public static String getPrediction(MlResult result) {
		String betType = result.getStatBettype();
		if (BetType._1T.getValue().equals(betType) || BetType._1F.getValue().equals(betType)
		        || BetType._3A.getValue().equals(betType) || BetType._2A.getValue().equals(betType)) {
			return result.getPredictRank123().substring(0,1);
		} else if (BetType._2T.getValue().equals(betType) || BetType._2F.getValue().equals(betType) 
				|| BetType._2M.getValue().equals(betType) || BetType._2N.getValue().equals(betType)
				|| BetType._3X.getValue().equals(betType) || BetType._3Y.getValue().equals(betType)
				) {
			return result.getPredictRank123().substring(0,2);
		} else {
			return result.getPredictRank123().substring(0,3);
		}
	}

	/**
	 * 指定のbetting金額で収益を計算してresultに設定して返却する。
	 * @param result 投票結果
	 * @param betAmt betthing金額
	 */
	public static MlResult calculateIncome(MlResult result) {
		result.setBetamt(MathUtil.ceilAmount(result.getBetamt(), 100));
		// hity, hitn, 
		// 적중여부
		if (result.getBetKumiban().equals(result.getResultKumiban())) {
			result.setHity(1);
			result.setHitn(0);
			result.setHitamt((int)(result.getBetamt() * result.getRaceOdds()));
		} else {
			result.setHity(0);
			result.setHitn(1);
			result.setHitamt(0);
		}
		
		return result;
	}
	
	/** 区間かした残高リストに対して、最初区間以後の区間に対して残高を再設定させる
	 * （前区内の前金額に対して、以前区間の最終残高を減額する）
	 * 全区間は同一サイズである条件
	 * @param startBalance 最初残高
	 * */
	public static void applyBalancing(List<Double>[] arrBalancelist, int startBalance) throws IllegalStateException {
		// 区間毎のサイズを取得
		int size = arrBalancelist[0].size();
		for (int i = 0; i < arrBalancelist.length; i++) {
			if (arrBalancelist[i].size() != size) {
				throw new IllegalStateException("残高分割処理が不正");
			}
		}
		
		// 全区間の最終残高を取得
		Double[] arrLastBalance = new Double[arrBalancelist.length];
		for (int i = 0; i < arrBalancelist.length; i++) {
			arrLastBalance[i] = arrBalancelist[i].get(size-1);
		}

		// 最初以後の区間をループ(i=0でなく1)
		for (int i = 1; i < arrBalancelist.length; i++) {
			for (int j = 0; j < size; j++) {
				// 残高 - 以前区間の最終残高 + 最初残高
				arrBalancelist[i].set(j, arrBalancelist[i].get(j) - arrLastBalance[i-1] + startBalance);	
			}
		}
	}
	
	public static MlResult copyOf(MlResult src) {
		MlResult result = new MlResult();
		
		result.setResultno(src.getResultno());
		result.setModelno(src.getModelno());
		result.setPatternid(src.getPatternid());
		result.setPattern(src.getPattern());
		result.setYmd(src.getYmd());
		result.setJyocd(src.getJyocd());
		result.setRaceno(src.getRaceno());
		result.setSime(src.getSime());
		
		result.setPredictRank123(src.getPredictRank123());
		result.setResultRank123(src.getResultRank123());
		result.setBettype(src.getBettype());
		result.setStatBettype(src.getStatBettype());
		result.setBetKumiban(src.getBetKumiban());
		result.setBetOdds(src.getBetOdds());
		result.setBetOddsrank(src.getBetOddsrank());
		result.setResultKumiban(src.getResultKumiban());
		result.setResultOdds(src.getResultOdds());
		result.setResultOddsrank(src.getResultOddsrank());
		result.setResultAmt(src.getResultAmt());
		result.setRaceOdds(src.getRaceOdds());
		result.setRaceOddsrank(src.getRaceOddsrank());

		result.setHity(src.getHity());
		result.setHitn(src.getHitn());
		result.setBetamt(src.getBetamt());
		result.setHitamt(src.getHitamt());
		result.setProbability(src.getProbability());
		result.setResultType(src.getResultType());
		result.setCustom(src.getCustom());
		result.setHitrateTransition(src.getHitrateTransition());
		result.setIncomerateTransition(src.getIncomerateTransition());
		result.setBalance(src.getBalance());
		
		return result;
	}
}
