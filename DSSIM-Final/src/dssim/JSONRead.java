/*
 * The MIT License
 *
 * Copyright 2016 kamre_000.
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
package dssim;

import com.mxgraph.model.mxCell;
import static dssim.MainForm.graph;
import org.json.simple.JSONObject;
import dssim.gui.FlowObject;
import dssim.gui.StockObject;
import dssim.gui.VariableObject;
import dssim.gui.ArrowObject;
import dssim.gui.ConnectableModelObject;
import dssim.gui.ModelingObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author kamre_000
 */
public class JSONRead extends MainForm{

    public static ArrayList<StockObject> readStock(JSONParser parser, File filename) {
        ArrayList<StockObject> stockArrayList = new ArrayList();

        try {
            Object obj = parser.parse(new FileReader(filename));
            JSONObject jStock = (JSONObject) obj;
            JSONArray jStockArray = (JSONArray) jStock.get("Stocks");
            Iterator it = jStockArray.iterator();
            while (it.hasNext()) {
                JSONObject Stock = (JSONObject) it.next();
                //String arg = (String) jsonObject.get("arg");
                String name = (String) Stock.get("name");
                String desc = (String) Stock.get("desc");
                String init = (String) Stock.get("init");
                String x = (String) Stock.get("x");
                x = x.substring(0, x.length() - 2);
                String y = (String) Stock.get("y");
                y = y.substring(0, y.length() - 2);
                //Object jobj = jStock.get("obj");
                Object parent = graph.getDefaultParent();
                Object node = graph.insertVertex(parent, null, name, Integer.parseInt(x),
                        Integer.parseInt(y), 100, 50, "Stock");//draw the node
                StockObject stock = new StockObject(node, name, desc, init, x, y);
                stockArrayList.add(stock);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return stockArrayList;
    }

    public static ArrayList<FlowObject> readFlow(JSONParser parser, File filename,ArrayList<StockObject> stocks,
            ArrayList<VariableObject> variables,ArrayList<ConnectableModelObject> flowpools) {
        ArrayList<FlowObject> flowArrayList = new ArrayList();
        ConnectableModelObject CMOFrom=null;
        ConnectableModelObject CMOTo=null;
        try {
            Object obj = parser.parse(new FileReader(filename));
            JSONObject jFlow = (JSONObject) obj;
            JSONArray jFlowArray = (JSONArray) jFlow.get("Flows");
            Iterator it = jFlowArray.iterator();
            while (it.hasNext()) {
                JSONObject Flow = (JSONObject) it.next();
                String name = (String) Flow.get("name");
                String eq = (String) Flow.get("eq");

                String from = (String) Flow.get("from");
                String to = (String) Flow.get("to");
                String fromStyle = (String) Flow.get("fromStyle")+"";
                String toStyle = (String) Flow.get("toStyle")+"";
                
                if(Integer.valueOf(fromStyle)==2){
                   for(int i = 0; i<stocks.size(); i++){
                       if(from.equals(stocks.get(i).getStockName()))
                          CMOFrom=stocks.get(i);
                   } 
                }
                else if(Integer.valueOf(fromStyle)==3){
                    for(int i = 0; i<variables.size(); i++){
                       if(from.equals(variables.get(i).getVarName()))
                          CMOFrom=variables.get(i);
                   } 
                }
                else{
                    for(int i = 0; i<flowpools.size(); i++){
                       if(from.equals(flowpools.get(i).getCMOName()))
                          CMOFrom=flowpools.get(i);
                   } 
                }
                
                if(Integer.valueOf(toStyle)==2){
                   for(int i = 0; i<stocks.size(); i++){
                       if(to.equals(stocks.get(i).getStockName()))
                          CMOTo=stocks.get(i);
                   } 
                }
                else if(Integer.valueOf(toStyle)==3){
                    for(int i = 0; i<variables.size(); i++){
                       if(to.equals(variables.get(i).getVarName()))
                          CMOFrom=variables.get(i);
                   } 
                }
                else{
                    for(int i = 0; i<flowpools.size(); i++){
                       if(to.equals(flowpools.get(i).getCMOName()))
                          CMOTo=flowpools.get(i);
                   } 
                }
                Object parent = graph.getDefaultParent();
                mxCell toGeo = (mxCell) CMOTo.getO_Object();
                mxCell fromGeo = (mxCell) CMOFrom.getO_Object();
                Object nodeFrom = getGraphComponent().getCellAt((int)fromGeo.getGeometry().getCenterX(),(int)fromGeo.getGeometry().getCenterY());
                Object nodeTo = getGraphComponent().getCellAt((int)toGeo.getGeometry().getCenterX(),(int)toGeo.getGeometry().getCenterY());
                Object node = graph.insertEdge(parent, null, null, nodeFrom, nodeTo, "Flow");//draw the node
                //Object node = graph.insertEdge(parent, null, null, CMOFrom, CMOTo, "Flow");
                FlowObject flow = new FlowObject(node, name, eq, CMOFrom, CMOTo);
                flowArrayList.add(flow);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return flowArrayList;
    }

    public static ArrayList<VariableObject> readVar(JSONParser parser, File filename) {
        ArrayList<VariableObject> varArrayList = new ArrayList();

        try {
            Object obj = parser.parse(new FileReader(filename));
            JSONObject jVar = (JSONObject) obj;
            JSONArray jVarArray = (JSONArray) jVar.get("Variables");
            Iterator it = jVarArray.iterator();
            while (it.hasNext()) {
                JSONObject Var = (JSONObject) it.next();
                String name = (String) Var.get("name");
                String desc = (String) Var.get("desc");
                String init = (String) Var.get("init");
                String x = (String) Var.get("x");
                x = x.substring(0, x.length() - 2);
                String y = (String) Var.get("y");
                y = y.substring(0, y.length() - 2);
                //Object jobj = jsonObject.get("obj");
                Object parent = graph.getDefaultParent();
                Object node = graph.insertVertex(parent, null, name, Integer.parseInt(x),
                        Integer.parseInt(y), 100, 50, "Variable");//draw the node
                VariableObject var = new VariableObject(node, name, desc, init, x, y);
                varArrayList.add(var);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return varArrayList;
    }
    
    public static ArrayList<String> readFlowPool(JSONParser parser, File filename) {
        ArrayList<String> fpArrayList = new ArrayList();

        try {
            Object obj = parser.parse(new FileReader(filename));
            JSONObject jFP = (JSONObject) obj;
            JSONArray jFPArray = (JSONArray) jFP.get("Flow Pools");
            Iterator it = jFPArray.iterator();
            while (it.hasNext()) {
                JSONObject FP = (JSONObject) it.next();
                String x = (String) FP.get("x");
                x = x.substring(0, x.length() - 2);
                String y = (String) FP.get("y");
                y = y.substring(0, y.length() - 2);
                String fp = x+":"+y;
                //String name = (String) FP.get("name");
                
                fpArrayList.add(fp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fpArrayList;
    }
    
    /*public static ArrayList<ArrowObject> readArrow(JSONParser parser, File filename,ArrayList<StockObject> stocks,
            ArrayList<VariableObject> variables, ArrayList<ConnectableModelObject> flowpools) {
        ArrayList<ArrowObject> arrowArrayList = new ArrayList();
        //ConnectableModelObject CMOFrom=null;
        //ConnectableModelObject CMOTo=null;
        Object CMOFrom = null;
        Object CMOTo = null;
        try {
            Object obj = parser.parse(new FileReader(filename));
            JSONObject jArrow = (JSONObject) obj;
            JSONArray jArrowArray = (JSONArray) jArrow.get("Arrows");
            Iterator it = jArrowArray.iterator();
            while (it.hasNext()) {
                JSONObject Arrow = (JSONObject) it.next();
                String name = (String) Arrow.get("name");
                String from = (String) Arrow.get("from");
                String to = (String) Arrow.get("to");
                String toStyle = (String) Arrow.get("toStyle")+"";
                String fromStyle = (String) Arrow.get("fromStyle")+"";
                
                if(Integer.valueOf(fromStyle)==2){
                   for(int i = 0; i<stocks.size(); i++){
                       if(from.equals(stocks.get(i).getStockName()))
                          CMOFrom=stocks.get(i);
                   } 
                }
                else if(Integer.valueOf(fromStyle)==3){
                    for(int i = 0; i<variables.size(); i++){
                       if(from.equals(variables.get(i).getVarName()))
                          CMOFrom=variables.get(i);
                   } 
                }
                else{
                    for(int i = 0; i<flowpools.size(); i++){
                       if(from.equals(flowpools.get(i).getCMOName()))
                          CMOFrom=flowpools.get(i);
                   } 
                }
                
                if(Integer.valueOf(toStyle)==2){
                   for(int i = 0; i<stocks.size(); i++){
                       if(to.equals(stocks.get(i).getStockName()))
                          CMOTo=stocks.get(i);
                   } 
                }
                else if(Integer.valueOf(toStyle)==3){
                    for(int i = 0; i<variables.size(); i++){
                       if(to.equals(variables.get(i).getVarName()))
                          CMOTo=variables.get(i);
                   } 
                }
                else{
                     for(int i = 0; i<flowpools.size(); i++){
                       if(to.equals(flowpools.get(i).getCMOName()))
                          CMOTo=flowpools.get(i);
                   } 
                }
                
                Object parent = graph.getDefaultParent();
                //mxCell toGeo = (mxCell) CMOTo.getO_Object();
                //mxCell fromGeo = (mxCell) CMOFrom.getO_Object();
                //Object nodeFrom = getGraphComponent().getCellAt((int)fromGeo.getGeometry().getCenterX(),(int)fromGeo.getGeometry().getCenterY());
                //Object nodeTo = getGraphComponent().getCellAt((int)toGeo.getGeometry().getCenterX(),(int)toGeo.getGeometry().getCenterY());
                ModelingObject moSrc = getModelObject(CMOFrom);
                ModelingObject moTo = getModelObject(CMOTo);
                Object node = graph.insertEdge(parent, null, null, CMOFrom, CMOTo, "Arrow");//draw the node
                //Object node = graph.insertEdge(parent, null, null, CMOFrom, CMOTo, "Arrow");
                ArrowObject arrow = new ArrowObject(name, node, (ConnectableModelObject)moSrc, (ConnectableModelObject)moTo);
                arrowArrayList.add(arrow);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return arrowArrayList;
    }*/
    public static ArrayList<String[]> readArrow(JSONParser parser, File filename,ArrayList<StockObject> stocks,
            ArrayList<VariableObject> variables, ArrayList<ConnectableModelObject> flowpools){
        ArrayList<String[]> tempArrows = new ArrayList();
        try {
            Object obj = parser.parse(new FileReader(filename));
            JSONObject jArrow = (JSONObject) obj;
            JSONArray jArrowArray = (JSONArray) jArrow.get("Arrows");
            Iterator it = jArrowArray.iterator();
            while (it.hasNext()) {
                JSONObject Arrow = (JSONObject) it.next();
                String[] arr = new String[5];
                arr[0] = (String) Arrow.get("name");
                arr[1] = (String) Arrow.get("from");
                arr[2] = (String) Arrow.get("to");
                arr[3] = (String) Arrow.get("toStyle")+"";
                arr[4] = (String) Arrow.get("fromStyle")+"";
                tempArrows.add(arr);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return tempArrows;
    }
    
    public static String[] readSettings(JSONParser parser, File filename) {
        String[] settings = new String[3];
        try {
            Object obj = parser.parse(new FileReader(filename));
            JSONObject mSettings = (JSONObject) obj;
            JSONObject ms = (JSONObject) mSettings.get("Model Settings");
            settings[0] = (String) ms.get("init");
            settings[1] = (String) ms.get("final");
            settings[2] = (String) ms.get("timestep");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return settings;
    }
}
