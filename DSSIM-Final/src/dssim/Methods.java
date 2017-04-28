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
import com.mxgraph.view.mxGraph;
import dssim.gui.FlowObject;
import dssim.gui.ModelingObject;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.mariuszgromada.math.mxparser.*;
import dssim.gui.StockObject;
import dssim.gui.VariableObject;
import java.util.Vector;

public final class Methods {

    //this data type is from the jfreechart library
    public XYSeriesCollection data;

    //as well as this one, this one allows an x y type table setup
    public DefaultTableModel tableModel = new DefaultTableModel();
    //these array lists are instantiated here to be used as global values to be passed to the mainform
    public ArrayList<Argument> argumentList = new ArrayList<Argument>();
    public ArrayList<Argument> variableArgList = new ArrayList<Argument>();
    public ArrayList<Double> y0 = new ArrayList<Double>();

    //public javax.swing.JProgressBar pbar= getProgressBar();

    public Methods() {
        // what is this doing? -PMC
    }

    public Methods(ArrayList<StockObject> stockArrayList,
            ArrayList<FlowObject> flowArrayList,
            ArrayList<VariableObject> variableArrayList, double t0,
            double tf, double stepSize, String choice, Argument[] varRefs) {

        StockObject stock;
        VariableObject var;
        mxGraph graph = MainForm.getGraph();
        Object node = graph.createVertex(null, null, "t", 0, 0, 100, 50, "Time");
        VariableObject time = new VariableObject(node, "t", "t", Double.toString(t0), "0", "0",varRefs);
        //for each stock get the stock initial values and add to the argumentList
        for (StockObject stockArrayList1 : stockArrayList) {
            stock = (StockObject) stockArrayList1; //get initial value
            argumentList.add(stock.getStockArg()); // add stock arge to argument list
        }
        //for each variable object, add to the argument array list for variables
        for (VariableObject variableArrayList1 : variableArrayList) {
            var = (VariableObject) variableArrayList1;
            variableArgList.add(var.getVarArg());
        }
        variableArgList.add(time.getVarArg());
        //the user choice of numerical analysis method is sent to these if statements
        if ("rk4".equals(choice)) {
            data = rk4(t0, tf, stepSize, argumentList, variableArgList, stockArrayList, flowArrayList, variableArrayList);
        } else if ("rk2".equals(choice)) {
            data = rk2(t0, tf, stepSize, argumentList, variableArgList, stockArrayList, flowArrayList, variableArrayList);
        } else {
            data = eulers(t0, tf, stepSize, argumentList, variableArgList, stockArrayList, flowArrayList, variableArrayList);
        }

    }

    public XYSeriesCollection returnData() {
        return data;
    }

    public DefaultTableModel getTable() {
        return tableModel;
    }

    public XYSeriesCollection rk4(double t0, double tF, double stepSize,
            ArrayList<Argument> argumentList, ArrayList<Argument> variableArgList,
            ArrayList<StockObject> stockArrayList, ArrayList<FlowObject> flowArrayList, ArrayList<VariableObject> variableArrayList) {

        //aVarList is an argument array that is created from the argument ArrayList given to it. 
        Argument[] aVarList = argumentList.toArray(new Argument[argumentList.size()]);
        //aTempVarList only created to later create the temparg array list
        Argument[] aTempVarList = new Argument[argumentList.size()];
        for (int j = 0; j < aVarList.length; j++) {
            aTempVarList[j] = aVarList[j].clone();
        }
        int numSteps = (int) ((tF - t0) / stepSize);
        double t = t0;

        //int cutoff = String.valueOf(stepSize).length();
        ArrayList<Argument> aTempArgArrayList = new ArrayList<Argument>();
        for (int j = 0; j < aVarList.length; j++) {
            aTempArgArrayList.add(aTempVarList[j]);
        }
        
        double[] dydt = new double[argumentList.size()];

        ArrayList<Double> k1 = new ArrayList<Double>();

        ArrayList<Double> k2 = new ArrayList<Double>();

        ArrayList<Double> k3 = new ArrayList<Double>();

        ArrayList<Double> k4 = new ArrayList<Double>();

        //idea is to set k1 through k4 ArrayLists to double values of 0
        //used to have stockArrayList.size()
        for (int x = 0; x < stockArrayList.size(); x++) {
            k1.add(0.0);
            k2.add(0.0);
            k3.add(0.0);
            k4.add(0.0);
        }

        //array list length of amount of stocks
        ArrayList<XYSeries> series = new ArrayList<XYSeries>();

        //create series to hold graph data
        for (int i = 0; i < stockArrayList.size(); i++) {
            XYSeries tempSeries = new XYSeries(stockArrayList.get(i).getObjName());
            series.add(tempSeries);
        }

        final XYSeriesCollection data = new XYSeriesCollection();

        //add initial values to series
        for (int i = 0; i < stockArrayList.size(); i++) {
            series.get(i).add(0, argumentList.get(i).getArgumentValue());
        }

        int numOfStocks = stockArrayList.size();
        double value;

        // Generates a string to represent the column headers.
        // The first part of the string aka x-axis will always be time
        String stockNames = "Time,";
        for (int x = 0; x < stockArrayList.size(); x++) {
            stockNames += argumentList.get(x).getArgumentName() + ",";
        }
        String[] tableStrings = new String[stockArrayList.size() + 1];
        String columns[] = stockNames.split(",");
        tableModel = new DefaultTableModel(0, stockArrayList.size() + 1);
        tableModel.setColumnIdentifiers(columns); // set the column headers

        for (int n = 0; n < numSteps; n++) {
         
            t = t0 + (n * stepSize);
            //t = Math.floor(t * Math.pow(10,cutoff)) / Math.pow(10,cutoff);
            variableArgList.get(variableArgList.size() - 1).setArgumentValue(t);
            //Let's find k1:
            dydt = RightHandSide(variableArgList, argumentList, flowArrayList, stockArrayList,variableArrayList);

            for (int i = 0; i < numOfStocks; i++) {

                k1.set(i, stepSize * dydt[i]);
            }

            //next let's find k2:
            for (int i = 0; i < numOfStocks; i++) {
                variableArgList.get(variableArgList.size() - 1).setArgumentValue(t + (stepSize / 2));
                value = (argumentList.get(i).getArgumentValue() + (k1.get(i) / 2));
                aTempArgArrayList.get(i).setArgumentValue(value);

                dydt = RightHandSide(variableArgList, aTempArgArrayList, flowArrayList, stockArrayList,variableArrayList);
            }
            for (int i = 0; i < numOfStocks; i++) {
                k2.set(i, stepSize * dydt[i]);
            }

            //next let's find k3:
            for (int i = 0; i < numOfStocks; i++) {
                variableArgList.get(variableArgList.size() - 1).setArgumentValue(t + (stepSize / 2));
                value = argumentList.get(i).getArgumentValue() + (k2.get(i) / 2);
                aTempArgArrayList.get(i).setArgumentValue(value);
                dydt = RightHandSide(variableArgList, aTempArgArrayList, flowArrayList, stockArrayList,variableArrayList);
            }
            for (int i = 0; i < numOfStocks; i++) {
                k3.set(i, stepSize * dydt[i]);
            }

            //next let's find k4:
            for (int i = 0; i < numOfStocks; i++) {
                variableArgList.get(variableArgList.size() - 1).setArgumentValue(t + stepSize);
                value = argumentList.get(i).getArgumentValue() + (k3.get(i));
                aTempArgArrayList.get(i).setArgumentValue(value);
                dydt = RightHandSide(variableArgList, aTempArgArrayList, flowArrayList, stockArrayList,variableArrayList);
            }
            for (int i = 0; i < numOfStocks; i++) {
                k4.set(i, stepSize * dydt[i]);
            }

            //now we update y
            for (int i = 0; i < numOfStocks; i++) {

                value = argumentList.get(i).getArgumentValue() + ((k1.get(i) + (2 * k2.get(i)) + (2 * k3.get(i)) + k4.get(i)) / 6);
                //value = Math.ceil(value * 1000000) / 1000000;
                argumentList.get(i).setArgumentValue(value);
            }

            int row = n + 1;
            tableStrings[0] = Double.toString(row * stepSize);
            for (int col = 0; col < stockArrayList.size(); col++) {
                //tableStrings[col + 1] = Double.toString(Math.floor(argumentList.get(col).getArgumentValue()* Math.pow(10,cutoff)) / Math.pow(10,cutoff));
                tableStrings[col + 1] = Double.toString(argumentList.get(col).getArgumentValue());
            }
            tableModel.addRow(tableStrings);
            for (int i = 0; i < stockArrayList.size(); i++) {
                series.get(i).add(t, argumentList.get(i).getArgumentValue());
            }

        }
        for (int i = 0; i < stockArrayList.size(); i++) {
            data.addSeries(series.get(i));
        }
        return data;
    }

    /**
     * 
     * @param t0
     * @param tF
     * @param stepSize
     * @param argumentList
     * @param variableArgList
     * @param stockArrayList
     * @param flowArrayList
     * @param variableArrayList
     * @return 
     */
    public XYSeriesCollection rk2(double t0, double tF, double stepSize,
            ArrayList<Argument> argumentList, ArrayList<Argument> variableArgList,
            ArrayList<StockObject> stockArrayList, ArrayList<FlowObject> flowArrayList, ArrayList<VariableObject> variableArrayList) {

        //Used to help create the tempvarlist
        Argument[] aVarList = argumentList.toArray(new Argument[argumentList.size()]);
        //aTempVarList only created to later create the temparg array list
        Argument[] aTempVarList = new Argument[argumentList.size()];
        for (int j = 0; j < aVarList.length; j++) {
            aTempVarList[j] = aVarList[j].clone();
        }
        //double numSteps = (tF - t0) / stepSize;
        int numSteps = (int) ((tF - t0) / stepSize);
        double t = t0;
        //javax.swing.JProgressBar progress = new javax.swing.JProgressBar(0,numSteps);
        //int cutoff = String.valueOf(t).length()-1;
        ArrayList<Argument> aTempArgArrayList = new ArrayList<Argument>();
        for (int j = 0; j < aVarList.length; j++) {
            aTempArgArrayList.add(aTempVarList[j]);
        }
        double[] dydt = new double[argumentList.size()];

        ArrayList<Double> k1 = new ArrayList<Double>();

        ArrayList<Double> k2 = new ArrayList<Double>();

        //idea is to set k1 through k2 ArrayLists to double values of 0
        //used to have stockArrayList.size()
        for (int x = 0; x < stockArrayList.size(); x++) {
            k1.add(0.0);
            k2.add(0.0);
        }

        //array list length of amount of stocks
        ArrayList<XYSeries> series = new ArrayList<XYSeries>();

        //create series to hold graph data
        for (int i = 0; i < stockArrayList.size(); i++) {
            XYSeries tempSeries = new XYSeries(stockArrayList.get(i).getObjName());
            series.add(tempSeries);
        }

        final XYSeriesCollection data = new XYSeriesCollection();

        //add initial values to series
        for (int i = 0; i < stockArrayList.size(); i++) {
            series.get(i).add(0, argumentList.get(i).getArgumentValue());
        }

        int numOfStocks = stockArrayList.size();
        double value;

        // label the string array for columns
        String stockNames = "Time,";
        for (int x = 0; x < stockArrayList.size(); x++) {
            stockNames += argumentList.get(x).getArgumentName() + ",";
        }
        String[] tableStrings = new String[stockArrayList.size() + 1];
        String columns[] = stockNames.split(",");
        tableModel = new DefaultTableModel(0, stockArrayList.size() + 1);
        tableModel.setColumnIdentifiers(columns); // set the labels

        for (int n = 0; n < numSteps; n++) {
            t = t0 + (n * stepSize);
            //t = Math.ceil(t * 10000) / 10000;
            variableArgList.get(variableArgList.size() - 1).setArgumentValue(t);

            //Let's find k1:
            dydt = RightHandSide(variableArgList, argumentList, flowArrayList, stockArrayList,variableArrayList);

            for (int i = 0; i < numOfStocks; i++) {

                k1.set(i, stepSize * dydt[i]);
            }

            //next let's find k2:
            for (int i = 0; i < numOfStocks; i++) {
                variableArgList.get(variableArgList.size() - 1).setArgumentValue(t + stepSize);
                value = (argumentList.get(i).getArgumentValue() + (k1.get(i)));
                aTempArgArrayList.get(i).setArgumentValue(value);

                dydt = RightHandSide(variableArgList, aTempArgArrayList, flowArrayList, stockArrayList,variableArrayList);
            }
            for (int i = 0; i < numOfStocks; i++) {
                k2.set(i, stepSize * dydt[i]);
            }
            for (int i = 0; i < numOfStocks; i++) {

                value = (argumentList.get(i).getArgumentValue() + ((k1.get(i) + k2.get(i)) / 2));
                argumentList.get(i).setArgumentValue(value);

            }

            int row = n + 1;
            //double tablex=row*stepSize;
            //tableStrings[0] = Double.toString(Math.floor(tablex*Math.pow(10,cutoff))/Math.pow(10,cutoff));
            tableStrings[0] = Double.toString(row * stepSize);
            for (int col = 0; col < stockArrayList.size(); col++) {
                tableStrings[col + 1] = Double.toString(argumentList.get(col).getArgumentValue());

            }
            tableModel.addRow(tableStrings);

            for (int i = 0; i < stockArrayList.size(); i++) {
                series.get(i).add(t, argumentList.get(i).getArgumentValue());
            }

        }
        for (int i = 0; i < stockArrayList.size(); i++) {
            data.addSeries(series.get(i));
        }
        return data;
    }

    public XYSeriesCollection eulers(double t0, double tF, double stepSize,
            ArrayList<Argument> argumentList, ArrayList<Argument> variableArgList,
            ArrayList<StockObject> stockArrayList, ArrayList<FlowObject> flowArrayList, ArrayList<VariableObject> variableArrayList) {

        //Used to help create the tempvarlist
        Argument[] aVarList = argumentList.toArray(new Argument[argumentList.size()]);
        //aTempVarList only created to later create the temparg array list
        Argument[] aTempVarList = new Argument[argumentList.size()];
        for (int j = 0; j < aVarList.length; j++) {
            aTempVarList[j] = aVarList[j].clone();
        }
        int numSteps = (int) ((tF - t0) / stepSize);
        double t = t0;
        //javax.swing.JProgressBar pbar = new javax.swing.JProgressBar(0,numSteps);
        //int cutoff = String.valueOf(t).length()-1;
        ArrayList<Argument> aTempArgArrayList = new ArrayList<Argument>();
        for (int j = 0; j < aVarList.length; j++) {
            aTempArgArrayList.add(aTempVarList[j]);
        }
        double[] dydt = new double[argumentList.size()];

        ArrayList<Double> k1 = new ArrayList<Double>();

        //idea is to set k1 to double 0
        for (int x = 0; x < stockArrayList.size(); x++) {
            k1.add(0.0);
        }

        //array list length of amount of stocks
        ArrayList<XYSeries> series = new ArrayList<XYSeries>();

        //create series to hold graph data
        for (int i = 0; i < stockArrayList.size(); i++) {
            XYSeries tempSeries = new XYSeries(stockArrayList.get(i).getObjName());
            series.add(tempSeries);
        }

        final XYSeriesCollection data = new XYSeriesCollection();
        //create strings to hold table data

        //add initial values to series
        for (int i = 0; i < stockArrayList.size(); i++) {
            series.get(i).add(0, argumentList.get(i).getArgumentValue());
        }
        int numOfStocks = stockArrayList.size();
        double value;
        // label the string array for columns
        String stockNames = "Time,";
        for (int x = 0; x < stockArrayList.size(); x++) {
            stockNames += argumentList.get(x).getArgumentName() + ",";
        }
        String[] tableStrings = new String[stockArrayList.size() + 1];
        String columns[] = stockNames.split(",");
        tableModel = new DefaultTableModel(0, stockArrayList.size() + 1);
        tableModel.setColumnIdentifiers(columns); // set the labels

        for (int n = 0; n < numSteps; n++) {
            
            t = t0 + (n * stepSize);
            variableArgList.get(variableArgList.size() - 1).setArgumentValue(t);
           // t = Math.ceil(t * 10000) / 10000;

            //Let's find k1:
            dydt = RightHandSide(variableArgList, argumentList, flowArrayList, stockArrayList, variableArrayList);

            for (int i = 0; i < numOfStocks; i++) {

                k1.set(i, stepSize * dydt[i]);
            }

            for (int i = 0; i < numOfStocks; i++) {

                value = argumentList.get(i).getArgumentValue() + (k1.get(i));
                //value = Math.ceil(value * 10000) / 10000;
                argumentList.get(i).setArgumentValue(value);
                
            }

            int row = n + 1;
            //double tablex=row*stepSize;
            //tableStrings[0] = Double.toString(Math.floor(tablex*Math.pow(10,cutoff))/Math.pow(10,cutoff));
            tableStrings[0] = Double.toString(row * stepSize);
            for (int col = 0; col < stockArrayList.size(); col++) {
                tableStrings[col + 1] = Double.toString(argumentList.get(col).getArgumentValue());
            }
            tableModel.addRow(tableStrings);

            for (int i = 0; i < stockArrayList.size(); i++) {
                series.get(i).add(t, argumentList.get(i).getArgumentValue());
            }
        }
        for (int i = 0; i < stockArrayList.size(); i++) {
            data.addSeries(series.get(i));
        }
        return data;
    }
    
    //This method handles the actual equation the user creates.
    public double[] RightHandSide(ArrayList<Argument> variableArgList, ArrayList<Argument> stockArgList, 
            ArrayList<FlowObject> flowArrayList, ArrayList<StockObject> stockArrayList,
            ArrayList<VariableObject> variableArrayList) {

        //set double array of size of stockArrayList
        double[] ret = new double[stockArgList.size()];

        ArrayList<Argument> globalArgList = new ArrayList<Argument>();
        globalArgList.addAll(stockArgList);
        globalArgList.addAll(variableArgList);
        Argument[] globalvariables = globalArgList.toArray(new Argument[globalArgList.size()]);

        Expression e;
        Expression v;
        //for how ever many stocks there are, you get each stock and find the solution to each equation from the stock using
        // the variables array. it then returns that to the double ret array at the appropriate index
        
        //code for updating variable values. needs to be fixed -KM
        /*for (int k = 0;k<variableArgList.size(); k++){
            VariableObject var = variableArrayList.get(k);
            String eq=var.getVarEquation();
            v = new Expression(eq, globalvariables);
            variableArgList.get(k).setArgumentValue(v.calculate());
            for(int r=0;r<globalArgList.size();r++){
                if(globalArgList.get(r).getArgumentName().equals(variableArgList.get(k).getArgumentName())){
                    globalArgList.get(r).setArgumentValue(variableArgList.get(k).getArgumentValue());
                }
                else{}
            }
        }*/
        for (int i = 0; i < stockArgList.size(); i++) {
            StockObject stock = stockArrayList.get(i);
            Vector<ModelingObject> inputs = stock.getInputs();
            Vector<ModelingObject> outputs = stock.getOutputs();
            String equations = "";
            for (ModelingObject mobj : inputs) {
                if (mobj.getStyle() == 0) { // check if the incoming is flow
                    FlowObject fobj = (FlowObject) mobj;
                    equations += fobj.getFlowEquation();// if it is flow then add the equation
                }

            }
            for (ModelingObject mobj : outputs) {
                if (mobj.getStyle() == 0) { // check if the incoming is flow
                    FlowObject fobj = (FlowObject) mobj;
                    equations += "-" + fobj.getFlowEquation();// if it is flow th  en add the equation
                }
            }
            e = new Expression(equations, globalvariables);
            ret[i] = e.calculate();
        }

        return ret;
    }

}
/* for(int i=0;i<stockArrayList.size();i++){
 for(int j=0;j<stockArrayList.get(i).getFlows(j).length();j++){
 e = new Expression(stockArrayList.get(i).getFlows(j),globalvariables);
 ret[i] = e.calculate();
 }
 }
 */