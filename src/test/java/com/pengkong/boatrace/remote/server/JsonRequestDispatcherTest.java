package com.pengkong.boatrace.remote.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pengkong.boatrace.exp10.enums.ServiceType;
import com.pengkong.boatrace.exp10.factory.ServiceFactory;
import com.pengkong.boatrace.exp10.remote.common.RemoteResponse;
import com.pengkong.boatrace.exp10.remote.server.service.ClassificationService;
import com.pengkong.boatrace.exp10.remote.server.service.JsonRequestDispatcher;

class JsonRequestDispatcherTest {
	@Mock ServiceFactory serviceFactory;
	@Mock ClassificationService service;
	
	@InjectMocks
	JsonRequestDispatcher target;
	
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
	void 正常にexecuteを行う() throws Exception {
		// 準備
		when( serviceFactory.create(any(ServiceType.class)) ).thenReturn(service);
		when( service.execute(any())).thenReturn(new RemoteResponse("ad1", "cf_bayesnet-1_wk", new double[]{1.0}, "OK"));
		
		// 実行
		String jsonStr = "{\"id\":\"id1\",\"algorithmId\":\"cf_bayesnet-1_wk\",\"param\":{\"exNo\":\"3\",\"modelNo\":\"3\",\"rankNo\":\"3\",\"pattern\":\"nopattern\",\"ymd\":\"20162015\",\"modelFileName\":\"00001_nopattern_20151231_rank1.model\",\"values\":[\"4546\",\"3707\",\"4738\",\"4882\",\"4746\",\"4845\"]}}";
		target = new JsonRequestDispatcher();
		target.setServiceFactory(serviceFactory);
		String res = target.dispatch(jsonStr);
		
		// 結果
		assertNotNull(res);
		assertEquals("{\"id\":\"ad1\",\"algorithmId\":\"cf_bayesnet-1_wk\",\"values\":[1.0],\"status\":\"OK\"}", res);
	}
	
	@Test
	void executeで例外発生() throws Exception {
		// 準備
		when( serviceFactory.create(any(ServiceType.class)) ).thenReturn(service);
		when( service.execute(any())).thenThrow(new IllegalStateException("test error"));
		
		// 実行
		String jsonStr = "{\"id\":\"id1\",\"algorithmId\":\"cf_bayesnet-1_wk\",\"param\":{\"exNo\":\"3\",\"modelNo\":\"3\",\"rankNo\":\"3\",\"pattern\":\"nopattern\",\"ymd\":\"20162015\",\"modelFileName\":\"00001_nopattern_20151231_rank1.model\",\"values\":[\"4546\",\"3707\",\"4738\",\"4882\",\"4746\",\"4845\"]}}";
		target = new JsonRequestDispatcher();
		target.setServiceFactory(serviceFactory);
		String res = target.dispatch(jsonStr);
		
		// 結果
		assertNotNull(res);
		assertEquals("{\"id\":\"id1\",\"algorithmId\":\"cf_bayesnet-1_wk\",\"values\":[],\"status\":\"Service execution error: java.lang.IllegalStateException: test error\"}", res);
	}
}
