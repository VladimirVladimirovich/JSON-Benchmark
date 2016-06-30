package com.vladimir.json.libs;

import org.json.*;

public class JsonJava extends com.vladimir.json.bench.JsonInter {
	Object obj;
	
	public JsonJava() {
		init();
	}
	
	@Override
	public void init() {
		obj = null;
	}

	@Override
	public Object parseObj(String json) throws Exception {
		obj = new JSONObject(json);
		return obj;
	}

	@Override
	public Object parseArray(String json) throws Exception {
		obj = new JSONArray(json);
		return obj;
	}

	@Override
	public String toJsonString() {
		return obj.toString();
	}
        
        @Override
	public String toJsonString(Object obj) {
		return obj.toString();
	}
        
        @Override
        public String toJsonStringArray(Object obj) {
		return obj.toString();
	}

	@Override
	public String getSimpleName() {
		return "JSON java";
	}
}
