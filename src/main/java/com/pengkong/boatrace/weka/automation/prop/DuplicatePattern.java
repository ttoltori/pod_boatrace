package com.pengkong.boatrace.weka.automation.prop;

import java.util.ArrayList;
import java.util.List;

//패턴유닛에서부터 중복을 고려하므로 이제는 필요없는 클래스이다.
@Deprecated
public class DuplicatePattern {

	public boolean isExist = false;
	public List<String> listPattern = new ArrayList<>();
	public DuplicatePattern() {
		super();
	}
}
