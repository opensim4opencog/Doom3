/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor.util.graph;


/**
 * @author Elias Torres ( <a href="mailto:eliast@us.ibm.com">eliast@us.ibm.com </a>)
 * 
 */
public class HasCycle extends DFS {
	
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";

    private boolean hasCycle = false;

    // not-impl, makes no sense
    public synchronized void execute(INode start, INode end) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.algorithms.DFS#reinit()
     */
    protected void reinit() {
        super.reinit();
        hasCycle = false;
    }

    protected void foundBackEdge(IEdge edge) {
        hasCycle = true;
        done = true;
    }

    public Object result() {
        checkState();
        return new Boolean(hasCycle);
    }

    public boolean hasCycle() {
        checkState();
        return hasCycle;
    }
}