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

import java.util.Vector;

/**
 *
 * @author paulcuenin
 */
public class ConnectableModelObject extends ModelingObject {
    
    private Vector<ModelingObject> vInputs = new Vector<ModelingObject>();
    private Vector<ModelingObject> vOutputs= new Vector<ModelingObject>();
    private String CMOName;

    public ConnectableModelObject(String inputname, Object graphobject) {
        super(inputname, graphobject);
        CMOName = inputname;
    }
    
    public String getCMOName(){
       return CMOName; 
    }
    
    public void addInputObj(ModelingObject moob){
        vInputs.add(moob);
    }
    
    public void addOutputObj(ModelingObject moob){
        vOutputs.add(moob);
    }
    
    public void deleteInputObj(ModelingObject moob){
        vInputs.remove(moob);
    }
    public void deleteOutputObj(ModelingObject moob){
        vOutputs.remove(moob);
    }
    
   public Vector<ModelingObject> getInputs(){
       //what sould I return?
       return vInputs;
   }
   public Vector<ModelingObject> getOutputs(){
       // what should I return?
       return vOutputs;
   }
   
   public Vector<ModelingObject> getConnectedObjects(){
       Vector <ModelingObject> vMo= new Vector <ModelingObject>();
       for(int i =0; i<vInputs.size();i++){
           vMo.add(vInputs.get(i));
           
       }
       for(int i = 0; i<vOutputs.size();i++){
           vMo.add(vOutputs.get(i));
       }
       return vMo;
   }
    
    
}
