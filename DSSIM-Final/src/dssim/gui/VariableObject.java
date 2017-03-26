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
public class VariableObject extends ConnectableModelObject {

    private String sVarSymbol;
    private String sVarName;
    private String sVarEquation;
    //public String sFlowEquation;
    private Argument aVarArg;
    private String x;
    private String y;

    public VariableObject(Object graphobject, String inputname, String inputSymbol, String inputequation, String sVarX, String sVarY) {
        super(inputname, graphobject);
        //sObjJgraphName = graphobject.toString();
        sVarName = inputname;
        sVarSymbol = inputSymbol;
        sVarEquation = inputequation;
        //oObj = graphobject;
        //will cause issue if user inputs into variable a string like "x*54"
        //may use if statement to check for what type of argument constructor to use to avoid errors
        
        //aVarArg = new Argument(inputDescription, Double.parseDouble(inputinitial),vars[])
        aVarArg = new Argument(inputSymbol, inputequation);
        //aVarArg = new Argument(inputSymbol, Double.parseDouble(inputequation));
        x = sVarX;
        y = sVarY;
    }
    public String getVarX(){
        return x;
    }
  
    public String getVarY(){
        return y;
    }
    
    public Argument getVarArg() {
        return aVarArg;
    }
    
    public String getVarName(){
        return sVarName;
    }

    public void setVarArg(String symbol, String equation) {
        //aVarArg = new Argument(symbol, Double.parseDouble(equation));
        aVarArg = new Argument(symbol,equation);
    }

    public String getVarSymbol() {
        return sVarSymbol;
    }

    public void setVarDescrip(String newsymbol) {
        sVarSymbol = newsymbol;
    }

    public String getVarEquation() {
        return sVarEquation;
    }

    public void setVarInitial(String newequation) {
        sVarEquation = newequation;
        setVarArg(sVarSymbol, sVarEquation);
    }

}
