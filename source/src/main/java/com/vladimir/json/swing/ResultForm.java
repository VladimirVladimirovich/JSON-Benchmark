/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vladimir.json.swing;

import com.vladimir.json.data.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.*;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Vladimir
 */
public class ResultForm extends javax.swing.JFrame {
     private ArrayList<Vector<String>> rowVectorList;
     private ArrayList<Vector<String>> rowVectorListPercent;
     
     private ArrayList<String> usedLibsNames;
     
     private DefaultTableModel timeTableModel;
     private DefaultTableModel percentTableModel;

    /**
     * Creates new form ResultForm
     * @param parent
     * @param modal
     */
    public ResultForm(java.awt.Frame parent, boolean modal) {
        //super(parent, modal);
        
        timeTableModel = new DefaultTableModel();
        percentTableModel = new DefaultTableModel();

        initComponents();
    }
    
    public void setTestLabel(String text) {
        testLabel.setText(text);
    }
    
    public void setResultDataList(ArrayList<Vector<String>> rowVectorList, ArrayList<Vector<String>> rowVectorListPercent, ArrayList<String> usedLibsNames) {
        this.rowVectorList = new ArrayList<Vector<String>>();
        this.rowVectorListPercent = new ArrayList<Vector<String>>();
        this.usedLibsNames = new ArrayList<String>();
        
        this.usedLibsNames = usedLibsNames;
        this.rowVectorList = rowVectorList;
        this.rowVectorListPercent = rowVectorListPercent;
    }
    
    public void fillTimeTable() {
        if (timeTableModel.getColumnCount() == 0)
            timeTableModel.addColumn("JSON type");
            
        for(String name : usedLibsNames) {
            timeTableModel.addColumn(name);
        }
        
        for(Vector row : rowVectorList) {
            timeTableModel.addRow(row);
        }
    }
    
    public void fillPercentTable() {
        if (percentTableModel.getColumnCount() == 0)
            percentTableModel.addColumn("JSON type");
            
        for(String name : usedLibsNames) {
            percentTableModel.addColumn(name);
        }
        
        for(Vector row : rowVectorListPercent) {
            percentTableModel.addRow(row);
        }
    }
    
    public void setTablesWidht() {
        int [] resTableDimensions = new int [resultTable.getColumnCount()];
        int [] percTableDimensions = new int [percentTable.getColumnCount()];
        
        for(int i = 0; i < resultTable.getColumnCount(); i++)
            resTableDimensions[i] = 65;
        
        for(int i = 0; i < percentTable.getColumnCount(); i++)
            percTableDimensions[i] = 65;
            
        for(int i = 0; i < resultTable.getColumnCount(); i++)
            resultTable.getColumnModel().getColumn(i).setMinWidth(resTableDimensions[i]);
        
        for(int i = 0; i < percentTable.getColumnCount(); i++)
            percentTable.getColumnModel().getColumn(i).setMinWidth(percTableDimensions[i]);
    }
    
    public void updateChart() {
        JFreeChart chart = createChart();
        JFreeChart percentChart = createPercentageChart();
        
        JPanel chartPanel = new ChartPanel(chart);
        JPanel percentChartPanel = new ChartPanel(percentChart);
        
        chartPanel.setSize(jPanelChart.getSize());
        percentChartPanel.setSize(jPanelPercentChart.getSize());
        
        jPanelChart.add(chartPanel);
        jPanelPercentChart.add(percentChartPanel);
        jPanelChart.getParent().validate();
        jPanelPercentChart.getParent().validate();
    }
    
    private JFreeChart createChart() {
        JFreeChart barChart = ChartFactory.createBarChart(
            "Avarage benchmarking results",           
            "Libraries",            
            "Time (ms)",            
            createDataset(),          
            PlotOrientation.VERTICAL,           
            true, true, false);
        
        /*XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        plot.setRangeGridlinesVisible(false);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        */
        return barChart;
    }
    
    private JFreeChart createPercentageChart() {
        JFreeChart barChart = ChartFactory.createBarChart(
            "Performance boost percentage",           
            "Libraries",            
            "Percents (%)",            
            createPercentageDataset(),          
            PlotOrientation.VERTICAL,           
            true, true, false);
        
        /*XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        plot.setRangeGridlinesVisible(false);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        */
        return barChart;
    }
    
    private CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        int rows = resultTable.getRowCount();
        int cols = resultTable.getColumnCount();
        
        String [] testNames = new String[rows];
        String [] libNames = new String[cols];
       
        libNames[0] = "JSON type";
        
        for(int col = 1; col < cols; col++) {
            libNames[col] = String.valueOf(resultTable.getColumnName(col));
        }
        
        for(int row = 0; row < rows; row++) {
            testNames[row] = String.valueOf(resultTable.getValueAt(row, 0));
        }
        
        for(int col = 1; col < cols; col++) {
            for(int row = 0; row < rows; row++) {
                dataset.addValue(Double.parseDouble(resultTable.getValueAt(row, col).toString().replaceFirst("ms", "")), libNames[col], testNames[row]);
            }
        }
        
        return dataset;
    }
    
    private CategoryDataset createPercentageDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        int rows = percentTable.getRowCount();
        int cols = percentTable.getColumnCount();
        
        String [] testNames = new String[rows];
        String [] libNames = new String[cols];
       
        libNames[0] = "JSON type";
        
        for(int col = 1; col < cols; col++) {
            libNames[col] = String.valueOf(percentTable.getColumnName(col));
        }
        
        for(int row = 0; row < rows; row++) {
            testNames[row] = String.valueOf(percentTable.getValueAt(row, 0));
        }
        
        for(int col = 1; col < cols; col++) {
            for(int row = 0; row < rows; row++) {
                dataset.addValue(Double.parseDouble(percentTable.getValueAt(row, col).toString().replaceFirst("%", "")), libNames[col], testNames[row]);
            }
        }
        
        return dataset;
    }
    
    private void saveToFile() throws Exception {
        Date date = new Date();
        String fileTitle = "JSON benchmark from " + date.getTime() + ".res";
        
        fileTitle = fileTitle.replace(":", "_");
        
        SavedData save = new SavedData(rowVectorList, rowVectorListPercent, usedLibsNames, testLabel.getText());
        
        FileOutputStream fos = new FileOutputStream(fileTitle);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(save);
        oos.flush();
        oos.close();
        
        JOptionPane.showMessageDialog(this, "File was saved successfully", "", JOptionPane.INFORMATION_MESSAGE);
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelTabel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        percentTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        testLabel = new javax.swing.JLabel();
        jPanelChart = new javax.swing.JPanel();
        jPanelPercentChart = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelTabel.setPreferredSize(new java.awt.Dimension(745, 300));

        resultTable.setModel(timeTableModel);
        resultTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(resultTable);

        percentTable.setModel(percentTableModel);
        percentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                percentTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(percentTable);

        jLabel1.setText("Avarage benchmarking results (ms)");

        jLabel2.setText("Performance boost percentage (%)");

        javax.swing.GroupLayout jPanelTabelLayout = new javax.swing.GroupLayout(jPanelTabel);
        jPanelTabel.setLayout(jPanelTabelLayout);
        jPanelTabelLayout.setHorizontalGroup(
            jPanelTabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTabelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
                    .addGroup(jPanelTabelLayout.createSequentialGroup()
                        .addGroup(jPanelTabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(testLabel)
                            .addComponent(jLabel2))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelTabelLayout.setVerticalGroup(
            jPanelTabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTabelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(testLabel)
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Resulting tables", jPanelTabel);

        javax.swing.GroupLayout jPanelChartLayout = new javax.swing.GroupLayout(jPanelChart);
        jPanelChart.setLayout(jPanelChartLayout);
        jPanelChartLayout.setHorizontalGroup(
            jPanelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 745, Short.MAX_VALUE)
        );
        jPanelChartLayout.setVerticalGroup(
            jPanelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 334, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Resulting chart", jPanelChart);

        javax.swing.GroupLayout jPanelPercentChartLayout = new javax.swing.GroupLayout(jPanelPercentChart);
        jPanelPercentChart.setLayout(jPanelPercentChartLayout);
        jPanelPercentChartLayout.setHorizontalGroup(
            jPanelPercentChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 745, Short.MAX_VALUE)
        );
        jPanelPercentChartLayout.setVerticalGroup(
            jPanelPercentChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 334, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Percentage chart", jPanelPercentChart);

        jMenu1.setText("File");

        jMenuItem2.setText("Save to file");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Close");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void resultTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultTableMouseClicked
        resultTable.getTransferHandler().exportToClipboard(resultTable, getToolkit().getSystemClipboard(), TransferHandler.COPY);
    }//GEN-LAST:event_resultTableMouseClicked

    private void percentTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_percentTableMouseClicked
        percentTable.getTransferHandler().exportToClipboard(percentTable, getToolkit().getSystemClipboard(), TransferHandler.COPY);
    }//GEN-LAST:event_percentTableMouseClicked

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        try {
            saveToFile();
        }
        catch(Exception e) {showWarningMessage("File was not saved");}
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ResultForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ResultForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ResultForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ResultForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ResultForm dialog = new ResultForm(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanelChart;
    private javax.swing.JPanel jPanelPercentChart;
    private javax.swing.JPanel jPanelTabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable percentTable;
    private javax.swing.JTable resultTable;
    private javax.swing.JLabel testLabel;
    // End of variables declaration//GEN-END:variables
}
