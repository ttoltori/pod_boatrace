package com.pengkong.boatrace.util;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.pengkong.boatrace.common.BoatProperty;
import com.pengkong.boatrace.mybatis.client.ProcedureExecutorMapper;
import com.pengkong.boatrace.mybatis.entity.ParamGeneratePower;
import com.pengkong.boatrace.scraping.BoatWebDownloader;
import com.pengkong.boatrace.simulation.model.BettingRule;

/**
 * 
 * @author qwerty
 *
 *  1TF, 2TF, 3TFのいずれかを選択して出力したい場合
 *  　①generate_powerの手動分岐を修正
 *  　②StatGenerator.javaの手動分岐を修正
 *
 */
public class SimulationDailyBatch {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public BettingRule rule;
	public SimulationDailyBatch(BettingRule rule) {
		this.rule = rule;
	}

	public void execute(String fromYmd, String toYmd) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);
		while (currDate.compareTo(toDate) <= 0) {
			String yyyyMMdd = sdf.format(currDate);
			System.out.println(yyyyMMdd);
			
			execute(yyyyMMdd);
		
			// 1日増加
			calendar.add(Calendar.DATE, 1);
			currDate = calendar.getTime();
		}
	}
	
	public void execute(String yyyyMMdd) throws Exception {
		new BoatStatGenerator(rule).execute(yyyyMMdd);
	}

	public static void main(String[] args) {
		try {
			BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/" + args[2]);
			BettingRule rule = new BettingRule(BoatProperty.DIRECTORY_PROPERTY +  args[2]);
			
			new SimulationDailyBatch(rule).execute(args[0], args[1]);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
