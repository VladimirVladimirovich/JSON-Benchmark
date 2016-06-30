package com.vladimir.json.libs;

import java.lang.reflect.Type;

import com.google.gson.Gson;

public class GoogleGson extends com.vladimir.json.bench.JsonInter {
	Object obj;
	Gson p;
	Type type;

	public GoogleGson() {
		init();
	}
	
	@Override
	public void init() {
		p = new Gson();
		type = null;
		obj = null;
	}
	
	@Override
	public Object parseObj(String json) throws Exception {
		obj = p.fromJson(json, java.util.HashMap.class);
		type = java.util.HashMap.class;
		return obj;
	}

	@Override
	public Object parseArray(String json) throws Exception {
		obj = p.fromJson(json, java.util.ArrayList.class);
		type = java.util.ArrayList.class;
		return obj;
	}

	@Override
	public String toJsonString() {
		return p.toJson(obj, type);
	}

        @Override
        public String toJsonString(Object obj) throws Exception {
            return p.toJson(obj, java.util.HashMap.class);
        }
        
        @Override
        public String toJsonStringArray(Object obj) throws Exception {
            return p.toJson(obj, java.util.ArrayList.class);
        }

	@Override
	public String getSimpleName() {
		return "GSON";
	}
}
