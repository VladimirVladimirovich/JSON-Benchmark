package com.vladimir.json.libs;

import cc.plural.jsonij.JSON;

public class JsonIJ extends com.vladimir.json.bench.JsonInter {
	JSON obj;
	
	public JsonIJ() {
		init();
	}
	
	@Override
	public void init() {
		obj = null;
	}

	@Override
	public Object parseObj(String json) throws Exception {
		obj = JSON.parse(json);
		return obj;
	}

	@Override
	public Object parseArray(String json) throws Exception {
		obj= JSON.parse(json);
		return obj;
	}

	@Override
	public String toJsonString() {
		return obj.toJSON();
	}
        
        @Override
	public String toJsonString(Object obj) {
		return ((JSON)obj).toJSON();
	}
        
        @Override
	public String toJsonStringArray(Object obj) {
		return ((JSON)obj).toJSON();
	}
	
	@Override
	public String getSimpleName() {
		return "JsonIJ";
	}

}
