/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor.ant;

import org.apache.tools.ant.Task;


/**
 * 
 * @author Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>)
 * @author Elias Torres (<a href="mailto:eliast@us.ibm.com">eliast@us.ibm.com</a>) *
 */
public class CustomThingElement extends Task {
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
	
	private String thingInterface;
	private String thingImpl;
	private String thingFactory;	
	
    public String getThingFactory() {
        return thingFactory;
    }
    
    public void setThingFactory(String thingFactory) {
        this.thingFactory = thingFactory;
    }
    
    public String getThingImpl() {
        return thingImpl;
    }
    
    public void setThingImpl(String thingImpl) {
        this.thingImpl = thingImpl;
    }
    
    public String getThingInterface() {
        return thingInterface;
    }
    
    public void setThingInterface(String thingInterface) {
        this.thingInterface = thingInterface;
    }
}
