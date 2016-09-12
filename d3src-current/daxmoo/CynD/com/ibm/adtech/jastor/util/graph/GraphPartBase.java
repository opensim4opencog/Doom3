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
public abstract class GraphPartBase implements IGraphPart {
	
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";

    protected String name;

    protected Object data = null;

    public GraphPartBase(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.IGraphPart#setData(java.lang.Object)
     */
    public void setData(Object data) {
        this.data = data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.IGraphPart#getData()
     */
    public Object getData() {
        return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.adtech.slingshot.graph.IGraphPart#getName()
     */
    public String getName() {
        return name;
    }

}