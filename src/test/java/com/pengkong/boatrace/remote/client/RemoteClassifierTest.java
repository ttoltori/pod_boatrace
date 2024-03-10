package com.pengkong.boatrace.remote.client;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.pengkong.boatrace.exp10.model.ModelInfoManager;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.remote.client.RemoteClassifier;
import com.pengkong.boatrace.exp10.remote.client.RemoteCommunication;

import ch.qos.logback.classic.Logger;

class RemoteClassifierTest {
	@Mock MLPropertyUtil prop;
	@Mock Logger logger;
	@Mock RemoteCommunication rc;
	@Mock ModelInfoManager mim;
	
	@InjectMocks
	RemoteClassifier target;
	
	@BeforeAll
	/** executed before all test cases */
	public void beforeAll() throws Exception {
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void beforeEach() {
		MockitoAnnotations.openMocks(this);
	}
	
	
	@Test
	void 正常にclassifyを行う() throws Exception {
		// 準備
		
		// 実行
		
		// 結果
	}
	
}
