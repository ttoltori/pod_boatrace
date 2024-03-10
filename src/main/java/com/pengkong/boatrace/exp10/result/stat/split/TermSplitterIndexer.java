package com.pengkong.boatrace.exp10.result.stat.split;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.util.BoatUtil;

import lombok.Getter;

public class TermSplitterIndexer {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	@Getter
	int[] spliterYmds;
	
	void initialize() {
		String startYmd = prop.getString("result_start_ymd");
		String endYmd = prop.getString("result_end_ymd");
		int termSplit = prop.getInteger("term_split");
		
		spliterYmds = new int[termSplit];
		int days = BoatUtil.daysBetween(startYmd, endYmd) + 1;
		int termDays = days / termSplit;
		
		String lastYmd = null;
		for (int i = 0; i < termSplit-1; i++) {
			if (lastYmd == null) {
				lastYmd = startYmd;
			}
			lastYmd = BoatUtil.daysAfterYmd(lastYmd, termDays);
			spliterYmds[i] = Integer.valueOf(lastYmd);
		}
		spliterYmds[termSplit-1] = Integer.valueOf(endYmd);
	}

	public int getIndex(String ymd) {
		if (spliterYmds == null) {
			initialize();
		}
		int iYmd = Integer.valueOf(ymd);
		for (int i = 0; i < spliterYmds.length-1; i++) {
			if (iYmd <= spliterYmds[i]) {
				return i;
			}
		}
		
		return spliterYmds.length-1;
	}
	
	public static void main(String[] args) {
		MLPropertyUtil.getInstance().putProperty("result_start_ymd", "20210101");
		MLPropertyUtil.getInstance().putProperty("result_end_ymd", "20210131");
		MLPropertyUtil.getInstance().putProperty("term_split", "3");
		
		TermSplitterIndexer indexer = new TermSplitterIndexer();
		indexer.initialize();
		for (int i = 0; i < indexer.getSpliterYmds().length; i++) {
			System.out.println("split[" + i + "]=" + indexer.getSpliterYmds()[i]);	
		}
		
		System.out.println(indexer.getIndex("20201231"));
		System.out.println(indexer.getIndex("20220502"));
		System.out.println(indexer.getIndex("20220831"));
		System.out.println(indexer.getIndex("20221231"));
		System.out.println(indexer.getIndex("20230101"));
	}
}
