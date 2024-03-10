package com.pengkong.boatrace.remote.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pengkong.boatrace.exp10.model.ModelInfo;
import com.pengkong.boatrace.exp10.model.ModelInfoManager;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.remote.server.classifier.AbstractBoatClassifier;
import com.pengkong.boatrace.exp10.remote.server.classifier.BoatClassifier;
import com.pengkong.boatrace.exp10.remote.server.classifier.ClassifierQueue;

class ClassifierQueueTest {

	ClassifierQueue queue;;
	
	ModelInfoManager mim = ModelInfoManager.getInstance();
	
	@BeforeAll
	/** executed before all test cases */
	public static void beforeAll() throws Exception {
		MLPropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties");
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void beforeEach() {
	}
	
	@Test
	void 追加する() throws Exception {
		// 準備
		queue = new ClassifierQueue(1);
		ModelInfo mi = mim.get("3", "1", "nopattern", "20210201");
		
		// 実行
		addQueue(mi);
		
		// 結果
		assertEquals(queue.size(), 1);
	}

	@Test
	void max以上に複数追加する() throws Exception {
		// 準備
		queue = new ClassifierQueue(2);
		ModelInfo mi1 = mim.get("3", "1", "nopattern", "20210201");
		ModelInfo mi2 = mim.get("3", "2", "nopattern", "20210201");
		ModelInfo mi3 = mim.get("3", "3", "nopattern", "20210201");
		
		// 実行＋結果
		addQueue(mi1);
		assertEquals(queue.size(), 1);
		addQueue(mi2);
		assertEquals(queue.size(), 2);
		addQueue(mi3);
		assertEquals(queue.size(), 2);
		  
		// 結果
		AbstractBoatClassifier clf;
		clf = queue.get(createQueueKey(mi1));
		assertNull(clf);
		
		clf = queue.get(createQueueKey(mi2));
		assertNotNull(clf);
		
		clf = queue.get(createQueueKey(mi3));
		assertNotNull(clf);
		
	}

	@Test
	void 取得する() throws Exception {
		// 準備
		queue = new ClassifierQueue(1);
		ModelInfo mi = mim.get("3", "1", "nopattern", "20210201");
		addQueue(mi);
		
		// 実行
		String key = createQueueKey(mi);
		AbstractBoatClassifier clf = queue.get(key);
		
		// 結果
		assertNotNull(clf);
	}

	
	private String createQueueKey(ModelInfo mi) {
		return String.join("_", mi.modelNo, mi.pattern, String.valueOf(mi.ymd), mi.rankNo);
	}
	
	private void addQueue(ModelInfo mi) throws Exception {
		AbstractBoatClassifier clf = new BoatClassifier(mi);
	    String key = createQueueKey(mi);
		queue.add(key, clf);
	}
}
