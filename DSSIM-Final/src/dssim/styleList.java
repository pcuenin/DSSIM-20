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
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import java.awt.Color;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Logan
 */
public class styleList {

    MainForm m_MainForm = null;

    //initailizes the Hashtables that store the data for each individual object
    Hashtable<String, Object> Flow = new Hashtable<>();
    Hashtable<String, Object> FlowPool = new Hashtable<>();
    Hashtable<String, Object> Stock = new Hashtable<>();
    Hashtable<String, Object> Arrow = new Hashtable<>();
    Hashtable<String, Object> Variable = new Hashtable<>();

    Hashtable<String, Object> Time = new Hashtable<>();
    Map<String, Object> edgeStyle = new HashMap<String, Object>();
    Map<String, Object> edgeConStyle = new HashMap<String, Object>();

    styleList(MainForm mainform) {

        //creates a style for a flow. 
        Flow.put(mxConstants.STYLE_RESIZABLE, "false");
        Flow.put(mxConstants.STYLE_EDITABLE, "false");
        Flow.put(mxConstants.STYLE_DASHED, "false");
        Flow.put(mxConstants.STYLE_OPACITY, "100");
        Flow.put(mxConstants.STYLE_STROKECOLOR, "#0000ff");
        Flow.put(mxConstants.STYLE_STROKEWIDTH, "10");
        Flow.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);

        //creates a style for a stock
        Stock.put(mxConstants.STYLE_SHAPE, mxConstants.STYLE_IMAGE);
        Stock.put(mxConstants.STYLE_IMAGE, "file:Images/StockImg.png");
        Stock.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
        Stock.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
        Stock.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);

        //create a style for flowpool
        FlowPool.put(mxConstants.STYLE_SHAPE, mxConstants.STYLE_IMAGE);
        FlowPool.put(mxConstants.STYLE_IMAGE, "file:Images/FlowPoolImg.png");
        FlowPool.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
        FlowPool.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
        FlowPool.put(mxConstants.STYLE_NOLABEL, "1");

        //creates a style for a right facing arrow
        Arrow.put(mxConstants.STYLE_MOVABLE, "false");

        //Arrow.put(mxConstants.STYLE_IMAGE, "file:Images/ArrowImg.png");
        Arrow.put(mxConstants.STYLE_RESIZABLE, "false");
        Arrow.put(mxConstants.STYLE_EDITABLE, "false");
        Arrow.put(mxConstants.STYLE_DASHED, "false");
        Arrow.put(mxConstants.STYLE_OPACITY, "100");
        Arrow.put(mxConstants.STYLE_STROKECOLOR, "#000000");
        Arrow.put(mxConstants.STYLE_STROKEWIDTH, "2");
        
        Arrow.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);

        //creates a variable
        Variable.put(mxConstants.STYLE_SHAPE, mxConstants.STYLE_IMAGE);
        Variable.put(mxConstants.STYLE_IMAGE, "file:Images/VariableImg.png");
        Variable.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
        Variable.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
        Variable.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);
    }

    public Hashtable<String, Object> getFlow() {
        Hashtable<String, Object> x = this.Flow;
        return x;
    }

    public Hashtable<String, Object> getStock() {
        Hashtable<String, Object> x = this.Stock;
        return x;
    }

    public Hashtable<String, Object> getArrow() {
        Hashtable<String, Object> x = this.Arrow;
        return x;
    }

    public Hashtable<String, Object> getVariable() {
        Hashtable<String, Object> x = this.Variable;
        return x;
    }

    public Hashtable<String, Object> getFlowPool() {
        Hashtable<String, Object> x = this.FlowPool;
        return x;
    }

}
