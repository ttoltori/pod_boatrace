package com.pengkong.boatrace.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParser {

	static ObjectMapper mapper = new ObjectMapper();
	
	/** Objectをjsonへ変換する */
	public static String encode(Object obj) throws JsonProcessingException {
		return mapper.writeValueAsString(obj);
	}

	/** json文字列をJsonNodeへ変換する */
	public static JsonNode decode(String jsonStr) throws IOException {
		JsonNode jn = mapper.readTree(jsonStr);
		
		return jn;
	}
	
	/** json文字列をObjectへ変換する */
	public static Object decodeObject(String jsonStr, TypeReference<?> t) throws IOException {
		return mapper.readValue(jsonStr, t);
	}

	/** json文字列をObjectへ変換する */
	public static Object decodeObject(String jsonStr, Class<?> clazz) throws IOException {
		return mapper.readValue(jsonStr, clazz);
	}
	
	/** JsonNodeをObjectへ変換する */
	public static Object decodeObject(JsonNode json, Class<?> clazz) throws IOException {
		return mapper.treeToValue(json, clazz);
	}
}
