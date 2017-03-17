package dssim;

/*
 * The MIT License
 *
 * Copyright 2016 Lander University.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/**
 *
 * @author Lander University
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author TJ
 */
public class Table {

    public Table(DefaultTableModel tableModel) {
        final JFrame tableFrame = new JFrame("Table");
        tableFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Create and set up the content pane.
        final JTable table = new JTable(tableModel);
        //make it scrollable
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        tableFrame.setContentPane(scrollPane);

        //make a menue bar with options
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenuItem exitAction = new JMenuItem("Exit");
        JMenuItem saveAction = new JMenuItem("Save");
        JMenuItem printAction = new JMenuItem("Print");
        fileMenu.add(exitAction);
        fileMenu.add(saveAction);
        fileMenu.add(printAction);
        tableFrame.setJMenuBar(menuBar);

        //exit action
        exitAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                tableFrame.setVisible(false);
                tableFrame.dispose();
            }
        });
        //save action using file saver
        saveAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser fc = new JFileChooser();
                JFrame frame = new JFrame("Save");
                int userSelection = fc.showSaveDialog(frame);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fc.getSelectedFile();
                    //System.out.println("Save as file: " + fileToSave.getAbsolutePath());
                    File file = new File(fileToSave.getAbsolutePath() + ".csv");
                    try {
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileWriter fw = new FileWriter(file.getAbsoluteFile());
                        
                        BufferedWriter bw = new BufferedWriter(fw);
                        for (int i = 0; i < table.getRowCount(); i++) {
                            for (int j = 0; j < table.getColumnCount(); j++) {
                                bw.write(table.getModel().getValueAt(i, j) + ",");
                            }
                            bw.newLine();
                        }
                        bw.close();
                        fw.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        });
        //print action
        printAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    table.print();
                } catch (Exception pe) {
                    System.err.println("Error printing: " + pe.getMessage());
                }
            }
        });
        //show the table
        tableFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        tableFrame.pack();
        tableFrame.setVisible(true);
    }

}
