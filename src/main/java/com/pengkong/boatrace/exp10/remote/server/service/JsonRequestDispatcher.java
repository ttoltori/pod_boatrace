package com.pengkong.boatrace.exp10.remote.server.service;

import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.pengkong.boatrace.exp10.enums.ServiceType;
import com.pengkong.boatrace.exp10.factory.AbstractServiceFactory;
import com.pengkong.boatrace.exp10.factory.ServiceFactory;
import com.pengkong.boatrace.exp10.remote.common.RemoteRequest;
import com.pengkong.boatrace.exp10.remote.common.RemoteRequestParam;
import com.pengkong.boatrace.exp10.remote.common.RemoteResponse;
import com.pengkong.boatrace.util.JsonParser;

public class JsonRequestDispatcher extends AbstractRequestDispatcher {
	Logger logger = LoggerFactory.getLogger(JsonRequestDispatcher.class);

	AbstractServiceFactory serviceFactory = new ServiceFactory();
	
	/**
	 * servicemap ex) key="cf_bayes_wk" value=instance of AbstractClassificationService
	 */
	HashMap<String, AbstractService> mapClassificationService;

	boolean isInitialized = false; 
	
	public JsonRequestDispatcher() {
		initialize();
	}

	private void initialize() {
		// サービスマップ登録
		mapClassificationService = new HashMap<>();
		for (ServiceType e : ServiceType.values()) {
				mapClassificationService.put(e.getValue(), serviceFactory.create(e));
		}
	}

	private void ensureInitialized() {
		if (!isInitialized) {
			initialize();
			isInitialized = true;
		}
	}

	/**
	 * リスト形式のjson要求文字列から要求毎のサービスを実行して結果をリストで返却する。
	 * 
	 * @param jsonStr json文字列（リスト形式のRemoteRequest）
	 * @return Strinng json文字列（リスト形式のRemoteResponses）
	 * @throws Exception
	 */
	@Override
	public String dispatch(String jsonStr) {
		ensureInitialized();
		
		Object resultObject = null;
		
		// classification or regression
		RemoteRequest req;
		try {
			// decode classification request
			req = getRequest(jsonStr);
			
			resultObject = dispatchClassification(req);
		} catch (IOException e) {
			logger.error("Error:json decode failed." + jsonStr, e);
			return "Error:json decode failed.";
		}

		// 結果return
		try {
			return JsonParser.encode(resultObject);
		} catch (JsonProcessingException e) {
			logger.error("Error:json encode failed.", e);
			return "Error:json encode failed.";
		}

	}

	public void setServiceFactory(AbstractServiceFactory factory) {
		this.serviceFactory = factory;
	}

	/** RemoteRequestのjsonをInstanceへ変換する */
	RemoteRequest getRequest(String jsonStr) throws IOException {
		JsonNode json = JsonParser.decode(jsonStr);
		
		RemoteRequestParam param = 
				(RemoteRequestParam) JsonParser.decodeObject(json.get("param"), RemoteRequestParam.class);

		return new RemoteRequest(json.get("id").asText(), json.get("algorithmId").asText(), param);
	}
	
	
	/* dispatchMultiJson()は必要になった時実装する */
	
	/**
	 * リスト形式のjson要求文字列から要求毎のサービスを実行して結果をリストで返却する。
	 * 
	 * @param jsonStr json文字列（リスト形式のRemoteRequest）
	 * @return Strinng json文字列（リスト形式のRemoteResponses）
	 * @throws Exception
	 */
	RemoteResponse dispatchClassification(RemoteRequest req) {
		String status;

		// RemoteRequest実行する
		String id = req.id;
		String algorithmId = req.algorithmId;

		// algorithmidをserviceTypeへ変換する
		// ex) cf_bayes-1_wk -> cf_bayes_wk
		String[] token = algorithmId.split("_");
		String serviceType = null;
		// classification, regressionはserviceTypeはcf_generic_wkで統一する!!!
		if (token[0].equals("cf") || token[0].equals("rg")) {
			serviceType = String.join("_", token[0], "generic", token[token.length-1]);
		} else {
			serviceType = String.join("_", token[0], token[1].split("-")[0], token[token.length-1]);
		}
		
		
		// サービス名をキーとしてサービス取得
		AbstractService service = mapClassificationService.get(serviceType);
		if (service == null) {
			status = "Service not exist";
			return new RemoteResponse(id, algorithmId, new double[] {}, status);
		}

		//該当サービス実行
		try {
			return service.execute(req);
			
		} catch (Exception e) {
			logger.error(id + "_" + algorithmId, e);
			status = "Service execution error: " + e;
			return new RemoteResponse(id, algorithmId, new double[] {}, status);
		}
	}
}
