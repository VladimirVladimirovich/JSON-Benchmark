package com.vladimir.json.libs;

import net.minidev.json.JSONAware;
import net.minidev.json.parser.JSONParser;

public class JsonSmart extends com.vladimir.json.bench.JsonInter {
	JSONParser p;
	Object obj;
	
	public JsonSmart() {
		init();
	}
	
	@Override
	public void init() {
		obj = null;
		p = new JSONParser(JSONParser.MODE_PERMISSIVE);
	}

	
	@Override
	public Object parseObj(String json) throws Exception {
		obj = p.parse(json);
		return obj;
	}

	@Override
	public Object parseArray(String json) throws Exception {
		obj = p.parse(json);
		return obj;
	}

	@Override
	public String toJsonString() {
		return ((JSONAware)obj).toJSONString();
	}
        
        @Override
	public String toJsonString(Object obj) {
		return ((JSONAware)obj).toJSONString();
	}
        
        @Override
	public String toJsonStringArray(Object obj) {
		return ((JSONAware)obj).toJSONString();
	}

	@Override
	public String getSimpleName() {
		return "Json-Smart";
	}

}
