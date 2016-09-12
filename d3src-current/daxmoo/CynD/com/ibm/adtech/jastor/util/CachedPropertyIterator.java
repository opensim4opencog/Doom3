/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/

package com.ibm.adtech.jastor.util;

import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.ibm.adtech.jastor.Thing;

/**
 * 
 * @author Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>)
 *
 */
public class CachedPropertyIterator implements Iterator {
    public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
    
    Iterator it;
    Object cur;
    Resource resource;
    Property property;
    boolean objectProperty;
    
    public CachedPropertyIterator(List cache, Resource resource, Property property, boolean objectProperty) {
        it = cache.iterator();
        this.resource = resource;
        this.property = property;
        this.objectProperty = objectProperty;
    }
    
    public boolean hasNext() {
        return it.hasNext();
    }
    
    public Object next() {
        cur = it.next();
        return cur;
    }
    
    public void remove() {
        it.remove();
        // now remove from model.
        if (cur != null) {
            Model model = resource.getModel();
            if (!objectProperty) {
                StmtIterator itr = model.listStatements(resource,property,model.createTypedLiteral(cur));
                model.remove(itr);
            } else {
                Thing thing = (Thing)cur;
                StmtIterator itr = model.listStatements(resource,property,thing.resource());
                model.remove(itr);
            }
            cur = null;
        }
    }
    

}
