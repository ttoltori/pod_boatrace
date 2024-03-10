package com.pengkong.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;

import jp.gr.java_conf.dangan.util.lha.LhaFile;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;

public class LhaFileExtractor {

	@SuppressWarnings("unchecked")
	public static void extractAll(String lzhFilepath, String extractPath) throws Exception {
		File file = new File(lzhFilepath);
		LhaFile lha = new LhaFile(file);
		Enumeration<LhaHeader> headers = lha.entries();
		while (headers.hasMoreElements()) {
			LhaHeader head = headers.nextElement();
			// ファイル名
			String fname = head.getPath();
			// ファイルの出力
			File exFile = new File(extractPath + fname);
			try (InputStream is = lha.getInputStream(head); FileOutputStream fos = new FileOutputStream(exFile)) {
				int len = 0;
				byte[] data = new byte[100];
				while ((len = is.read(data)) > 0) {
					fos.write(data, 0, len);
				}
			}
		}
	}
}
