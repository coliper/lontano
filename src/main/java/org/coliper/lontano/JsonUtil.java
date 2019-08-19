package org.coliper.lontano;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

import javax.json.Json;
import javax.json.stream.JsonParser;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JsonUtil {

	private JsonUtil() {
		// no instances
	}

	public static String[] splitJsonArray(String json) {
		final JsonParser jsonParser = Json.createParser(new StringReader(json));
		jsonParser.next();
		return jsonParser.getArrayStream().map(Object::toString).toArray(String[]::new);
	}

	public static class Bean {
		private int intValue;
		private String stringValue;

		public int getIntValue() {
			return intValue;
		}

		public void setIntValue(int intValue) {
			this.intValue = intValue;
		}

		public String getStringValue() {
			return stringValue;
		}

		public void setStringValue(String stringValue) {
			this.stringValue = stringValue;
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Hello");

		Bean bean = new Bean();
		bean.setIntValue(209387);
		Object[] objects = new Object[4];
		objects[0] = bean;
		objects[1] = new Date();
		objects[2] = LocalDateTime.now();
		objects[3] = null;

		ObjectMapper om = new ObjectMapper();
		String json = om.writeValueAsString(objects);
		System.out.println("JSON:");
		System.out.println(json);

		String[] elements = splitJsonArray(json);
		System.out.println(Arrays.asList(elements));
	}
}
