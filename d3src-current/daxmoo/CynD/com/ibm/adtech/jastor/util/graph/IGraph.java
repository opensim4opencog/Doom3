/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor.util.graph;

import java.util.List;

/**
 * @author Elias Torres ( <a href="mailto:eliast@us.ibm.com">eliast@us.ibm.com </a>)
 * 
 * 
 */
public interface IGraph extends IGraphPart {
	
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";

    public boolean contains(IEdge e);

    public boolean contains(INode n);

    public List edges();

    public List nodes();

    public boolean isEmpty();

    public int getEdgeCount();

    public int getNodeCount();

    public void addEdge(IEdge e);

    public void removeEdge(IEdge e);

    public void addNode(INode n);

    public void removeNode(INode n);

    public INode getNodeByName(String name);

    public IEdge getEdgeByName(String name);
}