package com.pengkong.boatrace.util;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.mybatis.client.OddsResultMapper;
import com.pengkong.boatrace.mybatis.entity.OddsResult;
import com.pengkong.boatrace.mybatis.entity.OddsResultExample;
import com.pengkong.boatrace.service.manager.ResultOddsManager;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

/**
 * 実行前にRaceFileUploadが終わってないといけない！！ 
 * m 
 * 
 * @author qwerty
 *
 */
public class OddsResultFileUploader {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	String betType;
	String kumiban; 
	
	/** 確定オッズ管理者 */
	ResultOddsManager oddsMgr = ResultOddsManager.getInstance();
	
	public OddsResultFileUploader(String betType, String kumiban) {
		super();
		this.betType = betType;
		this.kumiban = kumiban;
	}

	public void execute(String fromYmd, String toYmd) throws Exception {
		String resource = "mybatis-config.0.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		
		// open session with auto-transaction mode = false
		try(SqlSession session = sqlSessionFactory.openSession(false)) {
			OddsResultMapper mapper = session.getMapper(OddsResultMapper.class);
			OddsResultExample example = new OddsResultExample();
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(fromYmd));
			Date currDate = calendar.getTime();
			Date toDate = sdf.parse(toYmd);
			
			while (currDate.compareTo(toDate) <= 0) {
				String yyyyMMdd = sdf.format(currDate);

				// 以前データ削除
				example.clear();
				example.createCriteria().andYmdEqualTo(yyyyMMdd).andBettypeEqualTo(this.betType).andKumibanEqualTo(this.kumiban);
				mapper.deleteByExample(example);
				
				oddsMgr.load(yyyyMMdd); // 確定オッズロード
				
				String filepath = PropertyUtil.getInstance().getString("DIRECTORY_ODDS") + "odds_" + yyyyMMdd + ".csv";
				File file = new File(filepath);
				if (!file.exists()) {
					// 1日増加
					calendar.add(Calendar.DATE, 1);
					currDate = calendar.getTime();
					continue;
				}
				
				String fname = file.getName();
				filepath = file.getPath();
				List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
				for (String line : lines) {
					// ex) 20210918,02,1,1T,1=1.4,2=9.5,3=5.1,4=11.8,5=4.3,6=50.2
					String[] token = line.split(Delimeter.COMMA.getValue());
					if (!isValidToken(token))
						continue;
					
					OddsResult oddsResult = convertOddsResult(token);
					mapper.insert(oddsResult);
				}
				
				System.out.println(fname);

				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
			}
			
			session.commit();
		}
	}
	
	/**
	 * DB用DTOを返却する
	 * @param token
	 * @return
	 */
	OddsResult convertOddsResult(String[] token) {
		OddsResult result = new OddsResult();
		result.setYmd(token[0]);
		result.setJyocd(token[1]);
		result.setRaceno(Short.parseShort(token[2]));
		result.setBettype(token[3]);
		result.setKumiban(this.kumiban);
		
		Float odds = oddsMgr.getOddsValue(token[1], token[2], token[3], this.kumiban);
		result.setOdds(Double.parseDouble(odds.toString()));
		
		return result;
	}
	
	/**
	 * oddsmonitorのCSVがアップロード対象かを判定する
	 * @param token ex) 114706,20210602,06,2,1T,1,1=1.0,2=10.5,3=0.0,4=0.0,5=4.2,6=0.0
	 * @return true=対象 false=対象外
	 */
	boolean isValidToken(String[] token) {
		return (token[3].equals(betType)); 
		
	}
	
	public static void main(String[] args) {
		String fromYmd;
		String toYmd;
		String betType;
		String kumiban;
		
		fromYmd = args[0];
		toYmd = args[1];
		betType = args[2];
		kumiban = args[3];
		
		try {
			PropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/race_result_rebuild.properties");
			new OddsResultFileUploader(betType, kumiban).execute(fromYmd, toYmd);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
