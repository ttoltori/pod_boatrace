package com.pengkong.common;

import java.io.File;
import java.util.regex.Pattern;

import lombok.Getter;

public class FileEx extends File {

	private static final long serialVersionUID = 1L;

	@Getter
	String fullPath;
	
	@Getter
	String nameOnly;
	
	@Getter
	String extension;
	
	@Getter
	String dir;
	
	@Getter
	String[] pathToken;
	
	public FileEx(String fullpath) {
		super(fullpath);
		parse();
	}
	
	public FileEx(File file) {
		super(file.getAbsolutePath());
		parse();
	}

	public String getDescription() {
		return nameOnly.split("_")[0];
	}
	
	public String getPattern() throws Exception {
		return pathToken[pathToken.length -1];
	}
	
	private void parse() {
		File file = this;
		
		this.fullPath = file.getAbsolutePath();
		String name = file.getName();
		String[] token = name.split("\\.");
		this.nameOnly = token[0];
		this.extension = token[1];
		this.dir = file.getParent();
		this.pathToken = dir.split(Pattern.quote(File.separator));
	}
	
	public static void main(String[] args) {
		FileEx file = new FileEx("F:\\Dev\\experiment\\expr02\\model_release\\0005\\01\\0005_01_20160628_rank1.model");
		try {
			System.out.println(file.getAbsolutePath());
			System.out.println(file.getCanonicalPath());
			System.out.println(file.getPath());
			System.out.println(file.getParent());
			System.out.println(file.getName());
			System.out.println(file.getDescription());
			System.out.println(file.getPattern());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
