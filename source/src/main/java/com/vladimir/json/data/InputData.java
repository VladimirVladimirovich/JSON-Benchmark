/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vladimir.json.data;

import java.util.ArrayList;

/**
 *
 * @author Vladimir
 */
public class InputData {
    private ArrayList<Integer> libsIndexList;
    private ArrayList<Integer> testsIndexList;
    private TestType testType;
    private int testsCount;
    private int iterationsCount;
    private boolean isFileChoosed;
    private String fileString;
    
    public InputData() {
        libsIndexList = new ArrayList<Integer>();
        testsIndexList = new ArrayList<Integer>();
        testType = TestType.PARSE;
        testsCount = 0;
        iterationsCount = 0;
        isFileChoosed = false;
        fileString = null;
    }
    
    public InputData(InputData input) {
        this.libsIndexList = new ArrayList<Integer>();
        this.libsIndexList = input.getLibsIndexList();
        this.testsIndexList = new ArrayList<Integer>();
        this.testsIndexList = input.getTestsIndexList();
        this.testType = input.getTestType();
        this.testsCount = input.getTestsCount();
        this.iterationsCount = input.getIterationsCount();
        this.isFileChoosed = input.isFileChoosed();
        this.fileString = input.getFileString();
    }
    
    public void addLibIndex(int index) {
        libsIndexList.add(index);
    }
    
    public void addTestIndex(int index) {
        testsIndexList.add(index);
    }
    
    public void setTestType(TestType testType) {
        this.testType = testType;
    }
    
    public void setTestsCount(int count) {
        this.testsCount = count;
    }
    
    public void setIterationsCount(int count) {
        this.iterationsCount = count;
    }
    
    public void setIsFileChoosed(boolean value) {
        this.isFileChoosed = value;
    }
    
    public void setFileString(String string) {
        this.fileString = string;
    }
    
    public ArrayList<Integer> getLibsIndexList() {
        return this.libsIndexList;
    }
    
    public ArrayList<Integer> getTestsIndexList() {
        return this.testsIndexList;
    }
    
    public TestType getTestType() {
        return this.testType;
    }
    
    public int getTestsCount() {
        return this.testsCount;
    }
    
    public int getIterationsCount() {
        return this.iterationsCount;
    }
    
    public boolean isFileChoosed() { return this.isFileChoosed; }
    
    public String getFileString() { return this.fileString; }
}
