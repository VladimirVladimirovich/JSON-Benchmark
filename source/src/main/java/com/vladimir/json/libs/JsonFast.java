package com.vladimir.json.libs;

import com.alibaba.fastjson.*;

public class JsonFast extends com.vladimir.json.bench.JsonInter {
	Object obj;
	
	public JsonFast() {
		init();
	}
	
	@Override
	public void init() {
		obj = null;
	}

	@Override
	public Object parseObj(String json) throws Exception {
		obj = JSONObject.parse(json);
		return obj;
	}

	@Override
	public Object parseArray(String json) throws Exception {
		obj = JSONObject.parse(json);
		return obj;
	}

	@Override
	public String toJsonString() {
		return obj.toString();
	}

        @Override
        public String toJsonString(Object obj) throws Exception {
            return obj.toString();
        }
        
        @Override
        public String toJsonStringArray(Object obj) throws Exception {
            return obj.toString();
        }

	@Override
	public String getSimpleName() {
		return "Json-fast";
	}
}
