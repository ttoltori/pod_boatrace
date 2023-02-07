package com.pengkong.boatrace.exp02;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.DBCol;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.mybatis.client.RankExtBalanceMapper;
import com.pengkong.boatrace.mybatis.entity.RankExtBalance;
import com.pengkong.boatrace.server.db.dto.RankDbRecord;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.collection.HashMapList;

public class RankBalanceGenerator {
	Logger logger = LoggerFactory.getLogger(RankBalanceGenerator.class);

	private HashMapList<RankDbRecord> mapList = null;

	PropertyUtil prop = PropertyUtil.getInstance();
	
	SqlSession session;
	CustomMapper mapper;
	RankExtBalanceMapper balMapper;
	
	public RankBalanceGenerator() {
		// TODO Auto-generated constructor stub
	}

	public void execute() throws Exception {
		session =DatabaseUtil.open(prop.getString("target_db"), false);	

		try {
			mapper = session.getMapper(CustomMapper.class);
			balMapper = session.getMapper(RankExtBalanceMapper.class);
			
			List<String> listDescription = RankModelManager.getInstance().getDescriptionList();
			int commitCnt = 0; // 커밋단위수
			
			for (String description : listDescription) {
				logger.info("description processing :" + description);
				HashMapList<RankDbRecord> mapRecord = loadRankExtMonthly(description);
				Iterator<String> it = mapRecord.keySet().iterator();
				while(it.hasNext()) {
					String extkey = it.next();
					List<RankDbRecord> listData = mapRecord.get(extkey);
					int balance = 0;
					for (int i = 0; i < listData.size(); i++) {
						RankDbRecord mon = listData.get(i);
						// monthly로부터 balance레코드를 복사
						RankExtBalance bal = convert(mon);
						balance += bal.getIncomeamt();
						// debug
						if (balance != bal.getBalance()) {
							logger.debug("balace different: extkey=" + extkey + ", ym=" + bal.getYm());
						}
						bal.setBalance(balance);
						
						balMapper.insert(bal);
						commitCnt++;
						
						// 지정건수단위로 커밋
						if (commitCnt > 10000) {
							commitCnt = 0;
						}
					}
				}
			}
			
		} finally {
			DatabaseUtil.close(session);
		}
	}

	/**
	 * rank_ext_monthlyのDBレコードをRankExtBalanceへ変換する
	 * @param recMon
	 * @return
	 */
	private RankExtBalance convert(RankDbRecord rec) {
		RankExtBalance bal = new RankExtBalance();
		bal.setAvgIncomeamt(rec.getInteger("avg_incomeamt"));
		bal.setBetamt(rec.getInteger("betamt"));
		bal.setBetcnt(rec.getInteger("betcnt"));
		bal.setBetKumiban(rec.getString("bet_kumiban"));
		bal.setBettype(rec.getString("bettype"));
		bal.setDescription(rec.getString("description"));
		bal.setExtkey(rec.getString("extkey"));
		bal.setHitamt(rec.getInteger("hitamt"));
		bal.setHitcnt(rec.getInteger("hitcnt"));
		bal.setHitrate(rec.getBigDecimal("hitrate"));
		bal.setIncomeamt(rec.getInteger("incomeamt"));
		bal.setIncomerate(rec.getBigDecimal("incomerate"));
		bal.setPattern(rec.getString("pattern"));
		bal.setRanktype(rec.getString("ranktype"));
		bal.setWaku(rec.getString("waku"));
		bal.setYm(rec.getString("ym"));
		// debug
		bal.setBalance(rec.getInteger("balance"));
		
		return bal;
	}
	
	private HashMapList<RankDbRecord> loadRankExtMonthly(String description) {
		HashMapList<RankDbRecord> mapList = new HashMapList<>();
		
		HashMap<String, String> param = new HashMap<>();
		param.put("description", description);
		List<RankDbRecord> result = mapper.selectRankExtMonthly(param);
		for (RankDbRecord rec : result) {
			mapList.addItem(rec.getString(DBCol.EXTKEY), rec);
		}
		
		return mapList;
	}
	
	public static void main(String[] args) {
		// RankModelManager필수 프로퍼티
		PropertyUtil.getInstance().putProperty("rank_model_file", "C:/Dev/workspace/Oxygen/pod_boatrace_test/experiment/expr02/rank_model.csv");
		// DB프로퍼티
		PropertyUtil.getInstance().putProperty("target_db", "mybatis-config.0.xml");
		
		try {
			new RankBalanceGenerator().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
