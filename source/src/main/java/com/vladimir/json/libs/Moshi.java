/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vladimir.json.libs;

import com.squareup.moshi.JsonAdapter;

public class Moshi extends com.vladimir.json.bench.JsonInter {
        JsonAdapter<Object> p;
        Object obj;
        
        public Moshi() {
		init();
	}
	
	@Override
	public void init() {
		obj = null;
		com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().build();
                p = moshi.adapter(Object.class);
	}

	
	@Override
	public Object parseObj(String json) throws Exception {
                obj = p.fromJson(json);
		return obj;
	}

	@Override
	public Object parseArray(String json) throws Exception {
		obj = p.fromJson(json);
		return obj;
	}

	@Override
	public String toJsonString() {
		return p.toJson(obj);
	}
        
        @Override
	public String toJsonString(Object obj) {
		return p.toJson(obj);
	}
        
         @Override
	public String toJsonStringArray(Object obj) {
		return p.toJson(obj);
	}

	@Override
	public String getSimpleName() {
		return "Moshi";
	}
}
