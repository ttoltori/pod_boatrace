package com.pengkong.boatrace.scraping.parser;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatConst;
import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.RaceResult;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.util.BoatUtil;

public class RaceResultPageParser {
	Logger logger = LoggerFactory.getLogger(RaceResultPageParser.class);

	public RaceEx parseAndSetResult(RaceEx race) throws Exception {
		String url = BoatTemplate.URL_RACE_RESULT
				.replace("{raceNo}", String.valueOf(race.raceInfo.no))
				.replace("{jyoCd}", race.setu.jyoCd)
				.replace("{yyyyMMdd}", race.raceInfo.ymd);
		
		Document doc = Jsoup.connect(url).get();
		race.raceResult = setPrize(race.raceResult, doc);
		
		return race;
	}
	
	public RaceResult parseAndSet(RaceResult raceResult, List<Waku> wakuList, String url) throws Exception {
		logger.debug("url=" + url);
		
		Document doc = Jsoup.connect(url).get();

		Elements elements = doc.selectFirst("table.is-w495").select("tbody tr");
		int size = elements.size();
		if (size != 6) {
			throw new WarningException("failed to parse resultRank in result page. url=" + url);
		}
		String[] rank123 = new String[3];
		for (int i = 0; i < size; i++) {
			Element eleRank = elements.get(i);
			String waku = Normalizer.normalize(eleRank.child(1).text(), Normalizer.Form.NFKC);
			int wakuIdx = BoatUtil.toInt(waku) - 1;
			String rank = eleRank.child(0).text();
			// 枠毎の着順
			if ("１２３４５６".contains(rank)) {
				wakuList.get(wakuIdx).resulRank = Integer.parseInt(rank);
			} else {
				// 事故発生
				wakuList.get(wakuIdx).resulRank = BoatConst.RANK_OUT;
			}
			if (i < 3) {
				rank123[i] = waku;
			}
		}

		Elements elementsNumber = doc.select("table.is-w495.is-h414 span.table1_boatImage1Number");
		elements = doc.select("table.is-w495.is-h414 span.table1_boatImage1TimeInner");
		size = elements.size();
		for (int i = 0; i < size; i++) {
			int wakuIndex = BoatUtil.toInt(elementsNumber.get(i).text()) - 1;
			Element eleStart = elements.get(i);
			// スタート成績
			String resultStart =eleStart.text().split("　")[0];
			if (resultStart.contains("F")) {
				// start flying 失格
				wakuList.get(wakuIndex).resultStart = BoatUtil.toFloat(resultStart.replace("F", "")) * -1f;
			} else if (resultStart.contains("L")) {
				// late失格
				wakuList.get(wakuIndex).resultStart = -1f;
			} else {
				wakuList.get(wakuIndex).resultStart = BoatUtil.toFloat(eleStart.text().split("　")[0]);
			}
		}
		
		raceResult = setPrize(raceResult, doc);

		doc = null;
		return raceResult;
	}

	private RaceResult setPrize(RaceResult raceResult, Document doc) {
		// 払い戻し
		Elements elements = doc.select("table.is-w495 tr.is-p3-0");
		Element tr = elements.get(0);
		raceResult.sanrentanNo = tr.child(1).text().replaceAll("-", "");
		raceResult.sanrentanPrize = BoatUtil.toInt(tr.child(2).text().replace("¥", "").replace(",", ""));
		raceResult.sanrentanPopular = BoatUtil.toInt(tr.child(3).text());

		tr = elements.get(2);
		raceResult.sanrenhukuNo = tr.child(1).text().replaceAll("=", "");
		raceResult.sanrenhukuPrize = BoatUtil.toInt(tr.child(2).text().replace("¥", "").replace(",", ""));
		raceResult.sanrenhukuPopular = BoatUtil.toInt(tr.child(3).text());
		
		tr = elements.get(4);
		raceResult.nirentanNo = tr.child(1).text().replaceAll("-", "");
		raceResult.nirentanPrize = BoatUtil.toInt(tr.child(2).text().replace("¥", "").replace(",", ""));
		raceResult.nirentanPopular = BoatUtil.toInt(tr.child(3).text());
		
		tr = elements.get(6);
		raceResult.nirenhukuNo = tr.child(1).text().replaceAll("=", "");
		raceResult.nirenhukuPrize = BoatUtil.toInt(tr.child(2).text().replace("¥", "").replace(",", ""));
		raceResult.nirenhukuPopular = BoatUtil.toInt(tr.child(3).text());

		tr = elements.get(8);
		raceResult.kakuren1No = tr.child(1).text().replaceAll("=", "");
		raceResult.kakuren1Prize = BoatUtil.toInt(tr.child(2).text().replace("¥", "").replace(",", ""));
		raceResult.kakuren1Popular = BoatUtil.toInt(tr.child(3).text());
		
		tr = elements.get(9);
		raceResult.kakuren2No = tr.child(0).text().replaceAll("=", "");
		raceResult.kakuren2Prize = BoatUtil.toInt(tr.child(1).text().replace("¥", "").replace(",", ""));
		raceResult.kakuren2Popular = BoatUtil.toInt(tr.child(2).text());
		
		tr = elements.get(10);
		raceResult.kakuren3No = tr.child(0).text().replaceAll("=", "");
		raceResult.kakuren3Prize = BoatUtil.toInt(tr.child(1).text().replace("¥", "").replace(",", ""));
		raceResult.kakuren3Popular = BoatUtil.toInt(tr.child(2).text());
		
		tr = elements.get(13);
		raceResult.tansyoNo = tr.child(1).text();
		raceResult.tansyoPrize = BoatUtil.toInt(tr.child(2).text().replace("¥", "").replace(",", ""));
		
		tr = elements.get(15);
		raceResult.hukusyo1No = tr.child(1).text();
		String huku1p = tr.child(2).text().replace("¥", "").replace(",", "");
		raceResult.hukusyo1Prize = BoatUtil.toInt(huku1p);
		
		tr = elements.get(16);
		raceResult.hukusyo2No = tr.child(0).text();
		String huku2p =tr.child(1).text().replace("¥", "").replace(",", "");
		// 以下URLで内容バグあるため
		raceResult.hukusyo2Prize = BoatUtil.toInt(huku2p);	

		// 決まり手
		// 20190831 error Element eleKimari = doc.selectFirst("table.is-w243.is-h168 td.is-fs16");
		Element eleKimari = doc.selectFirst("table.is-w243 td.is-fs16");
		raceResult.kimariTe = eleKimari.text();
				
		return raceResult;
	}
	
	public static void main(String[] args) {
		try {
			Race race = new Race();
			race.wakuList = Arrays.asList(new Waku(),new Waku(),new Waku(),new Waku(),new Waku(),new Waku());
			RaceResultPageParser parser = new RaceResultPageParser();
			parser.parseAndSet(race.raceResult, race.wakuList, "https://www.boatrace.jp/owpc/pc/race/raceresult?rno=1&jcd=06&hd=20110101");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
