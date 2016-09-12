/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Thing is the base ontology class of all other ontology classes.
 * @author Joe Betz (<a href="mailto:betz@us.ibm.com">betz@us.ibm.com</a>)
 * @author Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>)
 */
public interface Thing {
    
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
	
	public static final Resource TYPE = RDFS.Resource;
    
	public String uri();
    
	public Resource resource();
    
	public Model model();
	
	public List listStatements();
	
	public void removeStatements();
	
	public void clearCache();
    
    void registerListener(ThingListener listener);
    
    void unregisterListener(ThingListener listener);
    
    public boolean isRDFType(Resource type);
	
}