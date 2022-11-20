package com.pengkong.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class FileUtil {

	
	public static List<String> readFileByLineArr(File file) throws Exception {
		return readFileByLineArr(file.getAbsolutePath(), "UTF-8", true, "#");
	}

	public static List<String> readFileByLineArr(String filepath) throws Exception {
		return readFileByLineArr(filepath, "UTF-8", true, "#");
	}

	/**
	 * 指定したencodingでファイルを読込み、ArrayListで返却する。<br>
	 * 첫컬럼이 "#"이면 주석으로 처리한다.
	 * 
	 * @param filepath
	 *            ファイルフールパス
	 * @param p_sCharSet
	 *            encoding 例）UTF-8, EUC-JP, SJIS
	 * @return 파일내
	 */
	public static List<String> readFileByLineArr(String filepath, String p_sCharSet) throws Exception {
		return readFileByLineArr(filepath, p_sCharSet, true, "#");
	}
	
	public static List<String> readFileByLineArr(String filepath, String p_sCharSet, boolean forceTrim, String commentSign) throws Exception {
		try (BufferedReader brReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filepath), p_sCharSet));) {
			ArrayList<String> alList = new ArrayList<String>();
			String szLine = null;

			while ((szLine = brReader.readLine()) != null) {
				// trim 
				if (forceTrim) {
					szLine = szLine.trim();
				}
				
				// ignore comment line
				if (szLine.startsWith(commentSign)) {
					continue;
				}
				
				alList.add(szLine);
			}
			return alList;
		}
	}

	/**
	 * 파일을 모두 읽어 하나의 스트링으로 반환한다.
	 * 첫컬럼이 "--"이면 주석으로 처리한다.
	 * @param filepath
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String readFileIntoString(String filepath, String encoding) throws Exception {
		List<String> listStr = readFileByLineArr(filepath, encoding, false, "--");
		return String.join(" ", listStr);
	}
	
	public static void deleteFile(String filepath) {
		File file = new File(filepath);
		if (file.exists()) {
			file.delete();
		}
	}
	
	/** delete directory recursively if it exists */
	public static void deleteDirectory(String dirpath) throws IOException {
		File file = new File(dirpath);
		if (file.exists()) {
			FileUtils.deleteDirectory(file);
		}
		
	}
	
	public static void appendFileByLine(String filepath, String line) throws Exception {
		writeFileByLine(filepath, line, true, "UTF-8");
	}

	public static void writeFile(String filepath, String line) throws Exception {
		writeFileByLine(filepath, line, false, "UTF-8");
	}

	public static File[] listFilesByExtension(String dir, String ext) throws Exception {
		File file = new File(dir);
		return file.listFiles(new FilenameFilterExtension(ext));
	}
	
	public static List<String> listDirName(String dir) throws Exception {
		if (!FileUtil.isExist(dir)) {
			return null;
		}

		List<String> result = new ArrayList<>();
		File[] files = new File(dir).listFiles();
		for (File file : Arrays.asList(files)) {
			if (file.isDirectory()) {
				result.add(file.getName());
			}
		}

		return result;
	}
	
	/**
	 * 
	 * @param dir listするディレクトリ
	 * @param ext
	 * @return 파일풀패스의 리스트
	 * @throws Exception
	 */
	public static List<String> listFileName(String dir, String ext) throws Exception {
		List<String> result = new ArrayList<>();
		List<File> listFile = Arrays.asList(new File(dir).listFiles(new FilenameFilterExtension(ext)));
		for (File file : listFile) {
			result.add(file.getName()) ;
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static List<File> listFilesByExtensionRecursively(String dir, String ext) throws Exception {
		return new ArrayList<File>( FileUtils.listFiles(new File(dir), new String[] {ext}, true) );
	}
	
	public static void writeFileByLine(String filepath, String line, boolean append, String encoding) throws Exception {
		try(BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath, append), encoding))) {
			bf.write(line);
			bf.newLine();
		}
	}
	
	public static boolean isExist(String fileFullpath) {
		File file = new File(fileFullpath);
		return file.exists();
	}
	
	public static void createDirIfNotExist(String dir) throws Exception {
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			boolean result = dirFile.mkdirs();
			if (!result) {
				throw new Exception("directory creation failed. dir=" + dir);
			}
		}
	}

	private static class FilenameFilterExtension implements FilenameFilter {
		private String ext;
		
		public FilenameFilterExtension(String ext) {
			this.ext = ext;
		}
		
		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith(ext)) {
				return true;
			} else {
				return false;
			}
		}
	}

}
