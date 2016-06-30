/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vladimir.json.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author Vladimir
 */
public class SavedData implements Serializable {
        private ArrayList<Vector<String>> rowVectorList;
        private ArrayList<Vector<String>> rowVectorListPercent;
        private ArrayList<String> usedLibsNames;
        String testInfoLabel;
        
        public SavedData(ArrayList<Vector<String>> rowVectorList, ArrayList<Vector<String>> rowVectorListPercent, ArrayList<String> usedLibsNames, String label) {
            this.rowVectorList = new ArrayList<Vector<String>>();
            this.rowVectorListPercent = new ArrayList<Vector<String>>();
            this.usedLibsNames = new ArrayList<String>();
            
            this.rowVectorList = rowVectorList;
            this.rowVectorListPercent = rowVectorListPercent;
            this.usedLibsNames = usedLibsNames;
            this.testInfoLabel = label;
        }
        
        public ArrayList<Vector<String>> getRowVectorList() {
            return this.rowVectorList;
        }
        
        public ArrayList<Vector<String>> getRowVectorListPercent() {
            return this.rowVectorListPercent;
        }
        
        public ArrayList<String> getUsedLibsNames() {
            return this.usedLibsNames;
        }
        
        public String getTestInfoLabel() {
            return this.testInfoLabel;
        }
}
