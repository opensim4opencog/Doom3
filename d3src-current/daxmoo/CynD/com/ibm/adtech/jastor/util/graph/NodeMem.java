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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * @author Elias Torres ( <a href="mailto:eliast@us.ibm.com">eliast@us.ibm.com </a>)
 * 
 */
public class NodeMem extends GraphPartBase implements INode {
	
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";

    private GraphMem graph = null;

    public NodeMem(String name) {
        super(name);
    }

    protected LinkedList incomingEdges = new LinkedList();

    protected LinkedList outgoingEdges = new LinkedList();

    protected Map        incoming      = new HashMap();

    protected Map        outgoing      = new HashMap();

    void addIncomingEdge(EdgeMem e) {
        incoming.put(e.getSource(), e);
        incomingEdges.add(e);
    }

    void addOutgoingEdge(EdgeMem e) {
        outgoing.put(e.getDestination(), e);
        outgoingEdges.add(e);
    }

    public IEdge getIncomingEdge(INode n) {
        return (IEdge) incoming.get(n);
    }

    public boolean hasIncomingEdge(INode n) {
        return incoming.containsKey(n);
    }

    public IEdge getOutgoingEdge(INode n) {
        return (IEdge) outgoing.get(n);
    }

    public boolean hasOutgoingEdge(INode n) {
        return outgoing.containsKey(n);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.Node#isInGraph()
     */
    public boolean isInGraph() {
        return (graph != null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.Node#isInGraph(com.ibm.adtech.slingshot.graph.Graph)
     */
    public boolean isInGraph(IGraph graph) {
        return this.graph == graph;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.INode#getGraph()
     */
    public IGraph getGraph() {
        // TODO Auto-generated method stub
        return this.graph;
    }

    void setInGraph(GraphMem graph) {
        this.graph = graph;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.Node#getIncomingEdges()
     */
    public List getIncomingEdges() {
        return Collections.unmodifiableList(incomingEdges);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.Node#getOutgoingEdges()
     */
    public List getOutgoingEdges() {
        return Collections.unmodifiableList(outgoingEdges);
    }

    public String toString() {
        return getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.INode#removeIncomingEdge(com.ibm.adtech.slingshot.graph.IEdge)
     */
    public void removeIncomingEdge(IEdge e) {
        IEdge removed = (IEdge) incoming.remove(e.getSource());
        incomingEdges.remove(removed);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.INode#removeOutgoingEdge(com.ibm.adtech.slingshot.graph.IEdge)
     */
    public void removeOutgoingEdge(IEdge e) {
        IEdge removed = (IEdge) outgoing.remove(e.getDestination());
        outgoingEdges.remove(removed);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof INode)) {
            return false;
        }

        return getName().equals(((INode) obj).getName());
    }
}