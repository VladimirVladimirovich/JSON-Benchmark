/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vladimir.json.swing;

import com.vladimir.json.bench.*;
import com.vladimir.json.data.*;
import com.vladimir.json.libs.Genson;
import com.vladimir.json.libs.GoogleGson;
import com.vladimir.json.libs.Jackson;
import com.vladimir.json.libs.JsonFast;
import com.vladimir.json.libs.JsonIJ;
import com.vladimir.json.libs.JsonSimple;
import com.vladimir.json.libs.JsonSmart;
import com.vladimir.json.libs.LoganSquare;
import com.vladimir.json.libs.Moshi;
import com.vladimir.json.libs.JsonJava;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Vladimir
 */
public class MainForm extends javax.swing.JFrame implements PropertyChangeListener {
    private ResultForm resultForm;
    
    private InputData inputData;
    private BenchTask benchTask;

    private ArrayList<JCheckBox> checkBoxLibs;
    private ArrayList<JCheckBox> checkBoxTests;
    
    private File file;
    
    Class[] clazz = new Class[] { Moshi.class, LoganSquare.class, Genson.class, GoogleGson.class, JsonJava.class,
             JsonFast.class, JsonSimple.class, JsonIJ.class, Jackson.class, JsonSmart.class };
        
    String[] testTypes = new String [] { "text", "int", "float", "bool", "unicode", "mix" };
    
    class BenchTask extends SwingWorker<Void, String> {
        private JsonBench jsonBench;
        int curTestNum;
        int totalTestNum;

        public BenchTask() {
            if(inputData.isFileChoosed()) 
                jsonBench = new JsonBench(inputData.getIterationsCount(), inputData.getTestsCount(), inputData.getFileString());
            else
                jsonBench = new JsonBench(inputData.getIterationsCount(), inputData.getTestsCount());
            
            curTestNum = 0;
            totalTestNum = inputData.getLibsIndexList().size() * inputData.getTestsIndexList().size() * inputData.getTestsCount();
        }

        @Override
        protected Void doInBackground() {
            ArrayList<Integer> libsIndexList = new ArrayList<Integer>();
            libsIndexList = inputData.getLibsIndexList();
            
            ArrayList<Integer> testsIndexList = new ArrayList<Integer>();
            testsIndexList = inputData.getTestsIndexList();
            
            TestType testType = inputData.getTestType();
            int testsCount = inputData.getTestsCount();

            String progress;
            
            BenchData.changeTest(testTypes[0]);
            
            for(int i = 0; i < testsCount; i++) {
                BenchData.cleanCache();
                
                for(Integer testIndex : testsIndexList) {
                    if(testIndex != -1)
                        BenchData.changeTest(testTypes[testIndex]);
                    
                    for(Integer libIndex : libsIndexList) {
                        try {
                             if(this.isCancelled())
                                break;
                             
                            long res = jsonBench.doBenchmark(testIndex, libIndex, testType);
                            curTestNum++;

                            if(!inputData.isFileChoosed())
                                progress = testTypes[testIndex] + " > "  + clazz[libIndex].getSimpleName() + " > " + res + " ms";
                            else
                                progress = "Custom JSON > "  + clazz[libIndex].getSimpleName() + " > " + res + " ms";
                            
                            setProgress(curTestNum * 100 / totalTestNum);
                            publish(progress);
                        } catch (Exception e) {}
                     }
                }
            }

            return null;
        }

        @Override
        protected void process(List<String> list) {
            for(String message : list){
                 textArea.append(message + "\n");
            }
        }

         @Override
        public void done() {
            jButtonStart.setEnabled(true);
            stopButton.setEnabled(false);
            jButton1.setEnabled(true);
            
            if(this.isCancelled()) {
                return;
            }
            
            textArea.append("Done!");
        
            jsonBench.processResultDataList();
            
            resultForm = new ResultForm(MainForm.this, true);
            resultForm.setResultDataList(jsonBench.getRowVectorList(), jsonBench.getRowVectorListPercent(), jsonBench.getUsedLibsList());

            String text;
            
            if(inputData.getTestType().equals(TestType.PARSE))
                text = "Type: Parse; ";
            else
                text = "Type: Generate; ";
            
            text = text + "Tests: " + inputData.getTestsCount() + "; Iterations: " + inputData.getIterationsCount();
            
            resultForm.setTestLabel(text);
            resultForm.fillTimeTable();
            resultForm.fillPercentTable();
            resultForm.setTablesWidht();
            resultForm.updateChart();
            resultForm.setVisible(true);
            
            progressBar.setValue(0);
        }
    }
    
    /**
     * Creates new form MainForm
     */
    public MainForm() {
        initComponents();
        setCheckBoxGroups();
    }

    private void setCheckBoxGroups() {
        checkBoxLibs = new ArrayList<>();
        checkBoxLibs.add(jCheckBox4);
        checkBoxLibs.add(jCheckBox5);
        checkBoxLibs.add(jCheckBox6);
        checkBoxLibs.add(jCheckBox7);
        checkBoxLibs.add(jCheckBox8);
        checkBoxLibs.add(jCheckBox9);
        checkBoxLibs.add(jCheckBox10);
        checkBoxLibs.add(jCheckBox11);
        checkBoxLibs.add(jCheckBox12);
        checkBoxLibs.add(jCheckBox13);
        
        checkBoxTests = new ArrayList<>();
        checkBoxTests.add(jCheckBox2);
        checkBoxTests.add(jCheckBox14);
        checkBoxTests.add(jCheckBox15);
        checkBoxTests.add(jCheckBox16);
        checkBoxTests.add(jCheckBox17);
        checkBoxTests.add(jCheckBox18);
    }
    
    private boolean checkInputData() {
        try {
            boolean libsFlag = false, testsFlag = false;
            
            for ( JCheckBox checkBox : checkBoxLibs ) {
                if(checkBox.isSelected()) {
                    libsFlag = true;
                    break;
                }
            }
            
            if(file == null) {
                for ( JCheckBox checkBox : checkBoxTests ) {
                    if(checkBox.isSelected()) {
                        testsFlag = true;
                        break;
                    }
                }
            }
            else
                testsFlag = true;
            
            if(!libsFlag || !testsFlag)
            {
                showWarningMessage("At least one library or one test type must be choosen!");
                return false;
            }
            else if (Integer.parseInt(jTextFieldTestCount.getText().toString()) <= 0 || Integer.parseInt(jTextFieldTestCount.getText().toString()) > 10000000) {
                showWarningMessage("Enter correct iterations count value! (10 000 000 - max)");
                return false;
            }
            else if (Integer.parseInt(textFieldTests.getText().toString()) <= 0 || Integer.parseInt(textFieldTests.getText().toString()) > 1000) {
                showWarningMessage("Enter correct tests count value! (10000 - max)");
                return false;
            }
            else
                return true;
        }
        catch(NumberFormatException e) {
            showWarningMessage("Enter correct tests count value! (10 000 000 - max)");
            return false;
        }
    }
    
    private void getInputData() {
        inputData = new InputData();

        for(int i = 0; i < checkBoxLibs.size(); i++) {
            if(checkBoxLibs.get(i).isSelected())
                inputData.addLibIndex(i);
        }

        if(buttonGroup.isSelected(radioButtonParse.getModel()))
            inputData.setTestType(TestType.PARSE);
        else
            inputData.setTestType(TestType.GENERATE);
        
        inputData.setIterationsCount(Integer.parseInt(jTextFieldTestCount.getText().toString()));
        inputData.setTestsCount(Integer.parseInt(textFieldTests.getText().toString()));
        
        try {
            if(file != null) {
                getJsonFromFile();
                inputData.addTestIndex(-1);
                return;
            }
        } catch (Exception e) {}
        
        for(int i = 0; i < checkBoxTests.size(); i++) {
            if(checkBoxTests.get(i).isSelected())
                inputData.addTestIndex(i);
        }
    }
    
    private void getJsonFromFile() throws Exception {
        FileReader reader = new FileReader(file);
        com.owlike.genson.Genson genson = new com.owlike.genson.Genson();
        Object obj = genson.deserialize(reader, Object.class);
        
        inputData.setIsFileChoosed(true);
        inputData.setFileString(genson.serialize(obj));

        /*JSONObject object = (JSONObject) JSONValue.parse(reader);
        
        inputData.setIsFileChoosed(true);
        inputData.setFileString(object.toJSONString());*/
    }
    
    private void openCustomJsonFile(){
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON file", "json");
        
        fileChooser.setFileFilter(filter);
        int res = fileChooser.showDialog(this, "Open file");
        
        if (res == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            jTextFieldPath.setText(file.getPath());
            
            for ( JCheckBox checkBox : checkBoxTests ) {
                checkBox.setEnabled(false);
            }
            jCheckBox1.setEnabled(false);
        }
    }
    
    private void runBenchmark() {
        benchTask = new BenchTask();
        benchTask.addPropertyChangeListener(this);
        benchTask.execute();
        
        stopButton.setEnabled(true);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);   
        }
    }
    
    private void loadFromFile() throws Exception {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Saved files", "res");
        fileChooser.setFileFilter(filter);
        //fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            try {
                FileInputStream fis = new FileInputStream(selectedFile);
                ObjectInputStream oin = new ObjectInputStream(fis);
                SavedData save = (SavedData) oin.readObject();
                
                resultForm = new ResultForm(MainForm.this, true);
                resultForm.setResultDataList(save.getRowVectorList(), save.getRowVectorListPercent(), save.getUsedLibsNames());
                resultForm.setTestLabel(save.getTestInfoLabel());
                resultForm.fillTimeTable();
                resultForm.fillPercentTable();
                resultForm.setTablesWidht();
                resultForm.updateChart();
                resultForm.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }
    
    public void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        jCheckBox9 = new javax.swing.JCheckBox();
        jCheckBox10 = new javax.swing.JCheckBox();
        jCheckBox11 = new javax.swing.JCheckBox();
        jCheckBox12 = new javax.swing.JCheckBox();
        jCheckBox13 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox14 = new javax.swing.JCheckBox();
        jCheckBox15 = new javax.swing.JCheckBox();
        jCheckBox16 = new javax.swing.JCheckBox();
        jCheckBox17 = new javax.swing.JCheckBox();
        jCheckBox18 = new javax.swing.JCheckBox();
        jButtonStart = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        stopButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        radioButtonParse = new javax.swing.JRadioButton();
        radioButtonSerialize = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldTestCount = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        textFieldTests = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jFileChooserButton = new javax.swing.JButton();
        jFileChooserLabel = new javax.swing.JLabel();
        jTextFieldPath = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "JSON libraries", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N
        jPanel1.setToolTipText("");

        jCheckBox3.setText("All");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        jCheckBox4.setText("Moshi");

        jCheckBox5.setText("LoganSquare");

        jCheckBox6.setText("Genson");

        jCheckBox7.setText("GSON");

        jCheckBox8.setText("Json-java");

        jCheckBox9.setText("Json-fast");

        jCheckBox10.setText("Json-simple");

        jCheckBox11.setText("JsonIJ");

        jCheckBox12.setText("Jackson");

        jCheckBox13.setText("Json-smart");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox3)
                    .addComponent(jCheckBox4)
                    .addComponent(jCheckBox5)
                    .addComponent(jCheckBox6)
                    .addComponent(jCheckBox7)
                    .addComponent(jCheckBox8)
                    .addComponent(jCheckBox9)
                    .addComponent(jCheckBox10)
                    .addComponent(jCheckBox11)
                    .addComponent(jCheckBox12)
                    .addComponent(jCheckBox13))
                .addGap(5, 5, 5))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jCheckBox3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox13)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "JSON values types", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N
        jPanel2.setToolTipText("");

        jCheckBox1.setText("All");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox2.setText("String");

        jCheckBox14.setText("Int");

        jCheckBox15.setText("Float");

        jCheckBox16.setText("Boolean");

        jCheckBox17.setText("Unicode (UTF-8)");

        jCheckBox18.setText("Mix");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2)
                    .addComponent(jCheckBox14)
                    .addComponent(jCheckBox15)
                    .addComponent(jCheckBox16)
                    .addComponent(jCheckBox17)
                    .addComponent(jCheckBox18))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox18)
                .addGap(5, 5, 5))
        );

        jButtonStart.setText("START");
        jButtonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartActionPerformed(evt);
            }
        });

        progressBar.setStringPainted(true);

        stopButton.setText("STOP");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Current benchmarking progress", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N

        textArea.setColumns(20);
        textArea.setFont(new java.awt.Font("Arial Unicode MS", 0, 11)); // NOI18N
        textArea.setRows(5);
        jScrollPane1.setViewportView(textArea);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jScrollPane1)
                .addGap(5, 5, 5))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Initial settings", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N

        buttonGroup.add(radioButtonParse);
        radioButtonParse.setSelected(true);
        radioButtonParse.setText("Parse (JSON to Obj)");

        buttonGroup.add(radioButtonSerialize);
        radioButtonSerialize.setText("Generate (Obj to JSON)");

        jLabel1.setText("Iterations");

        jTextFieldTestCount.setText("10000");

        jLabel2.setText("Tests");

        textFieldTests.setText("10");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(radioButtonSerialize, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(0, 0, 0)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textFieldTests, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldTestCount, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(radioButtonParse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(radioButtonParse)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonSerialize)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldTestCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(textFieldTests, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Open custom JSON-file", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N

        jFileChooserButton.setText("Open");
        jFileChooserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooserButtonActionPerformed(evt);
            }
        });

        jFileChooserLabel.setText("Path:");

        jTextFieldPath.setEditable(false);

        jButton1.setText("Clear");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jFileChooserLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldPath)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jFileChooserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFileChooserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFileChooserLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(2, 2, 2))
        );

        jMenu2.setText("File");

        jMenuItem3.setText("Load saved file");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem4.setText("Open custom JSON file");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar2.add(jMenu2);

        setJMenuBar(jMenuBar2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonStart, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addGap(5, 5, 5)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(5, 5, 5)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(stopButton, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                            .addComponent(jButtonStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        if(!jCheckBox3.isSelected()) {
            for ( JCheckBox checkBox : checkBoxLibs ) {
                checkBox.setSelected(false);
            }
        }
        else {
            for ( JCheckBox checkBox : checkBoxLibs ) {
                checkBox.setSelected(true);
            }
        }
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        if(!jCheckBox1.isSelected()) {
            for ( JCheckBox checkBox : checkBoxTests ) {
                checkBox.setSelected(false);
            }
        }
        else {
            for ( JCheckBox checkBox : checkBoxTests ) {
                checkBox.setSelected(true);
            }
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jButtonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartActionPerformed
        if(!checkInputData())
            return;
        else
            getInputData();
        
        jButtonStart.setEnabled(false);
        jButton1.setEnabled(false);
        textArea.setText("");
        progressBar.setValue(0);

        runBenchmark();
    }//GEN-LAST:event_jButtonStartActionPerformed

    private void jFileChooserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooserButtonActionPerformed
        openCustomJsonFile();
    }//GEN-LAST:event_jFileChooserButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        benchTask.cancel(true);
    }//GEN-LAST:event_stopButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(!jTextFieldPath.getText().isEmpty()) {
            jTextFieldPath.setText("");
            
            for ( JCheckBox checkBox : checkBoxTests ) {
                checkBox.setEnabled(true);
            }
            
            jCheckBox1.setEnabled(true);
        }
            
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        openCustomJsonFile();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        try {
            loadFromFile();
        } catch(Exception e) {showWarningMessage("Can not load file");}
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonStart;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox11;
    private javax.swing.JCheckBox jCheckBox12;
    private javax.swing.JCheckBox jCheckBox13;
    private javax.swing.JCheckBox jCheckBox14;
    private javax.swing.JCheckBox jCheckBox15;
    private javax.swing.JCheckBox jCheckBox16;
    private javax.swing.JCheckBox jCheckBox17;
    private javax.swing.JCheckBox jCheckBox18;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JButton jFileChooserButton;
    private javax.swing.JLabel jFileChooserLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldPath;
    private javax.swing.JTextField jTextFieldTestCount;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton radioButtonParse;
    private javax.swing.JRadioButton radioButtonSerialize;
    private javax.swing.JButton stopButton;
    private javax.swing.JTextArea textArea;
    private javax.swing.JTextField textFieldTests;
    // End of variables declaration//GEN-END:variables
}
