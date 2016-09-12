/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor.util.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Elias Torres ( <a href="mailto:eliast@us.ibm.com">eliast@us.ibm.com </a>)
 * 
 */
public abstract class AlgorithmsBase {
	
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";

    protected final static Integer WHITE        = new Integer(0);

    protected final static Integer GRAY         = new Integer(1);

    protected final static Integer BLACK        = new Integer(2);

    protected final static int     NOTEXECUTED  = -1;

    protected final static int     EXECUTED     = 0;

    protected final static int     EXECUTING    = 1;

    protected final static Integer NILNODE      = new Integer(-1);

    protected final static int     INVALID_TIME = -1;

    protected IGraph               graph        = null;

    protected int                  state        = NOTEXECUTED;

    public void setGraph(IGraph graph) {
        this.graph = graph;
        resetState();
    }

    public IGraph getGraph() {
        return this.graph;
    }

    protected void checkState() {
        if (EXECUTED != state)
            throw new IllegalStateException();
    }

    protected void resetState() {
        state = NOTEXECUTED;
    }

    public abstract void execute();

    public abstract Object result();

    public static String[][] convertToPrintMatrix(IGraph graph, INode[] nodes) {
        String[][] closure = new String[nodes.length + 1][nodes.length + 1];

        closure[0][0] = "    ";

        for (int n = 1; n < closure.length; n++) {
            String name = nodes[n - 1].getName();
            if (name.length() < 3) {
                while (name.length() != 3) {
                    name = " " + name;
                }
            }
            name = " " + name.substring(name.length() - 3);

            closure[n][0] = name;
            closure[0][n] = name;
        }

        for (int n = 1; n < closure.length; n++) {
            for (int m = 1; m < closure.length; m++) {
                INode source = graph.getNodeByName(nodes[n - 1].getName());
                INode dest = graph.getNodeByName(nodes[m - 1].getName());
                closure[n][m] = source.hasOutgoingEdge(dest) ? "   1" : "    ";
            }
        }
        return closure;
    }

    public static String[][] convertToPrintMatrix(IGraph graph) {
        INode[] nodes = new INode[graph.getNodeCount()];
        graph.nodes().toArray(nodes);
        return convertToPrintMatrix(graph, nodes);
    }

    public static int[][] convertToAdjMatrix(IGraph graph, INode[] nodes) {
        int[][] closure = new int[nodes.length][nodes.length];

        for (int n = 0; n < nodes.length; n++) {
            for (int m = 0; m < nodes.length; m++) {
                INode source = graph.getNodeByName(nodes[n].getName());
                INode dest = graph.getNodeByName(nodes[m].getName());
                closure[n][m] = source.hasOutgoingEdge(dest) ? 1 : 0;
            }
        }
        return closure;
    }

    public static int[][] convertToAdjMatrix(IGraph graph) {
        INode[] nodes = new INode[graph.getNodeCount()];
        graph.nodes().toArray(nodes);
        return convertToAdjMatrix(graph, nodes);
    }

    public static List convertToAdjList(IGraph graph) {
        INode[] nodes = new INode[graph.getNodeCount()];
        graph.nodes().toArray(nodes);
        List list = new ArrayList(nodes.length);
        for (int n = 0; n < nodes.length; n++) {
            List edges = new LinkedList();
            Iterator it = nodes[n].getOutgoingEdges().iterator();
            while (it.hasNext()) {
                edges.add(((IEdge) it.next()).getDestination());
            }
            list.add(edges);
        }
        return list;
    }

    protected void startExecution() {
        state = EXECUTING;
    };

    protected void endExecution() {
        state = EXECUTED;
    };

    public static void printMatrix(String[][] matrix) {
        for (int n = 0; n < matrix.length; n++) {
            for (int m = 0; m < matrix.length; m++) {
                System.out.print(matrix[n][m]);
            }
            System.out.println();
        }
    }

    public static void printMatrix(IGraph graph) {
        printMatrix(convertToPrintMatrix(graph));
    }

    public static void printMatrix(IGraph graph, INode[] nodes) {
        printMatrix(convertToPrintMatrix(graph, nodes));
    }
}