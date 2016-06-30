package com.vladimir.json.libs;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.ObjectMapper;

public class Jackson extends com.vladimir.json.bench.JsonInter {
	ObjectMapper p;
	JsonNode obj;

	public Jackson() {
		init();
	}

	@Override
	public void init() {
		p = new ObjectMapper();
		obj = null;
	}

	@Override
	public Object parseObj(String json) throws Exception {
		obj = p.readTree(json);
		return obj;
	}

	@Override
	public Object parseArray(String json) throws Exception {
		obj = p.readTree(json);
		return obj;
	}

	@Override
	public String toJsonString() {
		try {
			return p.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

        @Override
        public String toJsonString(Object obj) throws Exception {
            try {
                return p.writeValueAsString(obj);
            } catch (Exception e) {
		throw new RuntimeException(e);
            }
        }
        
        @Override
        public String toJsonStringArray(Object obj) throws Exception {
            try {
                return p.writeValueAsString(obj);
            } catch (Exception e) {
		throw new RuntimeException(e);
            }
        }
        
	@Override
	public String getSimpleName() {
		return "Jackson";
	}
}
