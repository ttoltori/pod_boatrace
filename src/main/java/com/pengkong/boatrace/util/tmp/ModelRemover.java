package com.pengkong.boatrace.util.tmp;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.exp02.RankModelManager;
import com.pengkong.boatrace.exp02.RankPatternManager;
import com.pengkong.boatrace.exp02.model.ModelFile;
import com.pengkong.common.FileEx;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public class ModelRemover {

	private HashMap<String, Integer> mapModel  = new HashMap<>();
	PropertyUtil prop = PropertyUtil.getInstance();

	/**
	 * 확장자가 model_delete였던 파일들을 모두 model확장자로 되돌린다.
	 * @throws Exception
	 */
	public void restoreModel() throws Exception {
		String dirModelRelease = "F:/Dev/experiment/expr02/model_release";
		
		List<File> listFile = FileUtil.listFilesByExtensionRecursively(dirModelRelease, "model_delete");
		int cnt = 1;
		int total = listFile.size();
		System.out.println("target count" + total);
		for (File file : listFile) {
			System.out.println(cnt++ + "/" + total);
			FileEx fe = new FileEx(file);
			String newPath = fe.getDir() + File.separator + fe.getNameOnly() + ".model";
			fe.renameTo(new File(newPath));
		}
	}
	
	/**
	 * 지정된 최소베팅카운트를 만족하지 못하는 패턴디렉토리를 모두 "*_exclude"로 리네임한다.
	 */
	public void renamePatternDir() throws Exception {
		String dirModelRelease = "F:/Dev/experiment/expr02/model_release/";

		RankModelManager modelMgr = RankModelManager.getInstance();
		RankPatternManager ptnMgr = RankPatternManager.getInstance();
		
		// modelループ
		List<String> listDirModel = FileUtil.listDirName(dirModelRelease);
		for (String dirModel : listDirModel) {
			List<String> listDirPtn = FileUtil.listDirName(dirModelRelease + dirModel);
			// パタンループ
			for (String dirPtn : listDirPtn) {
				if (dirPtn.endsWith("_exclude")) {
					continue;
				}
				
				String dirPtnFullpath = dirModelRelease + dirModel + "/" + dirPtn;
				
				String patternName = modelMgr.getPatternName(dirModel);
				if (!ptnMgr.isValidForModel(patternName, dirPtn)) {
					String dirPtnFullpathNew = dirModelRelease + dirModel + "/" + dirPtn + "_exclude";
					System.out.println(dirPtnFullpath + " is not qualified.");
					new File(dirPtnFullpath).renameTo(new File(dirPtnFullpathNew));
				} else {
					System.out.println(dirPtnFullpath + " is qualified.");
				}
			}
		}
	}
	
	/**
	 * 모든 패턴에 대해 년월이 지정되어있는 파일을 제외하고 모두 model_delete확장자로 리네임한다.  
	 * @throws Exception
	 */
	public void reenameModel() throws Exception {
		String dirHome = "C:/Dev/workspace/Oxygen/pod_boatrace_test/experiment/tmp/";
		String dirModelRelease = "F:/Dev/experiment/expr02/model_release/";
		
		// 후보 년월 리스트 취득
		//List<String> listYm = FileUtil.readFileByLineArr(dirHome + "model_monthlist_3.txt", "UTF-8");
		//List<String> listYm = FileUtil.readFileByLineArr(dirHome + "model_monthlist_6.txt", "UTF-8");
		List<String> listYm = FileUtil.readFileByLineArr(dirHome + "model_monthlist_12.txt", "UTF-8");
		
		// modelループ
		List<String> listDirModel = FileUtil.listDirName(dirModelRelease);
		for (String dirModel : listDirModel) {
			dirModel += "/";
			List<String> listDirPtn = FileUtil.listDirName(dirModelRelease + dirModel);
			// パタンループ
			for (String dirPtn : listDirPtn) {
				// 후보년월 설정
				for (String ym : listYm) {
					mapModel.put(ym, new Integer(0));
				}
				
				// model file ループ
				List<String> listModelfile = FileUtil.listFileName(dirModelRelease + dirModel + dirPtn, ".model"); 
				Collections.sort(listModelfile);
				String dirPtnFullpath = dirModelRelease + dirModel + dirPtn + "/";
				
				for (String fileName : listModelfile) {
//					if (fileName.endsWith("delete")) {
//						String newName = fileName.replace(".delete", "");
//						File file = new File(dirPtnFullpath + fileName);
//						file.renameTo(new File(dirPtnFullpath + newName));
//						continue;
//					}
					
					ModelFile mFile = new ModelFile(fileName);
					if (!mFile.isValid()) {
						throw new Exception("file name is wrong. " + dirPtnFullpath + fileName);
					}
					
					String key = mFile.getYm();
					if (mapModel.containsKey(key)) {
						Integer value = mapModel.get(key);
						value++;
						if (!fileName.endsWith("rank" + value + ".model")) {
							throw new Exception("file name wrong. " + dirPtnFullpath + fileName );
						}
						mapModel.put(key, value);
						if (value == 3) {
							mapModel.remove(key);
						}
					} else {
						// 대상년월이 아니라면
						File file = new File(dirPtnFullpath + fileName);
						file.renameTo(new File(dirPtnFullpath + fileName + "_delete"));
					}
				}
			}
		}
	}
	
	public ModelRemover() {
	}
	
	public static void main(String[] args) {
		try {
			PropertyUtil.getInstance().putProperty("rank_model_file", "C:\\Dev\\workspace\\Oxygen\\pod_boatrace_test\\experiment\\expr02\\rank_model.csv");
			PropertyUtil.getInstance().putProperty("rank_pattern_file", "C:\\Dev\\workspace\\Oxygen\\pod_boatrace_test\\experiment\\expr02\\rank_pattern.csv");
			PropertyUtil.getInstance().putProperty("min_betcnt_model", "100");
			
			ModelRemover rm = new ModelRemover();
			rm.renamePatternDir();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
