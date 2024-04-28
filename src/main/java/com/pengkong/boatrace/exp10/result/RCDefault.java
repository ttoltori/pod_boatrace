package com.pengkong.boatrace.exp10.result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.exp10.odds.provider.ResultOddsProvider;
import com.pengkong.boatrace.exp10.simulation.calculator.expectation.ProbabilityExpCalculatorFactory;
import com.pengkong.boatrace.exp10.simulation.calculator.probability.ProbabilityCalculatorFactory;
import com.pengkong.boatrace.exp10.simulation.data.rmi.client.RmiBeforeOddsProvider;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.server.db.dto.DBRecord;

/**
 * 投票結果を生成するdefaultクラス
 * 
 * @author ttolt
 *
 */
public class RCDefault extends AbstractResultCreator {

	Logger logger = LoggerFactory.getLogger(RCDefault.class);

	public RCDefault() {
		super();
	}

	@Override
	protected void preExecute() {
		// 予想確率組み合わせクラス (statBettype기준)
		probabilityCalculator = ProbabilityCalculatorFactory.create();
		
		// 기대치 계산용  예상확률을 취득 (bettype기준) 
		probabilityExpCalculator = ProbabilityExpCalculatorFactory.create();
		
		// 直前オッズ
		beforeOddsProvider = new RmiBeforeOddsProvider();
		//beforeOddsProvider = new BeforeOddsProvider();
		
		// 確定オッズ
		//resultOddsProvider = new RmiResultOddsProvider();
		resultOddsProvider = new ResultOddsProvider();
	}

	@Override
	protected List<MlResult> get1Tresult(String kumiban, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		result.add(createDefault(BetType._1T, BetType._1T, kumiban, rec));
		
		return result;
	}

	@Override
	protected List<MlResult> get2Tresult(String kumiban, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		result.add(createDefault(BetType._2T, BetType._2T, kumiban, rec));
		
		return result;
	}

	@Override
	protected List<MlResult> get3Tresult(String kumiban, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		result.add(createDefault(BetType._3T, BetType._3T, kumiban, rec));

		return result;
	}

	@Override
	protected List<MlResult> get2Fresult(String kumiban, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		result.add(createDefault(BetType._2F, BetType._2F, kumiban, rec));

		return result;
	}

	@Override
	protected List<MlResult> get3Fresult(String kumiban, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		result.add(createDefault(BetType._3F, BetType._3F, kumiban, rec));

		return result;
	}

	@Override
	protected List<MlResult> get2Mresult(String[] predictions, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		result.add(createDefault(BetType._2M, BetType._2T, String.join("", predictions[0], predictions[1]), rec));
		result.add(createDefault(BetType._2M, BetType._2T, String.join("", predictions[1], predictions[0]), rec));
		return result;
	}

	@Override
	protected List<MlResult> get3Nresult(String[] predictions, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		result.add(createDefault(BetType._3N, BetType._2T, String.join("", predictions[0], predictions[1]), rec));
		result.add(createDefault(BetType._3N, BetType._2T, String.join("", predictions[0], predictions[2]), rec));

		return result;
	}

	@Override
	// // ３連単1-2-3456, 4点
	protected List<MlResult> get2Nresult(String[] predictions, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<String> kumiban3List = getRemainKumibans(predictions[0], predictions[1]);
		for (String kumiban3 : kumiban3List) {
			result.add(createDefault(BetType._2N, BetType._3T,
					String.join("", predictions[0], predictions[1], kumiban3), rec));
		}

		return result;
	}

	@Override
	// ３連単1-2-3, 1-3-2, 2-1-3, 3-1-2 4点
	protected List<MlResult> get3Mresult(String[] predictions, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		result.add(createDefault(BetType._3M, BetType._3T,
				String.join("", predictions[0], predictions[1], predictions[2]), rec));
		result.add(createDefault(BetType._3M, BetType._3T,
				String.join("", predictions[0], predictions[2], predictions[1]), rec));
		result.add(createDefault(BetType._3M, BetType._3T,
				String.join("", predictions[1], predictions[0], predictions[2]), rec));
		result.add(createDefault(BetType._3M, BetType._3T,
				String.join("", predictions[2], predictions[0], predictions[1]), rec));

		return result;
	}

	@Override
	// ３連単1-2-3, 1-3-2, 2-1-3, 2-3-1, 3-1-2, 3-2-1 6点
	protected List<MlResult> get3Presult(String[] predictions, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		result.add(createDefault(BetType._3P, BetType._3T,
				String.join("", predictions[0], predictions[1], predictions[2]), rec));
		result.add(createDefault(BetType._3P, BetType._3T,
				String.join("", predictions[0], predictions[2], predictions[1]), rec));
		result.add(createDefault(BetType._3P, BetType._3T,
				String.join("", predictions[1], predictions[0], predictions[2]), rec));
		result.add(createDefault(BetType._3P, BetType._3T,
				String.join("", predictions[1], predictions[2], predictions[0]), rec));
		result.add(createDefault(BetType._3P, BetType._3T,
				String.join("", predictions[2], predictions[0], predictions[1]), rec));
		result.add(createDefault(BetType._3P, BetType._3T,
				String.join("", predictions[2], predictions[1], predictions[0]), rec));

		return result;
	}
	
	@Override
	// ３連単1-2-3456, 1-3-2456  8点
	protected List<MlResult> get3Rresult(String[] predictions, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<String> kumiban3List = getRemainKumibans(predictions[0], predictions[1]);
		for (String kumiban3 : kumiban3List) {
			result.add(createDefault(BetType._3R, BetType._3T,
					String.join("", predictions[0], predictions[1], kumiban3), rec));
		}

		kumiban3List = getRemainKumibans(predictions[0], predictions[2]);
		for (String kumiban3 : kumiban3List) {
			result.add(createDefault(BetType._3R, BetType._3T,
					String.join("", predictions[0], predictions[2], kumiban3), rec));
		}

		return result;
	}
	
	@Override
	@Deprecated
	/**
	 * ３連単1-2-3456, 1-3456-2  8点
	 *  
	 * 3X와 같지만 ResultHelper}.getPrediction에 반영을 안해 3단위 통계를 생성해버렸으므로 3X로 대체한다.
	 */
	protected List<MlResult> get3Uresult(String[] predictions, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<String> kumiban3List = getRemainKumibans(predictions[0], predictions[1]);
		for (String kumiban3 : kumiban3List) {
			result.add(createDefault(BetType._3U, BetType._3T,
					String.join("", predictions[0], predictions[1], kumiban3), rec));
		}

		for (String kumiban3 : kumiban3List) {
			result.add(createDefault(BetType._3U, BetType._3T,
					String.join("", predictions[0], kumiban3, predictions[1]), rec));
		}

		return result;
	}

	@Override
	// ３連単1-2-3456, 1-3456-2  8点
	protected List<MlResult> get3Xresult(String[] predictions, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<String> kumiban3List = getRemainKumibans(predictions[0], predictions[1]);
		for (String kumiban3 : kumiban3List) {
			result.add(createDefault(BetType._3X, BetType._3T,
					String.join("", predictions[0], predictions[1], kumiban3), rec));
		}

		for (String kumiban3 : kumiban3List) {
			result.add(createDefault(BetType._3X, BetType._3T,
					String.join("", predictions[0], kumiban3, predictions[1]), rec));
		}

		return result;
	}

	@Override
	// ３連単1-3456-2, 2-3456-1  8点
	protected List<MlResult> get3Yresult(String[] predictions, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<String> kumiban3List = getRemainKumibans(predictions[0], predictions[1]);
		for (String kumiban3 : kumiban3List) {
			result.add(createDefault(BetType._3Y, BetType._3T,
					String.join("", predictions[0], kumiban3, predictions[1]), rec));
		}

		for (String kumiban3 : kumiban3List) {
			result.add(createDefault(BetType._3Y, BetType._3T,
					String.join("", predictions[1], kumiban3, predictions[0]), rec));
		}

		return result;
	}
	
    @Override
    // 
    protected List<MlResult> get3Aresult(String[] predictions, DBRecord rec) throws Exception {
        List<MlResult> result = new ArrayList<>();
        List<String> kumiban2List = getRemainKumibans(predictions[0]);
        for (String kumiban2 : kumiban2List) {
            List<String> kumiban3List = getRemainKumibans(predictions[0], kumiban2);
            for (String kumiban3 : kumiban3List) {
                result.add(createDefault(BetType._3A, BetType._3T,
                        String.join("", predictions[0], kumiban2, kumiban3), rec));
            }
        }

        return result;
    }

    @Override
    // 
    protected List<MlResult> get2Aresult(String[] predictions, DBRecord rec) throws Exception {
        List<MlResult> result = new ArrayList<>();
        List<String> kumiban2List = getRemainKumibans(predictions[0]);
        for (String kumiban2 : kumiban2List) {
            result.add(createDefault(BetType._2A, BetType._2T,
                    String.join("", predictions[0], kumiban2), rec));
        }

        return result;
    }
    
	/**
	 * 組番1,2,3,4,5,6からstrings以外を返却する
	 * 
	 * @param strings 除外するnum ex)1,2
	 * @return ex)3,4,5,6
	 */
	private final List<String> getRemainKumibans(String... strings) {
		List<String> numList = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6"));
		for (String str : strings) {
			numList.remove(str);
		}

		return numList;
	}

	@Override
	// ２連複 12,13 2点 (통계단위 3자리)
	protected List<MlResult> get2Gresult(String[] predictions, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		result.add(createDefault(BetType._2G, BetType._2F, String.join("", predictions[0], predictions[1]), rec));
		result.add(createDefault(BetType._2G, BetType._2F, String.join("", predictions[0], predictions[2]), rec));

		return result;
	}

	@Override
	// ３連複 1-2-3456 4点 (통계단위 2자리)
	protected List<MlResult> get3Gresult(String[] predictions, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<String> kumiban3List = getRemainKumibans(predictions[0], predictions[1]);
		for (String kumiban3 : kumiban3List) {
			result.add(createDefault(BetType._3G, BetType._3F,
					String.join("", predictions[0], predictions[1], kumiban3), rec));
		}

		return result;
	}

	@Override
	// 3連単 123,132 2点 (통계단위 3자리)
	protected List<MlResult> get3Bresult(String[] predictions, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		result.add(createDefault(BetType._3B, BetType._3T, String.join("", predictions[0], predictions[1], predictions[2]), rec));
		result.add(createDefault(BetType._3B, BetType._3T, String.join("", predictions[0], predictions[2], predictions[1]), rec));

		return result;
	}
	
	@Override
	// 3連単 123,124 2点 (통계단위 4자리)
	protected List<MlResult> get3Cresult(String[] predictions, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		result.add(createDefault(BetType._3C, BetType._3T, String.join("", predictions[0], predictions[1], predictions[2]), rec));
		result.add(createDefault(BetType._3C, BetType._3T, String.join("", predictions[0], predictions[1], predictions[3]), rec));

		return result;
	}

	@Override
	// 3連単 123,124,213,214 4点 (통계단위 4자리)
	protected List<MlResult> get3Dresult(String[] predictions, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		result.add(createDefault(BetType._3D, BetType._3T, String.join("", predictions[0], predictions[1], predictions[2]), rec));
		result.add(createDefault(BetType._3D, BetType._3T, String.join("", predictions[0], predictions[1], predictions[3]), rec));
		result.add(createDefault(BetType._3D, BetType._3T, String.join("", predictions[1], predictions[0], predictions[2]), rec));
		result.add(createDefault(BetType._3D, BetType._3T, String.join("", predictions[1], predictions[0], predictions[3]), rec));

		return result;
	}

	@Override
	// 3連単 123,124,132,134,142,143 6点 (통계단위 4자리)
	protected List<MlResult> get3Eresult(String[] predictions, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		result.add(createDefault(BetType._3E, BetType._3T, String.join("", predictions[0], predictions[1], predictions[2]), rec));
		result.add(createDefault(BetType._3E, BetType._3T, String.join("", predictions[0], predictions[1], predictions[3]), rec));
		result.add(createDefault(BetType._3E, BetType._3T, String.join("", predictions[0], predictions[2], predictions[1]), rec));
		result.add(createDefault(BetType._3E, BetType._3T, String.join("", predictions[0], predictions[2], predictions[3]), rec));
		result.add(createDefault(BetType._3E, BetType._3T, String.join("", predictions[0], predictions[3], predictions[1]), rec));
		result.add(createDefault(BetType._3E, BetType._3T, String.join("", predictions[0], predictions[3], predictions[2]), rec));

		return result;
	}
}
