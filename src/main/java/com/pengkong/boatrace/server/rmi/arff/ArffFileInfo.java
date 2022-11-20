package com.pengkong.boatrace.server.rmi.arff;

import java.util.List;

import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.weka.automation.DefineFileParser;

public class ArffFileInfo extends DefineFileParser {
	/**
	 * listDbRecord의 최근 사용 인덱스+1.
	 * 누적적으로 arff파일을 출력하기 위해 다음의 출력위치 인덱스를 보존한다.
	 */
	public int nextStartIndex = 0;
	public List<DBRecord> listDbRecord;
}
