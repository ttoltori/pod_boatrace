package com.pengkong.boatrace.weka.simulation;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.mybatis.client.StatMlResultWekaMapper;
import com.pengkong.boatrace.mybatis.client.StatMlResultWekaptnMapper;
import com.pengkong.boatrace.mybatis.entity.StatMlResultWekaExample;
import com.pengkong.boatrace.mybatis.entity.StatMlResultWekaptnExample;
import com.pengkong.boatrace.simulation.model.BettingRule;
import com.pengkong.common.PropertyUtil;

import lombok.Getter;

public class WekaSimulationUser {
	private Logger logger = LoggerFactory.getLogger(WekaSimulationUser.class);

	@Getter
	private volatile int balance = 0;
	@Getter
	private volatile int dailyStartBalance = 0;

	@Getter
	private BettingRule rule = null;

	@Getter
	protected String todayYmd;

	private List<WekaSimulationWorker> workerList = new ArrayList<>();

	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	protected String description;
	
	protected SqlSession session;
	protected boolean isSaveResultToDB = false;
	
	public synchronized void addBalance(int value) {
		this.balance += value;
	}

	public synchronized void subtractBalance(int value) {
		this.balance -= value;
	}

	public WekaSimulationUser(String userPropertyFilepath) throws Exception {
		rule = new BettingRule(userPropertyFilepath);
	}

	public void start(String fromYmd, String toYmd) {
		try {
			description = fromYmd + "_" + toYmd;
			isSaveResultToDB = false;
			if (rule.getString("writeStatMlResultTarget").equals("database")) {
				// DBセッション開始
				isSaveResultToDB = true;
				String resource = "mybatis-config.0.xml";
				InputStream inputStream = Resources.getResourceAsStream(resource);
				SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
				session = sqlSessionFactory.openSession(false);
				
				// 既存DB結果データ削除
				deleteStatMlResultWekaByDescription(description);
//				deleteStatMlResultWekaptnByDescription(description);
			}
			
			String[] workerPproperties = rule.getString("workerProperties").split(",");
			for (int i = 0; i < workerPproperties.length; i++) {
				String workerPropertyFilepath = PropertyUtil.getInstance().getString("DIRECTORY_PROPERTY") + workerPproperties[i].trim()
						+ ".properties";
				BettingRule workerRule = new BettingRule(workerPropertyFilepath);
				WekaSimulationWorker worker = new WekaSimulationWorker(this, workerRule);
				workerList.add(worker);
			}

			balance = rule.getInt("totalStartBalance"); // tohyoManager.depositIfNeeded();
			dailyStartBalance = balance;

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(fromYmd));
			Date currDate = calendar.getTime();
			Date toDate = sdf.parse(toYmd);
			while (currDate.compareTo(toDate) <= 0) {
				todayYmd = sdf.format(currDate);
				dailyStartBalance = balance;
				dailyStartBalance = 0;

				ExecutorService executorService = Executors.newFixedThreadPool(workerList.size());
				for (WekaSimulationWorker worker : workerList) {
					executorService.execute(worker);
				}
				executorService.shutdown();
				try {
					executorService.awaitTermination(10, TimeUnit.MINUTES);
				} catch (InterruptedException e) {
					logger.error("worker thread failed!", e);
					executorService.shutdownNow();
				}

				dailyStartBalance = 0;

				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
			}
			
			// DBセッション終了
			if (isSaveResultToDB) {
				session.commit();
				session.close();
			}
		} catch (Exception e) {
			logger.error("user failed!!! ", e);
			e.printStackTrace();
		}
	}

	// 既存のDBログを削除する
	private void deleteStatMlResultWekaByDescription(String description) {
		StatMlResultWekaMapper mapper = session.getMapper(StatMlResultWekaMapper.class);
		StatMlResultWekaExample example = new StatMlResultWekaExample();
		
		// delete records of same description
		example.createCriteria().andDescriptionEqualTo(description);
		mapper.deleteByExample(example);
	}
	
	private void deleteStatMlResultWekaptnByDescription(String description) {
		StatMlResultWekaptnMapper mapper = session.getMapper(StatMlResultWekaptnMapper.class);
		StatMlResultWekaptnExample example = new StatMlResultWekaptnExample();
		
		// delete records of same description
		example.createCriteria().andDescriptionEqualTo(description);
		mapper.deleteByExample(example);
	}
	
	public static void main(String[] args) {
		args = new String[] { "C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/user.properties",
				"20190101", // simulation from ymd
				"20190830" // simulation to ymd
		};

		if (args.length != 3) {
			System.out.println(
					"Usage: WekaSimulationUser {user.properties} {fromYmd} {toYmd}");
			System.exit(1);
		}

		try {
			PropertyUtil.getInstance().addFile(args[0]);
			WekaSimulationUser user = new WekaSimulationUser(args[0]);
			user.start(args[1], args[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
