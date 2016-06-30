package com.vladimir.json.libs;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class JsonSimple extends com.vladimir.json.bench.JsonInter {
	Object obj;
	JSONParser p;
	
	public JsonSimple() {
		init();
	}
	
	@Override
	public void init() {
		obj = null;
		p = new JSONParser();
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
		return "Json-Simple";
	}
}
