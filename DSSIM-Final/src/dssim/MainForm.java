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
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;
import dssim.gui.ArrowDialog;
import dssim.gui.ArrowObject;
import dssim.gui.ConnectableModelObject;
import dssim.gui.FlowDialog;
import dssim.gui.FlowObject;
import dssim.gui.ModelingObject;
import dssim.gui.StockDialog;
import dssim.gui.StockObject;
import dssim.gui.VariableDialog;
import dssim.gui.VariableObject;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.jfree.data.xy.XYSeriesCollection;
import org.json.simple.parser.JSONParser;

public class MainForm extends javax.swing.JFrame {

    private static mxGraphComponent graphComponent;
    protected static mxGraph graph = new mxGraph();
    private Object cell;
    int objectLoc = -1;
    private String methodChoice = "";
    private String XTitle = "";
    private String YTitle = "";
    private String graphTitle = "";
    XYSeriesCollection data;
    DefaultTableModel tableModel;
    int amountOfStocks;
    final JFileChooser fc = new JFileChooser();
    String style = "";
    styleList stylelist = new styleList(this);
    ModelSettings modelSettings = new ModelSettings();
    ProgressBar progressBar = new ProgressBar();
    
    static final String SELECTION_MSG = "Make selection.";
    static final String SELECTION_STK = "Place Stock.";
    static final String SELECTION_VAR = "Place Variable.";
    static final String SELECTION_ARW_SRC = "Select Arrow Source.";
    static final String SELECTION_ARW_TGT = "Select Arrow Target.";
    static final String SELECTION_FLW_SRC = "Select Flow Origin.";
    static final String SELECTION_FLW_TGT = "Select Flow Destination.";
    

    boolean runnable = false;
    String inputname;
    String inputdescrip;
    String inputinitial;
    String inputequation;
    String inputValue;
    public static final String STOCK_STYLE = "Stock";
    public static final String VARIABLE_STYLE = "Variable";
    public static final String ARROW_STYLE = "Arrow";
    public static final String FLOW_STYLE = "Flow";
    public static final String FLOWPOOL_STYLE = "FlowPool";

    MouseEvent last = null;
    boolean isFirstClickArrow = true;
    boolean isFirstClickFlow = true;
    boolean isFromFlowPool = false;

    static MainForm mainForm = null;


    /*these ArrayLists are for keeping track of the graph objects placed by the user.
     another method could be to keep track
     by using a hashtable. Yet, arraylists have very useful methods. */
    public ArrayList<StockObject> stockArrayList = new ArrayList<StockObject>();
    public ArrayList<FlowObject> flowArrayList = new ArrayList<FlowObject>();
    public ArrayList<VariableObject> variableArrayList = new ArrayList<VariableObject>();
    public ArrayList<ArrowObject> arrowArrayList = new ArrayList<ArrowObject>();
    public ArrayList<ConnectableModelObject> flowPoolArrayList = new ArrayList<ConnectableModelObject>();

    //Creates new form MainForm
    public MainForm() {

        mainForm = this;
        initComponents();
        //This sets up the inital graph. This is what all the objects are added to.
        graph = new mxGraph() {
            public boolean isCellMovable(Object cell) {
                return isCellsMovable() && !isCellLocked(cell) && !getModel().isVertex(getModel().getParent(cell));
            }
        ;
        };
        graph.setCellsResizable(false);
        graph.setCellsEditable(false);
        graph.setCellsDisconnectable(false);

        Object parent = graph.getDefaultParent();
        //update graph object
        graph.getModel().beginUpdate();
        try {
            mxStylesheet stylesheet = graph.getStylesheet();
        } finally {
            graph.getModel().endUpdate();
        }

        graphComponent = new mxGraphComponent(graph);
        graphComponent.setGridVisible(true);
        jScrollPane1.setViewportView(graphComponent); //removed this becuase it was blocking the custom cursor

        // allow the user to use the mouse to navigate
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                cell = graphComponent.getCellAt(e.getX(), e.getY());
            }

            //sets up what happens when a user clicks on the graph to place an object
            public void mousePressed(MouseEvent e) {
                cell = graphComponent.getCellAt(e.getX(), e.getY());

                if (e.getClickCount() >= 2) {

                    // if the item is double clicked PMC 100716
                    //figure out what type of object they double clicked
                    // args back when you figure that out
                    Object obj = graph.getSelectionCell();//gives the object you have clicked on
                    Map<String, Object> objMap = graph.getCellStyle(obj); //
                    mxCell mxobj = (mxCell) obj;// cast generic object to be a mxCell
                    String objst = (String) mxobj.getStyle(); // mull the mxCell id to use

                    if (objst.equals(MainForm.STOCK_STYLE)) {
                        //do stock dialog
                        StockObject so = getStock(mxobj);
                        StockDialog dialog = new StockDialog(new javax.swing.JFrame(), true, so, variableArrayList);
                        dialog.setVisible(true);

                    } else if (objst.equals(MainForm.FLOW_STYLE)) {

                        // do Flow dialog
                        FlowObject fo = getFlow(mxobj);
                        FlowDialog dialog = new FlowDialog(new javax.swing.JFrame(), true, fo, variableArrayList);
                        dialog.setVisible(true);

                    } else if (objst.equals(MainForm.ARROW_STYLE)) {  // not flow not stock must be arrow

                        ArrowObject ao = getArrow(mxobj); // find the arrow object from the mxcell
                        ArrowDialog dialog = new ArrowDialog(new javax.swing.JFrame(), true, ao);
                        dialog.setVisible(true);

                        //ArrowDialog.main(ao); // pass the arrow object to the the arrow dialogva
                    } else if (objst.equals(MainForm.VARIABLE_STYLE)) {
                        // do variable dialog
                        VariableObject vo = getVar(mxobj); // find the arrow object from the mxcell
                        VariableDialog dialog = new VariableDialog(new javax.swing.JFrame(), true, vo, variableArrayList);
                        dialog.setVisible(true);

                    }

                    // for look through each stock/flow/arrow array to find the object
                    // then call the form based on the result
                    // use graph to access info about vertex
                    //how are the objects stored... need visual
                }

                // different objectLoc will call different Add functions
                if (objectLoc == 1) { // stock loc

                    AddStock(inputname, style, inputdescrip, inputinitial, inputequation, e.getX(), e.getY());

                    objectLoc = -1;
                    jLabel1.setText(SELECTION_MSG);
                } else if (objectLoc == 2) { // Flow lock

                    if (isFirstClickFlow) { // if you are on the first click
                        last = e; // store click
                        if (graphComponent.getCellAt(e.getX(), e.getY()) == null) { //create flow pool if null
                            AddFlowPool(e.getX(), e.getY());
                            //isFromFlowPool=true; pmc 112016
                            isFromFlowPool = true;

                            //System.out.print("Stored first click");
                            isFirstClickFlow = false;
                            jLabel1.setText(SELECTION_FLW_TGT);

                        } else {
                            isFromFlowPool = false;
                            isFirstClickFlow = false;
                            jLabel1.setText(SELECTION_FLW_TGT);
                        }
                    } else { // if on second click

                        if (graphComponent.getCellAt(e.getX(), e.getY()) == null) { //create flow pool if second click null
                            // should not create if first click location is flow pool
                            if (isFromFlowPool) {
                                Object[] options = {"OK"};
                                JOptionPane.showOptionDialog(mainForm,
                                        "Flow should connect with stock", "Error",
                                        JOptionPane.PLAIN_MESSAGE,
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        options,
                                        options[0]);
                                Object fp = graphComponent.getCellAt(last.getX(), last.getY());
                                removeFlowPool((mxCell) fp);
                                isFirstClickFlow = true;
                                objectLoc = -1;
                                jLabel1.setText(SELECTION_MSG);
                                return;

                            } else {
                                AddFlowPool(e.getX(), e.getY());
                            }

                            /*else{
                             // throw message a flow must be connected to a stock
                             JOptionPane.showConfirmDialog(this, "Flow requires at least a source or target");
                             //delete flow pool at e
                            
                             }*/
                            Object from = graphComponent.getCellAt(last.getX(), last.getY());
                            Object to = graphComponent.getCellAt(e.getX(), e.getY());
                            //System.out.print("Stored second click");
                            AddFlowEdge(inputname, style, from, to);

                            isFirstClickFlow = true;
                            objectLoc = -1;
                            jLabel1.setText(SELECTION_MSG);
                            isFromFlowPool = false;
                        } else { // clicked location has an object
                            Object from = graphComponent.getCellAt(last.getX(), last.getY());
                            Object to = graphComponent.getCellAt(e.getX(), e.getY());
                            //System.out.print("Stored second click");
                            AddFlowEdge(inputname, style, from, to);

                            isFirstClickFlow = true;
                            objectLoc = -1;
                            jLabel1.setText(SELECTION_MSG);
                            isFromFlowPool = false;
                        }

                        jLabel1.setText(SELECTION_MSG);
                    }
                } else if (objectLoc == 3) { //store first object arrow object
                    if (isFirstClickArrow && graphComponent.getCellAt(e.getX(), e.getY()) != null) {
                        last = e;
                        //System.out.print("Stored first click");
                        isFirstClickArrow = false;
                        jLabel1.setText(SELECTION_ARW_TGT);
                    } else if (graphComponent.getCellAt(e.getX(), e.getY()) != null
                            && graphComponent.getCellAt(last.getX(), last.getY()) != null) {
                        Object from = graphComponent.getCellAt(last.getX(), last.getY());
                        Object to = graphComponent.getCellAt(e.getX(), e.getY());

                        AddArrowEdge(style, from, to);

                        isFirstClickArrow = true;
                        objectLoc = -1;
                        jLabel1.setText(SELECTION_MSG);

                    }else{
                        isFirstClickArrow = true;
                        jLabel1.setText(SELECTION_MSG);
                    }
                } else if (objectLoc == 4) {
                    AddVariable(style, inputname, inputdescrip, inputValue, e.getX(), e.getY());
                    objectLoc = -1;
                    jLabel1.setText(SELECTION_MSG);
                } else {
                    //objectLoc should always be equal to -1 when not placing an object
                    objectLoc = -1;
                    jLabel1.setText(SELECTION_MSG);
                }
            }

        }
        );

        //This initalizes the stylesheets from stylelist.java class for each object
        graph.getStylesheet()
                .putCellStyle("Stock", stylelist.getStock());
        graph.getStylesheet()
                .putCellStyle("Flow", stylelist.getFlow());
        graph.getStylesheet()
                .putCellStyle("Arrow", stylelist.getArrow());
        graph.getStylesheet()
                .putCellStyle("Variable", stylelist.getVariable());
        graph.getStylesheet()
                .putCellStyle("FlowPool", stylelist.getFlowPool());
    }

    public static mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    // paul added this PMC 9-27-16
    public void CustomCursor() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image img = toolkit.getImage("lilcombo.png");
        Point point = new Point(0, 0);
        Cursor cursor = toolkit.createCustomCursor(img, point, "Cursor");

        graphComponent.setCursor(cursor); // still doesn't work PMC

        this.jScrollPane1.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

    }

//This method will add a stock to the graph
    void AddStock(String name, String styleName,
            String inputsymbol, String inputinitial, String inputequation, int x, int y) {
        Object parent = graph.getDefaultParent();
        graphComponent.setConnectable(false);
        graph.setCellsBendable(false);
        graph.setCellsCloneable(false);
        graph.getModel().beginUpdate();
        //stockObject is created and added to the stockArrayList
        Object node = graph.insertVertex(parent, null, name, x, y, 100, 50, styleName);//draw the node

        graph.getModel().endUpdate();
        StockObject stockobject = new StockObject(node, name, inputsymbol, inputinitial, x + "", y + "");
        stockArrayList.add(stockobject);
        runnable = true;
        runSimBtn.enable(runnable);
    }

    // This method will add a stock to the graph from a save file
    void AddStock(StockObject stock) {
        //Object parent = graph.getDefaultParent();
        graphComponent.setConnectable(false);
        graph.setCellsBendable(false);
        graph.setCellsCloneable(false);
        graph.getModel().beginUpdate();
        //stockObject is created and added to the stockArrayList

        graph.getModel().endUpdate();

    }

    // get arrow object from mxCell
    ArrowObject getArrow(mxCell mxc) {
        ArrowObject ao;
        for (int i = 0; i < arrowArrayList.size(); i++) {
            ao = arrowArrayList.get(i);
            if (ao.getO_Object() == mxc) {
                return ao;
            }
        }

        return null;
    }

    // get stock object from mxCell
    StockObject getStock(mxCell mxc) {
        StockObject so;
        for (int i = 0; i < stockArrayList.size(); i++) {
            so = stockArrayList.get(i);
            if (so.getO_Object() == mxc) {
                return so;
            }
        }

        return null;
    }

    ConnectableModelObject getFlowPool(mxCell mxc) {
        ConnectableModelObject cmo;
        for (int i = 0; i < flowPoolArrayList.size(); i++) {
            cmo = flowPoolArrayList.get(i);
            if (cmo.getO_Object() == mxc) {
                return cmo;
            }

        }

        return null;
    }

    // get stock flow from mxCell
    FlowObject getFlow(mxCell mxc) {
        FlowObject fo;
        for (int i = 0; i < flowArrayList.size(); i++) {
            fo = flowArrayList.get(i);
            if (fo.getO_Object() == mxc) {
                return fo;
            }
        }

        return null;
    }

    // get variable from mxCell
    VariableObject getVar(mxCell mxc) {
        VariableObject vo;
        for (int i = 0; i < variableArrayList.size(); i++) {
            vo = variableArrayList.get(i);
            if (vo.getO_Object() == mxc) {
                return vo;
            }
        }

        return null;
    }

    void AddFlowPool(int x, int y) {

        Object parent = graph.getDefaultParent();
        graphComponent.setConnectable(true);
        graph.setCellsCloneable(false);
        graph.setCellsBendable(false);

        String inputName = "flowpool" + this.flowPoolArrayList.size();

        String styleName = "FlowPool";

        graph.getModel().beginUpdate();
        try {

            Object node = graph.insertVertex(parent, null, inputName, x, y, 70, 35, styleName);//draw the node
            ConnectableModelObject cmo = new ConnectableModelObject(inputName, node); // need to do something with this
            flowPoolArrayList.add(cmo);

        } finally {
            graph.getModel().endUpdate();
        }

    }

    void AddFlowEdge(String name, String styleName, Object src, Object to) {
        Object parent = graph.getDefaultParent(); // good
        graphComponent.setConnectable(false);
        graph.setCellsBendable(false);
        graph.setCellsCloneable(false);

        graph.getModel().beginUpdate();

        try {

            Object node = graph.insertEdge(parent, null, null, src, to, styleName);
            ModelingObject moSrc = this.getModelObject(src);
            ModelingObject moTo = this.getModelObject(to);
            if (moSrc == null && moTo == null) {
                JOptionPane.showConfirmDialog(this, "Flow requires at least a source or target");
            } else {

                FlowObject flowobject = new FlowObject(node, name, inputequation, (ConnectableModelObject) moSrc, (ConnectableModelObject) moTo);
                flowArrayList.add(flowobject);
            }

        } finally {
            graph.getModel().endUpdate();
        }

    }

    void AddFlowEdge(FlowObject flow) {
        graph.getModel().beginUpdate();
        graph.getModel().endUpdate();
    }

    ModelingObject getModelObject(Object obj) {
        // find which object it in the list

        Map<String, Object> objMap = graph.getCellStyle(obj); //
        mxCell mxobj = (mxCell) obj;// cast generic object to be a mxCell
        String objst = (String) mxobj.getStyle(); // mull the mxCell id to use
        if (objst.equals(MainForm.FLOW_STYLE)) {
            return this.getFlow(mxobj);

        } else if (objst.equals(VARIABLE_STYLE)) {
            return this.getVar(mxobj);
        } else if (objst.equals(MainForm.STOCK_STYLE)) {
            return this.getStock(mxobj);
        } else if (objst.equals(MainForm.FLOWPOOL_STYLE)) {
            return this.getFlowPool(mxobj);
        }
        return null;

    }

    void AddArrowEdge(String styleName, Object src, Object to) {
        Object parent = graph.getDefaultParent(); // get object parent
        graphComponent.setConnectable(false);
        graph.setCellsCloneable(false);
        graph.setCellsBendable(false);

        graph.getModel().beginUpdate();
        try {

            Object node = graph.insertEdge(parent, null, null, src, to, styleName);
            ModelingObject moSrc = this.getModelObject(src);
            ModelingObject moTo = this.getModelObject(to);
            if (moSrc == null || moTo == null) {
                JOptionPane.showConfirmDialog(this, "Arrow requires both source and target");
            } else {
                //Object node = graph.insertE(parent, null, null, x, y, 100, 50, styleName);//draw the node
                ArrowObject arrowobject = new ArrowObject("arrow", node, (ConnectableModelObject) moSrc, (ConnectableModelObject) moTo);
                this.arrowArrayList.add(arrowobject);
            }

        } finally {
            graph.getModel().endUpdate();
        }
    }

// This method will add a variable to the graph
    void AddVariable(String styleName, String inputName,
            String inputSymbol, String inputInitial, int x, int y) {
        Object parent = graph.getDefaultParent();
        graphComponent.setConnectable(true);
        graph.setCellsCloneable(false);
        graph.setCellsBendable(false);

        graph.getModel().beginUpdate();
        try {
            Object node = graph.insertVertex(parent, null, inputName, x, y, 100, 50, styleName);//draw the node
            VariableObject variableobject = new VariableObject(node, inputName,
                    inputSymbol, inputInitial, x + "", y + "");
            variableArrayList.add(variableobject);
        } finally {
            graph.getModel().endUpdate();
        }
    }

    void AddVariable(VariableObject var) {

        graphComponent.setConnectable(true);
        graph.setCellsCloneable(false);
        graph.setCellsBendable(false);
        graph.getModel().beginUpdate();

        graph.getModel().endUpdate();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")   // what does this do? -PMC
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jButton3 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jSeparator4 = new javax.swing.JSeparator();
        stockBtn = new javax.swing.JButton();
        flowBtn = new javax.swing.JButton();
        variableBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JToggleButton();
        modelSettingsBtn = new javax.swing.JButton();
        runSimBtn = new javax.swing.JButton();
        viewGraphButton = new javax.swing.JButton();
        viewTableButton = new javax.swing.JButton();
        analysisMethodLabel = new javax.swing.JLabel();
        methodChoiceCombo = new javax.swing.JComboBox();
        closeBtn = new javax.swing.JButton();
        Arrowbtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        SaveMenuItem = new javax.swing.JMenuItem();
        OpenMenuItem = new javax.swing.JMenuItem();
        PrintMenuItem = new javax.swing.JMenuItem();
        EditMenu = new javax.swing.JMenu();
        DeleteMenuItem = new javax.swing.JMenuItem();
        AddMenu = new javax.swing.JMenu();
        StockMenuItem = new javax.swing.JMenuItem();
        FlowMenuItem = new javax.swing.JMenuItem();
        VariableMenuItem = new javax.swing.JMenuItem();
        ArrowMenuItem = new javax.swing.JMenuItem();
        HelpMenu = new javax.swing.JMenu();
        AboutMenuItem = new javax.swing.JMenuItem();

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jButton3.setText("jButton3");

        jScrollPane2.setViewportView(jTextPane1);

        jScrollPane3.setViewportView(jTextPane2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dynamic System Simulator - DSSIM 2.0");

        jScrollPane1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jScrollPane1.setForeground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setName(""); // NOI18N

        jSeparator4.setBackground(new java.awt.Color(51, 51, 51));
        jSeparator4.setForeground(new java.awt.Color(51, 51, 51));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator4, javax.swing.GroupLayout.DEFAULT_SIZE, 1, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43))
        );

        stockBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dssim/stockicon-r2.png"))); // NOI18N
        stockBtn.setToolTipText("Create Stock");
        stockBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockBtnActionPerformed(evt);
            }
        });

        flowBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dssim/flowicon.png"))); // NOI18N
        flowBtn.setToolTipText("Create Flow");
        flowBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flowBtnActionPerformed(evt);
            }
        });

        variableBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dssim/variableicon-r2.png"))); // NOI18N
        variableBtn.setToolTipText("Add Variable");
        variableBtn.setMaximumSize(new java.awt.Dimension(66, 58));
        variableBtn.setMinimumSize(new java.awt.Dimension(66, 58));
        variableBtn.setPreferredSize(new java.awt.Dimension(66, 58));
        variableBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                variableBtnActionPerformed(evt);
            }
        });

        deleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dssim/deleteicon.png"))); // NOI18N
        deleteBtn.setToolTipText("Delete Selected Item");
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });

        modelSettingsBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dssim/settingsicon.png"))); // NOI18N
        modelSettingsBtn.setToolTipText("Change Model Settings");
        modelSettingsBtn.setAlignmentY(0.0F);
        modelSettingsBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        modelSettingsBtn.setIconTextGap(0);
        modelSettingsBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        modelSettingsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modelSettingsBtnActionPerformed(evt);
            }
        });

        runSimBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dssim/runicon.png"))); // NOI18N
        runSimBtn.setToolTipText("Run Simulation");
        runSimBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runSimBtnActionPerformed(evt);
            }
        });

        viewGraphButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dssim/graphicon.png"))); // NOI18N
        viewGraphButton.setToolTipText("View Graph");
        viewGraphButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewGraphButtonActionPerformed(evt);
            }
        });

        viewTableButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dssim/tableicon.png"))); // NOI18N
        viewTableButton.setToolTipText("View Table");
        viewTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewTableButtonActionPerformed(evt);
            }
        });

        analysisMethodLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        analysisMethodLabel.setText("Analysis Method");

        methodChoiceCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Eulers", "Runge-Kutta 2", "Runge Kutta 4" }));
        methodChoiceCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                methodChoiceComboActionPerformed(evt);
            }
        });

        closeBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dssim/closeicon.png"))); // NOI18N
        closeBtn.setToolTipText("Close Program");
        closeBtn.setMaximumSize(new java.awt.Dimension(107, 23));
        closeBtn.setMinimumSize(new java.awt.Dimension(107, 23));
        closeBtn.setPreferredSize(new java.awt.Dimension(107, 23));
        closeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBtnActionPerformed(evt);
            }
        });

        Arrowbtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dssim/arrowicon.png"))); // NOI18N
        Arrowbtn.setToolTipText("Add Arrow");
        Arrowbtn.setActionCommand("Arrowbtn");
        Arrowbtn.setMaximumSize(new java.awt.Dimension(66, 58));
        Arrowbtn.setMinimumSize(new java.awt.Dimension(66, 58));
        Arrowbtn.setPreferredSize(new java.awt.Dimension(66, 58));
        Arrowbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ArrowbtnActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(SELECTION_MSG);

        FileMenu.setText("File");

        SaveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        SaveMenuItem.setText("Save");
        SaveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(SaveMenuItem);

        OpenMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        OpenMenuItem.setText("Open");
        OpenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(OpenMenuItem);

        PrintMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        PrintMenuItem.setText("Print");
        PrintMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PrintMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(PrintMenuItem);

        jMenuBar1.add(FileMenu);

        EditMenu.setText("Edit");

        DeleteMenuItem.setText("Delete");
        DeleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteMenuItemActionPerformed(evt);
            }
        });
        EditMenu.add(DeleteMenuItem);

        AddMenu.setText("Add");
        AddMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddMenuActionPerformed(evt);
            }
        });

        StockMenuItem.setText("Stock");
        StockMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StockMenuItemActionPerformed(evt);
            }
        });
        AddMenu.add(StockMenuItem);

        FlowMenuItem.setText("Flow");
        FlowMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FlowMenuItemActionPerformed(evt);
            }
        });
        AddMenu.add(FlowMenuItem);

        VariableMenuItem.setText("Variable");
        VariableMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VariableMenuItemActionPerformed(evt);
            }
        });
        AddMenu.add(VariableMenuItem);

        ArrowMenuItem.setText("Arrow");
        ArrowMenuItem.setToolTipText("");
        ArrowMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ArrowMenuItemActionPerformed(evt);
            }
        });
        AddMenu.add(ArrowMenuItem);

        EditMenu.add(AddMenu);

        jMenuBar1.add(EditMenu);

        HelpMenu.setText("About");

        AboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        AboutMenuItem.setText("About this program");
        AboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutMenuItemActionPerformed(evt);
            }
        });
        HelpMenu.add(AboutMenuItem);

        jMenuBar1.add(HelpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(stockBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(flowBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Arrowbtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(variableBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(analysisMethodLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(methodChoiceCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(modelSettingsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(runSimBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(viewGraphButton, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(viewTableButton, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(closeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(stockBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(analysisMethodLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(methodChoiceCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(variableBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Arrowbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(flowBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(closeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(viewTableButton, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(viewGraphButton, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(runSimBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(modelSettingsBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        runSimBtn.getAccessibleContext().setAccessibleName("Run \nSimulation");
        methodChoiceCombo.getAccessibleContext().setAccessibleName("");
        methodChoiceCombo.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SaveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveMenuItemActionPerformed
        // TODO add your handling code here:
        //Brings up file chooser as to where to save objects
        JSONSave savefile = new JSONSave();

        try {

            // Writing to a file
            JFileChooser chooser = new JFileChooser();
            int retrival = chooser.showSaveDialog(null);
            if (retrival == JFileChooser.APPROVE_OPTION) {
                FileWriter fileWriter = new FileWriter(chooser.getSelectedFile() + ".dsm");

                //int returnVal = fc.showSaveDialog(MainForm.this);
                //if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {

                    // Writing to a file  
                    fileWriter.write("{ \n");
                    fileWriter.write("\"Stocks\" : \n");
                    System.out.println("Writing JSON objects to file");
                    System.out.println("-----------------------");
                    JSONArray stockList = new JSONArray();
                    for (int i = 0; i < stockArrayList.size(); i++) {
                        JSONObject stockObj = savefile.saveStock(stockArrayList.get(i));
                        stockList.add(stockObj);
                    }
                    fileWriter.write(stockList.toJSONString() + ", \n");
                    fileWriter.write("\n");
                    fileWriter.write("\"Flows\" : \n");
                    fileWriter.flush();

                    JSONArray flowList = new JSONArray();
                    for (int i = 0; i < flowArrayList.size(); i++) {
                        JSONObject flowObj = savefile.saveFlow(flowArrayList.get(i));
                        flowList.add(flowObj);
                    }
                    fileWriter.write(flowList.toJSONString() + ", \n");
                    fileWriter.write("\n");
                    fileWriter.write("\"Variables\" : \n");
                    fileWriter.flush();

                    JSONArray varList = new JSONArray();
                    for (int i = 0; i < variableArrayList.size(); i++) {
                        JSONObject varObj = savefile.saveVar(variableArrayList.get(i));
                        varList.add(varObj);
                    }
                    fileWriter.write(varList.toJSONString() + ", \n");
                    fileWriter.write("\n");
                    fileWriter.write("\"Arrows\" : \n");
                    fileWriter.flush();

                    JSONArray arrowList = new JSONArray();
                    for (int i = 0; i < arrowArrayList.size(); i++) {
                        JSONObject arrowObj = savefile.saveArrow(arrowArrayList.get(i));
                        arrowList.add(arrowObj);
                    }
                    fileWriter.write(arrowList.toJSONString() + ", \n");
                    fileWriter.write("\n");
                    fileWriter.write("\"Flow Pools\" : \n");
                    fileWriter.flush();

                    JSONArray fpList = new JSONArray();
                    for (int i = 0; i < flowPoolArrayList.size(); i++) {
                        JSONObject fpObj = savefile.saveFlowPool(flowPoolArrayList.get(i));
                        fpList.add(fpObj);
                    }
                    fileWriter.write(fpList.toJSONString() + ", \n");
                    fileWriter.write("\n");
                    fileWriter.write("\"Model Settings\" : \n");
                    fileWriter.flush();

                    JSONObject settings = savefile.saveSettings(modelSettings);
                    fileWriter.write(settings.toJSONString() + ", \n");
                    fileWriter.write("}");
                    fileWriter.close();
                    System.out.println("Finished writing file");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                // }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_SaveMenuItemActionPerformed

    private void AboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AboutMenuItemActionPerformed
        // TODO add your handling code here:
        //Displays the dialog box with information about the program
        //ImageIcon icon1 = new ImageIcon(getClass().getResource("/dssim/Lander.jpg"));
        JOptionPane.showMessageDialog(null, "This Project has Been Completed Under the Advisement of Dr. Gilliean Lee."
                + "\nVersion 2.0 was developed by"
                + "\naul Cuenin and Kamren Mangrum."
                + "\nVersion 1.0 was developed by "
                + " \nLogan Bautista, Jeleshia Freeman, TJ Shedd and Taylor Wilcox. "
                + "\nLander University class of April 2016"
                + "\n  ©2016", "About", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_AboutMenuItemActionPerformed

    private void runSimBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runSimBtnActionPerformed

        Methods method2 = new Methods();

        for (int i = 0; i < stockArrayList.size(); i++) {
            String s = stockArrayList.get(i).getObjName();
            final JTextField stockName = new JTextField(s);
            final JTextField stockSymbol = new JTextField(stockArrayList.get(i).getStockDescrip());
            final JTextField stockInitial = new JTextField(stockArrayList.get(i).getStockInitial());
            int cnt = i;
            stockArrayList.get(cnt).setObjName(stockName.getText());
            stockArrayList.get(cnt).setStockDescrip(stockSymbol.getText());
            stockArrayList.get(cnt).setStockInitial(stockInitial.getText());
            stockArrayList.get(cnt).setStockArg(stockSymbol.getText(), stockInitial.getText());
        }
        //one improvement is to make things like Double.parseDouble(modelSettings.getFinalTime() static variables

        method2 = new Methods((ArrayList) stockArrayList, flowArrayList, variableArrayList,
                Double.parseDouble(modelSettings.getInitialTime()),
                Double.parseDouble(modelSettings.getFinalTime()), Double.parseDouble(modelSettings.getTimeStep()),
                methodChoice);
        //try to reset data
        tableModel = null;
        //call the table model
        tableModel = method2.getTable();
        //try to reset data
        data = null;
        //call the data model
        if (!progressBar.isFrameAvailable()) {
            JFrame jf = new JFrame();
            jf.add(progressBar);
            progressBar.setFrame(jf);
            jf.setSize(340, 270);
            jf.setResizable(false);
            jf.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        }

        progressBar.setVisible(true);
        data = method2.returnData();
    }//GEN-LAST:event_runSimBtnActionPerformed

    private ConnectableModelObject getConnectedObject(mxCell cell) {
        for (int i = 0; i < stockArrayList.size(); i++) {

            if (stockArrayList.get(i).getO_Object().equals(cell)) {
                //StockObject so = stockArrayList.remove(i);
                return stockArrayList.get(i);
                // not sure if this makes sense

            }
        }
        for (int i = 0; i < flowArrayList.size(); i++) {

            if (flowArrayList.get(i).getO_Object().equals(cell)) {
                return flowArrayList.get(i);

            }
        }

        for (int i = 0; i < variableArrayList.size(); i++) {

            if (variableArrayList.get(i).getO_Object().equals(cell)) {
                return variableArrayList.get(i);
            }
        }
        return null;
    }

    private void deleteModelingObject(ModelingObject mo) {
        for (int i = 0; i < stockArrayList.size(); i++) {

            if (stockArrayList.get(i).equals(mo)) {
                stockArrayList.remove(i);
                return;
                //return stockArrayList.get(i);
                // not sure if this makes sense

            }
        }
        for (int i = 0; i < flowArrayList.size(); i++) {

            if (flowArrayList.get(i).equals(mo)) {
                flowArrayList.remove(i);
                return;

            }
        }
        for (int i = 0; i < arrowArrayList.size(); i++) {

            if (arrowArrayList.get(i).equals(mo)) {
                arrowArrayList.remove(i);
                return;

            }
        }
        for (int i = 0; i < variableArrayList.size(); i++) {

            if (variableArrayList.get(i).equals(mo)) {
                variableArrayList.remove(i);
                return;
            }
        }
        for (int i = 0; i < this.flowArrayList.size(); i++) {

            if (flowArrayList.get(i).equals(mo)) {
                flowArrayList.remove(i);
                return;
            }
        }

    }

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        // TODO add your handling code here:

        //System.out.println(cell.toString());
        ConnectableModelObject cmo = getConnectedObject((mxCell) cell); // cell would be stock
        // get the connectable object at that location
        if (cmo != null) { // if is a connectable modeling object
            Vector<ModelingObject> vMo = cmo.getConnectedObjects(); // connected (incoming/outgoing) obj
            for (int i = 0; i < vMo.size(); i++) {

                ModelingObject mo = vMo.get(i); // chech each connected object. would be flow/arrow

                if (mo.getClass().getName().contains("ArrowObject")) {
                    continue;
                }

                ConnectableModelObject ccmo = (ConnectableModelObject) mo;

                if (ccmo.getStyle() == ModelingObject.FLOW) {
                    FlowObject flow = (FlowObject) ccmo;
                    ModelingObject mofp = flow.getFlowPoolConnected(graph);
                    if (mofp != null) {
                        graph.getModel().remove(mofp.getO_Object());
                        deleteModelingObject(mofp);
                    }

                }

                // trying to check it the flowpool is attached to one of the connected objects
                // ccmo would be flow/arrow
                graph.getModel().remove(mo.getO_Object());
                deleteModelingObject(mo);

            }
        }
        graph.getModel().remove(cell);
        //each of these forloops is a way for the system to go through each graph object and check if it is the one that
        //needs to be deleted. The main key references here are the graphobject cell name kept in the arraylist of that object.

        deleteBtn.setSelected(false);
    }//GEN-LAST:event_deleteBtnActionPerformed

    void removeFlowPool(mxCell cell) {
        graph.getModel().remove(cell);
        flowPoolArrayList.remove(cell);

    }
    private void stockBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockBtnActionPerformed
        // TODO add your handling code here: PMC
        //increment amount of stocks, used for panels and default names
        //changed amount of stocks a different way, like by taking the size of the stockArrayList

        style = "Stock";
        //CustomCursor(); need to make work
        jLabel1.setText(SELECTION_STK);

        objectLoc = 1;
        stockInfoForm();

    }//GEN-LAST:event_stockBtnActionPerformed
    private void stockInfoForm() {

        //check if the user would like to input information
        //if yes, get info
        //Base values in case someone hits enter to fast, so it wont throw errors
        JTextField stockName = new JTextField("Stock" + stockArrayList.size());
        JTextField stockInitial = new JTextField(Double.toString(0.0));
        JTextField stockDescrip = new JTextField("Stock" + Integer.toString(stockArrayList.size()));
        JPanel addStock = new JPanel();
        addStock.setLayout(new BoxLayout(addStock, BoxLayout.PAGE_AXIS));
        addStock.add(new JLabel("Stock Name"));
        addStock.add(stockName);
        addStock.add(new JLabel("Stock Description"));
        addStock.add(stockDescrip);
        addStock.add(new JLabel("Stock Initial Value"));
        addStock.add(stockInitial);

        int option = JOptionPane.showConfirmDialog(null, addStock, "Add a Stock", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.CANCEL_OPTION) {
            objectLoc = 0;
            jLabel1.setText(SELECTION_MSG);
            return;
        }
        inputname = stockName.getText();
        inputdescrip = stockDescrip.getText();
        inputinitial = stockInitial.getText();

        //updates objectLoc for mouse event on where to place the object on the graph
    }
    private void modelSettingsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modelSettingsBtnActionPerformed
        // TODO add your handling code here:
        //Creates a new JFrame that displays the GUI from ModelSettings.java class

        if (!modelSettings.isFrameAvailable()) {
            JFrame jf = new JFrame();
            jf.add(modelSettings);
            modelSettings.setFrame(jf);
            jf.setSize(340, 270);
            jf.setResizable(false);
            jf.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        }

        modelSettings.getFrame().setVisible(true);

    }//GEN-LAST:event_modelSettingsBtnActionPerformed

    private void variableBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_variableBtnActionPerformed
        // TODO add your handling code here:
        style = "Variable";
        jLabel1.setText(SELECTION_VAR);
        //updates objectLoc for mouse event on where to place the object on the graph
        objectLoc = 4;
        inputValue = "0.0";
        //check if the user would like to input the information about the variable

        //if yes, get info
        //Base values in case someone hits enter to fast, so it wont throw errors
        JTextField varName = new JTextField("Variable" + variableArrayList.size());
        JTextField varSymbol = new JTextField("Variable" + variableArrayList.size());
        JTextField varValue = new JTextField("0.0");
        JPanel addVar = new JPanel();
        addVar.setLayout(new BoxLayout(addVar, BoxLayout.PAGE_AXIS));
        addVar.add(new JLabel("Variable Name"));
        addVar.add(varName);

        addVar.add(new JLabel("Variable Description"));
        addVar.add(varSymbol);
        addVar.add(new JLabel("Variable Equation"));
        addVar.add(varValue);
        int option = JOptionPane.showConfirmDialog(null, addVar, "Add a Variable", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.CANCEL_OPTION) {
            objectLoc = 0;
            jLabel1.setText(SELECTION_MSG);
            return;
        }
        inputname = varName.getText();
        inputValue = varValue.getText();
        inputdescrip = varSymbol.getText();

    }//GEN-LAST:event_variableBtnActionPerformed

    private void closeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeBtnActionPerformed

        System.exit(0);
    }//GEN-LAST:event_closeBtnActionPerformed

    private void StockMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StockMenuItemActionPerformed

        stockBtnActionPerformed(evt);

    }//GEN-LAST:event_StockMenuItemActionPerformed
    public static mxGraph getGraph() {
        return graph;
    }

    private void DeleteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteMenuItemActionPerformed

        deleteBtnActionPerformed(evt);

    }//GEN-LAST:event_DeleteMenuItemActionPerformed

    private void methodChoiceComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_methodChoiceComboActionPerformed
        if (methodChoiceCombo.getSelectedIndex() == 0) {
            methodChoice = "eulers";
        } else if (methodChoiceCombo.getSelectedIndex() == 1) {
            methodChoice = "rk2";
        } else if (methodChoiceCombo.getSelectedIndex() == 2) {
            methodChoice = "rk4";
        }
    }//GEN-LAST:event_methodChoiceComboActionPerformed

    private void viewGraphButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewGraphButtonActionPerformed
        //See if user would like to name the graph labels
        //if blank, ask if they would like to input

        //if the graph titles are default, ask if they would like to input information
        if (XTitle.equals("") && YTitle.equals("") && graphTitle.equals("")) {
            JPanel check = new JPanel();
            check.setLayout(new BoxLayout(check, BoxLayout.PAGE_AXIS));
            check.add(new JLabel("Would you like to label the graph?"));
            int yN = JOptionPane.showConfirmDialog(null, check);
            if (yN == 0) {
                //if yes get info
                JTextField graphTitleField = new JTextField(25);
                JTextField XTitleField = new JTextField(25);
                JTextField YTitleField = new JTextField(25);
                JPanel graphLabels = new JPanel();
                graphLabels.setLayout(new BoxLayout(graphLabels, BoxLayout.PAGE_AXIS));
                graphLabels.add(new JLabel("graph's title."));
                graphLabels.add(graphTitleField);
                graphLabels.add(new JLabel("X axis title."));
                graphLabels.add(XTitleField);
                graphLabels.add(new JLabel("Y axis title."));
                graphLabels.add(YTitleField);
                JOptionPane.showConfirmDialog(null, graphLabels);
                graphTitle = graphTitleField.getText();
                XTitle = XTitleField.getText();
                YTitle = YTitleField.getText();
                final GraphThis graph = new GraphThis("DSSIM Graph", data, graphTitle, XTitle, YTitle, tableModel);
            } //if no, just run with the blank values or preset values
            else {
                final GraphThis graph = new GraphThis("DSSIM Graph", data, graphTitle, XTitle, YTitle, tableModel);
            }
        } else {
            //if there are values, ask if they would like to change them
            JPanel check = new JPanel();
            check.setLayout(new BoxLayout(check, BoxLayout.PAGE_AXIS));
            check.add(new JLabel("Would you like to change the labels on the graph?"));
            int yN = JOptionPane.showConfirmDialog(null, check);
            if (yN == 0) {
                //if yes get info
                JTextField graphTitleField = new JTextField(25);
                JTextField XTitleField = new JTextField(25);
                JTextField YTitleField = new JTextField(25);
                JPanel graphLabels = new JPanel();
                graphLabels.setLayout(new BoxLayout(graphLabels, BoxLayout.PAGE_AXIS));
                graphLabels.add(new JLabel("graph's title."));
                graphLabels.add(graphTitleField);
                graphLabels.add(new JLabel("X axis title."));
                graphLabels.add(XTitleField);
                graphLabels.add(new JLabel("Y axis title."));
                graphLabels.add(YTitleField);
                JOptionPane.showConfirmDialog(null, graphLabels);
                graphTitle = graphTitleField.getText();
                XTitle = XTitleField.getText();
                YTitle = YTitleField.getText();
                final GraphThis graph = new GraphThis("DSSIM Graph", data, graphTitle, XTitle, YTitle, tableModel);
            } //if no, just run with the blank values or preset values
            else {
                final GraphThis graph = new GraphThis("DSSIM Graph", data, graphTitle, XTitle, YTitle, tableModel);
            }
        }
    }//GEN-LAST:event_viewGraphButtonActionPerformed

    private void viewTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewTableButtonActionPerformed
        final Table table = new Table(tableModel);
        //call table model and send it the global table variable
    }//GEN-LAST:event_viewTableButtonActionPerformed

    private void flowBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flowBtnActionPerformed
        // TODO add your handling code here:
        style = "Flow";
        jLabel1.setText(SELECTION_FLW_SRC);

        //if yes, get info
        //Base values in case someone hits enter to fast, so it wont throw errors
        JTextField flowName = new JTextField("Flow" + flowArrayList.size());
        JTextField flowEquation = new JTextField("");
        JPanel addFlow = new JPanel();
        addFlow.setLayout(new BoxLayout(addFlow, BoxLayout.PAGE_AXIS));

        addFlow.add(new JLabel("Flow Name"));
        addFlow.add(flowName);
        addFlow.add(new JLabel("Flow Equation"));
        addFlow.add(flowEquation);

        int option = JOptionPane.showConfirmDialog(null, addFlow, "Add a Flow", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.CANCEL_OPTION) {
            objectLoc = 0;
            jLabel1.setText(SELECTION_MSG);
            return;
        }

        inputname = flowName.getText();
        inputequation = flowEquation.getText();

        objectLoc = 2;
        //AddFlow(response, style);
    }//GEN-LAST:event_flowBtnActionPerformed

    private void AddMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AddMenuActionPerformed

    private void FlowMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FlowMenuItemActionPerformed
        // TODO add your handling code here:
        //This method is the same ad the flowBtn above. It is just an extra one in the top menus
        flowBtnActionPerformed(evt);

    }//GEN-LAST:event_FlowMenuItemActionPerformed

    private void PrintMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PrintMenuItemActionPerformed
        // TODO add your handling code here:
        //This method will allow the user to print the graph when completed
    }//GEN-LAST:event_PrintMenuItemActionPerformed

    private void VariableMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VariableMenuItemActionPerformed
        // TODO add your handling code here:
        variableBtnActionPerformed(evt);

    }//GEN-LAST:event_VariableMenuItemActionPerformed

    private void OpenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenMenuItemActionPerformed
        // TODO add your handling code here:
        //JSONParser parser = new JSONParser();

        JSONParser parser = new JSONParser();
        JFileChooser fc = new JFileChooser("C:");
        fc.setDialogTitle("Select Model");
        fc.setFileFilter(new dsmFilter());
        int returnVal = fc.showOpenDialog(null);
        //String srcName = "";
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File srcFile = fc.getSelectedFile();
        //String filename = "srcFile";
        graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));

        stockArrayList = JSONRead.readStock(parser, srcFile);
        for (int i = 0; i < stockArrayList.size(); i++) {
            AddStock(stockArrayList.get(i));
        }

        variableArrayList = JSONRead.readVar(parser, srcFile);
        for (int i = 0; i < variableArrayList.size(); i++) {
            AddVariable(variableArrayList.get(i));
        }

        flowPoolArrayList = new ArrayList<ConnectableModelObject>();
        ArrayList<String> tempFlowPoolArrayList = JSONRead.readFlowPool(parser, srcFile);
        for (int i = 0; i < tempFlowPoolArrayList.size(); i++) {
            String[] tempX = tempFlowPoolArrayList.get(i).split(":");
            int fpX = Integer.valueOf(tempX[0]);
            int fpY = Integer.valueOf(tempX[1]);
            AddFlowPool(fpX, fpY);
        }

        flowArrayList = JSONRead.readFlow(parser, srcFile, stockArrayList, variableArrayList, flowPoolArrayList);
        for (int i = 0; i < flowArrayList.size(); i++) {
            //AddFlowEdge(flowArrayList.get(i).getFlowName(),FLOW_STYLE,flowArrayList.get(i).getFlowFrom(),flowArrayList.get(i).getFlowTo());
            AddFlowEdge(flowArrayList.get(i));
        }

        ArrayList<String[]> tempArrows = JSONRead.readArrow(parser, srcFile, stockArrayList, variableArrayList, flowPoolArrayList);
        for (int j = 0; j < tempArrows.size(); j++) {
            Object tempFrom = null, tempTo = null;
            //AddArrowEdge(ARROW_STYLE,arrowArrayList.get(i).getArrowFrom(),arrowArrayList.get(i).getArrowTo());
            if (Integer.valueOf(tempArrows.get(j)[4]) == 2) {
                for (int i = 0; i < stockArrayList.size(); i++) {
                    if (tempArrows.get(j)[1].equals(stockArrayList.get(i).getStockName())) {
                        tempFrom = graphComponent.getCellAt(Integer.valueOf(stockArrayList.get(i).getStockX()), Integer.valueOf(stockArrayList.get(i).getStockY()));
                    }
                }
            } else if (Integer.valueOf(tempArrows.get(j)[4]) == 3) {
                for (int i = 0; i < variableArrayList.size(); i++) {
                    if (tempArrows.get(j)[1].equals(variableArrayList.get(i).getVarName())) {
                        tempFrom = graphComponent.getCellAt(Integer.valueOf(variableArrayList.get(i).getVarX()), Integer.valueOf(variableArrayList.get(i).getVarY()));
                    }
                }
            } else {
                for (int i = 0; i < flowArrayList.size(); i++) {
                    if (tempArrows.get(j)[1].equals(flowArrayList.get(i).getFlowName())) {
                        tempFrom = flowArrayList.get(i).getO_Object();
                    }
                }
            }

            if (Integer.valueOf(tempArrows.get(j)[3]) == 2) {
                for (int i = 0; i < stockArrayList.size(); i++) {
                    if (tempArrows.get(j)[2].equals(stockArrayList.get(i).getStockName())) {
                        tempTo = graphComponent.getCellAt(Integer.valueOf(stockArrayList.get(i).getStockX()), Integer.valueOf(stockArrayList.get(i).getStockY()));
                    }
                }
            } else if (Integer.valueOf(tempArrows.get(j)[3]) == 3) {
                for (int i = 0; i < variableArrayList.size(); i++) {
                    if (tempArrows.get(j)[2].equals(variableArrayList.get(i).getVarName())) {
                        tempTo = graphComponent.getCellAt(Integer.valueOf(variableArrayList.get(i).getVarX()), Integer.valueOf(variableArrayList.get(i).getVarY()));
                    }
                }
            } else {
                for (int i = 0; i < flowArrayList.size(); i++) {
                    if (tempArrows.get(j)[2].equals(flowArrayList.get(i).getFlowName())) {
                        tempTo = flowArrayList.get(i).getO_Object();
                    }
                }
            }
            AddArrowEdge("Arrow", tempFrom, tempTo);
        }

        if (!modelSettings.isFrameAvailable()) {
            JFrame jf = new JFrame();
            jf.add(modelSettings);
            modelSettings.setFrame(jf);
            jf.setSize(340, 270);
            jf.setResizable(false);
            jf.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            String[] mSettings = JSONRead.readSettings(parser, srcFile);

            modelSettings.initialTime = mSettings[0];
            modelSettings.initialTimeTField.setText(mSettings[0]);

            modelSettings.finalTime = mSettings[1];
            modelSettings.finalTimeTField.setText(mSettings[1]);

            modelSettings.timeStep = mSettings[2];
            modelSettings.timeStepTField.setText(mSettings[2]);
        } else {
            String[] mSettings = JSONRead.readSettings(parser, srcFile);

            modelSettings.initialTime = mSettings[0];
            modelSettings.initialTimeTField.setText(mSettings[0]);

            modelSettings.finalTime = mSettings[1];
            modelSettings.finalTimeTField.setText(mSettings[1]);

            modelSettings.timeStep = mSettings[2];
            modelSettings.timeStepTField.setText(mSettings[2]);
        }
        String[] mSettings = JSONRead.readSettings(parser, srcFile);
        modelSettings.initialTime = mSettings[0];
        modelSettings.finalTime = mSettings[1];
        modelSettings.timeStep = mSettings[2];

    }//GEN-LAST:event_OpenMenuItemActionPerformed

    private void ArrowbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ArrowbtnActionPerformed
        // TODO add your handling code here:
        jLabel1.setText(SELECTION_ARW_SRC);
        style = "Arrow";
        objectLoc = 3;
    }//GEN-LAST:event_ArrowbtnActionPerformed

    private void ArrowMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ArrowMenuItemActionPerformed
        // TODO add your handling code here:
        this.ArrowbtnActionPerformed(evt);
    }//GEN-LAST:event_ArrowMenuItemActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AboutMenuItem;
    private javax.swing.JMenu AddMenu;
    private javax.swing.JMenuItem ArrowMenuItem;
    private javax.swing.JButton Arrowbtn;
    private javax.swing.JMenuItem DeleteMenuItem;
    private javax.swing.JMenu EditMenu;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenuItem FlowMenuItem;
    private javax.swing.JMenu HelpMenu;
    private javax.swing.JMenuItem OpenMenuItem;
    private javax.swing.JMenuItem PrintMenuItem;
    private javax.swing.JMenuItem SaveMenuItem;
    private javax.swing.JMenuItem StockMenuItem;
    private javax.swing.JMenuItem VariableMenuItem;
    private javax.swing.JLabel analysisMethodLabel;
    private javax.swing.JButton closeBtn;
    private javax.swing.JToggleButton deleteBtn;
    private javax.swing.JButton flowBtn;
    private javax.swing.JButton jButton3;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextPane jTextPane2;
    private javax.swing.JComboBox methodChoiceCombo;
    private javax.swing.JButton modelSettingsBtn;
    private javax.swing.JButton runSimBtn;
    private javax.swing.JButton stockBtn;
    private javax.swing.JButton variableBtn;
    private javax.swing.JButton viewGraphButton;
    private javax.swing.JButton viewTableButton;
    // End of variables declaration//GEN-END:variables
}
