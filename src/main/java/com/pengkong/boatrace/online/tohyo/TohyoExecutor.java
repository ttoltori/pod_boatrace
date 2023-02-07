package com.pengkong.boatrace.online.tohyo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.mybatis.entity.OlRace;
import com.pengkong.boatrace.online.api.AbstractApiProvider;
import com.pengkong.boatrace.online.dao.MlResultDAO;
import com.pengkong.boatrace.online.exception.ApiException;
import com.pengkong.boatrace.online.exception.SimulatiorException;
import com.pengkong.boatrace.online.result.OnlineSimulationGenerator;
import com.pengkong.boatrace.online.tohyo.bet.Bet;
import com.pengkong.boatrace.online.tohyo.bet.BetRequest;
import com.pengkong.boatrace.util.DatabaseUtil;

/**
 * 投票を実施するクラス
 * @author ttolt
 *
 */
public class TohyoExecutor {
	MLPropertyUtil prop =  MLPropertyUtil.getInstance();
	
	/** API */
	protected AbstractApiProvider apiProvider;
	
	protected OnlineSimulationGenerator simulator;
	
	public TohyoExecutor(AbstractApiProvider apiProvider) {
		super();
		this.apiProvider = apiProvider;
		simulator = new OnlineSimulationGenerator();
	}

	/**
	 * ML判定及び投票API呼び出し。ml_resultに登録する
	 * @param race 対象レース
	 * @return 登録したml_resultの件数
	 * @throws SQLException 
	 */
	public int execute(OlRace race) throws SimulatiorException, ApiException, IOException, SQLException {
		List<MlResult> results;
		// simulator実施
		try {
			results = simulator.execute(race);
		} catch (Exception e) {
			throw new SimulatiorException(e);
		}
		
		// 投票実施
		BetRequest betReq = convert(race, results);
		try {
			apiProvider.bet(betReq);
		} catch (Exception e) {
			throw new ApiException(e);
		}
		
		// DB insert
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		try {
			MlResultDAO dao = new MlResultDAO(session);
			for (MlResult result : results) {
				dao.insert(result);
			}
			session.commit();
			
			return results.size();
		} finally {
			DatabaseUtil.close(session);
		}
	}

	/**
	 * Simulation結果からBetRequestを生成する。
	 * @param race レース情報
	 * @param results シミュレーション結果
	 * @return BetRequest
	 */
	BetRequest convert(OlRace race, List<MlResult> results) {
		BetRequest req = new BetRequest(race.getJyocd(), race.getRaceno().toString());
		
		for (MlResult result : results) {
			Bet bet = new Bet(result.getBettype(), result.getBetKumiban(), result.getBetamt());
			req.add(bet);
		}
		
		return req;
	}
	
}
