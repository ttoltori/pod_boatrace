package com.pengkong.boatrace.remote.server;


import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.pengkong.boatrace.TestUtil;
import com.pengkong.boatrace.exp10.factory.BoatClassifierFactory;
import com.pengkong.boatrace.exp10.model.ModelInfo;
import com.pengkong.boatrace.exp10.model.ModelInfoManager;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.remote.common.RemoteRequest;
import com.pengkong.boatrace.exp10.remote.common.RemoteResponse;
import com.pengkong.boatrace.exp10.remote.server.classifier.BoatClassifier;
import com.pengkong.boatrace.exp10.remote.server.service.ClassificationService;

class ClassificationServiceTest {
	@Mock MLPropertyUtil prop;
	@Mock ModelInfoManager mim;
	@Mock BoatClassifierFactory classifierFactory;
	@Mock BoatClassifier clf;
	
	@InjectMocks
	ClassificationService target;
	
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
		// this is not working. Mockito.mockStatic(LoggerFactory.class).when( LoggerFactory::getLogger(any(Class.class)) ).thenReturn(logger);
		Mockito.mockStatic(MLPropertyUtil.class).when(MLPropertyUtil::getInstance).thenReturn(prop);
		when(prop.getInteger("classifier_queue_max")).thenReturn(1);
		
		when( classifierFactory.create(any(ModelInfo.class)) ).thenReturn(clf);
		when( clf.predictProba(any())).thenReturn(new double[] {1.0,2.0,3.0,4.0,5.0,6.0});
		
		Mockito.mockStatic(ModelInfoManager.class).when(ModelInfoManager::getInstance).thenReturn(mim);
		when(mim.get(anyString(),anyString(),anyString(),anyString())).thenReturn(new ModelInfo("1", "00001_nopattern_20151231_rank1.model"));
		
		// 実行
		RemoteRequest req = TestUtil.createRemoteRequest("id1", "cf_bayesnet-1_wk", "3", "3", "3", "nopattern", "20162015",
				new String[] {"4546","3707","4738","4882","4746","4845"});
		target = new ClassificationService();
		target.setClassifierFactory(classifierFactory);
		RemoteResponse res = target.execute(req);
		
		// 結果
		assertNotNull(res);
		assertEquals(res.id, "id1");
		assertEquals(res.algorithmId, "cf_bayesnet-1_wk");
		assertEquals(res.status, "OK");
		assertEquals(res.values.length, 6);
		assertEquals(res.values[5], 6);
	}
}
