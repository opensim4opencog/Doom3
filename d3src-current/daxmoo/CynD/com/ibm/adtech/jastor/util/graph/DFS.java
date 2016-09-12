/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor.util.graph;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Elias Torres ( <a href="mailto:eliast@us.ibm.com">eliast@us.ibm.com </a>)
 * 
 */
public class DFS extends AlgorithmsBase {
	
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";

    // run data
    private HashMap   prev, color, discover, finish;

    private List      nodesByDiscover, nodesByFinish;

    private int       time = 0;

    protected boolean done = false;

    private INode     start, end;

    private GraphMem  tree = null;

    private NodeMem   root = null;

    public synchronized void executeSubgraph() {
        startExecution();
        reinit();
        Iterator it = graph.nodes().iterator();
        while (it.hasNext()) {
            if (isDone())
                break;
            INode node = (INode) it.next();
            if (color.get(node).equals(WHITE) && node.getIncomingEdges().size() == 0) {
                visit(node);
            }
        }
        endExecution();
    }

    public synchronized void executeSubgraph(INode start) {
        startExecution();
        reinit();
        this.start = start;
        visit(start);
        endExecution();
    }

    public synchronized void execute(INode start) {
        startExecution();
        reinit();
        this.start = start;
        visit(start);
        internalExecute();
        endExecution();
    }

    public synchronized void execute(INode start, INode end) {
        startExecution();
        reinit();
        this.start = start;
        this.end = end;
        visit(start);
        internalExecute();
        endExecution();
    }

    public synchronized void execute() {
        startExecution();
        reinit();
        internalExecute();
        endExecution();
    }

    public void internalExecute() {
        Iterator it = graph.nodes().iterator();
        while (it.hasNext()) {
            if (isDone())
                break;
            INode node = (INode) it.next();
            if (color.get(node).equals(WHITE) && node.getIncomingEdges().size() == 0) {
                visit(node);
            }
        }
        it = graph.nodes().iterator();
        while (it.hasNext()) {
            if (isDone())
                break;
            INode node = (INode) it.next();
            if (color.get(node).equals(WHITE)) {
                visit(node);
            }
        }
    }

    protected void reinit() {

        prev = new HashMap();
        color = new HashMap();
        discover = new HashMap();
        finish = new HashMap();

        nodesByDiscover = new LinkedList();
        nodesByFinish = new LinkedList();

        start = null;
        end = null;

        time = 0;

        Iterator it = graph.nodes().iterator();
        while (it.hasNext()) {
            INode node = (INode) it.next();
            color.put(node, WHITE);
            prev.put(node, NILNODE);
        }

        tree = new GraphMem("dfs-tree");
    }

    public void printResults(PrintWriter writer) {
        checkState();
        ArrayList ordered = new ArrayList(time);
        for (int i = 0; i < time; i++) {
            ordered.add(NILNODE);
        }

        Iterator it = graph.nodes().iterator();
        while (it.hasNext()) {
            INode node = (INode) it.next();
            ordered.set(getDiscoverTime(node) - 1, node);
            ordered.set(getFinishTime(node) - 1, node);
        }

        for (int i = 0; i < ordered.size(); i++) {
            System.out.print(ordered.get(i) + ",");
        }
    }

    private void visit(INode node) {
        color.put(node, GRAY);
        time++;
        discover.put(node, new Integer(time));
        nodesByDiscover.add(node);
        startVisit(node);

        Iterator it = node.getOutgoingEdges().iterator();
        while (it.hasNext()) {
            if (isDone())
                break;
            IEdge edge = (IEdge) it.next();
            INode adj = edge.getDestination();
            if (color.get(adj).equals(WHITE)) {
                prev.put(adj, node);
                visit(adj);
            } else if (color.get(adj).equals(GRAY)) { // back-edge
                foundBackEdge(edge);
            } else if (color.get(adj).equals(BLACK)) {
                if (getDiscoverTime(edge.getSource()) > getDiscoverTime(edge.getDestination())) {
                    foundCrossEdge(edge);
                } else {
                    foundForwardEdge(edge);
                }
            }
        }

        color.put(node, BLACK);
        time++;
        finish.put(node, new Integer(time));
        nodesByFinish.add(node);
        finishVisit(node);
    }

    public INode getParent(INode child) {
        checkState();

        Object object = prev.get(child);

        return (object != NILNODE) ? (INode) object : null;
    }

    private int getFinishTime(INode node) {
        if (!finish.containsKey(node))
            return INVALID_TIME;

        Integer val = (Integer) finish.get(node);
        return val.intValue();
    }

    private int getDiscoverTime(INode node) {
        if (!discover.containsKey(node))
            return INVALID_TIME;

        Integer val = (Integer) discover.get(node);
        return val.intValue();
    }

    public List getNodesByDiscoverTime() {
        checkState();
        return Collections.unmodifiableList(nodesByDiscover);
    }

    public List getNodesByFinishTime() {
        checkState();
        return Collections.unmodifiableList(nodesByFinish);
    }

    public Object result() {
        checkState();
        if (tree.getEdgeCount() == 0) {
            Iterator it = prev.keySet().iterator();
            while (it.hasNext()) {
                INode node = (INode) it.next();
                tree.addNode(new NodeMem(node.getName()));
            }

            it = prev.keySet().iterator();
            while (it.hasNext()) {
                INode node = (INode) it.next();
                if (!prev.get(node).equals(NILNODE)) {
                    INode prevNode = (INode) prev.get(node);
                    tree.addEdge(new EdgeMem("tree-edge", tree.getNodeByName(prevNode.getName()), tree.getNodeByName(node.getName())));
                }
            }
        }

        return tree;
    }

    public void printResult() {
        checkState();

    }

    private void internalPrintResult(INode node, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }
        System.out.println(node);
        Iterator it = node.getOutgoingEdges().iterator();
        while (it.hasNext()) {
            IEdge edge = (IEdge) it.next();
            internalPrintResult(edge.getDestination(), level + 1);
        }
    }

    protected boolean isDone() {
        return done;
    }

    protected void startVisit(INode n) {
        if (end != null && end.equals(n))
            done = true;
    }

    protected void finishVisit(INode n) {
    }

    protected void foundForwardEdge(IEdge edge) {
    }

    protected void foundBackEdge(IEdge edge) {
    }

    protected void foundCrossEdge(IEdge edge) {
    }
}