/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.listeners.StatementListener;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author Joe Betz (<a href="mailto:betz@us.ibm.com">betz@us.ibm.com</a>)
 * @author Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>)
 */
public class ThingImpl extends StatementListener implements Thing  {
	
    public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
	
	
    // must prefix these members so that they don't collide with
    // member variables in generated classes
    protected String _typeUri;
	protected Resource _resource;
    protected Model _model;
    
    
    protected ThingImpl() { 
    }
    
    public ThingImpl(Resource resource, Model model) throws JastorException {
		if (model == null)
			throw new JastorException("The model parameter must not be null.");
		this._model = model;
		this._resource = resource;
    }
        
	public String uri() {
		return _resource.getURI();
	}
    
    public Model model() {
        return _model;
    }
    
    public Resource resource() {
        return _resource;
    }
    
    public List listStatements() {
        return new ArrayList();
    }
    
    public void removeStatements() {
        _model.remove(listStatements());
    }
    
    public void clearCache() {
        
    }

	public void registerListener(ThingListener listener) {
        // Nothing to do since there are no properties on a thing object
    }
	
	public void unregisterListener(ThingListener listener) {
		// Nothing to do since there are no properties on a thing object	
	}
	
	public boolean isRDFType(Resource type) {
	    return _model.contains(_resource,RDF.type,type);
	}
	
	public String toString () {
    	Model m = ModelFactory.createDefaultModel();
    	m.add(listStatements());
        Writer w = new StringWriter();
        m.write(w);
        return w.toString();
    }
	
	public boolean equals(Object obj) {
        if(super.equals(obj)) return true;
        // This equals test might not be sufficient for the entire hierarchy
        if (obj instanceof Thing) {
            Thing other = (Thing)obj;
            if (other.resource().equals(this.resource())) {
                return true;
            }
        }
        return false;
    }
	
	

}
