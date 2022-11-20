package com.pengkong.boatrace.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.pengkong.boatrace.TestUtil;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.result.RCDefault;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.service.manager.BeforeOddsManager;


class RCDefaultTest {

	@Mock MLPropertyUtil prop;
	@Mock BeforeOddsManager beforeOddsManager;
	
	@InjectMocks
	RCDefault target;;
	
	@BeforeAll
	/** executed before all test cases */
	public static void beforeAll() throws Exception {
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void beforeEach() {
		MockitoAnnotations.openMocks(this);
		Mockito.mockStatic(MLPropertyUtil.class).when(MLPropertyUtil::getInstance).thenReturn(prop);
		Mockito.mockStatic(BeforeOddsManager.class).when(BeforeOddsManager::getInstance).thenReturn(beforeOddsManager);
	}
	
	@Test
	void executeOk() throws Exception {
		// 準備
		String targets = "1T,2T,2F,3T,3F";
		when(prop.getString("result_no")).thenReturn("1");
		when(prop.getString("target")).thenReturn(targets);
		when(beforeOddsManager.getOddsItemEx(anyString(),anyString(),anyString(),anyString())).thenReturn(null);
		DBRecord dbRec = TestUtil.createDBRecord(new String[] {"1","2","3"});
		
		// 実行
		target = new RCDefault();
		List<MlResult> result = target.execute(dbRec, prop.getString("bettype"), prop.getString("kumiban"));
		
		// 結果
		assertEquals(result.size(), 5);
	}
}
