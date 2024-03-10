package com.pengkong.boatrace.util;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.converter.Csv2RecOdds;
import com.pengkong.boatrace.converter.RecFameList2RecFameRank3;
import com.pengkong.boatrace.converter.RecFameList2RecFameWaku;
import com.pengkong.boatrace.converter.RecOddsList2RecFameList;
import com.pengkong.boatrace.converter.RecOddsList2RecOddsRankTop10;
import com.pengkong.boatrace.mybatis.client.RecFameMapper;
import com.pengkong.boatrace.mybatis.client.RecFameRank3Mapper;
import com.pengkong.boatrace.mybatis.client.RecFameWakuMapper;
import com.pengkong.boatrace.mybatis.client.RecOddsMapper;
import com.pengkong.boatrace.mybatis.client.RecOddsRank10Mapper;
import com.pengkong.boatrace.mybatis.client.RecRaceWakuMapper;
import com.pengkong.boatrace.mybatis.entity.RecFame;
import com.pengkong.boatrace.mybatis.entity.RecFameExample;
import com.pengkong.boatrace.mybatis.entity.RecFameRank3;
import com.pengkong.boatrace.mybatis.entity.RecFameRank3Example;
import com.pengkong.boatrace.mybatis.entity.RecFameWaku;
import com.pengkong.boatrace.mybatis.entity.RecFameWakuExample;
import com.pengkong.boatrace.mybatis.entity.RecOdds;
import com.pengkong.boatrace.mybatis.entity.RecOddsExample;
import com.pengkong.boatrace.mybatis.entity.RecOddsRank10;
import com.pengkong.boatrace.mybatis.entity.RecOddsRank10Example;
import com.pengkong.boatrace.mybatis.entity.RecRaceWaku;
import com.pengkong.boatrace.mybatis.entity.RecRaceWakuExample;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

/**
 * 実行前にRaceFileUploadが終わってないといけない！！ 
 * m 
 * 
 * @author qwerty
 *
 */
public class OddsBeforeFileUploader {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	public OddsBeforeFileUploader() {
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
			RecOddsMapper recOddsMapper = session.getMapper(RecOddsMapper.class);
//			RecOddsRank10Mapper recOddsRank10Mapper = session.getMapper(RecOddsRank10Mapper.class);
			RecRaceWakuMapper recRaceWakuMapper = session.getMapper(RecRaceWakuMapper.class);
//			RecFameMapper recFameMapper = session.getMapper(RecFameMapper.class);
//			RecFameWakuMapper recFameWakuMapper = session.getMapper(RecFameWakuMapper.class);
			RecFameRank3Mapper recFameRank3Mapper = session.getMapper(RecFameRank3Mapper.class);

			RecOddsExample recOddsExample = new RecOddsExample();
//			RecOddsRank10Example recOddsRank10Example = new RecOddsRank10Example();
			RecRaceWakuExample recRaceWakuExample = new RecRaceWakuExample();
//			RecFameExample recFameExample = new RecFameExample();
//			RecFameWakuExample recFameWakuExample = new RecFameWakuExample();
			RecFameRank3Example recFameRank3Example = new RecFameRank3Example();

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(fromYmd));
			Date currDate = calendar.getTime();
			Date toDate = sdf.parse(toYmd);
			
			while (currDate.compareTo(toDate) <= 0) {
				String yyyyMMdd = sdf.format(currDate);

				// delete records of current date ---- start
				recOddsExample.clear();
				recOddsExample.createCriteria().andYmdEqualTo(yyyyMMdd);
				recOddsMapper.deleteByExample(recOddsExample);

//				recOddsRank10Example.clear();
//				recOddsRank10Example. createCriteria().andYmdEqualTo(yyyyMMdd);
//				recOddsRank10Mapper.deleteByExample(recOddsRank10Example);
//				
//				recFameExample.clear();
//				recFameExample.createCriteria().andYmdEqualTo(yyyyMMdd);
//				recFameMapper.deleteByExample(recFameExample);
//				
//				recFameWakuExample.clear();
//				recFameWakuExample.createCriteria().andYmdEqualTo(yyyyMMdd);
//				recFameWakuMapper.deleteByExample(recFameWakuExample);
				
				recFameRank3Example.clear();
				recFameRank3Example.createCriteria().andYmdEqualTo(yyyyMMdd);
				recFameRank3Mapper.deleteByExample(recFameRank3Example);
				// delete records of current date ---- end
				
				
				
				String filepath = PropertyUtil.getInstance().getString("DIRECTORY_ODDSBEFORE") + "odds_" + yyyyMMdd + ".before.csv";
				//String filepath = BoatProperty.DIRECTORY_ODDS + "odds_" + yyyyMMdd + ".csv";
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
				// 実行前にRaceFileUploadが終わってないといけない！！
				for (String line : lines) {
					// 複勝はスキップする 20190110
					if (line.contains("KF") || line.contains("1F")) {
						continue;
					}
					
					// オッズ全件取得
					List<RecOdds> recOddsList = Csv2RecOdds.convert(line);
					
					// insert rec_odds 1~6位までの６件だけ保存する
					List<RecOdds> recOddsListTmp = Csv2RecOdds.getLimited(recOddsList, 6); 
					for (RecOdds recOdds : recOddsListTmp) {
						recOddsMapper.insert(recOdds);
					}
					
					// insert rec_odds_top10
//					RecOddsRank10 recOddsRank10 = RecOddsList2RecOddsRankTop10.convert(recOddsList);
//					recOddsRank10Mapper.insert(recOddsRank10);

					// insert rec_fame
					RecOdds recOdds = recOddsList.get(0);
					if (recOdds.getBettype().equals("3T")) {
						recRaceWakuExample.clear();
						recRaceWakuExample.createCriteria().andYmdEqualTo(recOdds.getYmd())
							.andJyocdEqualTo(recOdds.getJyocd())
							.andRacenoEqualTo(recOdds.getRaceno());
						List<RecRaceWaku> recRaceWakuList = recRaceWakuMapper.selectByExample(recRaceWakuExample);
						// 当該レースがなければスキップ
						if (recRaceWakuList.size() <= 0) {
							continue;
						}
						
						RecRaceWaku recRaceWaku = recRaceWakuList.get(0);
						String[] entries = 
							{	String.valueOf(recRaceWaku.getEntry1()),	
								String.valueOf(recRaceWaku.getEntry2()),
								String.valueOf(recRaceWaku.getEntry3()),
								String.valueOf(recRaceWaku.getEntry4()),
								String.valueOf(recRaceWaku.getEntry5()),
								String.valueOf(recRaceWaku.getEntry6()),
							};
						
						List<RecFame> recFameList = RecOddsList2RecFameList.convert(entries, recOddsList);
//						for (RecFame recFame : recFameList) {
//							recFameMapper.insert(recFame);
//						}
//						
//						// insert rec_fame_waku
//						RecFameWaku recFameWaku = RecFameList2RecFameWaku.convert(recFameList);
//						recFameWakuMapper.insert(recFameWaku);
						
						// insert rec_fame_rank3
						RecFameRank3 recFameRank3 = RecFameList2RecFameRank3.convert(recFameList);
						recFameRank3Mapper.insert(recFameRank3);

					}
				}
				
				System.out.println(fname);

				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
			}
			
			session.commit();
		}
	}
	
	public static void main(String[] args) {
		try {
			PropertyUtil.getInstance().addFile(args[2]);
			new OddsBeforeFileUploader().execute(args[0], args[1]);
			
//			BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/race_result_rebuild.properties");
//			new OddsFileUploader().execute("20190222", "20190304");
			//new RaceFileUploader().execute("20090730", "20121231");
			// 20090729 エラー発生
			//new RaceFileUploader().execute("20090101", "20170424");
			//new RaceFileUploader().execute("20090101", "20180725");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
