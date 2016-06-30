/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vladimir.json.libs;

import java.io.IOException;

public class LoganSquare extends com.vladimir.json.bench.JsonInter {
        Object obj;
        String str;
    
        public LoganSquare() {
		init();
	}
	
	@Override
	public void init() {
		obj = null;
                str = null;
	}

	
	@Override
	public Object parseObj(String json) throws Exception {
		obj = com.bluelinelabs.logansquare.LoganSquare.parse(json, Object.class);
		return obj;
	}

	@Override
	public Object parseArray(String json) throws Exception {
		obj = com.bluelinelabs.logansquare.LoganSquare.parse(json, Object.class);
		return obj;
	}

	@Override
	public String toJsonString() {
            try {
		str = com.bluelinelabs.logansquare.LoganSquare.serialize(obj);
            } catch(IOException e) {}
            
            return str;
	}
        
        @Override
	public String toJsonString(Object obj) {
            try {
		str = com.bluelinelabs.logansquare.LoganSquare.serialize(obj);
            } catch(IOException e) {}
            
            return str;
	}
        
        @Override
	public String toJsonStringArray(Object obj) {
            try {
		str = com.bluelinelabs.logansquare.LoganSquare.serialize(obj);
            } catch(IOException e) {}
            
            return str;
	}

	@Override
	public String getSimpleName() {
		return "LoganSquare";
	}
}
