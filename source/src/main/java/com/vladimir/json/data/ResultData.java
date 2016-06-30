/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vladimir.json.data;

/**
 *
 * @author Vladimir
 */
public class ResultData {
    private String testName;
    private String libName;
    private long resultTime;
    
    public ResultData(String testName, String libName, long resultTime) {
        this.testName = testName;
        this.libName = libName;
        this.resultTime = resultTime;
    }
    
    public String getTestName() {
        return this.testName;
    }
    
    public String getLibName() {
        return this.libName;
    }
    
    public long getResultTime() {
        return this.resultTime;
    }
}
