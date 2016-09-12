/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor.util;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * 
 * @author Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>)
 *
 */
public abstract class PropertyIterator extends FilteredMappedIterator {
    public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
    
    StmtIterator it;
    Resource type;
    
    public PropertyIterator(Resource resource, Property property, Resource type) {
        super(resource.listProperties(property));
        this.type = type;
    }
    
    protected Object premap(Object obj) {
    	Statement stmt = (Statement)obj;
    	return stmt.getObject();
    }
    
    protected boolean accept(Object object) {
    	RDFNode rdfNode = (RDFNode)object;
    	if (type == null)
    		return true;
    	if (rdfNode.canAs(Literal.class)) {
    		// add literal support though we won't use it right away
    		
    		// if we are expecting any literal, anything will do 
    		if (type.equals(RDFS.Literal))
    			return true;
    		Literal literal = (Literal)rdfNode.as(Literal.class);
    		if ((literal.getDatatype() == null && !type.equals(RDFS.Literal)) ||
    			!literal.getDatatype().getURI().equals(type.getURI()))
    			return false;
    		else
    			return true;
    	} else {
    		if (type.equals(RDFS.Resource))
    			return true;
    		Resource resource = (Resource)rdfNode.as(Resource.class);
    		return resource.hasProperty(RDF.type,type);
    	}
    }
    
    protected Object postmap(Object obj) {
    	return getPropertyValue((RDFNode)obj);
    }
    
    public abstract Object getPropertyValue(RDFNode value);

}
