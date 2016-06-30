/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vladimir.json.libs;

import com.owlike.genson.*;

public class Genson extends com.vladimir.json.bench.JsonInter {
        com.owlike.genson.Genson p;
        Object obj;
        
        public Genson() {
		init();
	}
	
	@Override
	public void init() {
		obj = null;
		p = new com.owlike.genson.Genson();
	}

	
	@Override
	public Object parseObj(String json) throws Exception {
                obj = p.deserialize(json, Object.class);
		return obj;
	}

	@Override
	public Object parseArray(String json) throws Exception {
		obj = p.deserialize(json, Object.class);
		return obj;
	}

	@Override
	public String toJsonString() {
		return p.serialize(obj);
	}
        
        @Override
	public String toJsonString(Object obj) {
		return p.serialize(obj);
	}

        
        @Override
	public String toJsonStringArray(Object obj) {
		return p.serialize(obj);
	}

	@Override
	public String getSimpleName() {
		return "Genson";
	}
}
