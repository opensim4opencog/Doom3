/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/cpl.php
 * 
 ******************************************************************************/
package com.ibm.adtech.jastor;

import java.util.HashMap;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * A basic factory that provides create methods for building block OWL types.
 * @author Joe Betz (<a href="mailto:betz@us.ibm.com">betz@us.ibm.com</a>)
 * @author Ben Szekely (<a href="mailto:bhszekel@us.ibm.com">bhszekel@us.ibm.com</a>)
 */
public class ThingFactory {
    
	public static final String copyright = "(C) Copyright IBM Corporation 2005  All Rights Reserved.";
	
	protected static HashMap objects = new HashMap();
	protected static HashMap listeners = new HashMap();

	public static Thing createThing(Resource resource, Model model) throws JastorException {
		return new ThingImpl(resource, model);
	}
	
	public static Thing createThing(String uri, Model model) throws JastorException {
		return new ThingImpl(model.createResource(uri), model);
	}

	public static Thing getThing(Resource resource, Model model) throws JastorException {
		return new ThingImpl(resource, model);
	}
	
	public static Thing getThing(String uri, Model model) throws JastorException {
		return getThing(model.getResource(uri), model);
	}
	
	/**
	 * Remove all objects from static cache of this factory.
	 */
	public static void clearCache () {
		objects.clear();
	}
	
	/**
	 * Clear the property caches of the individual objects cached in this factory.
	 * @param type if not null, clear caches of objects of this type only
	 */
	public static void clearCaches(Class type) {
	    Iterator it = objects.entrySet().iterator();
	    while (it.hasNext()) {
	        Thing thing = (Thing)it.next();
	        if (type == null || type.isInstance(thing))
	            thing.clearCache();
	    }
	}
	
	/**
	 * Register the given thing to be notified of events
	 * @param thing
	 */
	public static void registerThing(Thing thing) {
		Model model = thing.model();
		JastorModelListener listener = (JastorModelListener)listeners.get(model);
		if (listener == null) {
			listener = new JastorModelListener();
			model.register(listener);
		}
		listener.addThing(thing);
	}
	
	

}
