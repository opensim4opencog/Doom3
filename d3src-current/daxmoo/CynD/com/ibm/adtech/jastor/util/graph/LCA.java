/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor.util.graph;

import java.util.Iterator;
import java.util.Vector;


/**
 * @author Elias Torres ( <a href="mailto:eliast@us.ibm.com">eliast@us.ibm.com </a>)
 * 
 */
public class LCA extends AlgorithmsBase {
	
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";

    private DFS dfs = new DFS();

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.algorithms.AlgorithmsBase#setGraph(com.ibm.adtech.slingshot.graph.IGraph)
     */
    public void setGraph(IGraph graph) {
        super.setGraph(graph);
        dfs.setGraph(graph);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.algorithms.AlgorithmsBase#execute()
     */
    public void execute() {
        startExecution();
        dfs.execute();
        endExecution();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.algorithms.AlgorithmsBase#result()
     */
    public Object result() {
        // TODO Auto-generated method stub
        return null;
    }

    public INode getLCA(INode node1, INode node2) {

        checkState();

        INode ancestor = null;

        // LCA(v,v) = v
        if (node1.equals(node2)) {
            return node1;
        }

        Vector ancestors1 = getAncestry(node1);
        Vector ancestors2 = getAncestry(node2);

        // node2 is an ancestor of node1
        int index = ancestors1.indexOf(node2);
        if (index != -1 && index != (ancestors1.size() - 1)) {
            return node2;
        }

        // node1 is an ancestor of node2
        index = ancestors2.indexOf(node1);
        if (index != -1 && index != (ancestors2.size() - 1)) {
            return node1;
        }

        // node1 == root
        if (ancestors1.size() == 1) {
            return node1;
        } else if (ancestors2.size() == 1) {
            // node2 == root
            return node2;
        }

        Iterator it1 = ancestors1.iterator();
        Iterator it2 = ancestors2.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            INode ancestor1 = (INode) it1.next();
            INode ancestor2 = (INode) it2.next();

            if (ancestor1.getName().equals(ancestor2.getName())) {
                ancestor = ancestor1;
            } else {
                break;
            }
        }

        return ancestor;
    }

    private Vector getAncestry(INode node) {

        Vector ancestors = new Vector();
        ancestors.add(node);

        INode current = dfs.getParent(node);

        while (current != null) {
            ancestors.insertElementAt(current, 0);

            current = dfs.getParent(current);
        }

        return ancestors;
    }

}