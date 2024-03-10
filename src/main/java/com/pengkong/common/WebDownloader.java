package com.pengkong.common;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * download file from specific url
 * 
 * @author qwerty
 *
 */
public class WebDownloader {

	/**
	 * download file from url
	 * 
	 * @param url
	 *            file url
	 * @param filepath
	 *            absolute path of the file to be downloaded.
	 * @throws Exception
	 */
	public void download(String url, String filepath) throws Exception {
		URL website = new URL(url);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		try (FileOutputStream fos = new FileOutputStream(filepath);) {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		}
	}

	public static void main(String[] args) {
		WebDownloader wd = new WebDownloader();
		try {
			wd.download("http://www1.mbrace.or.jp/od2/K/201802/k180201.lzh",
					"C:/Dev/workspace/Oxygen/pod_boatrace/release/test.lzh");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
