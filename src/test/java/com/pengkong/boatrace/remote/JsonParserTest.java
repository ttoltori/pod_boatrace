package com.pengkong.boatrace.remote;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.pengkong.boatrace.TestUtil;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.remote.common.RemoteRequest;
import com.pengkong.boatrace.exp10.remote.common.RemoteRequestParam;
import com.pengkong.boatrace.util.JsonParser;

class JsonParserTest {

	JsonParser parser;
	
	@BeforeAll
	/** executed before all test cases */
	public static void beforeClass() throws Exception {
		MLPropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties");
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void before() {
		parser = new JsonParser();
	}
	
	
	@Test
	void 正常にRemoteRequestのencodeを行う() {
		// 準備
		RemoteRequest req = TestUtil.createRemoteRequest("id1", "cf_bayesnet-1_wk", "3", "3", "3", "nopattern", "20162015",
				new String[] {"4546","3707","4738","4882","4746","4845"});
		
		
		// 実行
		String result = null;
		try {
			result = JsonParser.encode(req); 
		} catch (Exception e) {
			fail(e);
		}
		
		
		// 結果
		assertNotNull(result);
	}

	@Test
	void 正常にRemoteRequestParamのencodeを行う() {
		// 準備
		RemoteRequestParam req = TestUtil.createRemoteRequestParam("3", "3", "3", "nopattern", "20162015",
				new String[] {"4546","3707","4738","4882","4746","4845"});
		
		// 実行
		String result = null;
		try {
			result = JsonParser.encode(req); 
		} catch (Exception e) {
			fail(e);
		}
		
		
		// 結果
		assertNotNull(result);
	}

	@Test
	void 正常にRemoteRequestParamのlistのencodeを行う() {
		// 準備
		List<RemoteRequestParam> listReq = new ArrayList<>();
		listReq.add(TestUtil.createRemoteRequestParam("1", "1", "1", "nopattern", "20162015",
				new String[] {"4546","3707","4738","4882","4746","4845"}));
		listReq.add(TestUtil.createRemoteRequestParam("3", "3", "3", "nopattern", "20162015",
				new String[] {"4546","3707","4738","4882","4746","4845"}));
		
		// 実行
		String result = null;
		try {
			result = JsonParser.encode(listReq); 
		} catch (Exception e) {
			fail(e);
		}
		
		
		// 結果
		assertNotNull(result);
	}

	@Test
	void 正常にRemoteRequestのdecodeを行う() {
		// 準備
		String jsonStr = "{\"id\":\"id1\",\"algorithmId\":\"cf_bayesnet-1_wk\",\"param\":{\"exNo\":\"3\",\"modelNo\":\"3\",\"rankNo\":\"3\",\"pattern\":\"nopattern\",\"ymd\":\"20162015\",\"modelFileName\":\"00001_nopattern_20151231_rank1.model\",\"values\":[\"4546\",\"3707\",\"4738\",\"4882\",\"4746\",\"4845\"]}}";
		
		// 実行
		JsonNode json = null;
		try {
			json = JsonParser.decode(jsonStr);
		} catch (Exception e) {
			fail(e);
		}
		
		// 結果
		assertEquals(json.get("id").asText(), "id1");;
		assertEquals(json.get("algorithmId").asText(), "cf_bayesnet-1_wk");;
		assertEquals(json.get("param").get("exNo").asText(), "3");;
	}
	
	@Test
	void 正常にRemoteRequestのlistのdecodeを行う() {
		// 準備
		String jsonStr = "[{\"id\":\"id1\",\"algorithmId\":\"cf_bayesnet-1_wk\",\"param\":{\"exNo\":\"3\",\"modelNo\":\"3\",\"rankNo\":\"3\",\"pattern\":\"nopattern\",\"ymd\":\"20162015\",\"modelFileName\":\"00001_nopattern_20151231_rank1.model\",\"values\":[\"4546\",\"3707\",\"4738\",\"4882\",\"4746\",\"4845\"]}},"
				+ "{\"id\":\"id2\",\"algorithmId\":\"cf_bayesnet-1_wk\",\"param\":{\"exNo\":\"4\",\"modelNo\":\"3\",\"rankNo\":\"3\",\"pattern\":\"nopattern\",\"ymd\":\"20162015\",\"modelFileName\":\"00001_nopattern_20151231_rank1.model\",\"values\":[\"4546\",\"3707\",\"4738\",\"4882\",\"4746\",\"4845\"]}}]";
		
		// 実行
		JsonNode json = null;
		try {
			json = JsonParser.decode(jsonStr);
		} catch (Exception e) {
			fail(e);
		}
		
		// 結果
		assertNotNull(json);
		assertEquals(json.size(), 2);
		assertEquals(json.get(0).get("id").asText(), "id1");;
		assertEquals(json.get(1).get("id").asText(), "id2");;
	}
	
	@Test
	void 正常にRemoteRequestParamのdecodeObjectを行う() {
		// 準備
		String jsonStr = "{\"id\":\"id1\",\"algorithmId\":\"cf_bayesnet-1_wk\",\"param\":{\"exNo\":\"3\",\"modelNo\":\"3\",\"rankNo\":\"3\",\"pattern\":\"nopattern\",\"ymd\":\"20162015\",\"modelFileName\":\"00001_nopattern_20151231_rank1.model\",\"values\":[\"4546\",\"3707\",\"4738\",\"4882\",\"4746\",\"4845\"]}}";

		// 実行
		JsonNode json = null;
		RemoteRequestParam cReq = null;
		try {
			json = JsonParser.decode(jsonStr);
			cReq = (RemoteRequestParam) JsonParser.decodeObject(json.get("param"), RemoteRequestParam.class);
		} catch (Exception e) {
			fail(e);
		}

		// 結果
		assertNotNull(cReq);
		assertEquals(cReq.exNo, "3");
		assertEquals(cReq.values[0], "4546");
	}

	
	@Test
	void ServiceErrorをチェックする() {
		// 準備
		String jsonStr = "Service Error";

		// 実行
		JsonNode json = null;
		try {
			json = JsonParser.decode(jsonStr);
		} catch (Exception e) {
		}
		
		// 結果
		assertNull(json);
	}
	
	@Test
	void JsonNodeのsizeをチェックする() {
		// 準備
		String jsonStr = "{\"key\":\"value\"}";

		// 実行
		JsonNode json = null;
		try {
			json = JsonParser.decode(jsonStr);
		} catch (Exception e) {
		}
		
		// 結果
		assertEquals(json.size(), 1);
	}
}
