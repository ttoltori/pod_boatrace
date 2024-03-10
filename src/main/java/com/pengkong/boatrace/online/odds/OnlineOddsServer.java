package com.pengkong.boatrace.online.odds;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.OddsHelper;
import com.pengkong.boatrace.exp10.odds.loader.OddsValueComparator;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.entity.OlRace;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.collection.ConcurrentHashMapList;
import com.pengkong.common.collection.HashMapList;

public class OnlineOddsServer implements OddsMonitorListener {
	Logger logger = LoggerFactory.getLogger(OnlineOddsServer.class);
	
	/** key=ymd,jyo,race,bettype,kumiban */
	ConcurrentHashMapList<Odds> mapOddsList;
	
	ExecutorService pool;
	
	public OnlineOddsServer() {
		mapOddsList = new ConcurrentHashMapList<Odds>();
	}
	
	public Odds get(String ymd, String jyoCd, String raceNo, String betType, String kumiban) throws Exception {
		String key = String.join("_", ymd, jyoCd, raceNo, betType, kumiban);
		List<Odds> listOdds = mapOddsList.get(key);
		
		if (listOdds == null) {
			logger.error("Odds has not been parsed yet." + key);
			return null;
		}
		
		/** 直近のオッズを返却する */
		return listOdds.get(listOdds.size()-1);
	}
	
	public void start() {
		int ret = 0;
		try {
			// オッズmonitor起動
			pool = Executors.newSingleThreadExecutor();
			pool.execute(new OddsMonitorRunner(this));

			// 投票、結果確認スレッド共に終了
			pool.shutdown();
			pool.awaitTermination(20, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			logger.error("online odds server interrupted failure. ", e);
			pool.shutdownNow();
			stop(2);
		} catch (Exception e) {
			logger.error("online odds server unknown failure. ", e);
			stop(3); // unknown error
		}
		
		logger.info("online odds server ended with return code:" + ret);
		stop(ret);
	}
	
	/**
	 * 終了処理
	 * @param ret 0=正常、1=監視スレッド異常終了、 2=Interrupted、3=unknown error、4=RMI要求
	 */
	public void stop(int ret) {
		if (!pool.isTerminated()) {
			pool.shutdownNow();
		}
		System.exit(ret);
	}
	
	@Override
	public void notifyItems(List<OddsMonitorItem> items) {
		// rankingを適用させたodds listを生成する.
		List<Odds> listOdds = applyRanking(items);
		
		// 組番単位のoddslistに追加する.  key=ymd,jyo,race,bettype,kumiban
		for (Odds odds : listOdds) {
			mapOddsList.addItem(odds.getUniqueKey(), odds);
		}
	}

	/**
	 * 取得したオッズ監視結果リストをrankingを適用したoddsのリストへ変換して取得する
	 * @param items オッズ監視結果リスト
	 * @return Oddsリスト
	 */
	List<Odds> applyRanking(List<OddsMonitorItem> items) {
		// bettype単位でoddslistを再配置する（rankingを設定するため）
		HashMapList<Odds> mapBettypeOdds = new HashMapList<>();
		
		// key=ymd,jyo,race,bettype
		for (OddsMonitorItem item : items) {
			mapBettypeOdds.addItem(item.createBettypeKey(), item.odds);
		}
		
		mapBettypeOdds = OddsHelper.applyRanking(mapBettypeOdds, new OddsValueComparator());
		
		return mapBettypeOdds.getAllItems();
	}
	
	@Override
	public void notifyRaceComplete(OlRace race) {
		logger.info("odds monitor for race completed. " + race);
	}

	@Override
	public void notifyOutOfTime(OlRace race) {
		logger.warn("odds monitor out of time. " + race);
	}

	@Override
	public void notifyMonitorComplete() {
		logger.info("odds monitor runner completed.");
	}

	@Override
	public void notifyMonitorFailure() {
		logger.error("odds monitor runner failed.");
		stop(1);
	}
	
	public static void main(String[] args) {
		String propertyFilepath = args[0];
		// PropertyUtilを使っている箇所もあるので互換性確保のために
		try {
			PropertyUtil.getInstance().addFile(propertyFilepath);
			MLPropertyUtil.getInstance().addFile(propertyFilepath);
			
			new OnlineOddsServer().start();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
