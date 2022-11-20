package com.pengkong.common;

/**
 * ログのように随時line単位にappendする処理を早くする
 * @author ttolt
 *
 */
public class CachedFileAppender {

	String content = "";
	String filepath;

	
	public CachedFileAppender(String filepath) {
		this.filepath = filepath;
	}

	public void append(String line) {
		content += line + "\n";
	}
	
	public boolean hasContent() {
		return (content.length() > 0);
	}
	
	public void flush() throws Exception {
		FileUtil.writeFile(filepath, content);
	}
}
