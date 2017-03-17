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

import dssim.gui.FlowObject;
import java.util.ArrayList;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

/**
 *
 * @author kamre_000
 */
public class RightHandSide {

    public double[] RightHandSide(ArrayList<Argument> variableArgList, ArrayList<Argument> stockArgList, ArrayList<FlowObject> flowArrayList) {

        //set double array of size of stockArrayList
        double[] ret = new double[stockArgList.size()];

        ArrayList<Argument> globalArgList = new ArrayList<Argument>();
        globalArgList.addAll(stockArgList);
        globalArgList.addAll(variableArgList);
        Argument[] globalvariables = globalArgList.toArray(new Argument[globalArgList.size()]);

        Expression e;
        //for how ever many stocks there are, you get each stock and find the solution to each equation from the stock using
        // the variables array. it then returns that to the double ret array at the appropriate index
        for (int j = 0; j < flowArrayList.size(); j++) {

            for (int i = 0; i < stockArgList.size(); i++) {
                FlowObject flow = flowArrayList.get(j);
                //Think about having general expressions passed to this loop, if you
                //can actually change parts of the expressions using e.whatever
                e = new Expression(flow.getFlowEquation(), globalvariables);

                ret[i] = e.calculate();

            }
        }
        return ret;
    }

}
