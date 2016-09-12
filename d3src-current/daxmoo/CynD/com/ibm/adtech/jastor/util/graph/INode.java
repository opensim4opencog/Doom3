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
 */
public interface INode extends IGraphPart {
	
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";

    public boolean isInGraph();

    public boolean isInGraph(IGraph graph);

    public IGraph getGraph();

    public List getIncomingEdges();

    public List getOutgoingEdges();

    public boolean hasIncomingEdge(INode n);

    public IEdge getIncomingEdge(INode n);

    public void removeIncomingEdge(IEdge e);

    public boolean hasOutgoingEdge(INode n);

    public IEdge getOutgoingEdge(INode n);

    public void removeOutgoingEdge(IEdge e);

}