package com.pengkong.boatrace.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.pengkong.boatrace.model.OddsTItem;
import com.pengkong.boatrace.mybatis.client.OddsTrendMapper;
import com.pengkong.boatrace.mybatis.entity.OddsTrend;
import com.pengkong.boatrace.mybatis.entity.OddsTrendExample;
import com.pengkong.boatrace.service.manager.AbstractOddsManager.JyoRace;
import com.pengkong.common.PropertyUtil;
import com.pengkong.boatrace.service.manager.ResultOddsManager;

/**
 * 2019/11/17
 * 리그레션으로 대표구미방의 확정옺즈를 예측하기 위한 옺즈데이터를 업로드한다.
 * 레이스성향(인니게도, 마쿠리도등)을 판정하기 위한 이하 대표구미방의 옺즈를 레이스별로 보존한다.
 * 123:인니게도 213;사시도 314;마쿠리사시도 412,512,612:마쿠리도
 * 
 * @author qwerty
 *
 */
public class OddsTrendUploader {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	private ResultOddsManager oddsMgr = ResultOddsManager.getInstance();
	
	public OddsTrendUploader() {
	}
	
	public void execute(String yyyyMMdd) throws Exception {
		execute(yyyyMMdd, yyyyMMdd);
	}

	public void execute(String fromYmd, String toYmd) throws Exception {
		String resource = "mybatis-config.0.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		
		// open session with auto-transaction mode = false
		try(SqlSession session = sqlSessionFactory.openSession(false)) {
			OddsTrendMapper mapper = session.getMapper(OddsTrendMapper.class);
			OddsTrendExample exam = new OddsTrendExample();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(fromYmd));
			Date currDate = calendar.getTime();
			Date toDate = sdf.parse(toYmd);
			String ymd = null;
			while (currDate.compareTo(toDate) <= 0) {
				ymd = sdf.format(currDate);
				
				// delete records of current date ---- start
				exam.clear();
				exam.createCriteria().andYmdEqualTo(ymd);
				mapper.deleteByExample(exam);

				oddsMgr.load(ymd);
				// 옺즈파일에 있는 해당 날짜의 장,레이스 일람을 취득
				List<JyoRace> listJyoRace = oddsMgr.getJyoRaceList();
				for (JyoRace jyoRace : listJyoRace) {
					OddsTrend rec = createRec(ymd, jyoRace.getJyoCd(), jyoRace.getRaceNo());
					mapper.insert(rec);
				}
				
				System.out.println(ymd);

				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
			}
			session.commit();
		}
	}
	
	private OddsTrend createRec(String ymd, String jyoCd, String raceNo) {
	
		OddsTrend rec = new OddsTrend();
		rec.setYmd(ymd);
		rec.setJyocd(jyoCd);
		rec.setRaceno(Short.valueOf(raceNo));
		float[] oddsSum = new float[] {0,0,0,0,0,0};
		int[] oddsSumCnt = new int[] {0,0,0,0,0,0};
		int[] rankSum = new int[] {0,0,0,0,0,0};
		int[] rankSumCnt = new int[] {0,0,0,0,0,0};
		List<OddsTItem> listOdds = oddsMgr.getOddsItemList(jyoCd, raceNo, "3T");
		for (int i = 0; i < listOdds.size(); i++) {
			OddsTItem odds = listOdds.get(i);
			String kumiban = odds.kumiban;
			int rank1 = Integer.parseInt(kumiban.substring(0,1));
			oddsSum[rank1-1] += odds.value;
			rankSum[rank1-1] += (i+1);
			oddsSumCnt[rank1-1]++;
			rankSumCnt[rank1-1]++;
			
			// 대표구미방의 옺즈,랭크를 구한다.
			if (kumiban.equals("123")) {
				rec.setOdds123(new BigDecimal(odds.value));
				rec.setRank123(i+1);
			} else if (kumiban.equals("213")) {
				rec.setOdds213(new BigDecimal(odds.value));
				rec.setRank213(i+1);
			} else if (kumiban.equals("314")) {
				rec.setOdds314(new BigDecimal(odds.value));
				rec.setRank314(i+1);
			} else if (kumiban.equals("412")) {
				rec.setOdds412(new BigDecimal(odds.value));
				rec.setRank412(i+1);
			} else if (kumiban.equals("512")) {
				rec.setOdds512(new BigDecimal(odds.value));
				rec.setRank512(i+1);
			} else if (kumiban.equals("612")) {
				rec.setOdds612(new BigDecimal(odds.value));
				rec.setRank612(i+1);
			}
		}
		
		// 1~6으로 시작하는 구미방의 옺즈평균, 랭크평균을 구한다
		rec.setOdds1(new BigDecimal(oddsSum[0] / oddsSumCnt[0]));
		rec.setRank1(rankSum[0] / rankSumCnt[0]);
		rec.setOdds2(new BigDecimal(oddsSum[1] / oddsSumCnt[1]));
		rec.setRank2(rankSum[1] / rankSumCnt[1]);
		rec.setOdds3(new BigDecimal(oddsSum[2] / oddsSumCnt[2]));
		rec.setRank3(rankSum[2] / rankSumCnt[2]);
		rec.setOdds4(new BigDecimal(oddsSum[3] / oddsSumCnt[3]));
		rec.setRank4(rankSum[3] / rankSumCnt[3]);
		rec.setOdds5(new BigDecimal(oddsSum[4] / oddsSumCnt[4]));
		rec.setRank5(rankSum[4] / rankSumCnt[4]);
		rec.setOdds6(new BigDecimal(oddsSum[5] / oddsSumCnt[5]));
		rec.setRank6(rankSum[5] / rankSumCnt[5]);
		
		return rec;
	}
	
	public static void main(String[] args) {
		String fromYmd = args[0];
		String toYmd = args[1];
//		String fromYmd = "20191130";
//		String toYmd = "20191130";
		try {
			PropertyUtil.getInstance().addFile(args[2]);		
			new OddsTrendUploader().execute(fromYmd, toYmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
