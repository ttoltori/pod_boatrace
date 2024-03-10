package com.pengkong.boatrace.exp10.util;

import java.io.File;
import java.util.List;

import com.pengkong.common.FileUtil;

public class GroupFileUpdater {

	List<String> contents;
	/**
	 * group fileの[PR,BOR,BORK]の一括bonusパラメータを更新する
	 *  BONUS_PR:x
     *  BONUS_BOR:x
     *  BONUS_BORK:x
	 */
	public void updateBonus(File file, String pr, String bor, String bork) throws Exception {
		String filepath = file.getAbsolutePath();
		
		// コメント(#)も含めて読み込む
		contents = FileUtil.readFileByLineArr(filepath, "UTF-8", false, "zzz");
		if (pr != null) {
			updateBonus("BONUS_PR", pr);
		}

		if (bor != null) {
			updateBonus("BONUS_BOR", bor);
		}
		
		if (bork != null) {
			updateBonus("BONUS_BORK", bork);
		}
		
		writeFile(filepath);
	}
	
	void writeFile(String filepath) throws Exception{
		StringBuilder sb = new StringBuilder();
		for (String str : contents) {
			sb.append(str);
			sb.append("\n");
		}
		FileUtil.writeFile(filepath, sb.subSequence(0, sb.length()-1).toString());
	}
	
	boolean updateBonus(String bonusKey, String value) throws Exception {
		String str;
		for (int i = 0; i < contents.size(); i++) {
			str = contents.get(i);
			if (!str.startsWith(bonusKey)) {
				continue;
			}
			
			contents.set(i, bonusKey + ":" + value);
			return true;
		}
		
		return false;
	}
	
	void doTemp1() throws Exception {
		File[] files = FileUtil.listFilesByExtension("D:/Dev/experiment/expr10/work/groups_store/selected", "tsv");
		for (File file : files) {
			updateBonus(file, null, "x", "x");
		}
	}
	
	public static void main(String[] args) {
		
		String filepath = "D:/Dev/experiment/expr10/work/00001_1T-1.tsv";
		try {
			//new GroupFileUpdater().updateBonus(new File(filepath), "x", null, "1~3=1");
			new GroupFileUpdater().doTemp1();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
