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

import com.mxgraph.view.mxGraph;
import dssim.MainForm;

/**
 *
 * @author paulcuenin
 */
public class FlowObject extends ConnectableModelObject {

    private String sFlowName;
    private String sFlowEquation;
    // add from and to super object types
    ConnectableModelObject superObjectTo = null;
    ConnectableModelObject superObjectFrom = null;
     

    public FlowObject(Object graphobject, String inputname, String inputequation,ConnectableModelObject from, ConnectableModelObject to) {

        
        super(inputname, graphobject);
        sFlowEquation = inputequation;
        sFlowName = inputname;
        to.addInputObj(this);
        from.addOutputObj(this); //
        superObjectTo =to;
        superObjectFrom=from;
        
    }
    public void removeFlow(){
        superObjectTo.deleteInputObj(this);
        superObjectFrom.deleteOutputObj(this);
    }
    public ConnectableModelObject getFlowFrom(){
        return superObjectFrom;
    }
    public ConnectableModelObject getFlowTo(){
        return superObjectTo;
    }

    //for use later by gui
  

    public String getFlowEquation() {
        return sFlowEquation;
    }
    
    public String getFlowName(){
        return sFlowName;
    }

    public void setFlowEquation(String newequation) {
        sFlowEquation = newequation;
    }
    public ConnectableModelObject getToObject(){
        return superObjectTo;
    }
    public ConnectableModelObject getFromObject(){
        return superObjectFrom;
    }
    public ModelingObject getFlowPoolConnected(mxGraph graph){
        if(superObjectTo.getStyle()==ModelingObject.FLOWPOOL){
           
               return superObjectTo;
        } else if (this.superObjectFrom.getStyle()==ModelingObject.FLOWPOOL){
            return this.superObjectFrom;
        } else return null;
    }    
}
