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

import com.mxgraph.model.mxCell;
import dssim.MainForm;

/**
 *
 * @author paulcuenin
 */
public class ModelingObject {

    private String sObjName; //for keeping the name given from the user
    private Object oObj; //the JGraph object that is created by placing the object
    private String sObjJgraphName; //JGraph object name 
    
    public final static int INVALID = -1;
    public final static int FLOW = 0;
    public final static int ARROW = 1;
    public final static int STOCK = 2;
    public final static int VARIABLE = 3;
    public final static int FLOWPOOL = 4;
    

    public ModelingObject(String inputname, Object graphobject) {
            sObjName = inputname;
            sObjJgraphName = graphobject.toString();
            oObj = graphobject;

        }
    //sets gets the object name
    public String getObjName() {
        return sObjName;
    }

    public void setObjName(String newname) {
        sObjName = newname;
    }
    //returns the graph object name this and the stockobject builds a reference table 

    public String getObjJgraphName() {
        return sObjJgraphName;
    }

    //returns the stock object of jgraph object type

    public Object getO_Object() {
        return oObj;
    }
    
    public int getStyle(){
        
        mxCell mxobj = (mxCell) getO_Object();// cast generic object to be a mxCell
        
        
        switch((String) mxobj.getStyle())  // mull the mxCell id to use
        {
            case MainForm.FLOW_STYLE: return FLOW;
            case MainForm.ARROW_STYLE: return ARROW;
            case MainForm.STOCK_STYLE: return STOCK;
            case MainForm.FLOWPOOL_STYLE: return FLOWPOOL;
            case MainForm.VARIABLE_STYLE: return VARIABLE;
            default: return INVALID;
            
        }
        
    }

}
