/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor.util.graph;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.hp.hpl.jena.graph.Graph;

/**
 * @author Elias Torres ( <a href="mailto:eliast@us.ibm.com">eliast@us.ibm.com </a>)
 * 
 */
public class GraphMem extends GraphPartBase implements IGraph {
	
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";

    protected Map        edgesByName = new TreeMap();

    protected Map        nodesByName = new TreeMap();

    protected LinkedList edges       = new LinkedList();

    protected LinkedList nodes       = new LinkedList();

    public GraphMem(String name) {
        super(name);
    }

    NodeMem getNode(String name) {
        return (NodeMem) nodesByName.get(name);
    }

    EdgeMem getEdge(String name) {
        return (EdgeMem) edgesByName.get(name);
    }

    public INode getNodeByName(String name) {
        return (INode) nodesByName.get(name);
    }

    public IEdge getEdgeByName(String name) {
        return (IEdge) edgesByName.get(name);
    }

    private Graph getGraph() {
        try {
            return (Graph) getData();
        } catch (ClassCastException e) {
        }
        return null;
    }

    public void addEdge(IEdge e) {

        INode source = e.getSource();
        INode dest = e.getDestination();

        if (!contains(source) || !contains(dest)) {
            //
        }

        ((NodeMem) source).addOutgoingEdge((EdgeMem) e);
        ((NodeMem) dest).addIncomingEdge((EdgeMem) e);

        ((EdgeMem) e).setInGraph(this);
        edgesByName.put(e.getName(), e);
        edges.add(e);
    }

    public void addNode(INode n) {
        ((NodeMem) n).setInGraph(this);
        nodesByName.put(n.getName(), n);
        nodes.add(n);
    }

    public boolean contains(IEdge e) {
        return edges.contains(e);
    }

    public boolean contains(INode n) {
        return nodes.contains(n);
    }

    public List edges() {
        return Collections.unmodifiableList(edges);
    }

    public List nodes() {
        return Collections.unmodifiableList(nodes);
    }

    public int getEdgeCount() {
        return edges.size();
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public boolean isEmpty() {
        return (nodes.size() == 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.IGraph#removeEdge(com.ibm.adtech.slingshot.graph.IEdge)
     */
    public void removeEdge(IEdge e) {
        if (!edges.contains(e))
            return;

        ((EdgeMem) e).setInGraph(null);
        edgesByName.remove(e.getName());
        edges.remove(e);

        e.getSource().removeOutgoingEdge(e);
        e.getDestination().removeIncomingEdge(e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.IGraph#removeNode(com.ibm.adtech.slingshot.graph.INode)
     */
    public void removeNode(INode n) {
        if (!nodes.contains(n))
            return;

        ((NodeMem) n).setInGraph(null);
        nodesByName.remove(n.getName());
        nodes.remove(n);

        Iterator it = n.getIncomingEdges().iterator();
        while (it.hasNext()) {
            removeEdge((IEdge) it.next());
        }
        it = n.getOutgoingEdges().iterator();
        while (it.hasNext()) {
            removeEdge((IEdge) it.next());
        }
    }
}