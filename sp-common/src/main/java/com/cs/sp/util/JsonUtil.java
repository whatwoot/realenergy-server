package com.cs.sp.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.cs.sp.common.WebAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * FastJson对应的Jackson相关对象/操作
 * 对象/操作	FastJson	Jackson
 * json对象	JSONObject	ObjectNode
 * json集合	JSONArrray	ArrayNode
 * 创建json对象	JSON.parseObject()	ObjectMapper.readTree()
 * 获取json对象中的json对象	jsonObject.getJSONObject(<KEY>)	jsonNode.with(<KEY>)
 * 获取json对象中的集合对象	jsonObject.getJSONArray(<KEY>)	jsonNode.withArray(<KEY>)
 */
public class JsonUtil {

	private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

	private static ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		//设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static String object2Json(Object o) {
		if (o == null) {
			return null;
		}

		String s = null;
		try {
			s = mapper.writeValueAsString(o);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			WebAssert.throwBizException("sp.jackson.writeError");
		}
		return s;
	}

	public static <T> List<String> listObject2ListJson(List<T> objects) {
		if (objects == null) {
			return null;
		}

		List<String> lists = new ArrayList<String>();
		for (T t : objects) {
			lists.add(JsonUtil.object2Json(t));
		}

		return lists;
	}

	public static <T> List<T> listJson2ListObject(List<String> jsons, Class<T> c) {
		if (jsons == null) {
			return null;
		}

		List<T> ts = new ArrayList<>();
		for (String j : jsons) {
			ts.add(JsonUtil.json2Object(j, c));
		}

		return ts;
	}

	public static <T> T json2Object(String json, Class<T> c) {
		if (!StringUtils.hasLength(json)) {
			return null;
		}

		T t = null;
		try {
			t = mapper.readValue(json, c);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			WebAssert.throwBizException("sp.jackson.json2objError");
		}
		return t;
	}

	@SuppressWarnings("unchecked")
	public static <T> T json2Object(String json, TypeReference<T> tr) {
		if (!StringUtils.hasLength(json)) {
			return null;
		}

		T t = null;
		try {
			t = mapper.readValue(json, tr);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			WebAssert.throwBizException("sp.jackson.json2objError");
		}
		return t;
	}


	public static JsonNode json2Object(String json){
		if(!StringUtils.hasText(json)){
			return null;
		}
		try {
			return mapper.readTree(json);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			WebAssert.throwBizException("sp.jackson.json2objError");
		}
		return null;
	}

	public static Map<String, Object> json2map(JsonNode jsonNode){
		return mapper.convertValue(jsonNode, new TypeReference<Map<String, Object>>(){});
	}


	public static JsonNode map2json(Map map){
		return mapper.convertValue(map, JsonNode.class);
	}


}
