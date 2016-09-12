/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/


package com.ibm.adtech.jastor.util.graph;

import java.util.Comparator;


/**
 * @author Elias Torres ( <a href="mailto:eliast@us.ibm.com">eliast@us.ibm.com </a>)
 * 
 */
public class EdgeMem extends GraphPartBase implements IEdge {
	
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";

    public class EdgeComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            IEdge e1 = (IEdge) o1;
            IEdge e2 = (IEdge) o2;

            // return e1.ge
            return 0;
        }
    }

    private GraphMem graph  = null;

    private NodeMem  source = null;

    private NodeMem  dest   = null;

    public EdgeMem(String name, INode source, INode dest) {
        super(name);
        this.source = (NodeMem) source;
        this.dest = (NodeMem) dest;
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

    void setInGraph(GraphMem graph) {
        this.graph = graph;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.Edge#getSource()
     */
    public INode getSource() {
        return source;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.Edge#getDestination()
     */
    public INode getDestination() {
        return dest;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Edge: ");
        buffer.append(getName());
        buffer.append(" ( ");
        buffer.append("source=");
        buffer.append(source.toString());
        buffer.append(", dest=");
        buffer.append(dest.toString());
        buffer.append(" )");
        return buffer.toString();
    }

}