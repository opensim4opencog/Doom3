/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor.resource;

import com.hp.hpl.jena.rdf.model.Resource;
import com.ibm.adtech.jastor.Thing;

/**
 * Makes Jastor Thing objects be Jena Resource object as well.
 * 
 * @author Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>)
 * @author Rob Gonzalez (<a href="mailto:gonzo@us.ibm.com">gonzo@us.ibm.com</a>)
 */
public interface ResourceThing extends Thing, Resource {
    public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
}
