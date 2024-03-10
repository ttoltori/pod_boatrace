package com.pengkong.boatrace.remote.client;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunner;

import com.pengkong.boatrace.exp10.factory.BoatWebSocketFactory;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.remote.client.BoatWebSocketClient;
import com.pengkong.boatrace.exp10.remote.client.RemoteCommunication;
import com.pengkong.boatrace.exp10.remote.client.RemoteCommunication.RemoteResponseHandler;
import com.pengkong.common.Prop;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RemoteCommunication.class)
class RemoteCommunicationTest {
	@Mock MLPropertyUtil prop;
	@Mock BoatWebSocketFactory socketFactory;
	@Mock BoatWebSocketClient clientWk;
	@Mock BoatWebSocketClient clientPy;
	
	@InjectMocks
	RemoteCommunication target;
	
	@BeforeAll
	/** executed before all test cases */
	public static void beforeAll() throws Exception {
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void beforeEach() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void 正常にconnectを行う() throws Exception {
		// 準備
		RemoteResponseHandler mockHandler = PowerMockito.mock(RemoteResponseHandler.class);
		
		
		Mockito.mockStatic(MLPropertyUtil.class).when(MLPropertyUtil::getInstance).thenReturn(prop);
//		when(prop.getProperties("websocket_url_")).thenReturn(List.of(
//				new Prop("websocket_url_weka","ws://localhost:8090"),
//				new Prop("websocket_url_python","ws://localhost:8091")));
		
		when( socketFactory.create("ws://localhost:8090") ).thenReturn(clientWk);
		when( socketFactory.create("ws://localhost:8091") ).thenReturn(clientPy);
		
		// 実行
		target = new RemoteCommunication();
		target.setWebSocketFactory(socketFactory);
		target.connect();
		
		// 結果
		verify(clientWk, times(1)).connect();
		verify(clientPy, times(1)).connect();
	}
}
