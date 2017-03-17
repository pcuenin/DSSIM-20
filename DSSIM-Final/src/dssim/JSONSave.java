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
import com.orsoncharts.util.json.JSONObject;
import dssim.gui.ArrowObject;
import dssim.gui.ConnectableModelObject;
import dssim.gui.FlowObject;
import dssim.gui.StockObject;
import dssim.gui.VariableObject;

/**
 *
 * @author kamre_000
 */
public class JSONSave {

    public JSONObject saveStock(StockObject stock) {
        JSONObject stockObj = new JSONObject();
        stockObj.put("name", stock.getStockName());
        stockObj.put("desc", stock.getStockDescrip());
        stockObj.put("init", stock.getStockInitial());
        mxCell stockGeo = (mxCell) stock.getO_Object();
        stockObj.put("x", String.valueOf(stockGeo.getGeometry().getCenterX()));
        stockObj.put("y", String.valueOf(stockGeo.getGeometry().getCenterY()));
        //stockObj.put("eq",stock.getStockEq());

        return stockObj;
    }

    public JSONObject saveFlow(FlowObject flow) {
        JSONObject flowObj = new JSONObject();
        flowObj.put("name", flow.getFlowName());
        flowObj.put("eq", flow.getFlowEquation());
        flowObj.put("to",flow.getFlowTo().getCMOName());
        flowObj.put("from",flow.getFlowFrom().getCMOName());
        flowObj.put("toStyle",Integer.toString(flow.getFlowTo().getStyle()));
        flowObj.put("fromStyle",Integer.toString(flow.getFlowFrom().getStyle()));
        return flowObj;
    }

    public JSONObject saveVar(VariableObject var) {
        JSONObject varObj = new JSONObject();
        varObj.put("name", var.getVarName());
        varObj.put("desc", var.getVarDescrip());
        varObj.put("init", var.getVarInitial());
        mxCell varGeo = (mxCell) var.getO_Object();
        varObj.put("x", String.valueOf(varGeo.getGeometry().getCenterX()));
        varObj.put("y", String.valueOf(varGeo.getGeometry().getCenterY()));
        //varObj.put("eq", var.getVarEq());
        return varObj;
    }

    public JSONObject saveArrow(ArrowObject arrow) {
        JSONObject arrowObj = new JSONObject();
        arrowObj.put("name", arrow.getArrowName());
        arrowObj.put("to", arrow.getArrowTo().getCMOName());
        arrowObj.put("from", arrow.getArrowFrom().getCMOName());
        arrowObj.put("toStyle", Integer.toString(arrow.getArrowTo().getStyle()));
        arrowObj.put("fromStyle", Integer.toString(arrow.getArrowFrom().getStyle()));
        return arrowObj;
    }
    
    public JSONObject saveFlowPool(ConnectableModelObject flowPool)
    {
        JSONObject fpObj = new JSONObject();
        mxCell fpGeo = (mxCell) flowPool.getO_Object();
        fpObj.put("x", String.valueOf(fpGeo.getGeometry().getCenterX()));
        fpObj.put("y", String.valueOf(fpGeo.getGeometry().getCenterY()));
        fpObj.put("name", flowPool.getCMOName());
        return fpObj;
    }

    public JSONObject saveSettings(ModelSettings ms) {
        JSONObject settings = new JSONObject();
        settings.put("init", ms.getInitialTime());
        settings.put("final", ms.getFinalTime());
        settings.put("timestep", ms.getTimeStep());
        return settings;
    }

}
