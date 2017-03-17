/*
 * The MIT License
 *
 * Copyright 2016 paulcuenin.
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
package dssim.gui;

import org.mariuszgromada.math.mxparser.Argument;

/**
 *
 * @author paulcuenin
 */
public class StockObject extends ConnectableModelObject {

    private String sStockDescrip; //for keeping the user entered symbol
    private String sStockName;
    private String sStockInitial; //for keeping the initial value the user enters
    private Argument aStockArg; //Argument data type is from the mxparser library
    private String x;
    private String y;

    //stock object is created for the purposes right now as a variable that is represented in the generated graphs and tables
    public StockObject(Object graphobject, String inputname, String inputDescripton, String inputinitial, String sStockX, String sStockY) {

        super(inputname, graphobject);
        //stock name from user
        sStockName = inputname;
        //jgraph object cell name
        //sObjJgraphName = graphobject.toString();
        //stock symbol from user
        sStockDescrip = inputDescripton;
        //stock initial value input from user. for reference later and building parser argument
        sStockInitial = inputinitial;
        //object given from jgraph
        //oObj = graphobject;
        x = sStockX;
        y = sStockY;
        

        aStockArg = new Argument(inputDescripton, Double.parseDouble(inputinitial));


    }
    public String getStockX(){
        return x;
    }
    public String getStockY(){
        return y;
    }
    public String getStockName(){
        return sStockName;
    }

    //return stock arg of argument type
    public Argument getStockArg() {
        return aStockArg;
    }

    //sets the argument
    public void setStockArg(String symbol, String initial) {
        aStockArg = new Argument(symbol, Double.parseDouble(initial));
    }

    //get stock desc
    public String getStockDescrip() {
        return sStockDescrip;
    }

    //set stock desc
    public void setStockDescrip(String newsymbol) {
        sStockDescrip = newsymbol;
    }

    //return initial value as a string
    public String getStockInitial() {
        return sStockInitial;
    }

    //set stock initial value
    public void setStockInitial(String newinitial) {
        sStockInitial = newinitial;
    }

        //returns the graph object name this and the stockobject builds a reference table 
}
